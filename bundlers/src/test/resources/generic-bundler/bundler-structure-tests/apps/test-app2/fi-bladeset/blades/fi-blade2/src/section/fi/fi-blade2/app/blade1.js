blade2 = {}
blade2.app = {}
blade2.app.blade1 = function() {
	write("hello from blade.app.blade1");
	new blade2.app.blade2();
}