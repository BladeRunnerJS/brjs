@appns.@bladeset.@blade.ExampleClass = function()
{
	this.message = new caplin.presenter.property.Property("Hello World!" );	
};

caplin.extend(@appns.@bladeset.@blade.ExampleClass, caplin.presenter.PresentationModel);

@appns.@bladeset.@blade.ExampleClass.prototype.buttonClicked = function()
{
 	var proxy = caplin.core.ServiceRegistry.getService("br.event-service").getProxy("caplin.workbench.model.WorkbenchEventListener", "caplin");
	proxy.logEvent("button clicked", "", {"greeting" : "Hello!"});
}
