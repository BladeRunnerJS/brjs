br.thirdparty("jsunitextensions");

DateParserTest = TestCase("DateParserTest");

DateParserTest.prototype.setUp = function() {
	this.oParser = new br.presenter.parser.DateParser();
	this.oDate = new Date(2010, 10, 12, 13, 14, 15, 16);
	this.oDateAtMidnight = new Date(2010, 10, 12);

	var that = this;

	this.getSecondsSinceEpoch = function(oDate) {
		return String(Math.floor(oDate.getTime() / 1000));
	};

	this.getMillisecondsSinceEpoch = function(oDate) {
		return String(oDate.getTime());
	};

	this._assertParse = function(sDateInput, sDateOutput, mAttributes) {
		var sExpected = sDateOutput;
		var sActual = that.oParser.parse(sDateInput, mAttributes);
		assertVariantEquals(sDateInput + " -> " + sDateOutput, sExpected, sActual);
	};

	this.assertParse = function(sOutputFormat, sDateOutput, sDateInput, sInputFormat) {
		var mAttributes = {
			inputFormats: sInputFormat,
			outputFormat: sOutputFormat
		};
		that._assertParse(sDateInput, sDateOutput, mAttributes);
	};

	this.assertParseAmerican = function(sOutputFormat, sDateOutput, sDateInput, sInputFormat) {
		var mAttributes = {
			inputFormats: sInputFormat,
			outputFormat: sOutputFormat,
			american: "true"
		};
		that._assertParse(sDateInput, sDateOutput, mAttributes);
	};

};

/*
 * EUROPEAN
 */

DateParserTest.prototype.test_parseJavascript_prescribed = function() {
	this.assertParse("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate, "javascript");
	this.assertParse("javascript", this.oDate, this.oDate, "javascript");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDate), this.oDate, "javascript");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDate), this.oDate, "javascript");
};

DateParserTest.prototype.test_parseYYYYMMDD_AsString_prescribed = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", "20101112", "YYYYMMDD");
	this.assertParse("javascript", this.oDateAtMidnight, "20101112", "YYYYMMDD");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "20101112", "YYYYMMDD");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "20101112", "YYYYMMDD");
};

DateParserTest.prototype.test_parseYYYYMMDD_AsNumber_prescribed = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", 20101112, "YYYYMMDD");
	this.assertParse("javascript", this.oDateAtMidnight, 20101112, "YYYYMMDD");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), 20101112, "YYYYMMDD");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), 20101112, "YYYYMMDD");
};

DateParserTest.prototype.test_parseJavascript = function() {
	this.assertParse("YYYY-MM-DD HH:mm:ss", "2010-11-12 13:14:15", this.oDate);
	this.assertParse("javascript", this.oDate, this.oDate);
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDate), this.oDate);
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDate), this.oDate);
};

DateParserTest.prototype.test_parseYYYYMMDD_AsString = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", "20101112");
	this.assertParse("javascript", this.oDateAtMidnight, "20101112");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "20101112");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "20101112");
};

DateParserTest.prototype.test_parseYYYYMMDD_AsNumber = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", 20101112);
	this.assertParse("javascript", this.oDateAtMidnight, 20101112);
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), 20101112);
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), 20101112);
};

DateParserTest.prototype.test_parseDD_MM_YYYY = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", "12-11-2010");
	this.assertParse("javascript", this.oDateAtMidnight, "12-11-2010");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "12-11-2010");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "12-11-2010");
};

DateParserTest.prototype.test_parseDD_MMM_YYYY = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", "12-Nov-2010");
	this.assertParse("javascript", this.oDateAtMidnight, "12-Nov-2010");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "12-Nov-2010");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "12-Nov-2010");
};

DateParserTest.prototype.test_parseDD_MMM_YYYY_slashes = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", "12/Nov/2010");
	this.assertParse("javascript", this.oDateAtMidnight, "12/Nov/2010");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "12/Nov/2010");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "12/Nov/2010");
};

DateParserTest.prototype.test_parseDD_MMM_YYYY_dots = function() {
	this.assertParse("YYYY-MM-DD", "2010-11-12", "12.Nov.2010");
	this.assertParse("javascript", this.oDateAtMidnight, "12.Nov.2010");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "12.Nov.2010");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "12.Nov.2010");
};

DateParserTest.prototype.test_parseYYYY_MM_DD = function() {
	this.assertParse("YYYYMMDD", "20101112", "2010-11-12");
	this.assertParse("javascript", this.oDateAtMidnight, "2010-11-12");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "2010-11-12");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "2010-11-12");
};

DateParserTest.prototype.test_parseYYYY_MM_DD_slashes = function() {
	this.assertParse("YYYYMMDD", "20101112", "2010/11/12");
	this.assertParse("javascript", this.oDateAtMidnight, "2010/11/12");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "2010/11/12");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "2010/11/12");
};

DateParserTest.prototype.test_parseYYYY_MM_DD_dots = function() {
	this.assertParse("YYYYMMDD", "20101112", "2010.11.12");
	this.assertParse("javascript", this.oDateAtMidnight, "2010.11.12");
	this.assertParse("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "2010.11.12");
	this.assertParse("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "2010.11.12");
};

/*
 * AMERICAN
 */

DateParserTest.prototype.test_parseDD_MM_YYYY_american = function() {
	this.assertParseAmerican("YYYY-MM-DD", "2010-11-12", "11-12-2010");
	this.assertParseAmerican("javascript", this.oDateAtMidnight, "11-12-2010");
	this.assertParseAmerican("U", this.getSecondsSinceEpoch(this.oDateAtMidnight), "11-12-2010");
	this.assertParseAmerican("java", this.getMillisecondsSinceEpoch(this.oDateAtMidnight), "11-12-2010");
};

/*
 * TO STRING
 */

DateParserTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oParser.toString() );
};
