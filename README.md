# DATP7
Plugin for the model-checking tool UPPAAL

## Setup
1. Install dependencies
2. Clone the repository
3. Set environment variable `UPPAAL_HOME` to the root folder of your UPPAAL installation, such that `%UPPAAL_HOME%/uppaal.jar` exists e.g. `C:/Users/<USERNAME>/Desktop/uppaal64-4.1.26-1`.

    **Note:** For auto-install to work, the directory must be writable.

4. Open repository root in commandline
5. Run `gradle runUppaal` *OR* `gradle installPlugin` to install without running UPPAAL

    **Note:** To manually install the plugin, see below
6. Run UPPAAL

### Dependencies
- Java - Tested with JVM 17 https://www.oracle.com/java/technologies/downloads/#java17
- Gradle https://gradle.org/install/
- UPPAAL version 4.1.26-1 or higher. https://uppaal.org/downloads/

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

### Troubleshooting
- **Missing Task "buildPlugin", "installPlugin" or "runUppaal"**
   
   See "Setting Environment Variables" above

- **Missing dependencies from UPPAAL**
    
   Environment variable is probably pointing to a wrong path.

## Examples
Examples of UCEL programs can be found in `examples`. 
Load them into UPPAAL via the UPPAAL editor to use them.
