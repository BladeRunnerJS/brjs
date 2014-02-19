LocalisedDateTest = TestCase("LocalisedDateTest");
LocalisedDateTest.prototype.setUp = function()
{
	this.m_pOrigUnprocessedI18NMessages = window.pUnprocessedI18NMessages;
	this.mDateMessages = {
		"br.i18n.date.format": "DD-MM-YYYY",
		"br.i18n.date.format.long": "ddd, DD MMM, YYYY, HH:mm:ss A",
		"br.i18n.date.month.january": "Leden",
		"br.i18n.date.month.short.april": "Dub",
		"br.i18n.date.month.short.december": "Pros",
		"br.i18n.date.day.saturday": "Sobota",
		"br.i18n.date.day.short.monday": "Mon",
		"br.i18n.date.day.short.wednesday": "Wed"
	};
	window.pUnprocessedI18NMessages = [this.mDateMessages];

	var i18n = require("br/I18n");
	i18n.reset();
	i18n.initialise(window.pUnprocessedI18NMessages);
};

LocalisedDateTest.prototype.tearDown = function()
{
	window.pUnprocessedI18NMessages = this.m_pOrigUnprocessedI18NMessages;
};

//test seconds since epoch
LocalisedDateTest.prototype.test_calculatesSecondsSinceEpoch = function()
{
	var oDate = new Date(2010, 10, 12, 13, 14, 15, 16);
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var secondsSinceUnixEpoch = String(Math.floor(oDate.getTime() / 1000));
	
	assertEquals("Seconds since unix epoch are not correct",secondsSinceUnixEpoch, oLocalisedDate.format("U") );
};

LocalisedDateTest.prototype.test_containsMonthNameMMMM = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsMonthName("d MMMM Y");

	assertTrue("The date format specifies a month name (MMMM), but none was detected", bContainsMonthName);
};

LocalisedDateTest.prototype.test_containsMonthNameMMM = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsMonthName("d MMM Y");

	assertTrue("The date format specifies a month name (MMM), but none was detected", bContainsMonthName);
};

LocalisedDateTest.prototype.test_doesNotContainMonthName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsMonthName("d mmmm Y");

	assertFalse("The date format does not specify a month name", bContainsMonthName);
};

LocalisedDateTest.prototype.test_containsLongMonthName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsLongMonthName("d MMMM Y");

	assertTrue("The date format specifies a long month name (MMMM), but none was detected", bContainsMonthName);
};

LocalisedDateTest.prototype.test_doesNotContainLongMonthName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsLongMonthName("d MMM Y");

	assertFalse("The date format does not specify a long month name", bContainsMonthName);
};

LocalisedDateTest.prototype.test_containsLongMonthNameIsCaseSensitive = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsLongMonthName("Y, d mmmm");

	assertFalse("The date format does not specify a long month name", bContainsMonthName);
};

LocalisedDateTest.prototype.test_containsAbbreviatedMonthName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsAbbreviatedMonthName("Y, d MMM");

	assertTrue("The date format specifies an abbreviated month name (MMM), but none was detected", bContainsMonthName);
};

LocalisedDateTest.prototype.test_doesNotContainAbbreviatedMonthName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsAbbreviatedMonthName("d MMMM Y");

	assertFalse("The date format does not specify an abbreviated month name", bContainsMonthName);
};

LocalisedDateTest.prototype.test_containsAbbreviatedMonthNameIsCaseSensitive = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsMonthName = oLocalisedDate._containsAbbreviatedMonthName("Y, d mmm");

	assertFalse("The date format does not specify an abbreviated month name", bContainsMonthName);
};

LocalisedDateTest.prototype.test_getMonthName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2012);
	oDate.setMonth(0);
	oDate.setDate(10);

	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sMonth = oLocalisedDate._getMonthName();

	assertEquals("The month-name was different from the expected month-name", "January", sMonth);
};

LocalisedDateTest.prototype.test_getAbbreviatedMonthName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2012);
	oDate.setMonth(11);
	oDate.setDate(10);

	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sMonth = oLocalisedDate._getAbbreviatedMonthName();

	assertEquals("The month-name was different from the expected month-name", "Dec", sMonth);
};

LocalisedDateTest.prototype.test_translateShortDate = function() {
	var oDate = new Date();
	oDate.setFullYear(2361);
	oDate.setDate(19);
	oDate.setMonth(3);
	oDate.setHours(7);
	oDate.setMinutes(39);
	oDate.setSeconds(1);

	var sDateFormat = this.mDateMessages["br.i18n.date.format"];
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sLocalisedDate = oLocalisedDate.format(sDateFormat);


	var sExpectedShortDate = "19-04-2361";
	assertEquals("The localised date was different from the expected date.", sExpectedShortDate, sLocalisedDate);
};

