module.exports = function(grunt) {
    var browsers = [{
        browserName: "firefox",
        version: "19",
        platform: "XP"
    }, {
        browserName: "chrome",
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
        platform: "VISTA",
        version: "9"
    }, {
        browserName: "internet explorer",
        platform: "VISTA",
        version: "8"
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
