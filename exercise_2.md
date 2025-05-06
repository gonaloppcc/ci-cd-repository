# CICD Exercise 2 (Matrix to build/push multiple docker images)

## Matrix to allow multiple steps simultaneous 

1. Let's enhance our workflow by introducing a matrix strategy to allow multiple steps to run simultaneously. This is particularly useful when you want to test your application across multiple environments or configurations. In our case, we will use it to build our image to both arm64 architecture and amd64 architecture.

2. First let's create our matrix that will define the jobs strategy. Create a new job in our workflow, it needs to execute before the other ones so put it in the first place. You can call it "matrix-setup" and the steps should follow the below template.

    ```
    - id: set_matrix
      run: |
        MATRIX_JSON='{
          "include": [
            { "var_x": "value1", ...},
            { "var_x": "value2", ...}
          ]
        }'
        MATRIX_JSON=$(jq -c <<< $MATRIX_JSON)
        echo "matrix=$MATRIX_JSON" >> $GITHUB_OUTPUT
    ```

- In this template above we will have two executions for the jobs using this matrix strategy where, for each job, we will use the values that are inside of each respective array. Example: While on one of the runs you will have the "var_x" with the value "value1" in the other simultaneous run, you will have it with the value "value2". The same for the other variables you might add.

3. Let's fill our matrix already, you will need 3 different variables for each array in the matrix, you can call them whatever you want but each array should have one variable for the architecture ("linux/arm64" and "linux/amd64"), one variable that should be again the architecture but choose a value without the "/" because we will need to differentiate the docker images and we cannot use the special character "/" on the name (this would give us trouble using the previous variable) and, for the last variable, we will need to define the "runs-on" machine that we will use. Since we are building in two different architectures in this last exercise, we will need a respective operative system, so the last variable needs to assume one of these values:
    - "ubuntu-24.04-arm" for the arm architecture
    - "ubuntu-latest" for the amd architecture

4. Add as an output of our matrix job the matrix you added in the GITHUB_OUTPUT in the following line "echo "matrix=$MATRIX_JSON" >> $GITHUB_OUTPUT". Example -> https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/passing-information-between-jobs#example-defining-outputs-for-a-job

5. Now, for the matrix job, add a "needs" property on both the other two jobs (build and push docker image). This will make sure they only run after the matrix is defined. NOTE: For the Push job you will need to have two values inside the "needs" property

6. We now know that our two previous jobs will only execute after we have our matrix defined. So, let's add our matrix strategy that comes from our first job to our two other jobs. Example -> https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/evaluate-expressions-in-workflows-and-actions#example-returning-a-json-object

7. You can commit and push the code. Even though it will fail, you should already be able to see the matrix strategy in action.

8. After defining the strategy with our matrix, you already have two jobs running side by side. But, it is still missing an important part of our matrix, we need to use the values defined in the matrix arrays. Let's start by adapting the "runs-on" to use the machines you specified in the matrix. You can see in the same URL as in Step 6 how to reference the value inside the matrix. (NOTE: You only need to replace the "runs-on" in the build job, for the push job there is no need to use the same architecture as our image).

9. We still want to keep our docker build custom action versatile so let's adapt it to our needs but still keep it backward compatible.
 - Add two new inputs (non-mandatory). One is the architecture for our docker image and, the other one should be the naming that we added to your matrix without any special characters to distinguish our images.
 - Now we want for the docker image tag to reflect the architecture chosen, let's add new code to add the architecture name to the end of the tag (do it after the if condition for the timestamp/tag_name) but only when the input of the architecture exists. (Same strategy you used on the tag for the tag name. Inside the if "-z" check if string is empty and "-n" checks if string has content).
 - For the docker build command, use an if-else condition and, in case the architecture input exists, add "--platform replace_with_input_variable" to the docker command.

10. As you can see, our image name and tag name are getting quite complex with a lot of logic hidden inside this custom action. To facilitate our workflow:
 - Let's export a new variable at the end of the docker build called "image_name_created" to the GITHUB_OUTPUT with the value for our image_name:tag_name. (Check the setup matrix step for an example)
 - And then add, below the "inputs" property, a "outputs" property with the following template:
    ```
    outputs:
        image_name_created:
            description: "description"
            value: ${{ steps.xxxx.outputs.image_name_created }}
    ```
 - The "xxxx" should be replaced with the id of the step where you exported the variable. If the step doesn't have an id yet just add the property "id:" and in front write an id for the step.

11. Back to our workflow, finish the new version of our custom docker build action by adding the two new inputs with the values coming from the matrix.

12. You can commit and push to test the docker build new custom action. Remember that the rest of the workflow will continue to fail. Let's fix that in the following steps.

13. Looking at the Save Docker Image step let's adapt it to save on tar file the new image that we built. Change the last part of the docker save command to use the same output we used on the 10. step. This way we will have this step also dynamic independently of the name we give to the image inside the build action. To use the output variable you can use the code below, once again you will need to add an "id" field to the step that is exporting the variable.

    ```
    ${{ steps.replace_with_step_id.outputs.replace_with_variable_name }}
    ```

14. To finish the build job, change the last step, where we upload the artifact image, to have a different name for the artifact depending on the architecture chosen. (NOTE: You can use the arch name without special characters for this). This is necessary to avoid having jobs in parallel uploading artifacts with the same name.

15. THE FINAL JOB. This job should have a yaml array on the needs property mentioning the first two jobs and should also have the strategy property with the matrix property using the output of the job_matrix job. Adapt the download artifact step to also use the same artifact name that you gave on the upload step in the previous job.

16. The Load Image step should need no changes since the tar file can still have the same name as it was used before. The same should also be true for the list docker images step and the docker login step.

17. Unfortunately, for the last step of our workflow, the Docker push step, we cannot have access to the Docker build output (in the previous job) as we did before on the exercise step 10 and step 13. This happens because even if the parallel jobs are running in the same matrix we cannot guarantee that our last job will use the same outputs of the respective previous job.

    In the normal scenario, we could define an output property in the previous job and just use it on this last job. But, what might happen is that the second job of the matrix running with a property of "arch: amd64" would run using the output of the first job of the matrix with the property "arch: arm64".

    To give a workaround to this problem, let's do a not-as-pretty solution. Change the list docker images step to store the image name for the image loaded from our artifact in an environment variable. You can use the code below to get the image name and then you can save it to an environment variable using the following example -> https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/workflow-commands-for-github-actions#environment-files

    ```
    IMAGE_NAME=$(docker images --format "{{.Repository}}:{{.Tag}}" | head -n 1)
    ```


18. To finish the exercise, just change our docker push step to use the image name we just saved to the environment variable (Note: Remember that the value inside the environment variable already includes the tag). Commit and Push, and everything should work. You should be able to see both images on DockerHub.