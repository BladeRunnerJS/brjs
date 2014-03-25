TranslatorTest = TestCase("TranslatorTest");
TranslatorTest.prototype.setUp = function() {
	var Mock4JS = require('mock4js');

	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
	
	this.subrealm = realm.subrealm();
	this.subrealm.install();
	
	var oThis = this;
	define('br/I18n/LocalisedTime', function(require, exports, module) {
		var MockLocalisedTime = function() {};
		MockLocalisedTime.prototype.format = function() { return "LocalisedTime.format"; };
		
		module.exports = MockLocalisedTime;
	});
	
	this.messages = {
		"caplin.test.key": "key 1 value.",
		"other.key": "value 2",
		"template.key": "this key has a [template.key.first] value and a [template.key.second] value"
	};

	this.moreMessages = {
		"23412.test.key": "numeric key value",
		"caplin.test.key": "key 1 value.",
		"other.key": "value 2",
		"utf8.key": "ΕΥΣΕΒΙΟΥ ΚΑΙΣΑΡΕΙΑΣ"
	};

	this.i18nTimeDateNumberMessages = {
		"br.i18n.date.format": "d-m-Y",
		"br.i18n.date.format.long": "D, d M, Y, h:i:s A",
		"br.i18n.date.month.january": "Leden",
		"br.i18n.date.month.short.april": "Dub",
		"br.i18n.date.month.short.december": "Pros",
		"br.i18n.time.format.separator": ":",
		"br.i18n.decimal.radix.character": ".",
		"br.i18n.number.grouping.separator": ","
	};

	this.invalidXmlCharMessages = {
		"entity.string": "the & quick < brown > fox ' jumps \" over & the << && lazy >&\"\"\"&& dog. \' "
	};

	this.miscForeignMessages = {
		"english.hello": "hello, world",
		"chinese.hello": "你好，世界",
		"japanese.hello": "こんにちは、世界",
		"korean.hello": "안녕하세요, 세상",
		"arabic.hello": "مرحبا، العالم",
		"hebrew.hello": "שלום, בעולם"
	};

	this.longXmlMessages = {
		"currency.aed.issuer": "United Arab Emirates",
		"currency.aed.name": "Dirhams",
		"currency.afn.issuer": "Afghanistan",
		"currency.afn.name": "Afghanis",
		"currency.all.issuer": "Albania",
		"currency.all.name": "Leke",
		"currency.amd.issuer": "Armenia",
		"currency.amd.name": "Drams"
	};
	
	this.camelCaseTokens = {
		"token.CamelcasetokeN": "a Translation"
	};
};

TranslatorTest.prototype.tearDown = function()
{
	this.subrealm.uninstall();
	
	Mock4JS.verifyAllMocks();
};

TranslatorTest.prototype.test_translateXMLString = function()
{
	var text = "<blah att=\"@{entity.string}\">somethingelse</blah>";
	var expected = "<blah att=\"the &amp; quick &lt; brown &gt; fox &apos; jumps &quot; over &amp; the &lt;&lt; &amp;&amp; lazy &gt;&amp;&quot;&quot;&quot;&amp;&amp; dog. &apos; \">somethingelse</blah>";

	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.invalidXmlCharMessages);

	var result = translator.translate(text);

	assertEquals(expected, result);
};

TranslatorTest.prototype.test_translateLongXmlString = function() {

	var text = "@{currency.aed.issuer} @{currency.aed.name} @{currency.afn.issuer} @{currency.afn.name}";
	var expected = "United Arab Emirates Dirhams Afghanistan Afghanis";

	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.longXmlMessages);

	var result = translator.translate(text);
	assertEquals(expected, result);
};

TranslatorTest.prototype.test_translateString = function()
{
	var text = "<test name='@{caplin.test.key}'>values</test><blah>@{other.key}</blah>";
	var expected = "<test name='key 1 value.'>values</test><blah>value 2</blah>";

	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.messages);

	var result = translator.translate(text, "text");

	assertEquals(expected, result);
};

// TODO: If formatTime is changed to not delegate to LocalisedTime - new tests must be added.
TranslatorTest.prototype.test_formatTimeDelegatesToLocalisedTime = function()
{
	var Translator = require('br/i18n/Translator');
	var oTranslator = new Translator(this.i18nTimeDateNumberMessages);
	assertEquals("20:00:00", oTranslator.formatTime("200000"));
};

// TODO: Add Test for Translator.setLocalizationPreferences()

TranslatorTest.prototype.test_missingMessage = function()
{
	var text = "<test name='@{caplin.missing.key}'>values</test><blah>@{other.key}</blah>";
	var expected = "<test name='??? caplin.missing.key ???'>values</test><blah>value 2</blah>";

	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.messages);

	var result = translator.translate(text, "text");

	assertEquals(expected, result);
};

