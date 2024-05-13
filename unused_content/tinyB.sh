#!/bin/bash

echo "Starting the TinyB download and installation process ...."

git clone https://github.com/intel-iot-devkit/tinyb.git

echo "Repo was cloned from Git......"

cd tinyb

# Build the Java TinyB packages (based on JNI)
echo "Building tinyB Java JAR...."
mkdir build
cd build
cmake -DBUILDJAVA=ON ..
make

# Move the resulting Java packages to "build_artifacts"
echo "Moving build artifact to directory: /tmp/build_artifacts"
cd ..
mkdir /tmp/build_artifacts/tinyB
cp -r build/. /tmp/build_artifacts/tinyB

# Extract the JAR for quick access
cp /tmp/build_artifacts/tinyB/java/tinyb.jar /tmp/build_artifacts
