# SIARD Suite - Programs for reading and writing files in the SIARD Format 2.1

This package contains interactive and command-line programs for reading 
and writing files in the SIARD Format 2.1

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
[./doc/manual/developer/index.html](./doc/manual/user/index.html) is the manual for developers wishing
build the binaries or work on the code.

More information about the build process can be found in
[./doc/manual/developer/build.html](./doc/manual/developer/build.html)