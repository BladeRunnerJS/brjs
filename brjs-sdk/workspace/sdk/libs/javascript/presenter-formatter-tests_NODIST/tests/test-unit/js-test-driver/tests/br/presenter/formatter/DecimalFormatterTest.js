DecimalFormatterTest = TestCase("DecimalFormatterTest");

require('br/presenter/formatter/DecimalFormatter');

DecimalFormatterTest.prototype.setUp = function() {
	 this.oFormatter = new br.presenter.formatter.DecimalFormatter();
};

DecimalFormatterTest.prototype.test_FourDp = function() {
debugger;
	// 4 Decimal places.
	var mAttributes = {dp: "4"};
	assertEquals("123.4500", this.oFormatter.format(123.45, mAttributes));
	assertEquals("123.4568", this.oFormatter.format(123.45678, mAttributes));
	assertEquals("123.4543", this.oFormatter.format(123.45432, mAttributes));
	assertEquals("0.0000", this.oFormatter.format(0, mAttributes));
	assertEquals("0.0000", this.oFormatter.format(0.00001, mAttributes));
	assertEquals("123.4500", this.oFormatter.format("123.45", mAttributes));
	assertEquals("ABC", this.oFormatter.format("ABC", mAttributes));
	assertEquals( this.oFormatter, this.oFormatter.format( this.oFormatter, mAttributes));
	assertEquals("", this.oFormatter.format("", mAttributes));
};

DecimalFormatterTest.prototype.test_ZeroDp = function() {
	var mAttributes = {dp: "0"};
	assertEquals("123", this.oFormatter.format(123.45, mAttributes));
	assertEquals("0", this.oFormatter.format(0, mAttributes));
	assertEquals("0", this.oFormatter.format(0.00001, mAttributes));
	assertEquals("DEF", this.oFormatter.format("DEF", mAttributes));
	assertEquals( this.oFormatter, this.oFormatter.format( this.oFormatter, mAttributes));
	assertEquals("", this.oFormatter.format("", mAttributes));
};

DecimalFormatterTest.prototype.test_Exponents = function() {
	var mAttributes = {dp: "3"};
	assertEquals("0.000", this.oFormatter.format("4.99e-4", mAttributes));
	assertEquals("0.001", this.oFormatter.format("5.00e-4", mAttributes));
	assertEquals("0.008", this.oFormatter.format("7.89e-3", mAttributes));
	assertEquals("1.000", this.oFormatter.format("1e+0", mAttributes));
	assertEquals("1.000", this.oFormatter.format("1e-0", mAttributes));
	assertEquals("1.000", this.oFormatter.format("1.0e+0", mAttributes));
	assertEquals("1.000", this.oFormatter.format("1.0e-0", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1e-2", mAttributes));
	assertEquals("100.000", this.oFormatter.format("1.0e+2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.0e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.00e-2", mAttributes));
	assertEquals("0.011", this.oFormatter.format("1.10e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.01e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.001e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.0001e-2", mAttributes));
};

DecimalFormatterTest.prototype.test_shouldNotTruncateIfNoDpSupplied = function() {
	var mAttributes = {};
	assertEquals(123.456789015678234, this.oFormatter.format(123.456789015678234, mAttributes));
};

DecimalFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};

