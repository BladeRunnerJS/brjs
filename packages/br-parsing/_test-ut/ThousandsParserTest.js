require('br-i18n/_resources/en.properties');
(function() {
	var I18nStore = require('br-i18n/I18nStore');
	var ThousandsParser = require('br-parsing/ThousandsParser');

	var decimalRadix;
	var numberGroupSep;
	var messageDefinitions;
	var ThousandsParserTest = TestCase("ThousandsParserTest");

	ThousandsParserTest.prototype.setUp = function() {
		messageDefinitions = I18nStore.messageDefinitions[I18nStore.locale];
		decimalRadix = messageDefinitions["br.i18n.decimal.radix.character"];
		numberGroupSep = messageDefinitions["br.i18n.number.grouping.separator"];

		this.oParser = new ThousandsParser();
	};

	ThousandsParserTest.prototype.tearDown = function()
	{
		messageDefinitions["br.i18n.decimal.radix.character"] = decimalRadix;
		messageDefinitions["br.i18n.number.grouping.separator"] = numberGroupSep;
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
		messageDefinitions["br.i18n.number.grouping.separator"] = ".";
		messageDefinitions["br.i18n.decimal.radix.character"] = "!";

		this.oParser = new ThousandsParser();

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
		assertEquals('br-parsing/ThousandsParser', this.oParser.toString());
	};

	ThousandsParserTest.prototype.test_toString = function() {
		assertEquals("string", typeof this.oParser.toString() );
	};
})();
