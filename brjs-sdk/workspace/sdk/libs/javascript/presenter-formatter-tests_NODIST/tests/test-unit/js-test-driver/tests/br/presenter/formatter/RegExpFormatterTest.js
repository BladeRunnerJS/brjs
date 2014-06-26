RegExpFormatterTest = TestCase("RegExpFormatterTest");

require('br/presenter/formatter/RegExpFormatter');

RegExpFormatterTest.prototype.setUp = function() {
	this.oFormatter = new br.presenter.formatter.RegExpFormatter();
};

RegExpFormatterTest.prototype.test_match = function() {
	assertEquals("hello, everybody", this.oFormatter.format("hello, world", { match:"world", replace:"everybody" }));
	assertEquals("hello, world", this.oFormatter.format("hello, world", { match:"WORLD", replace:"everybody" }));
};

RegExpFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
