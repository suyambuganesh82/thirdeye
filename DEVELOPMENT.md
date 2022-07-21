## Developer Guide
Please use Intellij and import ThirdEye as a maven project. Please import the code style from the file `intellij-code-style.xml`.

License headers will be checked during build time to ensure license are up-to-date.  
To add/update license headers to the files run `./mvnw license:format`

### Running ThirdEye Coordinator in debug mode
After setting up IntelliJ, navigate to `ai.startree.thirdeye.ThirdEyeServer` class. Press the `play ▶️` icon
and choose debug. This should run the application. However, it would need the right args to start the server.

In the debug configuration settings, set the following values:
- Set program args to `server config/server.yaml`

You are all set. Run the debug now (you can just press the debug button) to run the server. By default,
the server should be accessible at http://localhost:8080

### Swagger

ThirdEye Coordinator exposes documentation for most of apis under `/swagger`. By default, the server
should be accessible at http://localhost:8080/swagger

### ThirdEye Release

ThirdEye uses `maven-release-plugin` to do it's releases.

#### Starting with a clean slate
You can start with a fresh slate using the command below. This cleans up all temporary files
generated by the release plugin.
```
./mvnw release:clean
```

#### Perform the Release

> **Note! Please Note that this will create 2 commits AND PUSH to origin triggering the pipelines!**

Switches:
- `initialize` goal sets up the next version of ThirdEye by incrementing the
  minor version (default). Major releases are done differently.
- `-B` switch turns on batch mode and skips all prompts. ThirdEye is already configured so you can
  simply use the batch mode.
- `-e` spits out more context if errors occur.
```
./mvnw -B -DskipTests -Darguments=-DskipTests release:clean initialize release:prepare release:perform -DdryRun=true
```
Skip the `-DdryRun=true` to cut a release.