novox.app.x2 = function() { }
caplin.extend("novox.app.x2","novox.app.x1");
novox.app.x2.prototype.start = function() {
	write("hello from novox.app.x2");
	new novox.fi.app.bladeset1();
	new novox.fx.app.bladeset1();
}