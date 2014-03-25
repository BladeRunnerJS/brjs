ThousandsFormatterTest = TestCase("ThousandsFormatterTest");

ThousandsFormatterTest.prototype.setUp = function()
{
	this.oFormatter = new br.presenter.formatter.ThousandsFormatter();
	
	this.subrealm = realm.subrealm();
	this.subrealm.install();
};

ThousandsFormatterTest.prototype.tearDown = function()
{
	this.subrealm.uninstall();
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
	define('br/I18n', function(require, exports, module) {
		var Translator = require('br/i18n/Translator');
		var I18N = require('br/i18n/I18N');
		
		module.exports = I18N.create(new Translator({
			"br.i18n.number.grouping.separator":".",
			"br.i18n.decimal.radix.character":","
		}));
	});
	
	this.oFormatter = new br.presenter.formatter.ThousandsFormatter();

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
