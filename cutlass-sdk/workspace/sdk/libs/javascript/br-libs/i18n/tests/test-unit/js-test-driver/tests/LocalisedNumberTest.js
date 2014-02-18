// Hack to get jsunitextensions bundled but not throw JS error
//require("jsunitextensions");
LocalisedNumberTest = TestCase("LocalisedNumberTest");

LocalisedNumberTest.prototype.setUp = function()
{
};

function assertInstantiateNumberThrowsException(vNumber)
{
	var sErrorMessage = "An attempt to create an invalid LocalisedNumber object did not result in an exception being thrown.";
	var sExceptionMessage = "A LocalisedNumber object could not be instantiated from: " + vNumber;

	var Errors = require("br/Errors");
	var LocalisedNumber = require("br/i18n/LocalisedNumber");

	var oException = new Errors.InvalidParametersError(sExceptionMessage);

	assertFails(sErrorMessage, function() { new LocalisedNumber(vNumber); }, oException);

//	CT3 test code commented below to assist with debug/understanding (from brjs-last-stand)
//	var oException = new caplin.core.Error(caplin.core.Error.LEGACY, sExceptionMessage);
//	assertFails(sErrorMessage, function() { new caplin.i18n.LocalisedNumber(vNumber); }, oException);
};

LocalisedNumberTest.prototype.test_nonDecimals = function()
{
	var oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(0);
	assertEquals("0", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(1);
	assertEquals("1", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(100);
	assertEquals("100", oLocalisedNumber.format(",", "."));

	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(1000);
	assertEquals("1,000", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(1000000);
	assertEquals("1,000,000", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(-100);
	assertEquals("-100", oLocalisedNumber.format(",", "."));

	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(-1000);
	assertEquals("-1,000", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(-1000000);
	assertEquals("-1,000,000", oLocalisedNumber.format(",", "."));
};

LocalisedNumberTest.prototype.test_decimals = function()
{
	var oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(0.0234);
	assertEquals("0.0234", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(1.234);
	assertEquals("1.234", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(100.234);
	assertEquals("100.234", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(1000.234);
	assertEquals("1,000.234", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))(-1000.234);
	assertEquals("-1,000.234", oLocalisedNumber.format(",", "."));
};

LocalisedNumberTest.prototype.test_numberStrings = function()
{
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))("-1000000");
	assertEquals("-1,000,000", oLocalisedNumber.format(",", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))("-1000.234");
	assertEquals("-1,000.234", oLocalisedNumber.format(",", "."));
};

LocalisedNumberTest.prototype.test_spaceSeparator = function()
{
	var oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))("1000.234");
	assertEquals("1 000.234", oLocalisedNumber.format(" ", "."));
	
	oLocalisedNumber = new (require("br/i18n/LocalisedNumber"))("1234567.890");
	assertEquals("1 234 567.890", oLocalisedNumber.format(" ", "."));
};

LocalisedNumberTest.prototype.test_NullNumberThrowsException = function()
{
	debugger;
	assertInstantiateNumberThrowsException(null);
};

LocalisedNumberTest.prototype.test_UndefinedNumberThrowsException = function()
{
	assertInstantiateNumberThrowsException();
};

//LocalisedNumberTest.prototype.test_BooleanNumberThrowsException = function()
//{
//	assertInstantiateNumberThrowsException(false);
//	assertInstantiateNumberThrowsException(true);
//};
//
//LocalisedNumberTest.prototype.test_EmptyStringNumberThrowsException = function()
//{
//	assertInstantiateNumberThrowsException("");
//};
//
//LocalisedNumberTest.prototype.test_AlphaStringThrowsException = function()
//{
//	assertInstantiateNumberThrowsException("ABC");
//};
//
//LocalisedNumberTest.prototype.test_AlphaNumericStringThrowsException = function()
//{
//	assertInstantiateNumberThrowsException("1234E");
//};
