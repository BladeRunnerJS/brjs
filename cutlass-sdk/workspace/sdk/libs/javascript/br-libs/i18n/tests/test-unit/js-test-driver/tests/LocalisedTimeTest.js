//TODO this test has references to ct.i18n which need to be removed
LocalisedTimeTest = TestCase("LocalisedTimeTest");

LocalisedTimeTest.prototype.setUp = function()
{
	this.m_fRealCT_i18n = ct.i18n;

	var that = this;

	ct.i18n = function(sToken)
	{
		return that.mTimeFormats[sToken];
	};

	this.mTimeFormats = {
		"ct.i18n.time.format.separator": ":"
	};

};

LocalisedTimeTest.prototype.tearDown = function()
{
	ct.i18n = this.m_fRealCT_i18n;
};

function assertCallToFormatThrowsException(vTime)
{
	var sErrorMessage = "An attempt to create an invalid time did not result in an exception being thrown.";
	var sExceptionMessage = "A LocalisedTime object could not be instantiated from: " + vTime;

	var Errors = require("br/Errors");

	var oException = new Errors.TypeError(Errors.INVALID_DATA, sExceptionMessage);
	assertFails(sErrorMessage, function() { new (require("br/i18n/LocalisedTime"))(vTime); }, oException);
};

LocalisedTimeTest.prototype.test_NullTimeThrowsException = function()
{
	assertCallToFormatThrowsException(null);
};

LocalisedTimeTest.prototype.test_UndefinedTimeThrowsException = function()
{
	assertCallToFormatThrowsException(undefined);
};

LocalisedTimeTest.prototype.test_EmptyStringTimeThrowsException = function()
{
	assertCallToFormatThrowsException("");
};

LocalisedTimeTest.prototype.test_HyphenTimeThrowsException = function()
{
	assertCallToFormatThrowsException("-");
};

LocalisedTimeTest.prototype.test_AlphaTimeThrowsException = function()
{
	assertCallToFormatThrowsException("ABCDEF");
};

LocalisedTimeTest.prototype.test_AlphaNumericTimeThrowsException = function()
{
	assertCallToFormatThrowsException("0910AM");
};

LocalisedTimeTest.prototype.test_SevenCharactersThrowsException = function()
{
	assertCallToFormatThrowsException("1020300");
};

LocalisedTimeTest.prototype.test_ZeroTime = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))("000000");
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("00:00:00", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_TwoCharacters = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))(10);
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_FourCharacters = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))(1010);
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10:10", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_SixCharacters = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))(101010);
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10:10:10", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_SixCharacterString = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))('101010');
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10:10:10", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_OneCharacter = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))(1);
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("1", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_ThreeCharacters = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))(101);
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10:1", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_FiveCharacters = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))(10101);
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10:10:1", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_FiveCharacterString = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))('10101');
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10:10:1", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_TimeStringBeginningWithZero = function()
{
	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))('093001');
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("09:30:01", sLocalisedTime);
};

LocalisedTimeTest.prototype.test_DifferentTimeFormat = function()
{
	this.mTimeFormats["ct.i18n.time.format.separator"] = '.';

	var oLocalisedTime = new (require("br/i18n/LocalisedTime"))('101010');
	var sLocalisedTime = oLocalisedTime.format();

	assertEquals("10.10.10", sLocalisedTime);
};
