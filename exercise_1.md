# CICD Exercise (Build and Push Docker Image)

## Pre-Requesites

1. Create a GitHub account if you don't have it already. If you prefer to work on a new fresh Github account, you can create a throway email in Hotmail or Yahoo Mail. 

    1. Create a new repository called "academy" and make it Public.
    2. Make sure the "Actions" tab is enabled on the GitHub page.
    3. Generate an SSH Key and configure it on GitHub to make sure you have the right permissions to Read and Write to the repository.
        1. Execute the command 'ssh-keygen -t ed25519 -C "academy"'
            1. The default name and location is "../.ssh/id_ed25519", you can change it if you want, but the rest of the instructions use the default naming.
        2. Copy the content of the file "id_ed25519.pub".
        3. Go to GitHub Settings and then "SSH and GPG keys"
        4. Press "New SSH key" and Paste the content you copied in the previous steps.
    4. To add the ssh key locally to use it with git cli, just run the command "ssh-add id_ed25519"

## Import the exercise code and create the first workflow

1. Follow the instructions for SSH clone (They are also below) on the Github repository page to import the code inside the base_code folder (inside the resources folder) and create the "main" branch. After this, you should be able to see the base code for our exercise on GitHub.

- **NOTE: Use "git add ." to send all the code to github.**
    ```
    git init
    git add .
    git commit -m "first commit"
    git branch -M main
    git remote add origin git@github.com:{{github-username}}/{{repo-name}}.git
    git push -u origin main
    ```

2. Go to the Actions page and select the "Java with Maven" suggested workflow. A workflow template will be created for you, analyze the file and update it to look like this: (NOTE: Remove the step "Update dependency graph" in the end of the workflow)

    ```yaml
    name: Java CI with Maven

    on:
    push:
        branches: [ "main" ]
    pull_request:
        branches: [ "main" ]

    jobs:
    build:
        runs-on: ubuntu-latest
        steps:
            - uses: actions/checkout@v4

            - name: Set up JDK 21
            uses: actions/setup-java@v4
            with:
                java-version: '21'
                distribution: 'temurin'
                cache: maven

            - name: Build with Maven
            run: mvn -B package --file pom.xml
    ```

    Commit the changes to the main branch (For convenience we will only use the main branch across this exercise). 
    - Use the "Commit changes..." button and commit directly on the main branch.

3. After you commit to the Github UI in the Browser, you can go to the Actions tab and check the result of the build.
    - Check on the workflow file the reason why it ran automatically. (Hint: Check the "on" configuration on the workflow)

4. Pull the code to your machine and add a new step to the workflow to create a new docker image. You can run the command below in the new step. Commit and push to test that it builds the image in the Github Actions.

    ```bash
    docker build . --file [path_to_dockerfile] --tag academy:$(date +%s)
    ```

