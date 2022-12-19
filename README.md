# DATP7
Plugin for the model-checking tool UPPAAL

## Building From Source
1. Make sure UPPAAL is installed.
2. Install Gradle https://gradle.org/install/
3. Clone the repository
4. Set environment variable `UPPAAL_HOME` to the root folder of your UPPAAL installation, such that `%UPPAAL_HOME%/uppaal.jar` exists.

    **Note:** For auto-install to work, the directory must be writable.

5. Open repository root in commandline
6. Run `gradle installPlugin`

    **Note:** To manually install the plugin, see below
7. Run UPPAAL

### Setting Environment Variables

For the plugin to compile and install, the environment variable `UPPAAL_HOME` must be set to the root directory of Uppaal, such that `%UPPAAL_HOME%/uppaal.jar` exists.

The folder that `%UPPAAL_HOME%` should point to will likely be named something like `uppaal64-4.1.26-1`

Note that for auto-install to work, the directory may not be read-only.


### IntelliJ
To make tests work:
1. open settings (Ctrl-Alt-S)
2. Select `Build, Execution, Deployment`
3. Select `Build Tools`
4. Select `Gradle`
5. Set `Run tests using` to `IntelliJ IDEA`

### Manually installing the UCEL plugin
To build without installing,
1. Run `gradle buildPlugin`
    
    After build, a file named `UCEL.jar` is found in the root folder of the local repository.

2. In the folder of UPPAAL, add a folder named `plugins`
3. Copy `UCEL.jar` into `uppaal/plugins`

