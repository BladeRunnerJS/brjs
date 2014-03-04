LocalisedAmountParserTest = TestCase("LocalisedAmountParserTest");
LocalisedAmountParserTest.prototype.setUp = function() {	
	this.mAttributes = { "billions" : "B", "millions" : "M", "thousands" : "K"};
	this.oParser = new br.presenter.parser.LocalisedAmountParser();
};

LocalisedAmountParserTest.prototype.test_testParseK = function() {
	var sValue = "5k";
	assertEquals(5000, this.oParser.parse(sValue, this.mAttributes));
	var sStreamValue = "5K";
	assertEquals(5000, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_testParseMillion = function() {
	var sValue = "5m";
	assertEquals(5000000, this.oParser.parse(sValue, this.mAttributes));
	sValue = "5M";
	assertEquals(5000000, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_testParseBillion = function() {
	var sValue = "5b";
	assertEquals(5000000000, this.oParser.parse(sValue, this.mAttributes));
	sValue = "5B";
	assertEquals(5000000000, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_testParseDecimal = function() {
	var sValue = "1.2345K";
	assertEquals(1234.5, this.oParser.parse(sValue, this.mAttributes));
	sValue = "1.2345";
	assertEquals(1.2345, this.oParser.parse(sValue, this.mAttributes));
	sValue = "0.2345K";
	assertEquals(234.5, this.oParser.parse(sValue, this.mAttributes));
	sValue = "0.2345";
	assertEquals(0.2345, this.oParser.parse(sValue, this.mAttributes));
	sValue = ".2345K";
	assertEquals(234.5, this.oParser.parse(sValue, this.mAttributes));
	sValue = ".2345";
	assertEquals(0.2345, this.oParser.parse(sValue, this.mAttributes));
};


LocalisedAmountParserTest.prototype.test_testParseI18nDecimal = function() {
	this.oParser = new br.presenter.parser.LocalisedAmountParser();
	
	var sValue = "1,25l";
	assertEquals(62.5, this.oParser.parse(sValue, this.mAttributes));
	sValue = "1,25x";
	assertEquals(12.5, this.oParser.parse(sValue, this.mAttributes));
	sValue = "0,5X";
	assertEquals(5, this.oParser.parse(sValue, this.mAttributes));
	sValue = "0,23c";
	assertEquals(23, this.oParser.parse(sValue, this.mAttributes));
	sValue = ",2345";
	assertEquals(0.2345, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_testParseDecimalPlusShortcut = function()
{
	var sValue = "3.k";
	assertEquals(3000, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_testInvalidNumberToNull = function() 
{
	var sValue = ".";
	assertEquals(null, this.oParser.parse(sValue, this.mAttributes));
	sValue = "a";
	assertEquals(null, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_testIgnoreComma = function() 
{
	var sValue = "1,234";
	assertEquals(1234, this.oParser.parse(sValue, this.mAttributes));
};

LocalisedAmountParserTest.prototype.test_toString = function() 
{
	assertEquals("string", typeof this.oParser.toString() );
};
