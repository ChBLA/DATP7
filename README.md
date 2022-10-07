# DATP7
Plugin for the model-checking tool UPPAAL


## Building From Source 
- Clone the repository
### Downloading libraries
Binaries needed for the compiler can be downloaded using the [InstallLibs.sh](./lib/InstallLibs.sh) script or done manually.

Script: From the project root do
```shell
cd ./lib
chmod +x ./InstallLibs.sh 
sh ./InstallLibs.sh
```
Manually: Download complete Antlr4 binary jar into `/lib/antlr-[version]-complete.jar`
   * Download site: https://www.antlr.org/download
   * Or direct link: https://www.antlr.org/download/antlr-4.11.1-complete.jar

### IntelliJ
If using IntelliJ, right-click on the Antlr .jar-file and click "Add as Library..."

