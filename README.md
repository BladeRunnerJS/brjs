# BladeRunnerJS (BRJS)

**Divide & conquer web apps**

## Getting Started

The best way to get started depends on why you're here.

* **I want to build an application with BRJS:** Try the [getting started guide](http://bladerunnerjs.org/docs/use/getting_started/).
* **I want to learn more about BRJS:** Head to the [BRJS documentation](http://bladerunnerjs.org/docs/)
* **I'm already familiar with BRJS and want to write a plugin:** Check out the [CommandPlugin tutorial](http://bladerunnerjs.org/docs/extend/command_plugin_tutorial/)
* **I'd like to get the core BRJS development environment set up:** See the [BRJS Core Toolkit Development section](https://github.com/BladeRunnerJS/brjs#brjs-core-toolkit-development) below.

## Documentation

The main documentation for BRJS can be found via http://bladerunnerjs.org/docs. The source code for the documentation can be found via https://github.com/BladeRunnerJS/brjs-site so if you find any error or have any suggestions please submit a pull request.

API reference guides are coming soon.

## BRJS Core Toolkit Development

If you are interested in contributing to the BRJS core toolkit then the following information is of use.

### How to build BRJS

#### Get the code

    git clone git@github.com:BladeRunnerJS/brjs.git
    cd brjs
    git submodule init
    git submodule update

You may need to run `git submodule sync` and `git submodule update` when changing branches if submodules have changed.

#### Build a Distributable Zip

From the root `brjs` source directory:

    $ ./gradlew cutlass-sdk:distZip
    
The built zip file will be found in `BRJS_ROOT/cutlass-sdk/build/distributions/` in the format `BladeRunner-VERSION.zip`.

### More information    

See the [BRJS Developer Setup Guide](https://github.com/BladeRunnerJS/brjs/wiki/BRJS-Developer-Setup) for more information.

