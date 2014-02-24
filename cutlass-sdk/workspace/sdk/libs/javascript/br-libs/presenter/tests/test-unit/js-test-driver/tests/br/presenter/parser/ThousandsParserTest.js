ThousandsParserTest = TestCase("ThousandsParserTest");
ThousandsParserTest.prototype.setUp = function() {
	this.oParser = new br.presenter.parser.ThousandsParser();
};

ThousandsParserTest.prototype.test_Int = function() {
	assertEquals("12345", this.oParser.parse("12,345", {separator: ","}));
	
};

ThousandsParserTest.prototype.test_defaultSeparatorInt = function() {
	assertEquals("12345", this.oParser.parse("12,345", {}));
};

ThousandsParserTest.prototype.test_BigInt = function() {
	assertEquals("12345678901234567890", this.oParser.parse("12,345,678,901,234,567,890", {separator: ","}));
};

ThousandsParserTest.prototype.test_bigFloatAndDotSeparator = function() {
	var i18n = require("br/I18n")
	i18n.reset();
	i18n.initialise([{
		"br.i18n.number.grouping.separator":".",
		"br.i18n.decimal.radix.character":"!"
	}]);
	this.oParser = new br.presenter.parser.ThousandsParser();
	
	assertEquals("12345887.224", this.oParser.parse("12.345.887!224", {}));
};


ThousandsParserTest.prototype.test_Float = function() {
	assertEquals("12345.67", this.oParser.parse("12,345.67", {separator: ","}));
};

ThousandsParserTest.prototype.test_BigFloat = function() {
	assertEquals("1234567890.1234567890", this.oParser.parse("1,234,567,890.1234567890", {separator: ","}));
};

ThousandsParserTest.prototype.test_Negative = function() {
	assertEquals("-12345.67", this.oParser.parse("-12,345.67", {separator: ","}));
};

ThousandsParserTest.prototype.test_Brackets = function() {
	assertEquals("(12345.67)", this.oParser.parse("(12,345.67)", {separator: ","}));
};

ThousandsParserTest.prototype.test_NaN = function() {
	assertEquals("ABC", this.oParser.parse("ABC", {separator: ","}));
};

ThousandsParserTest.prototype.test_Null = function() {
	assertEquals(null, this.oParser.parse(null, {separator: ","}));
};

ThousandsParserTest.prototype.test_TestThousandsParserToString = function() {
	assertEquals("br.presenter.parser.ThousandsParser", this.oParser.toString());
};

ThousandsParserTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oParser.toString() );
};
