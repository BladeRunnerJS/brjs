var TestInterface = require('./TestInterface');

var ImplementingClass = function() {
};
topiarist.implement(ImplementingClass, TestInterface);

ImplementingClass.prototype.thingOne = function() {

};

ImplementingClass.prototype.thingTwo = function() {

};

module.exports = ImplementingClass;