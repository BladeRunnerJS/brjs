SignificantFiguresFormatterTest = TestCase("SignificantFiguresFormatterTest");
SignificantFiguresFormatterTest.prototype.setUp = function() {
	 this.oFormatter = new br.presenter.formatter.SignificantFiguresFormatter();
};

SignificantFiguresFormatterTest.prototype.test_sf1 = function() {
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

SignificantFiguresFormatterTest.prototype.test_sf4 = function() {
	assertEquals("0.000", this.oFormatter.format(0, { sf: 4 }));
	assertEquals("0.1000", this.oFormatter.format(0.1, { sf: 4 }));
	assertEquals("-0.1000", this.oFormatter.format(-0.1, { sf: 4 }));
	assertEquals("0.5000", this.oFormatter.format(0.5, { sf: 4 }));
	assertEquals("-0.5000", this.oFormatter.format(-0.5, { sf: 4 }));
	assertEquals("0.9000", this.oFormatter.format(0.9, { sf: 4 }));
	assertEquals("-0.9000", this.oFormatter.format(-0.9, { sf: 4 }));
	assertEquals("1.111", this.oFormatter.format(1.1111, { sf: 4 }));
	assertEquals("-1.111", this.oFormatter.format(-1.1111, { sf: 4 }));
};

SignificantFiguresFormatterTest.prototype.test_shouldNotTruncateIfNoSfSupplied = function() {
	assertEquals("123.4567890156", this.oFormatter.format(123.4567890156, {}));
};

SignificantFiguresFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};

