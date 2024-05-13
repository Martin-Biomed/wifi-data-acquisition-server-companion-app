
# This PowerShell Script builds and runs the project build container (for creating certain project artifacts).

# Prior to running this script, open the "Docker Desktop" application to start the Docker Engine.

# To run the container in interactive mode, ensure the (-it) option is used instead of (-d) in "docker run".

# To run this script, open a PowerShell terminal as Admin (or find another way to make PS1 scripts executable).
# Navigate to the app folder, use this command:
# - powershell -ExecutionPolicy Bypass -File .\run_container.ps1 -user_option "rebuild_all"

############################### Constants ####################################

# A single parameter is used to determine what the script does.
param([string]$user_option)

$LOCAL_DIR="${pwd}"

$IMAGE_NAME="wifi_locator_container"
$IMAGE_TAG="v1"
$CONTAINER_NAME="locator_build_container"

echo "Now Executing from: $LOCAL_DIR"

# This line defines the execution policy for the duration of the PS1 script
Set-ExecutionPolicy -ExecutionPolicy Unrestricted -Scope Process

#Start-Service com.docker.service

############################## Functions #################################
function build_docker_container
{
  docker build `
    -t ${IMAGE_NAME}:${IMAGE_TAG} `
    ${LOCAL_DIR}
}

function run_docker_container
{
  docker run -it `
    --name ${CONTAINER_NAME} `
    --volume ${LOCAL_DIR}/build_artifacts:/tmp/build_artifacts `
    --volume ${LOCAL_DIR}/shared_files:/tmp/shared_files `
    ${IMAGE_NAME}:${IMAGE_TAG}
}

############################# Options ##################################

# The "rebuild_all" option is also the default if the user leaves the argument field blank
if ($user_option -eq "rebuild_all" -or $user_option -eq $null) {
  # If no container image exists with selected attributes, create one
  if (!(docker images -q ${IMAGE_NAME}:${IMAGE_TAG} 2> $null))
  {
    echo "Building new container: ${IMAGE_NAME}:${IMAGE_TAG}"
    build_docker_container

    echo "Preparing to run the new container: $CONTAINER_NAME"
    run_docker_container
  }
  # Delete the old version of the container
  else{
    echo "Preparing to remove old container: $CONTAINER_NAME"
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME

    # Delete the old version of the container image
    echo "Removing old container: ${IMAGE_NAME}:${IMAGE_TAG}"
    docker image rm ${IMAGE_NAME}:${IMAGE_TAG}

    echo "Building new container: ${IMAGE_NAME}:${IMAGE_TAG}"
    build_docker_container

    echo "Preparing to run the new container: $CONTAINER_NAME"
    run_docker_container
  }
}

if ($user_option -eq "run"){
  echo "Preparing to run the new container: $CONTAINER_NAME"
  run_docker_container
}

if ($user_option -eq "clean_all"){
  echo "Preparing to remove old container: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME

  # Delete the old version of the container image
  echo "Removing old container: ${IMAGE_NAME}:${IMAGE_TAG}"
  docker image rm ${IMAGE_NAME}:${IMAGE_TAG}
}

if ($user_option -eq "rerun"){
  echo "Preparing to remove old container: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME

  echo "Preparing to run the new container: $CONTAINER_NAME"
  run_docker_container
}