5. Now let's list the docker images in the workflow and confirm that indeed the docker image was created.
    - Use the command "docker image ls" 
    - Transform the previous step instead of creating a new one by using a multi-line command (Example: https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions#jobsjob_idstepsrun)
    - Check in the GitHub run logs at the end of the build docker image step you should see the image listed.

6. As you have seen it is hard to see the output of your "docker image ls" command when you have a command that outputs so much logs like "docker build". Separate the "docker image ls" in its own step.

## Create a custom action for the maven build step

1. Create a file for it in the following path of your repository ".github/actions/maven_build/action.yml". You can follow the template on the "action.yaml" file in the resources for this exercise.

2. Call the new action from the workflow:
    - To reference the new action created on the workflow please add a new step but instead of adding a "run" property add the property "uses" on the GitHub action step.

        > uses: ./.github/actions/maven_build

    - Normally the path can be defined with a relative path from the root since the action is defined in the same repository of the workflow. But, for our case, since we are going to use Act in a few steps, this will help.

3. Update the action with code to run the maven build.
    - Replace the step that was building with maven with the new step that calls the action.

4. Push the code and confirm that the image is built the same way as before. Check the "Actions" tab on GitHub.

## Create a custom action for the docker build step

1. Create a file for it in the following path of your repository ".github/actions/docker_build/action.yml". You can follow the template on the "action_with_inputs.yaml" file in the resources for this exercise. You don't need to change anything in the "inputs" property, we will use it ahead.

2. Call the new action from the workflow:
    - To reference the new action created on the workflow please add the property "uses" on the GitHub action step.

        > -uses: ./.github/actions/docker_build


3. Update the action with the code to run the docker build.
    - Replace the step that was building the docker image with the new step that calls the action.

4. Push the code and confirm that the image is built the same way as before. Check the "Actions" tab on GitHub.

## Improve the docker build custom action

1. To enhance the flexibility of our custom action and enable it to handle various use cases beyond just building the image for our project, we need to add the following inputs on the action file (For the first you can use the template input already in the example, for the other two just copy and paste):
    - path_dockerfile: This allows us to target the correct Dockerfile, even if it has a different name or is located in a different directory.
        - Define as default value "Dockerfile" and not required.
    - image_name: Specifies the name for the generated Docker image.
        - Define as required with no default value
    - tag_name: Specifies the tag to be added to the final image.
        - Define as not required with no default value.

2. Adapt the action code to use the input variables to build the docker image with the following requirements:
    - Adapt the docker build command to use the values coming from the input values for the Docker file Path and Docker Image Name. (Check the next step to see how to use the input variables)
    - Unix timestamp needs to be used if the input tag is not sent.
        - Use the code below to put the value in the TAG environment variable.
        ```
            if [ -z "${{ inputs.replace_with_input_name }}" ]; then
                export TAG=$(date +%s)
            else
                export TAG="${{ inputs.replace_with_input_name }}"
            fi
        ```
    - Use this new TAG environment variable in the build command.


4. Declare the input parameters as below in the call of your custom action in your workflow file. This allows us to send our custom input values to the action. Do it for the Docker Image Name and Docker Tag Name.
    ```
    with:
    {{ input-name }}: {{ input-value }} 
    ```

5. Test by calling the action with a tag_name and without that input value. See the difference in the logs on the Docker image generated.

## Multiple Job vs Multiple Steps

1. If you still have the step for calling "docker image ls" now is the time to separate it in a different job, if you don't, just create it.

2. Inside the "jobs" key create another job below the "build" job. Use the same "runs-on" and inside the "steps" key, add the "docker image ls" step. (After adding it to the new job, delete from the previous job)

3. Commit and see the Run in Github. You will see that the new job will run a lot faster than the previous one because it has very little complexity.

4. Add a "needs" property to your new job to declare you need the "build" job finished before starting. (https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions#example-requiring-successful-dependent-jobs)

5. Now you should be able to see your new job running after the "build" job. (On the Summary page you should see a difference between the workflow with the "needs" property and before) You should also be capable of seeing that the image list command from docker is returning an empty list. This is normal because whenever you declare a new job, you are using a new machine so, you lose every resource you created on the other jobs like, for example, the base code that you need "checkout" again if needed. 

## Moving artifacts between jobs

1. So, if you can't use the same files from one job to another, let's learn how we can "upload" and "download" between jobs. First, let's declare an "env:" key on the top of our workflow, below and at the same level as the key "on:"

    ```
    env:
        replace_with_key_name: replace_with_key_value
    ```

2. Create two environment variables, Docker Image Name and Docker Tag Name, that you wrote in the inputs on the call of your custom docker build action with the same values. After that, you can use them on the custom action call by replacing in the "with" property, the values for the new environment variables (eg. $Image_Name). Push and test that it still builds like before.

3. Create a new step below the docker build step to save the docker image you just created to a file. Use the command below where, for the file name you can use the image name environment variable but with the extension ".tar" and, for the image name, you need the imageName:tagName so build it using the environment variables the same way you did it inside the custom action.

    ```
    docker save -o replace_with_file_name_with_extension replace_with_image_name
    ```

4. After you have the docker image saved in a file we need to upload it. Let's use the upload-artifact action from Github (Instructions here -> https://github.com/actions/upload-artifact?tab=readme-ov-file#upload-an-individual-file)
    - For the name, declare another environment variable because you will need to re-use it after. (Note: Call it "artifact_name") 
    - For the path, make it dynamic and use the environment variable inside the string to declare the file.
    - NOTE: to use the env variable inside quotation marks "" write it like ${{ env.replace_with_env_name }}

5. Now that the docker image is saved and uploaded, let's bring it back in the second job. Before the step of listing the docker images, you need to create two new steps.
    - First, a simple step with actions/download-artifact (instructions here -> https://github.com/actions/download-artifact?tab=readme-ov-file#download-single-artifact) where you will send the artifact name.
    - Secondly, we will create a "Load Docker Image" step with the command below that will allow us to load a docker image from a tar file. 

        ```
        docker load -i replace_with_image_name_env.tar
        ```

6. After all this, you should be able to run again the action and see that you finally can list with success the image in the second job.

## Login and Push the image to Docker Hub

1. Let's create a new step on our workflow to use the action provided by Docker to login on Docker (Use the step in the first example in the Usage section):
    - https://github.com/docker/login-action
    - Just adapt the step so that both values are coming from the secrets and not just the password.

2. As you can see, the action just needs two variables, the username and password. We are going to pass these two values to the action from the GitHub Secrets.
    - Go to your profile on Docker Hub (use the same account that you used on Docker Desktop) and retrieve your username. In the Docker Hub settings of your account generate an Access Token and save it somewhere on your machine. (NOTE: Make sure to give it READ/WRITE permissions.)
    - Go to the Settings on your Git Hub repository and add two repository secrets on the "Actions secrets and variables".
        - You can use the same names you see in the example in step 1.
        - DOCKERHUB_USERNAME
        - DOCKERHUB_TOKEN

3. Now that we can log in to our Docker account, we can build a new custom image to push the Docker image we built in the previous custom action. Let's create a new custom action to add to our current "docker_build" and "maven_build" actions. You can call it "docker_push" and make sure that:
    - Add two inputs in the custom aciton, one for the Docker Username and another one for the Docker image reference generated previously.
    - The code of the custom action should have two lines, one to tag the docker image we just loaded before with your username of Dockerhub to make sure we can publish it, and another line to execute the docker push.

4. Create another step (below the docker login) to call your new custom action. Please make sure that on the "docker image reference" input variable you should send something like image_name:image_tag of the previously loaded image. If you did not add it yet, please add the checkout code step to the start of the job as you have in the first job due to the reasons mentioned in the end of the "Multiple Job vs Multiple Steps" exercise, this way we can call the new custom action we just created.

5. Commit the code and after you run it successfully, go to DockerHub to confirm that your new image was uploaded. You should have a repository with the image name and inside of it the respective tag name.

## Running Quarkus Tests

1. Now let's go back to the maven build step. Go to the file "src/test/java/it/CarTest.java" and uncomment it. Commit and Push. You should see the build failing because of a test that needs a database running.

2. To run the tests successfully we need a Postgres container. For that, GitHub provides the necessary documentation to create a PostgreSQL "service container" on the workflow. Edit the workflow and add the necessary code to the workflow.
    - Use the example "services:" that is mentioned in the first example here -> https://docs.github.com/en/actions/use-cases-and-examples/using-containerized-services/creating-postgresql-service-containers#running-jobs-directly-on-the-runner-machine
    - The main values you need to configure are the POSTGRES_USER and POSTGRES_PASSWORD env variables with the values "academy", the POSTGRES_DB env variable with the value "postgres" and also map the port 5432 (you will find it in the examples in the link above)

3. Now that we have our Database ready and exposed at localhost, the test should run with success.

## Running Manually the Workflow on Github

1. If you go to our workflow on the Actions tab you will see that there is no button to Run the Job. This happens if the template you are using only has "on: push:" and "on: pull_request" which makes these two events the only triggers. To avoid this, we need to add another event to trigger the workflow, in this case just add an empty "workflow_dispatch" (Example below).

    ```
    on:
        ...
        workflow_dispatch:
    ```

2. Commit and after you push the code you should be able to run your workflow manually on GitHub.

## Using the environment to get User approval to proceed.

1. At the moment we have two jobs in our workflow, the first to build the image and the second one to push it. Let's imagine that for more critical environments, like E2E or PROD, we will need to approve the push. First, let's go to the Settings of our GitHub repository and create 4 Environments:
    - Test
    - Int
    - E2E
    - Prod

2. Now, to add the extra security, for our "critical" environments, go to their configuration and select "Required reviewers" and search for your GitHub username to add yourself to it and save the rules.

3. Back to the workflow, we need to know to which environment we are deploying to something that we currently have no way to know. Let's add an input to the workflow. The variable should be called "environment" and it should only have the 4 environments that we allow to be chosen. See -> https://docs.github.com/pt/enterprise-cloud@latest/actions/writing-workflows/workflow-syntax-for-github-actions#example-of-onworkflow_dispatchinputs.

4. Remove or Comment the "push" and "pull_request" triggers because, from now on, we only want to use the "workflow_dispatch" due to the mandatory variable for the target environment. (You can also define a default environment variable in case you still want to keep the build starting automatically with commmits)

5. Now that we are receiving the environment and we already configured the environments on our GitHub project, let's add the property "environment" to the second job of our workflow and reference as value the input of your workflow (You can see in the URL mentioned before how to use the variable).

6. Commit and Push the new code. Remember, for the first time, now you need to go to your workflow on Github and start it manually. Try out the environments.

7. As improvements:
    - Adapt also the name of the workflow to have the target environment mentioned in the title. Eg. "Docker build and push to E2E".
        - The title is a new property ("run-name") that you can add below the "name"
    - Change the tag of the image to be "{{tag}}-{{environment}}" where the tag is a new input on the workflow (free text) and the environment is your already implemented input. You can use "latest" as the default for the tag workflow input.