TranslatorTest.prototype.test_multiLineReplace = function()
{
	var text =
		"<test name='@{caplin.missing.key}'>values</test><blah>@{other.key}</blah>\n" +
		"caplin.missing.key <@{23412.test.key}\n" +
		"something else !!\"£@{utf8.key}^^\n";

	var expected =
		"<test name='??? caplin.missing.key ???'>values</test><blah>value 2</blah>\n" +
		"caplin.missing.key <numeric key value\n" +
		"something else !!\"£ΕΥΣΕΒΙΟΥ ΚΑΙΣΑΡΕΙΑΣ^^\n";

	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.moreMessages);

	var result = translator.translate(text, "text");

	assertEquals(expected, result);
};

TranslatorTest.prototype.test_simpleTokenTest = function() {
	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.messages);

	var result = translator.getMessage("template.key", {"template.key.first":"foo", "template.key.second":"bar"});
	var expected = "this key has a foo value and a bar value";

	assertEquals(expected, result);
};

{
	var sTest = "This [token] should be replaced as should this [token]";
	var mTokens = { token: "replaced-token" };
	var sExpected = "This replaced-token should be replaced as should this replaced-token";

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

function _assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected)
{
	var mMessages = { "test.key": sTest };
	var Translator = require('br/i18n/Translator');
	var oTranslator = new Translator(mMessages);
	var sResult = oTranslator.getMessage("test.key", mTokens);
	assertEquals(sExpected, sResult);
}

TranslatorTest.prototype.test_getMessageWhenTheMessageIsAnEmptyString = function()
{
	var sTest = "";
	var mTokens = { };
	var sExpected = "??? test.key ???";

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};


TranslatorTest.prototype.test_getMessageDoesNothingWhenATokenIsNotDefinedInTheText = function()
{
	var sTest = "This text doesn't contain a token";
	var mTokens = { token: "replaced-token" };
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenATokenIsNotSupplied = function()
{
	var sTest = "This [token] was not supplied";
	var mTokens = { };
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenATokenNameIsSuppliedWithALeadingSpace = function()
{
	var sTest = "This [ token] has a leading space";
	var mTokens = { token: "replaced-token" };
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenATokenNameIsSuppliedWithATrailingSpace = function()
{
	var sTest = "This [token ] has a leading space";
	var mTokens = { token: "replaced-token" };
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenATokenNameIsSurroundedByWhitespace = function()
{
	var sTest = "This [ token ] has a leading space";
	var mTokens = { token: "replaced-token" };
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenATokenNameContainingASpace = function()
{
	var sTest = "This [token name] contains a space";
	var mTokens = { "token name": "replaced token" };
	var sExpected = "This replaced token contains a space";

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenANullTokenMapIsDefined = function()
{
	var sTest = "This is a [token]";
	var mTokens = null;
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageWhenTheTokenMapIsUndefined = function()
{
	var sTest = "This is a [token]";
	var mTokens = undefined;
	var sExpected = sTest;

	_assertGetMessageReturnsCorrectValue(sTest, mTokens, sExpected);
};

TranslatorTest.prototype.test_getMessageForAnUnknownKeyReturnsAKeyWithQuestionMarks = function()
{
	var sKey = "unknown.key";
	var sExpected = "??? " + sKey + " ???";

	var Translator = require('br/i18n/Translator');
	var oTranslator = new Translator({});
	var sResult = oTranslator.getMessage(sKey, {});

	assertEquals(sExpected, sResult);
};

TranslatorTest.prototype.test_getMessageForAnUnknownKeyThatContainsATokenReturnsAKeyWithQuestionMarks = function()
{
	var sKey = "unknown[token]";
	var mTokens = { token: "bad" };
	var sExpected = "??? " + sKey + " ???";

	var Translator = require('br/i18n/Translator');
	var oTranslator = new Translator({});
	var sResult = oTranslator.getMessage(sKey, mTokens);

	assertEquals(sExpected, sResult);
};

TranslatorTest.prototype.test_utfForeignScriptTest = function() {
	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.miscForeignMessages);
	var text = "English: @{english.hello}\n" +
		"Chinese: @{chinese.hello}\n" +
		"Japanese: @{japanese.hello}\n" +
		"Korean: @{korean.hello}\n" +
		"Arabic: @{arabic.hello}\n" +
		"Hebrew: @{hebrew.hello}\n";

	var expected = "English: hello, world\n" +
		"Chinese: 你好，世界\n" +
		"Japanese: こんにちは、世界\n" +
		"Korean: 안녕하세요, 세상\n" +
		"Arabic: مرحبا، العالم\n" +
		"Hebrew: שלום, בעולם\n";
	var result = translator.translate(text, "text");

	assertEquals(expected, result);
};

TranslatorTest.prototype.test_correctTranslationWithCamelCaseTokens = function() {
	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.camelCaseTokens);

	var token = "token.CamelcasetokeN";

	var expected = "a Translation";
	var result = translator.getMessage(token);

	assertEquals(expected, result);
};

TranslatorTest.prototype.test_tokenExistsWithCamelCaseTokens = function() {
	var Translator = require('br/i18n/Translator');
	var translator = new Translator(this.camelCaseTokens);

	var token = "token.cameLcaseTOKEN";

	assertTrue(translator.tokenExists(token));
};
