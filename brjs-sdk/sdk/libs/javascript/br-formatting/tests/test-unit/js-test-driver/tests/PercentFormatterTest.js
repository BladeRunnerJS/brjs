(function() {
	PercentFormatterTest = TestCase("PercentFormatterTest");

	var PercentFormatter = require('br/presenter/formatter/PercentFormatter');

	PercentFormatterTest.prototype.setUp = function() {
		this.oFormatter = new PercentFormatter();
	};

	PercentFormatterTest.prototype.test_TestPercentFormatter_invalidValues = function(){
		assertEquals(null, this.oFormatter.format(null, {}));
		assertEquals(undefined, this.oFormatter.format(undefined, {}));
		assertEquals("", this.oFormatter.format("", {}));
		assertEquals("15A", this.oFormatter.format("15A", {}));
	};

	PercentFormatterTest.prototype.test_TestPercentFormatter_validValuesUnformatted = function() {
		assertEquals("5.75%", this.oFormatter.format(0.0575, {}));
		assertEquals("5.75%", this.oFormatter.format(".0575", {}));
	};

	PercentFormatterTest.prototype.test_TestPercentFormatter_validValuesFormatted = function() {
		assertEquals("5.750%", this.oFormatter.format(0.0575, {dp:3}));
		assertEquals("6%", this.oFormatter.format(0.0575, {sf:1}));
		assertEquals("6.00%", this.oFormatter.format(0.0575, {sf:1, dp:2}));
	};

	PercentFormatterTest.prototype.test_toString = function() {
		assertEquals("string", typeof this.oFormatter.toString());
	};
})();
