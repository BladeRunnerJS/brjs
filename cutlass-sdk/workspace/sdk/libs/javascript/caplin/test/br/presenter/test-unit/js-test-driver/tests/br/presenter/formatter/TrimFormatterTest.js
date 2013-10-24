TrimFormatterTest = TestCase("TrimFormatterTest");
TrimFormatterTest.prototype.setUp = function() {
	this.oFormatter = new br.presenter.formatter.TrimFormatter();
};

TrimFormatterTest.prototype.test_spaces = function() {
	assertEquals("word", this.oFormatter.format(" word"));
	assertEquals("word", this.oFormatter.format("word "));
	assertEquals("word", this.oFormatter.format(" word "));
	assertEquals("sentence  with   spaces", this.oFormatter.format(" sentence  with   spaces "));
};

TrimFormatterTest.prototype.test_tabs = function() {
	assertEquals("word", this.oFormatter.format("\tword"));
	assertEquals("word", this.oFormatter.format("word\t"));
	assertEquals("word", this.oFormatter.format("\tword\t"));
};

TrimFormatterTest.prototype.test_newlines = function() {
	assertEquals("word", this.oFormatter.format("\nword"));
	assertEquals("word", this.oFormatter.format("word\n"));
	assertEquals("word", this.oFormatter.format("\nword\n"));
};

TrimFormatterTest.prototype.test_mixture = function() {
	assertEquals("word", this.oFormatter.format("\n  \tword"));
	assertEquals("word", this.oFormatter.format("word\n\t  "));
	assertEquals("word", this.oFormatter.format("\n\tword\n \t"));
};

TrimFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
