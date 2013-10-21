UpperCaseFormatterTest = TestCase("UpperCaseFormatterTest");
UpperCaseFormatterTest.prototype.setUp = function() {
	this.oFormatter = new br.presenter.formatter.UpperCaseFormatter();
};

UpperCaseFormatterTest.prototype.test_lower = function() {
	assertEquals(" THIS IS A SENTENCE ", this.oFormatter.format(" this is a sentence "));
};

UpperCaseFormatterTest.prototype.test_upper = function() {
	assertEquals(" THIS IS A SENTENCE ", this.oFormatter.format(" THIS IS A SENTENCE "));
};

UpperCaseFormatterTest.prototype.test_mixed = function() {
	assertEquals(" THIS IS A SENTENCE ", this.oFormatter.format(" This is a Sentence "));
};

UpperCaseFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
