//file to capture the original class for singletons and store them so they can be replaced during testing
caplin.singletons = {};
caplin.realSingleton = caplin.singleton; 
caplin.singleton = function(className){
		caplin.singletons[className] = eval(className);
		caplin.realSingleton(className);
};
