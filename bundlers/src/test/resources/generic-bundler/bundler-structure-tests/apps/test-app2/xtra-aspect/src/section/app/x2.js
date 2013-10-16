section.app.x2 = function() { }
caplin.extend("section.app.x2","section.app.x1");
section.app.x2.prototype.start = function() {
	write("hello from section.app.x2");
	new section.fi.app.bladeset1();
	new section.fx.app.bladeset1();
}