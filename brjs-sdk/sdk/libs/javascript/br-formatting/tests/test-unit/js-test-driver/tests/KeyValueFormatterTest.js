(function() {
	KeyValueFormatterTest = TestCase("KeyValueFormatterTest");

	var KeyValueFormatter = require('br/presenter/formatter/KeyValueFormatter');

	KeyValueFormatterTest.prototype.setUp = function()
	{
		this.oFormatter = new KeyValueFormatter();
	};

	KeyValueFormatterTest.prototype.test_TestKeyValueFormatterReturnsCorrectValueForExistingKey = function()
	{
		var mAttributes = { map : {"a": "a1", "b": "b2", "c" : "c3"}};
		assertEquals("a1", this.oFormatter.format("a", mAttributes));
	};

	KeyValueFormatterTest.prototype.test_TestKeyValueFormatterReturnsPassedValueIfNothingFoundInTheMap = function()
	{
		var mAttributes = { map : {"a": "a1", "b": "b2", "c" : "c3"}};
		assertEquals("d", this.oFormatter.format("d", mAttributes));
	};

	KeyValueFormatterTest.prototype.test_toString = function() {
		assertEquals("string", typeof this.oFormatter.toString());
	};
})();
