blade = {}
blade.app = {}
blade.app.blade1 = function() {
	write("hello from blade.app.blade1");
	new section.a.blade1.app.blade2();
}