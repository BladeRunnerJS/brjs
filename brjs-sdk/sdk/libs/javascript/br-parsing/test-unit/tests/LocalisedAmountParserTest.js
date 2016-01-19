(function() {
	var LocalisedAmountParser = require('br/parsing/LocalisedAmountParser');
	
	LocalisedAmountParserTest = TestCase("LocalisedAmountParserTest");

	LocalisedAmountParserTest.prototype.setUp = function() {
		this.mAttributes = { "billions" : "B", "millions" : "M", "thousands" : "K"};
		this.oParser = new LocalisedAmountParser();
	};

	LocalisedAmountParserTest.prototype.test_testParseK = function() {
		var sValue = "5k";
		assertSame(5000, this.oParser.parse(sValue, this.mAttributes));
		sValue = "5K";
		assertSame(5000, this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testParseMillion = function() {
		var sValue = "5m";
		assertSame(5000000, this.oParser.parse(sValue, this.mAttributes));
		sValue = "5M";
		assertSame(5000000, this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testParseBillion = function() {
		var sValue = "5b";
		assertSame(5000000000, this.oParser.parse(sValue, this.mAttributes));
		sValue = "5B";
		assertSame(5000000000, this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testParseDecimal = function() {
		var sValue = "1.2345K";
		assertSame(1234.5, this.oParser.parse(sValue, this.mAttributes));
		sValue = "1.2345";
		assertSame("1.2345", this.oParser.parse(sValue, this.mAttributes));
		sValue = "0.2345K";
		assertSame(234.5, this.oParser.parse(sValue, this.mAttributes));
		sValue = "0.2345";
		assertSame("0.2345", this.oParser.parse(sValue, this.mAttributes));
		sValue = ".2345K";
		assertSame(234.5, this.oParser.parse(sValue, this.mAttributes));
		sValue = ".2345";
		assertSame(".2345", this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testParseTrailingZero = function() {
		var sValue = "123.0";
		assertSame("123.0", this.oParser.parse(sValue, this.mAttributes));
		var sValue = "123.0k";
		assertSame(123000, this.oParser.parse(sValue, this.mAttributes));
	};


	LocalisedAmountParserTest.prototype.test_testParseI18nDecimal = function() {
		this.oParser = new LocalisedAmountParser();

		var sValue = "1.25l";
		assertSame(62.5, this.oParser.parse(sValue, this.mAttributes));
		sValue = "1.25x";
		assertSame(12.5, this.oParser.parse(sValue, this.mAttributes));
		sValue = "0.5X";
		assertSame(5, this.oParser.parse(sValue, this.mAttributes));
		sValue = "0.23c";
		assertSame(23, this.oParser.parse(sValue, this.mAttributes));
		sValue = ".2345";
		assertSame(".2345", this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testParseDecimalPlusShortcut = function()
	{
		var sValue = "3.k";
		assertSame(3000, this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testInvalidNumberToNull = function()
	{
		var sValue = ".";
		assertSame(null, this.oParser.parse(sValue, this.mAttributes));
		sValue = "a";
		assertSame(null, this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_testIgnoreComma = function()
	{
		var sValue = "1,234";
		assertSame("1,234", this.oParser.parse(sValue, this.mAttributes));
	};

	LocalisedAmountParserTest.prototype.test_toString = function()
	{
		assertSame("string", typeof this.oParser.toString() );
	};
})();
