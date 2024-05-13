# From the Docker Image we selected
#FROM ubuntu_jre
FROM ubuntu:jammy

# Set the working directory in the container
WORKDIR /tmp

RUN apt update

RUN apt install -y \
    cmake build-essential openjdk-8-jdk libglib2.0-dev doxygen git checkinstall

# Make Port 80 (HTTP) available outside of the container
EXPOSE 80


#CMD ["bash, /tmp/tinyB.sh"]

