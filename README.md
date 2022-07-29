# SIARD Suite - Programs for reading and writing files in the SIARD Format 2.2

This package contains interactive and command-line programs for reading 
and writing files in the SIARD Format 2.2

## Getting started (for devs)

For building the binaries, Java JDK (1.8 or higher) and  Ant must 
have been installed - use one with the JavaFX extension! Adjust build.properties according to your system (if needed)

Run tests with 

```shell
ant test
```

Create a release

```shell
ant release
```

Start the GUI

* Intellij IDEA:
  * make sure that you have configured a JDK with JavaFX included!
    * File -> Project Structure ->  Project -> Project SDK
  * Open `ch.admin.bar.siard2.gui.SiardGui` and click the play button in the left gutter.
  * There will be some errors in the console! 
  * If you don't see a splash screen and/ or the application - it may have opened BEHIND your IDE. Move the IDE away to a different screen

## Documentation
[./doc/manual/user/index.html](./doc/manual/user/index.html) contains the manual for using the binaries.
[./doc/manual/developer/index.html](./doc/manual/user/index.html) is the manual for developers wishing to build the binaries or work on the code.

More information about the build process can be found in
[./doc/manual/developer/build.html](./doc/manual/developer/build.html)


## Download JREs for packaging

Azul Zulu 1.8 Fx for Windows (64-bit)

```bash
wget https://cdn.azul.com/zulu/bin/zulu8.64.0.15-ca-fx-jre8.0.342-win_x64.zip -O ./jre/jre-win-64-bit.zip
```

Azul Zulu 1.8 FX for Windows (32-bit)
```bash
wget https://cdn.azul.com/zulu/bin/zulu8.64.0.15-ca-fx-jre8.0.342-win_i686.zip -O ./jre/jre-win-32-bit.zip
```

Linux Azul Zulu 1.8 (64-bit)
```bash
wget https://cdn.azul.com/zulu/bin/zulu8.64.0.15-ca-fx-jre8.0.342-linux_x64.tar.gz -O ./jre/jre-linux-64-bit.tar.gz
```

Linux Azul Zulu 1.8 (32-bit)
```bash
wget https://cdn.azul.com/zulu/bin/zulu8.64.0.15-ca-fx-jre8.0.342-linux_i686.tar.gz -O ./jre/jre-linux-32-bit.tar.gz
```