LocalisedDateTest.prototype.test_translateLongDate = function()
{
	var oDate = new Date();
	oDate.setFullYear(2361);
	oDate.setDate(19);
	oDate.setMonth(3);
	oDate.setHours(7);
	oDate.setMinutes(39);
	oDate.setSeconds(1);

	var sDateFormat = this.mDateMessages["br.i18n.date.format.long"];
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sLocalisedDate = oLocalisedDate.format(sDateFormat);

	var sExpectedCzechDate = "Wed, 19 Dub, 2361, 07:39:01 AM";
	assertEquals("The localised date was different from the expected date.", sExpectedCzechDate, sLocalisedDate);
};

LocalisedDateTest.prototype.test_translateFullMonthName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2011);
	oDate.setMonth(0);
	oDate.setDate(1);

	var sDateFormat = "DD MMMM YYYY";


	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sLocalisedDate = oLocalisedDate.format(sDateFormat);

	var sExpectedCzechDate = "01 Leden 2011";
	assertEquals("The localised date was different from the expected date.", sExpectedCzechDate, sLocalisedDate);
};

LocalisedDateTest.prototype.test_translateAbbreviatedMonthName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2012);
	oDate.setMonth(11);
	oDate.setDate(31);

	var sDateFormat = "DD MMM YYYY";

	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sLocalisedDate = oLocalisedDate.format(sDateFormat);

	var sExpectedCzechDate = "31 Pros 2012";
	assertEquals("The localised date was different from the expected date.", sExpectedCzechDate, sLocalisedDate);
};

LocalisedDateTest.prototype.test_containsAmPm = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsAmPm = oLocalisedDate._containsAmPm("d F Y h:i a");

	assertTrue("The date format specifies am/pm, but it was not detected", bContainsAmPm);
};

LocalisedDateTest.prototype.test_containsDayNameddd = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsDayName("ddd j F Y");

	assertTrue("The date format specifies a day name (ddd), but none was detected", bContainsDayName);
};

LocalisedDateTest.prototype.test_containsDayNamedddd = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsDayName("dddd j F Y");

	assertTrue("The date format specifies a day name (dddd), but none was detected", bContainsDayName);
};

LocalisedDateTest.prototype.test_doesNotContainDayName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsDayName("j F Y");

	assertFalse("The date format does not specify a day name", bContainsDayName);
};

LocalisedDateTest.prototype.test_containsLongDayName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsLongDayName("dddd j F Y");

	assertTrue("The date format specifies a long day name (dddd), but none was detected", bContainsDayName);
};

LocalisedDateTest.prototype.test_doesNotContainLongDayName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsLongDayName("ddd j F Y");

	assertFalse("The date format does not specify a long day name", bContainsDayName);
};

LocalisedDateTest.prototype.test_containsLongDayNameIsCaseSensitive = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsLongDayName("DDDD j F Y");

	assertFalse("The date format does not specify a long day name", bContainsDayName);
};

LocalisedDateTest.prototype.test_containsAbbreviatedDayName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsAbbreviatedDayName("ddd j F Y");

	assertTrue("The date format specifies an abbreviated day name (ddd), but none was detected", bContainsDayName);
};

LocalisedDateTest.prototype.test_doesNotContainAbbreviatedDayName = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsAbbreviatedDayName("dddd j F Y");

	assertFalse("The date format does not specify an abbreviated day name", bContainsDayName);
};

LocalisedDateTest.prototype.test_containsAbbreviatedDayNameIsCaseSensitive = function()
{
	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))();
	var bContainsDayName = oLocalisedDate._containsAbbreviatedDayName("DDD j F Y");

	assertFalse("The date format does not specify an abbreviated day name", bContainsDayName);
};

LocalisedDateTest.prototype.test_getDayName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2011);
	oDate.setMonth(1);
	oDate.setDate(14);

	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sDay = oLocalisedDate._getDayName();

	assertEquals("The day-name was different from the expected day-name", "Monday", sDay);
};

LocalisedDateTest.prototype.test_getAbbreviatedDayName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2012);
	oDate.setMonth(11);
	oDate.setDate(12);

	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sDay = oLocalisedDate._getAbbreviatedDayName();

	assertEquals("The day-name was different from the expected day-name", "Wed", sDay);
};

LocalisedDateTest.prototype.test_translateLongDayName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2005);
	oDate.setMonth(0);
	oDate.setDate(22);

	var sDateFormat = "dddd DD MMMM YYYY";


	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sLocalisedDate = oLocalisedDate.format(sDateFormat);

	var sExpectedCzechDate = "Sobota 22 Leden 2005";
	assertEquals("The localised date was different from the expected date.", sExpectedCzechDate, sLocalisedDate);
};

LocalisedDateTest.prototype.test_translateAbbreviatedDayName = function()
{
	var oDate = new Date();
	oDate.setFullYear(2012);
	oDate.setMonth(11);
	oDate.setDate(31);

	var sDateFormat = "ddd DD MMM YYYY";

	var oLocalisedDate = new (require("br/i18n/LocalisedDate"))(oDate);
	var sLocalisedDate = oLocalisedDate.format(sDateFormat);

	var sExpectedCzechDate = "Mon 31 Pros 2012";
	assertEquals("The localised date was different from the expected date.", sExpectedCzechDate, sLocalisedDate);
};

