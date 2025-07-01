package com.krausfetchproject.application

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi // Imported for stickyHeader UI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width // Imported width for UI clarity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krausfetchproject.application.data.Item
import com.krausfetchproject.application.ui.theme.KrausFetchProjectApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues // Import PaddingValues for data structure to pad around UI components for better experience

sealed class ListItem {
    data class HeaderItem(val listId: Int, val itemCount: Int, val headerColor: Color) : ListItem()
    data class DataItem(val item: Item, val itemIndex: Int, val itemBoxColor: Color) : ListItem()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KrausFetchProjectApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFA0A0A0)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ItemListScreen()
                        Image(
                            painter = painterResource(id = R.drawable.fetch_logo_transparent),
                            contentDescription = "Fetch App Logo",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 30.dp)
                                .width(150.dp)
                                .height(75.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemListScreen() {
    var allProcessedItems by remember { mutableStateOf<List<ListItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    val expandedListIds = remember { mutableStateOf(mutableSetOf<Int>()) }
    val listState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val jsonString = withContext(Dispatchers.IO) {
                context.assets.open("data.json").bufferedReader().use { it.readText() }
            }

            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            val items = json.decodeFromString<List<Item>>(jsonString)

            val filteredAndSortedItems = items
                .filter { it.name != null && it.name.isNotBlank() }
                .sortedBy { it.listId }

            allProcessedItems = filteredAndSortedItems.toGroupedListItems()
            errorMessage = ""
        } catch (e: IOException) {
            errorMessage = "Error reading data file: ${e.message}"
            allProcessedItems = emptyList()
        } catch (e: Exception) {
            errorMessage = "Error processing data: ${e.message}"
            allProcessedItems = emptyList()
        }
    }

    if (errorMessage.isNotEmpty()) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    if (allProcessedItems.isEmpty() && errorMessage.isEmpty()) {
        Text("Loading items or no valid items to display...", modifier = Modifier.padding(16.dp))
    } else {
        // Separate headers and data items for easier processing in LazyColumn per required grouping
        val headers = allProcessedItems.filterIsInstance<ListItem.HeaderItem>()
        val dataItems = allProcessedItems.filterIsInstance<ListItem.DataItem>()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 115.dp),
            state = listState,
            // Apply contentPadding to the top to offset the sticky header
            contentPadding = PaddingValues(top = 0.dp) // This ensures sticky headers stop here for better UI experience
        ) {
            headers.forEach { header ->
                val isExpanded = expandedListIds.value.contains(header.listId)
                stickyHeader(key = "sticky-header-${header.listId}") {
                    HeaderView(headerItem = header, isExpanded = isExpanded) {
                        val currentSet = expandedListIds.value
                        val newSet = currentSet.toMutableSet()
                        if (isExpanded) {
                            newSet.remove(header.listId)
                        } else {
                            newSet.add(header.listId)
                        }
                        expandedListIds.value = newSet
                    }
                }

                if (isExpanded) {
                    val itemsForThisHeader = dataItems.filter { it.item.listId == header.listId }
                    items(
                        items = itemsForThisHeader,
                        key = { "data-${it.item.listId}-${it.item.id}" }
                    ) { listItem ->
                        ItemView(dataItem = listItem)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderView(headerItem: ListItem.HeaderItem, isExpanded: Boolean, onHeaderClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .clickable(onClick = onHeaderClick)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp)
                .background(headerItem.headerColor)
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "List ID: ${headerItem.listId}",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "(${headerItem.itemCount} Items)",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color.Black,
            modifier = Modifier.padding(end = 16.dp, start = 8.dp)
        )
    }
}

@Composable
fun ItemView(dataItem: ListItem.DataItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 32.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(dataItem.itemBoxColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${dataItem.itemIndex}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = dataItem.item.name ?: "No Name Provided",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            Text(
                text = "ID: ${dataItem.item.id}",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

val ITEM_INDEX_COLORS = listOf(
    Color(0xFFB0D9FF), // Light Blue
    Color(0xFFC8FFB0), // Light Green
    Color(0xFFFFF7B0), // Light Yellow
    Color(0xFFFFB0D9), // Light Pink
    Color(0xFFB0FFFF), // Light Cyan
    Color(0xFFE0B0FF)  // Light Purple
)

fun List<Item>.toGroupedListItems(): List<ListItem> {
    val groupedList = mutableListOf<ListItem>()
    val groupedMap = this.groupBy { it.listId }
        .toSortedMap()

    groupedMap.forEach { (listId, itemsInGroup) ->
        val currentGroupItemCount = itemsInGroup.size
        val colorIndex = (listId - 1) % ITEM_INDEX_COLORS.size
        val groupColor = ITEM_INDEX_COLORS.getOrNull(colorIndex) ?: Color.Gray

        groupedList.add(ListItem.HeaderItem(listId, currentGroupItemCount, groupColor))

        itemsInGroup.sortedWith(compareBy<Item> {
            it.name?.substringAfter("Item ")?.toIntOrNull() ?: Int.MAX_VALUE
        }.thenBy { it.name ?: "" }).forEachIndexed { index, item ->
            groupedList.add(ListItem.DataItem(item, index + 1, groupColor))
        }
    }
    return groupedList
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KrausFetchProjectApplicationTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFA0A0A0))) {
            val dummyItemsList = listOf(
                Item(id = 101, listId = 1, name = "Item 10"),
                Item(id = 102, listId = 1, name = "Item 2"),
                Item(id = 201, listId = 2, name = "Item 20"),
                Item(id = 202, listId = 2, name = "Item 1"),
                Item(id = 301, listId = 3, name = "Item 592"),
                Item(id = 302, listId = 3, name = "Item 6"),
                Item(id = 303, listId = 3, name = "Item 619")
            )
            val dummyGroupedItems = dummyItemsList.toGroupedListItems()

            val expandedInPreview = remember { mutableStateOf(mutableSetOf(1)) }
            val listState = rememberLazyListState()

            val headers = dummyGroupedItems.filterIsInstance<ListItem.HeaderItem>()
            val dataItems = dummyGroupedItems.filterIsInstance<ListItem.DataItem>()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 115.dp),
                state = listState,
                contentPadding = PaddingValues(top = 56.dp) // Set top padding here for preview
            ) {
                headers.forEach { header ->
                    val isExpanded = expandedInPreview.value.contains(header.listId)
                    stickyHeader(key = "sticky-header-${header.listId}") {
                        HeaderView(headerItem = header, isExpanded = isExpanded) {
                            val newSet = expandedInPreview.value.toMutableSet()
                            if (isExpanded) { newSet.remove(header.listId) } else { newSet.add(header.listId) }
                            expandedInPreview.value = newSet
                        }
                    }

                    if (isExpanded) {
                        val itemsForThisHeader = dataItems.filter { it.item.listId == header.listId }
                        items(
                            items = itemsForThisHeader,
                            key = { "data-preview-${it.item.listId}-${it.item.id}" }
                        ) { listItem ->
                            ItemView(dataItem = listItem)
                        }
                    }
                }
            }
            Image(
                painter = painterResource(id = R.drawable.fetch_logo_transparent),
                contentDescription = "Fetch App Logo",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp)
                    .width(150.dp)
                    .height(75.dp)
            )
        }
    }
}