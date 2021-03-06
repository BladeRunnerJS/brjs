(function() {
	var I18nStore = require('br/i18n/I18nStore');
	var ThousandsFormatter = require('br/presenter/formatter/ThousandsFormatter');

	var originalNumSep;
	var originalRadixChar;
	var messageDefinitions;
	var ThousandsFormatterTest = TestCase("ThousandsFormatterTest");

	ThousandsFormatterTest.prototype.setUp = function()
	{
		messageDefinitions = I18nStore.messageDefinitions['en'] || I18nStore.messageDefinitions['en_GB'];
		originalNumSep = messageDefinitions["br.i18n.number.grouping.separator"];
		originalRadixChar = messageDefinitions["br.i18n.decimal.radix.character"];
		this.oFormatter = new ThousandsFormatter();
	};

	ThousandsFormatterTest.prototype.tearDown = function()
	{
		messageDefinitions["br.i18n.number.grouping.separator"] = originalNumSep;
		messageDefinitions["br.i18n.decimal.radix.character"] = originalRadixChar;
	};

	ThousandsFormatterTest.prototype.test_nonDecimals = function()
	{
		var mAttributes = {};
		assertEquals("0", this.oFormatter.format("0", mAttributes));
		assertEquals("0", this.oFormatter.format(0, mAttributes));
		assertEquals("1", this.oFormatter.format("1", mAttributes));
		assertEquals("1", this.oFormatter.format(1, mAttributes));
		assertEquals("100", this.oFormatter.format("100", mAttributes));
		assertEquals("100", this.oFormatter.format(100, mAttributes));
		assertEquals("1,000", this.oFormatter.format("1000", mAttributes));
		assertEquals("1,000", this.oFormatter.format(1000, mAttributes));
		assertEquals("1,000,000", this.oFormatter.format("1000000", mAttributes));
		assertEquals("1,000,000", this.oFormatter.format(1000000, mAttributes));
		assertEquals("-100", this.oFormatter.format("-100", mAttributes));
		assertEquals("-1,000", this.oFormatter.format("-1000", mAttributes));
		assertEquals("-1,000,000", this.oFormatter.format("-1000000", mAttributes));
		assertEquals("-1,000,000", this.oFormatter.format(-1000000, mAttributes));
		assertEquals("(100)", this.oFormatter.format("(100)", mAttributes));
		assertEquals("(1,000)", this.oFormatter.format("(1000)", mAttributes));
		assertEquals("(1,000,000)", this.oFormatter.format("(1000000)", mAttributes));
		assertEquals("1MM", this.oFormatter.format("1MM", mAttributes));
		assertEquals("100MM", this.oFormatter.format("100MM", mAttributes));
		assertEquals("1,000MM", this.oFormatter.format("1000MM", mAttributes));
		assertEquals("1,000,000MM", this.oFormatter.format("1000000MM", mAttributes));
		assertEquals("-1MM", this.oFormatter.format("-1MM", mAttributes));
		assertEquals("-100MM", this.oFormatter.format("-100MM", mAttributes));
		assertEquals("-1,000MM", this.oFormatter.format("-1000MM", mAttributes));
		assertEquals("-1,000,000MM", this.oFormatter.format("-1000000MM", mAttributes));
		assertEquals("(1MM)", this.oFormatter.format("(1MM)", mAttributes));
		assertEquals("(100MM)", this.oFormatter.format("(100MM)", mAttributes));
		assertEquals("(1,000MM)", this.oFormatter.format("(1000MM)", mAttributes));
		assertEquals("(1,000,000MM)", this.oFormatter.format("(1000000MM)", mAttributes));
	};

	ThousandsFormatterTest.prototype.test_decimals = function()
	{
		var mAttributes = {};
		assertEquals("0.0234", this.oFormatter.format("0.0234", mAttributes));
		assertEquals("0.0234", this.oFormatter.format("0.0234", mAttributes));
		assertEquals("1.234", this.oFormatter.format("1.234", mAttributes));
		assertEquals("1.234", this.oFormatter.format("1.234", mAttributes));
		assertEquals("100.234", this.oFormatter.format("100.234", mAttributes));
		assertEquals("100.234", this.oFormatter.format("100.234", mAttributes));
		assertEquals("1,000.234", this.oFormatter.format("1000.234", mAttributes));
		assertEquals("1,000.234", this.oFormatter.format(1000.234, mAttributes));
		assertEquals("-1,000.234", this.oFormatter.format("-1000.234", mAttributes));
		assertEquals("-1,000.234", this.oFormatter.format(-1000.234, mAttributes));
		assertEquals("(1,000.234)", this.oFormatter.format("(1000.234)", mAttributes));
		assertEquals("1,000.234MM", this.oFormatter.format("1000.234MM", mAttributes));
		assertEquals("-1,000.234MM", this.oFormatter.format("-1000.234MM", mAttributes));
		assertEquals("(1,000.234MM)", this.oFormatter.format("(1000.234MM)", mAttributes));
	};

	ThousandsFormatterTest.prototype.test_notNumbers = function()
	{
		var mAttributes = {};
		assertEquals("ABC", this.oFormatter.format("ABC", mAttributes));
		assertEquals("", this.oFormatter.format("", mAttributes));
		assertEquals("", this.oFormatter.format(null, mAttributes));
	};

	ThousandsFormatterTest.prototype.test_spaceSeparator = function()
	{
		assertEquals("1 000.234", this.oFormatter.format("1000.234", {separator:" "}));
		assertEquals("1 234 567.890", this.oFormatter.format("1234567.890", {separator:" "}));
	};

	ThousandsFormatterTest.prototype.test_toString = function() {
		assertEquals("string", typeof this.oFormatter.toString());
	};

	ThousandsFormatterTest.prototype.test_preFormatedNumbers = function()
	{
		messageDefinitions["br.i18n.number.grouping.separator"] = ".";
		messageDefinitions["br.i18n.decimal.radix.character"] = ",";

		this.oFormatter = new ThousandsFormatter();

		assertEquals("1,000", this.oFormatter.format("1.000", {}));
		assertEquals("10,00", this.oFormatter.format("10.00", {}));
		assertEquals("100.000,", this.oFormatter.format("100,000,", {}));
		assertEquals("500.000,00", this.oFormatter.format("500,000.00", {}));
		assertEquals("1.500.000,00", this.oFormatter.format("1,500,000.00", {}));
		assertEquals("100.500.000,00", this.oFormatter.format("100,500,000.00", {}));
		assertEquals("100.500.000,00", this.oFormatter.format("100,500,000.00", {}));
		assertEquals("-100.500.000,00", this.oFormatter.format("-100,500,000.00", {}));
		assertEquals("(100.500.000,00)", this.oFormatter.format("(100,500,000.00)", {}));
	};
})();
