# Unit Testing

This application was designed and intended to be used in conjunction with the (BLE_GATT_Client_for_Windows.exe) 
application that is available in (..........).

The tests developed as part of this application are done to try to reflect the interaction between these applications.

## Version of JUnit

The JUnit3 testing library was used for this project because it was the default version included in the Java module.

## How to Create Unit Tests

This project uses the JUnit testing framework provided by Java [does not require additional dependencies].

**Note:** Unit tests for a module are stored in directory (wifi_locator_app/src/test). These files are only viewable if
we select the "Project Files" view on the "Project" directory structure in the top-left corner of the IntellIJ IDE display.

To create a new set of tests for a Class:
1. Navigate to the Java Class file containing the class to be tested
2. Highlight the Class Name (Ex: outgoingApiCaller)
3. Right-click on the highlighted name, and select "Go To" -> "Test" -> "Create New Test" [Ensure its got the correct format for JUnit 4]
4. This will prompt the user to select the name of the test file and testing library to use
5. Test files created this way will be stored in (wifi_locator_app/src/test/java)

## Disabling Automatic Compilation of Unit Tests

Test files can often create problems when using the "Run" command in IntelliJ, such as IntelliJ preventing you from
running a Java module because there are dependencies which are not detected in Unit Test Files (because they are only
scoped to be provided during testing in the POM).

Remove these files from compilation by:
1. Navigating to "File" -> "Settings"
2. Navigate to "Build, Execution and Deployment" -> Compiler -> Excludes -> Add the "src/test/java" folder