novox.app.main2 = function() { }
caplin.extend("novox.app.main2","novox.app.main1");
novox.app.main2.prototype.start = function() {
	write("hello from novox.app.main2");
	new novox.fi.app.bladeset1();
	new novox.fx.app.bladeset1();
}