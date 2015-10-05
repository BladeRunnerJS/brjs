# BladeRunnerJS (BRJS)

**Divide & conquer complex web apps**

[![Build Status](https://travis-ci.org/BladeRunnerJS/brjs.svg)](https://travis-ci.org/BladeRunnerJS/brjs)

## Getting Started

The best way to get started depends on why you're here.

* [I want to build an application with BRJS](http://bladerunnerjs.org/docs/use/getting_started/).
* [I want to learn more about BRJS](http://bladerunnerjs.org/docs/).
* [I'm already familiar with BRJS and want to write a plugin](http://bladerunnerjs.org/docs/extend/command_plugin_tutorial/).
* [I'd like to get the core BRJS development environment set up](https://github.com/BladeRunnerJS/brjs#brjs-core-toolkit-development).

## Documentation

The main documentation for BRJS can be found via http://bladerunnerjs.org/docs. The source code for the documentation can be found via https://github.com/BladeRunnerJS/brjs-site so if you find any error or have any suggestions please submit a pull request.

API reference guides are coming soon.

## BRJS Core Toolkit Development

If you are interested in contributing to the BRJS core toolkit then the following information is of use.

### How to build BRJS

#### Get the code

    git clone git@github.com:BladeRunnerJS/brjs.git
    cd brjs

#### Install JDK

BRJS is written and compiled using Java 8.

  * Install Java 8 JDK.
  * Configure `JAVA_HOME` to point to the Java 8 JDK home directory.
  * Configure the `PATH` environment variable to include `$JAVA_HOME/bin`.

If you install Java using the Windows installer than it places a 'java' executable in 'C:\windows\system32' that proxies to the most recently installed version of Java. You will either need to install your preferred implementation of Java last, or ensure that the path to the Java 'bin' directory appears before 'C:\windows\system32'.



#### Build a Distributable Zip

From the root `brjs` source directory:

    $ ./gradlew brjs-sdk:distZip

The built zip file will be found in `BRJS_ROOT/brjs-sdk/build/distributions/` in the format `BladeRunner-VERSION.zip`.

### More information

See the [BRJS Developer Setup Guide](https://github.com/BladeRunnerJS/brjs/wiki/BRJS-Developer-Setup) for more information.
See how the [BRJS team use Github and git](https://github.com/BladeRunnerJS/brjs/wiki/How-do-we-use-GitHub-and-Git%3F).
