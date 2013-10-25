# BladeRunnerJS (BRJS) - Divide & conquer web apps

## Documentation

The documentation for BRJS can be found via http://bladerunnerjs.org/docs. The source code for the documentation can be found via https://github.com/BladeRunnerJS/brjs-site so if you find any error or have any suggestions please submit a pull request.

Find out more and register your interest at http://bladerunnerjs.org/

## BRJS Core Toolkit Development

If you are interested in contributing to the BRJS core toolkit then the following information is of use.

### How to build BRJS

#### Get the code

    git clone git@github.com:BladeRunnerJS/brjs.git
    git submodule init
    git submodule update

#### Build a Distributable Zip

From the root `brjs` source directory:

    $ ./gradlew cutlass-sdk:distZip

See the [BRJS Developer Setup Guide](https://github.com/BladeRunnerJS/brjs/wiki/BRJS-Developer-Setup) for more information.