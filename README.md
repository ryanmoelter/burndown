# Burndown
A simple window into Fitbit's body fat data.

# Running
1. Create a fitbit application and put the client ID and secret in a `config.yaml` in the directory in which you plan to run the server.
    ```yaml
    fitbitClientId: "<clientId>"
    fitbitClientSecret: "<clientSecret>"
    ```
1. Create prod `application.conf` file
1. Run the server
    1. Option 1: run `./gradlew run`
    1. Option 2: Package and deploy (Coming soon)
1. Use either http or https (port 80 or 443)
