(function() {
	NullValueFormatterTest = TestCase("NullValueFormatterTest");

	var NullValueFormatter = require('br/presenter/formatter/NullValueFormatter');

	NullValueFormatterTest.prototype.setUp = function()
	{
		this.oFormatter = new NullValueFormatter();
	};

	NullValueFormatterTest.prototype.test_TestNullValueFormatter_NoNullValue = function()
	{
		var mAttributes = {};
		assertEquals("\u00a0", this.oFormatter.format(null, mAttributes));
		assertEquals(123.45, this.oFormatter.format(123.45, mAttributes));
		assertEquals("ABC", this.oFormatter.format("ABC", mAttributes));
		assertEquals(this.oFormatter, this.oFormatter.format(this.oFormatter, mAttributes));
	};

	NullValueFormatterTest.prototype.test_TestNullValueFormatter_NoEmptyStringValue = function()
	{
		var mAttributes = { nullValue:"-" };
		assertEquals("-", this.oFormatter.format("", mAttributes));
	};

	NullValueFormatterTest.prototype.test_TestNullValueFormatter_NullValueXYZ = function()
	{
		var mAttributes = {nullValue: "XYZ"};
		assertEquals("XYZ", this.oFormatter.format(null, mAttributes));
		assertEquals(123.45, this.oFormatter.format(123.45, mAttributes));
		assertEquals("ABC", this.oFormatter.format("ABC", mAttributes));
		assertEquals(this.oFormatter, this.oFormatter.format(this.oFormatter, mAttributes));
	};

	NullValueFormatterTest.prototype.test_toString = function() {
		assertEquals("string", typeof this.oFormatter.toString());
	};
})();
