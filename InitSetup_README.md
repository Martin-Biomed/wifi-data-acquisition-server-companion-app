
## Java Version
This Project was built using OpenJDK8.
- File Downloaded from: https://adoptium.net/en-GB/temurin/releases/?os=windows&package=jdk&version=8
- Installed using the MSI Installer: (OpenJDK8U-jdk_x64_windows_hotspot_8u402b06.msi)

OpenJDK8 must be manually linked to project:
- Navigate to: File -> Project Structure:
  - Project Settings -> Project
  - Platform Settings -> SDK

## Build Tool Setup: Maven

This project uses the Maven build tool to produce build artifacts.

### Add Maven Support to the Project

These steps were used in an existing Java project in order to add Maven support.
1. Right-click on the Java Project where you want to add Maven support.
2. Select "Add Framework Support".
3. Select "Maven" from the options and click OK.

Completing these steps adds a default (pom.xml) file and a standard Maven layout.
- Navigate to the (pom.xml) file to add a Group ID (usually the name of the company or group that created the project)
- Ensure that the (pom.xml) is set up to compile for OpenJDK8 by checking: <maven.compiler.source>

### Change the Location of Local Maven Repository

By default, Maven will use the repository installed in your PC (C:\Users\[username]\.m2).
For this project, we want our local MVN repository to be version controlled with the rest of the project.

To change the location of the local Maven repository, follow these steps:
1. In IntelliJ, navigate to: Files -> Settings -> Build, Execution and Deployment -> Build Tools -> Maven

2. Update the repository locations:
   - In "User Settings File", tick the (Override) option. 
      Fill in the field with: {absolute_path_to_java_project}\maven_repo\settings.xml
   - In "Local Repository", tick the (Override) option.
            Fill in the field with: {absolute_path_to_java_project}\maven_repo\repository

### Adding Dependencies to the Main POM

These instructions can be used if we need to add packages (dependencies) at the project-level to the main POM:
1. Navigate to the main project (pom.xml) file. 
2. Press keys [Alt] + [Insert]. This brings up the "Generate" menu.
3. Select "Add Dependency". This opens up the "Dependencies" view of the project where we can search for packages.
4. After we download the dependencies we need, navigate to the "Maven" Sidebar on the RHS of the screen.
   1. Select "Reload All Maven Projects"
   2. Select "Generate Sources and Update Folders for all Projects"