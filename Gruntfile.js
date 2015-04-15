module.exports = function(grunt) {
    var browsers = [{
        browserName: "firefox",
        platform: "WIN7"
    }, {
        browserName: "firefox",
        version: "19",
        platform: "XP"
    }, {
        browserName: "chrome",
        platform: "WIN7"
    }, {
        browserName: "chrome",
        version: "26",
        platform: "XP"
    }, {
        browserName: "chrome",
        platform: "linux"
    }, {
        browserName: "internet explorer",
        platform: "WIN8.1",
        version: "11"
    }, {
        browserName: "internet explorer",
        platform: "WIN8",
        version: "10"
    }, {
        browserName: "internet explorer",
        platform: "WIN7",
        version: "9"
    }, {
        browserName: "internet explorer",
        platform: "WIN7",
        version: "8"
    }, {
        browserName: "internet explorer",
        platform: "XP",
        version: "8"
    }, {
        browserName: "safari",
        platform: "OS X 10.9",
        version: "7"
    }, {
        browserName: "safari",
        platform: "OS X 10.8",
        version: "6"
    }];

    grunt.initConfig({
        connect: {
            server: {
                options: {
                    base: "",
                    hostname: "",
                    port: 9999
                }
            }
        },
        'saucelabs-jasmine': {
            all: {
                options: {
                    urls: ["http://127.0.0.1:9999/spec/index.html"],
                    tunnelTimeout: 5,
                    build: process.env.TRAVIS_JOB_ID,
                    concurrency: 3,
                    maxRetries: 3,
                    browsers: browsers,
                    testname: "topiarist tests"
                }
            }
        },
        watch: {}
    });

    // Loading dependencies
    for (var key in grunt.file.readJSON("package.json").devDependencies) {
        if (key !== "grunt" && key.indexOf("grunt") === 0) grunt.loadNpmTasks(key);
    }

    grunt.registerTask("serve", ["connect", "watch"]);
    grunt.registerTask("saucelabs-test", ["connect", "saucelabs-jasmine"]);
};
