RoundingFormatterTest = TestCase("RoundingFormatterTest");
RoundingFormatterTest.prototype.setUp = function() {
	this.oFormatter = new br.presenter.formatter.RoundingFormatter();
	this.mDefaultAttributes = {};
	this.mDpAttributes = { dp: 3 };
	this.mSfAttributes = { sf: 3 };
	this.mRoundingAttributes = { rounding: 2 };
};

RoundingFormatterTest.prototype.test_dp0 = function() {
	assertEquals("0", this.oFormatter.format(0, { dp: 0 }));
	assertEquals("0", this.oFormatter.format(0.1, { dp: 0 }));
	assertEquals("0", this.oFormatter.format(-0.1, { dp: 0 }));
	assertEquals("1", this.oFormatter.format(0.5, { dp: 0 }));
	assertEquals("-1", this.oFormatter.format(-0.5, { dp: 0 }));
	assertEquals("1", this.oFormatter.format(0.9, { dp: 0 }));
	assertEquals("-1", this.oFormatter.format(-0.9, { dp: 0 }));
	assertEquals("1", this.oFormatter.format(1.2345, { dp: 0 }));
	assertEquals("-1", this.oFormatter.format(-1.2345, { dp: 0 }));
};

RoundingFormatterTest.prototype.test_dp2 = function() {
	assertEquals("0.00", this.oFormatter.format(0, { dp: 2 }));
	assertEquals("0.10", this.oFormatter.format(0.1, { dp: 2 }));
	assertEquals("-0.10", this.oFormatter.format(-0.1, { dp: 2 }));
	assertEquals("0.50", this.oFormatter.format(0.5, { dp: 2 }));
	assertEquals("-0.50", this.oFormatter.format(-0.5, { dp: 2 }));
	assertEquals("0.90", this.oFormatter.format(0.9, { dp: 2 }));
	assertEquals("-0.90", this.oFormatter.format(-0.9, { dp: 2 }));
	assertEquals("1.23", this.oFormatter.format(1.2345, { dp: 2 }));
	assertEquals("-1.23", this.oFormatter.format(-1.2345, { dp: 2 }));
};

RoundingFormatterTest.prototype.test_sf1 = function() {
	assertEquals("0", this.oFormatter.format(0, { sf: 1 }));
	assertEquals("0.1", this.oFormatter.format(0.1, { sf: 1 }));
	assertEquals("-0.1", this.oFormatter.format(-0.1, { sf: 1 }));
	assertEquals("0.5", this.oFormatter.format(0.5, { sf: 1 }));
	assertEquals("-0.5", this.oFormatter.format(-0.5, { sf: 1 }));
	assertEquals("0.9", this.oFormatter.format(0.9, { sf: 1 }));
	assertEquals("-0.9", this.oFormatter.format(-0.9, { sf: 1 }));
	assertEquals("1", this.oFormatter.format(1.2345, { sf: 1 }));
	assertEquals("-1", this.oFormatter.format(-1.2345, { sf: 1 }));
};

RoundingFormatterTest.prototype.test_sf4 = function() {
	assertEquals("0.000", this.oFormatter.format(0, { sf: 4 }));
	assertEquals("0.1000", this.oFormatter.format(0.1, { sf: 4 }));
	assertEquals("-0.1000", this.oFormatter.format(-0.1, { sf: 4 }));
	assertEquals("0.5000", this.oFormatter.format(0.5, { sf: 4 }));
	assertEquals("-0.5000", this.oFormatter.format(-0.5, { sf: 4 }));
	assertEquals("0.9000", this.oFormatter.format(0.9, { sf: 4 }));
	assertEquals("-0.9000", this.oFormatter.format(-0.9, { sf: 4 }));
//	assertEquals("1.235", this.oFormatter.format(1.2345, { sf: 4 }));
//	assertEquals("-1.235", this.oFormatter.format(-1.2345, { sf: 4 }));
	assertEquals("1.111", this.oFormatter.format(1.1111, { sf: 4 }));
	assertEquals("-1.111", this.oFormatter.format(-1.1111, { sf: 4 }));
};

RoundingFormatterTest.prototype.test_rounding0 = function() {
	assertEquals("0", this.oFormatter.format(0, { rounding: 0 }));
	assertEquals("0", this.oFormatter.format(0.1, { rounding: 0 }));
	assertEquals("0", this.oFormatter.format(-0.1, { rounding: 0 }));
	assertEquals("1", this.oFormatter.format(0.5, { rounding: 0 }));
	assertEquals("-1", this.oFormatter.format(-0.5, { rounding: 0 }));
	assertEquals("1", this.oFormatter.format(0.9, { rounding: 0 }));
	assertEquals("-1", this.oFormatter.format(-0.9, { rounding: 0 }));
	assertEquals("1", this.oFormatter.format(1.2345, { rounding: 0 }));
	assertEquals("-1", this.oFormatter.format(-1.2345, { rounding: 0 }));
};

RoundingFormatterTest.prototype.test_rounding2 = function() {
	assertEquals("0", this.oFormatter.format(0, { rounding: 2 }));
	assertEquals("0.1", this.oFormatter.format(0.1, { rounding: 2 }));
	assertEquals("-0.1", this.oFormatter.format(-0.1, { rounding: 2 }));
	assertEquals("0.5", this.oFormatter.format(0.5, { rounding: 2 }));
	assertEquals("-0.5", this.oFormatter.format(-0.5, { rounding: 2 }));
	assertEquals("0.9", this.oFormatter.format(0.9, { rounding: 2 }));
	assertEquals("-0.9", this.oFormatter.format(-0.9, { rounding: 2 }));
	assertEquals("1.23", this.oFormatter.format(1.2345, { rounding: 2 }));
	assertEquals("-1.23", this.oFormatter.format(-1.2345, { rounding: 2 }));
};

RoundingFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
