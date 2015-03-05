(function() {
	LowerCaseFormatterTest = TestCase("LowerCaseFormatterTest");

	var LowerCaseFormatter = require('br/presenter/formatter/LowerCaseFormatter');

	LowerCaseFormatterTest.prototype.setUp = function() {
		this.oFormatter = new LowerCaseFormatter();
	};

	LowerCaseFormatterTest.prototype.test_lower = function() {
		assertEquals(" this is a sentence ", this.oFormatter.format(" this is a sentence "));
	};

	LowerCaseFormatterTest.prototype.test_upper = function() {
		assertEquals(" this is a sentence ", this.oFormatter.format(" THIS IS A SENTENCE "));
	};

	LowerCaseFormatterTest.prototype.test_mixed = function() {
		assertEquals(" this is a sentence ", this.oFormatter.format(" This is a Sentence "));
	};

	LowerCaseFormatterTest.prototype.test_toString = function() {
		assertEquals("string", typeof this.oFormatter.toString());
	};

})();
