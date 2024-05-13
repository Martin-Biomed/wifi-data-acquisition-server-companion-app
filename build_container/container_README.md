

**Note:** These instructions are based on the use of PowerShell, and may not work for Linux-based OS.

### Downloading the Base Docker Image

The following image was selected for this project to be used as the build container:
*ubuntu/jre:8-22.04_39*

To download this image as a TAR, the following steps were used:

- Pull the image from the Repo: **docker pull ubuntu/jre:8-22.04_39**
- Save the image as a TAR: **docker save --output=ubuntu_jre.tar [Container ID]**
  - **Note:** The *ubuntu/jre:8-22.04_39* image needs to be deleted after this step to load the TAR.
- Load the Base Image to Docker: **docker load --input .\ubuntu_jre.tar**
- Give the Docker Image a Name: **docker tag [Container ID] ubuntu_jre**

The resulting TAR was moved to the "build_container" project of the folder.

### Using the Container Script (run_container)

When possible, it is recommended to use the PowerShell script to interact with the build container.
To use the PowerShell script:
- Open the Docker Desktop Application to start the Docker Engine.
- On a PowerShell terminal (Admin privileges), navigate to the root of the project directory "wifi_locator_app".
- Use the following command: 
  - powershell -ExecutionPolicy Bypass -File .\run_container.ps1 -user_option "rebuild_all"
  - Valid $user_option values include:
    - **rebuild_all** = Deletes containers and images (with the defined tag) and rebuilds them again
    - **run** = Executes the Docker run command defined by "run_docker_container"
    - **clean_all** = Deletes containers and images (with the defined name and tag in the PS1 script)
    - **rerun** = Stops and deletes the default project container, then executes "run_docker_container"

### Running the Container

If necessary, the container can also be ran using these PowerShell commands:
- Open the Docker Desktop Application to start the Docker Engine.
- On a PowerShell terminal (Admin privileges), navigate to the root of the project directory.
- Build the container: **docker build -t wifi_locator_container:v1 build_container**
- Run the container: **docker run -d --name=locator_build_container -v ${git rev-parse --show-toplevel}/build_container/build_artifacts:/tmp wifi_locator_container:v1**
- To go into the container shell, use: **docker exec -it locator_build_container bash**

### Available Builds
The container is structured such that each build artifact that can be generated has an associated Bash script
(located in shared_files).