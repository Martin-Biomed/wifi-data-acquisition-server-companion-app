#!/bin/bash


echo "Starting the Bluetooth-Manager download and installation process ...."

git clone https://github.com/sputnikdev/bluetooth-manager-bluegiga.git

echo "Repo was cloned from Git......"

# The bluetooth-manager-bluegiga directory is created from the Git cloning
cd bluetooth-manager-bluegiga
bash .travis/install-dependencies.sh

# Extract the build artifact JAR
# The JAR filename is defined by (install-dependencies.sh) from the Git repo, not the actual version of the sw
cp lib/com.zsmartsystems.bluetooth.bluegiga-1.0.0-SNAPSHOT.jar /tmp/build_artifacts
