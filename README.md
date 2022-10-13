# DATP7
Plugin for the model-checking tool UPPAAL

## Set Environment Variables
For the plugin to compile and install, the environment variable `UPPAAL_HOME` must be set to the root directory of Uppaal, such that `%UPPAAL_HOME/uppaal.jar` exists.

Note that for auto-install to work, the directory may not be read-only.

## Building From Source 
- Clone the repository
- Open repository root in commandline
- Run command `gradle build`

### IntelliJ
To make tests work:
1. open settings (Ctrl-Alt-S)
2. Select `Build, Execution, Deployment`
3. Select `Build Tools`
4. Select `Gradle`
5. Set `Run tests using` to `IntelliJ IDEA`


