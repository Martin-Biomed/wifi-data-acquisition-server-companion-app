
## Java Version
This Project was built using OpenJDK8.
- File Downloaded from: https://adoptium.net/en-GB/temurin/releases/?os=windows&package=jdk&version=8
- Installed using the MSI Installer: (OpenJDK8U-jdk_x64_windows_hotspot_8u402b06.msi)

OpenJDK8 [OpenJDK Version 1.8] must be manually linked to project:
- Navigate to: File -> Project Structure:
  - Project Settings -> Project
  - Platform Settings -> SDK

## Build Tool Setup: Maven

This project uses the Maven build tool (version 3.8.1) to produce build artifacts.

### Add Maven Support to the Project

These steps were used in an existing Java project in order to add Maven support.

**Note:** To use these steps, you must select the "Project" view on the "Project" directory structure in the top-left
corner of the IntellIJ IDE display, which will display the files relevant only to the Java project [no tests are displayed].

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

### Adding Dependencies to the Main POM (From Maven Repo)

These instructions can be used if we need to add packages (dependencies) at the project-level to the main POM:
1. Navigate to the (https://mvnrepository.com/artifact) website to identify whether the potential package exists.
2. Add the dependency to the main POM "dependencies" with the necessary data.

To manage existing dependencies, use the following instructions:
1. Navigate to the main project (pom.xml) file. 
2. Press keys [Alt] + [Insert]. This brings up the "Generate" menu.
3. Select "Add Dependency". This opens up the "Dependencies" view of the project where we can search for downloaded packages.
4. After we download the dependencies we need, navigate to the "Maven" Sidebar on the RHS of the screen.
   1. Select "Reload All Maven Projects"
   2. Select "Generate Sources and Update Folders for all Projects"

### Adding Dependencies to the Project (custom JAR)

Occasionally, there may be a need to create and compile your own Java JARs to be used as a dependency 
for your project. These files cannot be added from the default Maven repos and should be added using 
the following instructions.

**Method 1 (Unused):** For this reason, the Maven-Install Plugin was added to the main project POM (Using Maven "Generate"). 
This Maven plugin can be used to install the JARs as part of the Maven build (if we decide to use it).
- When adding JARs (which have been compiled elsewhere) to a Maven project, it is necessary to generate a POM
  for that JAR dependency to create the necessary metadata required by Maven to add the dependency.

**Method 2:** The dependency can be added by using the IntelliJ Project menu.
1. Navigate to: "File" -> "Project Structure"
2. Under "Project Settings" on the LHS Tab, select "Modules".
3. On the "Modules" menu, select "Dependencies".
4. Click on the "+" sign and select "JARs or Directories.."
   1. Navigate to the location of your custom JAR (should be stored in maven_repo/project_custom_jars) and select it.
   2. Press "OK".

After these steps, the Classes defined in the JAR can now be imported into your project Classes.

### Importing Dependencies into Class Files

If you know that the function you want to use is already listed in the main project POM.xml, then we can automatically add 
the "import" statements using IntelliJ. 
- Write the function or object declaration in the code. The IDE will often allow the user to add the import statement
that corresponds to the object/function by using: [Alt] + [Enter]

### How to Build the App

#### Using IntelliJ
On the left-hand side of the IDE, you will normally find the Maven tab (if the project was opened as a MVN project).
You can double-click on any of the build lifecycle stages to run that stage.

#### Using the Command Line
Ensure that your Java version running on the terminal is the same as what we set up in the IDE [OpenJDK version 1.8].
On the command line:
1. Navigate to the base location of the project repo (wifi_locator_app)
2. Enter the following on the terminal: `mvn --settings .\maven_repo\settings.xml -f pom.xml clean install`

#### Maven Final Build Artifact (Local Maven Repo)

When executing a Maven build using the command in the previous section, a new module will be added to the local 
project "maven_repo".  This directory includes the final (Application) JAR that is generated by the Maven build.

For this project, this directory has been called "practice_projects" because that is the name of the folder where this
project was developed.

It is recommended that this auto-generated directory is ignored in favour of the project "target" directory, as this 
is guaranteed to contain the latest version of this application after every Maven build.



