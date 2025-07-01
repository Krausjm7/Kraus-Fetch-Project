# Kraus-Fetch-Project
Android Software Application for Grouping Users with Sorted Receipt Items

## Project Description
This Android application, developed in Kotlin using Jetpack Compose, is my submission for Fetch's interview coding challenge. It retrieves a list of items from a provided data source, processes the data according to specific business criteria, and displays them to the user in a clear, easy-to-read, and organized list format in an enhanced UI experience.

## Business Goals
The primary goals of this project is to demonstrate that the software application will:
* **Consume external data:** Effectively retrieve and handle JSON data from a data source.
* **Implement data processing logic:** Apply grouping, sorting, and filtering rules to the raw user data to meet specific display requirements.
* **Develop a user-friendly interface:** Presenting complex data in an intuitive and accessible list view on a mobile phone device.
* **Android software development skills:** Delivering a buildable and functional application using Kotlin language, Jetpack Compose, and other tools).

## Functionality & Features
The application implements the following core functionalities as per the business requirements:

1.  **Data Retrieval (I created a static source for this project, and would propose a future version enhancement for dynamic processing using LiveData):** The application retrieves item data from a data.json file included within the application's assets folder that comes from the static JSON formatted data located at `https://hiring.fetch.com/hiring.json`. In future production versions, this mechanism would be updated to extract data from a dynamic API endpoint, allowing for real-time data updates and more robust data management.
2.  **Data Filtering:** Automatically filters out any items where the "name" field is blank or `null` before display.
3.  **Data Grouping:** Displays all valid items grouped by their "listId".
4.  **Data Sorting:** Within each "listId" group, items are sorted first by "listId" in ascending order, and then by "name" in ascending order.
5.  **Easy-to-Read List Display:** The final results are presented in a clean and organized list with an intuitive UI.
    * **Fixed Header:** A static header for "List ID: 1" remains pinned at the top of the screen before the navigation menu to avoid overlapping.
    * **Sticky Headers:** All other "List ID" group headers are implemented as sticky headers, pinning directly below the fixed "List ID: 1" header as the user scrolls through the item names.
    * **Nesting:** All List ID headings are nestable to enhance the UI experience
    * **Labeling:** Each item within a group displays its numerical order, name, and ID clearly while labeling each List ID bucket with its subsequent names as different colors enabling the user to quickly keep track of the data while scrolling. The total items valid after filtering are displayed on the subsequent List ID heading to the far right.

## Technical Details
* **Platform:** Android
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Declarative UI)
* **Target Android Version:** Android 16 (API Level 36), per business requirements to be supporting the current mobile OS (released June 10, 2025)
* **Minimum Supported Android Version:** Android 16 (API Level 36), per business requirements to be supporting the current mobile OS (released June 10, 2025)
* **Serialization:** Kotlinx Serialization for parsing JSON data.
* **Build Tools:** The project is configured to build successfully with the latest stable (non-pre-release) Android Studio tools.

## How to Run the Application
After reviewing the code in the Master branch, these are instructions to set up and run this project locally:

1.  **Clone the Repository:**
    * Open your terminal or command prompt (e.g., Git Bash, PowerShell).
    * Navigate to the directory where you want to save the project (e.g., `cd Documents/AndroidStudioProjects`).
    * Execute the following command to clone the repository:
        ```bash
        git clone [https://github.com/Krausjm7/Kraus-Fetch-Project.git](https://github.com/Krausjm7/Kraus-Fetch-Project.git)
        ```
    * This will create a new folder named `Kraus-Fetch-Project` containing the project files.
2.  **Open in Android Studio:**
    * Launch Android Studio.
    * Select "Open an existing Android Studio project" and navigate to the cloned `Kraus-Fetch-Project` directory.
3.  **Sync Project:** Android Studio should automatically sync the Gradle project. If not, click "Sync Project with Gradle Files" (left click the small elephant icon in the top right toolbar).
4.  **Build and Run:**
    * Ensure you have an Android Virtual Device (AVD) or a physical Android device connected (running **Android 16 / API Level 36** or higher is required, as this is the minimum supported version).
    * Click the "Run 'app'" button (green play icon) in the top toolbar to build and deploy the application to your selected device/emulator.

---
