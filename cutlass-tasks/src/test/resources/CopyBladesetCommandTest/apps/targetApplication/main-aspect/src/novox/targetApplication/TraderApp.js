
caplin.namespace("novox.targetApplication");
// #include caplin.grid.GridGenerator
// #include caplinx.example.helloworld.HelloWorldPresentationModel

novox.targetApplication.TraderApp = function()
{
};

novox.targetApplication.TraderApp.prototype.initWebcentric = function(event)
{
	// needed by webcentric. 
	this.initialiseFactories();
	
	caplin.widget = {};
	caplin.widget.events = {};
	caplin.widget.events.GlobalEventManager = {};
	caplin.widget.events.GlobalEventManager.raiseEvent = function()
	{
		
	};
	
	caplin.core.ApplicationProperties.setProperty("CAPLIN.WEBCENTRIC.START.PAGE", "application_layout");
	window.webcentricParams = {theme_path:"unbundled-resources/webcentric/", theme:"blue", layout_theme:"blue"};
	
	caplin.framework.WebcentricAdapter.init(event);
}

novox.targetApplication.TraderApp.prototype.initialiseFactories = function()
{
	var oTranslator = caplin.i18n.getTranslator()
	oTranslator.getLocale = function() 
	{
		return "en";
	};
	
	var fFactory = function() 
	{
		
	};
	
	fFactory.prototype =
	{
		getInstance: function() {},
		setInstance: function() {},
		getAlertDispatcher: function() {}
	};
	
	caplin.extend(fFactory, caplin.framework.ApplicationFactory);
	caplin.extend(fFactory, caplin.component.AbstractFactory);
	caplin.extend(fFactory, caplin.dom.AbstractFactory);
	caplin.extend(fFactory, caplin.framework.AbstractFactory);
	caplin.extend(fFactory, caplin.grid.AbstractFactory);
	caplin.extend(fFactory, caplin.renderer.AbstractFactory);
	caplin.extend(fFactory, caplin.security.AbstractFactory);
	caplin.extend(fFactory, caplin.services.AbstractFactory);
	caplin.extend(fFactory, caplin.trading.AbstractFactory);
	caplin.extend(fFactory, caplin.core.AbstractFactory);
	
	var oFactory = new fFactory();
	caplin.dom.AbstractFactory.setInstance(oFactory);
};

