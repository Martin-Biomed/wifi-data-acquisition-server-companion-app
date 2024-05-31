# Using JavaFX

This project uses JavaFx to provide a GUI to allow the user to execute the functions included in this
project from a convenient interface.

## Downloading JavaFx

JavaFx was imported as a Maven package (chose version 17 because that has LTS until 2026).

### Using the JavaFx Archetype

The original Maven project was imported with no particular archetype because we wanted to develop some basic functions.

The approach taken to retroactively add JavaFx to the project is based on: https://mkyong.com/javafx/javafx-hello-world-example/

To retroactively apply the JavaFx archetype, the following steps were used:
1. On you main project POM, add the following entries:
   1. Dependency: javafx-controls
   2. Plugin: javafx-maven-plugin [Choosing the application Main file]

## Scene Builder (GUI Design Software)

Scene Builder is an Open-Source program that is separate from IntelliJ IDEA that can be used to design JavaFx GUIs.
Link to software: https://gluonhq.com/products/scene-builder/

The latest version of this SW that was compatible with Java 8 was downloaded as an executable JAR, and is included
as part of this Java project.

### The (.fxml) Files

The Scene Builder Software creates an (.fxml) file output after we save it.
A "resources" directory was manually created for this project's "src.main", and we store the (.fxml) files here.

When we create JavaFx Scenes in the "Gui" module, we can select a specific (.fxml) file from the resources directory
for the project so that we can invoke the prepared Scene.

### Linking to Java Controller File

The functionality of the GUI objects is normally defined in a Controller file.

To link the (.fxml) file on Scene Builder to a local Controller file (to star integrating the functionality with the GUI), 
you need to Select:

Document -> Controller -> Select your local "Controller Class" (Gui.Controller for this project)