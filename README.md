# Ashe

Check out our docs 🚀 at https://ashe-docs.vercel.app/

## Installation Guide

To set up and run the project, please follow these steps:

1. Ensure that you have Docker installed on your machine. If you do not have Docker installed, please follow the instructions provided at https://docs.docker.com/get-docker/.

2. Clone the repository by running the following command: `git clone https://github.com/freyam/ashe`.

3. Navigate to the root directory of the project and run the following command: `docker compose up`.

4. This will start the frontend and backend services in Docker containers. If this is the first time you are running this command, Docker will download the necessary images from the Docker Hub, which may take some time depending on your internet connection speed. Once the command completes successfully, the frontend will be accessible at `http://localhost:3000`, and the backend will be accessible at `http://localhost:8000`.

5. To access both the frontend and backend, you can simply go to http://localhost without specifying any port number, as we have set up Nginx for easy access.

That's it! You should now have Ashe up and running on your machine.
