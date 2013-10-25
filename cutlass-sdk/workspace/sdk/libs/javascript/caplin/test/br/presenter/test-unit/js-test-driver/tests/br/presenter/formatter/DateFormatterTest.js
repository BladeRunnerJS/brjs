br.thirdparty("jsunitextensions");

DateFormatterTest = TestCase("DateFormatterTest");

DateFormatterTest.prototype.setUp = function() {
	
	this.m_oDate = Date;
	
	this.oFormatter = new br.presenter.formatter.DateFormatter();
	this.oDate = new Date(2010, 10, 12, 13, 14, 15, 16);
	this.oDateAtMidnight = new Date(2010, 10, 12);
	
	this.getSecondsSinceEpoch = function(oDate) {
		return String(Math.floor(oDate.getTime() / 1000));
	};
	
	this.getMillisecondsSinceEpoch = function(oDate) {
		return String(oDate.getTime());
	};
	
	var that = this;
	// convert from date object to string
	this.assertSimpleFormat = function(sDatePicture, sDateOutput, oDateInput) {
		assertEquals(sDatePicture, sDateOutput, that.oFormatter.formatDate(oDateInput, sDatePicture));
	};
	
	// convert from date string to object
	this.assertSimpleParse = function(sDatePicture, sDateInput, oDateOutput) {
		assertVariantEquals(sDatePicture, oDateOutput, that.oFormatter.parseDate(sDateInput, sDatePicture));
	};
	
	this.assertFormat = function(sOutputFormat, sDateOutput, sDateInput, sInputFormat) {
		var mAttributes = {
			inputFormat: sInputFormat,
			outputFormat: sOutputFormat
		};
		var sExpected = sDateOutput;
		var sActual = that.oFormatter.format(sDateInput, mAttributes);
		assertVariantEquals(sDateInput + " -> " + sDateOutput, sExpected, sActual);
	};
	
	this.assertFormatWithTimezone = function(sOutputFormat, sDateOutput, sDateInput, sInputFormat) {
		var mAttributes = {
				inputFormat: sInputFormat,
				outputFormat: sOutputFormat,
				adjustForTimezone: "true"
		};
		var sExpected = sDateOutput;
		var sActual = that.oFormatter.format(sDateInput, mAttributes);
		assertVariantEquals(sDateInput + " -> " + sDateOutput, sExpected, sActual);
	};
	
	this.fixTimeZone = function(nAdjustment) {
		Date.prototype.getTimezoneOffset = function()
		{
			return nAdjustment;
		};
	};
};

DateFormatterTest.prototype.tearDown = function() {
	Date = this.m_oDate;
};


/*
 * FORMAT
 */

DateFormatterTest.prototype.test_formatDateWithExtDatePictures = function() {
	this.assertSimpleFormat("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate);
	this.assertSimpleFormat("javascript", this.oDate, this.oDate);
	this.assertSimpleFormat("U", this.getSecondsSinceEpoch(this.oDate), this.oDate);
	this.assertSimpleFormat("java", this.getMillisecondsSinceEpoch(this.oDate), this.oDate);
}

DateFormatterTest.prototype.test_formatDateWithCaplinExtensionsToExtDatePictures = function() {
	this.assertSimpleFormat("YYYY-MM-DD HH:mm:ss.SSS", "2010-11-12 13:14:15.016", this.oDate);
};

/*
 * PARSE
 */

DateFormatterTest.prototype.test_parseDateWithExtDatePictures = function() {
	this.assertSimpleParse("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate);
	this.assertSimpleParse("javascript", this.oDate, this.oDate);
	this.assertSimpleParse("U", this.getSecondsSinceEpoch(this.oDate), this.oDate);
	this.assertSimpleParse("java", this.getMillisecondsSinceEpoch(this.oDate), this.oDate);
};

/*
 * CONVERT
 */

DateFormatterTest.prototype.test_formatJavascript = function() {
	this.assertFormat("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate, "javascript");
	this.assertFormat("javascript", this.oDate, this.oDate, "javascript");
	this.assertFormat("U", this.getSecondsSinceEpoch(this.oDate), this.oDate, "javascript");
	this.assertFormat("java", this.getMillisecondsSinceEpoch(this.oDate), this.oDate, "javascript");
};

DateFormatterTest.prototype.test_formatJava = function() {
	this.assertFormat("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate, "java");
	this.assertFormat("javascript", this.oDate, this.oDate, "java");
	this.assertFormat("U", this.getSecondsSinceEpoch(this.oDate), this.oDate, "java");
	this.assertFormat("java", this.getMillisecondsSinceEpoch(this.oDate), this.oDate, "java");
};

DateFormatterTest.prototype.test_formatYYYYMMDD_AsString = function() {
	this.assertFormat("YYYY-MM-DD", "2010-11-12", "20101112", "YYYYMMDD");
	this.assertFormat("javascript", this.oDateAtMidnight, "20101112", "YYYYMMDD");
	this.assertFormat("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "20101112", "YYYYMMDD");
	this.assertFormat("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "20101112", "YYYYMMDD");
};

DateFormatterTest.prototype.test_formatYYYYMMDD_AsNumber = function() {
	this.assertFormat("YYYY-MM-DD", "2010-11-12", 20101112, "YYYYMMDD");
	this.assertFormat("javascript", this.oDateAtMidnight, 20101112, "YYYYMMDD");
	this.assertFormat("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), 20101112, "YYYYMMDD");
	this.assertFormat("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), 20101112, "YYYYMMDD");
};

DateFormatterTest.prototype.test_adjustForTimeZoneSetsCorrectDate = function() {
	this.fixTimeZone(-60);
	this.assertFormatWithTimezone("YYYY-MM-DD HH:mm:ss", "2010-11-12 14:14:15", this.oDate, "javascript");
};
DateFormatterTest.prototype.test_notAdjustingForTimeZoneSetsCorrectDate = function() {
	this.fixTimeZone(-60);
	this.assertFormat("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate, "javascript");
};

DateFormatterTest.prototype.test_adjustForTimeZoneReadjustsTimeWhenTimeZoneChanges = function() {
	this.fixTimeZone(-60);
	this.assertFormatWithTimezone("YYYY-MM-DD HH:mm:ss", "2010-11-12 14:14:15", this.oDate, "javascript");
	this.fixTimeZone(-180);
	this.assertFormatWithTimezone("YYYY-MM-DD HH:mm:ss", "2010-11-12 16:14:15", this.oDate, "javascript");
};

DateFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
