myApp = {};

myApp.Greeter = function() { };

myApp.Greeter.prototype.greet = function(name) {
  return "Hello " + name + "!";
};