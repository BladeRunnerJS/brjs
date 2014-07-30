// this requires a namespacedJS class since it seems to be break KO binding tests in some browsers the methods are written as a commonJS module
// TODO: move the namespacedJS class into this module and work out why some KO binding tests fail
module.exports = require("./ns/Utils");
