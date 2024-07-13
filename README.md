
# About this App

This application was developed to be a companion app to an ESP32 that has been flashed with the
(https://github.com/Martin-Biomed/wifi-data-acquisition-server) project.

The original ESP32 project allows the board to be controlled via BLE messaging in order to execute some functions
which can provide Wi-Fi information around the surrounding area and its location. Refer to the project page for
the (wifi-data-acquisition-server) for more details.

This application further expands on the capabilities provided by the ESP32 and provides an interface to easily control 
the ESP32. Using the basic data sent from the ESP32, we provide further functionality with the additional processing 
power of a PC.

Refer to the "documentation" directory in this project for a full description on how this project works.

At its core, this project was developed as a learning exercise for myself, who wanted to have experience developing
Desktop applications with Java, and to exercise some of my knowledge of the basic OSI Reference Model (Networking).

## Pre-Requisites for using this App

### The (BLE_Client_for_Windows.exe) App

A separate application was developed that functions as a way to translate RESTful API requests sent on localhost to
BLE GATT messages to send to a BLE-capable endpoint device (which uses BLE GATT).

This separate project was required because support for the BLE Stack on Windows was limited for Java-based projects,
so it was easier to develop a general-purpose Python-based project to translate our messaging.

**Note:** To successfully use the (BLE_Client_for_Windows.exe) app with this project, you must run the app in API mode. 
This can only be done by opening the (BLE_Client_for_Windows.exe) app first and using its GUI to select API mode.

Link to project: https://github.com/Martin-Biomed/ble_client_for_windows

### The (wifi-data-acquisition-server) ESP32 Project

As mentioned previously, this application is meant to expand on the capabilities of an ESP32 running the
(wifi-data-acquisition-server) project.

Link to project: https://github.com/Martin-Biomed/wifi-data-acquisition-server

## Unused Content

I have decided to maintain the unused content that was supposed to go into this project but for various reasons, did
not actually end up being used.

Additionally, the "build_container" included in this project is not necessary for this project. It was originally
created to provide a standardised Linux build environment to build certain software packages that were meant to be
used for this project. But ultimately, those software packages ended up as part of the "unused_content".

I could have made a Build container based on a Windows Docker Image (to provide a controlled build env so that I can run
my Maven builds), but ultimately decided against it because Windows has behaviour which makes it harder
to work with Docker.