TruncateDecimalFormatterTest = TestCase("TruncateDecimalFormatterTest");
TruncateDecimalFormatterTest.prototype.setUp = function() {
	this.oFormatter = new br.presenter.formatter.TruncateDecimalFormatter();
};

TruncateDecimalFormatterTest.prototype.test_limitStringValues = function(){
	var mAttributes = {	"dp": "3" };
	assertEquals("123", this.oFormatter.format("123", mAttributes));
	assertEquals("123.", this.oFormatter.format("123.", mAttributes));
	assertEquals("123.0", this.oFormatter.format("123.0", mAttributes));
	assertEquals("123.00", this.oFormatter.format("123.00", mAttributes));
	assertEquals("123.000", this.oFormatter.format("123.000", mAttributes));
	assertEquals("123.000", this.oFormatter.format("123.0000", mAttributes));
	assertEquals("123.40", this.oFormatter.format("123.40", mAttributes));
	assertEquals("123.45", this.oFormatter.format("123.45", mAttributes));
	assertEquals("123.450", this.oFormatter.format("123.450", mAttributes));
	assertEquals("123.400", this.oFormatter.format("123.4000", mAttributes));
	assertEquals("123.040", this.oFormatter.format("123.0400", mAttributes));
	assertEquals("123.004", this.oFormatter.format("123.0040", mAttributes));
	assertEquals("123.000", this.oFormatter.format("123.0004", mAttributes));
	assertEquals("123.457", this.oFormatter.format("123.45678", mAttributes));
	assertEquals("0.000", this.oFormatter.format("0.00049", mAttributes));
	assertEquals("0.001", this.oFormatter.format("0.00050", mAttributes));
};

TruncateDecimalFormatterTest.prototype.test_limitNumericValues = function() {
	var mAttributes = { "dp": "3" };
	assertEquals("123", this.oFormatter.format(123, mAttributes));
	assertEquals("123.45", this.oFormatter.format(123.45, mAttributes));
	assertEquals("123.457", this.oFormatter.format(123.45678, mAttributes));
	assertEquals("0.008", this.oFormatter.format(7.654e-3, mAttributes));
	assertEquals("0.000", this.oFormatter.format(4.99e-4, mAttributes));
	assertEquals("0.001", this.oFormatter.format(5.00e-4, mAttributes));
};

TruncateDecimalFormatterTest.prototype.test_limitExponentValues = function() {
	var mAttributes = { "dp": "3" };
	assertEquals("0.000", this.oFormatter.format("4.99e-4", mAttributes));
	assertEquals("0.001", this.oFormatter.format("5.00e-4", mAttributes));
	assertEquals("0.008", this.oFormatter.format("7.89e-3", mAttributes));
	assertEquals("1", this.oFormatter.format("1e+0", mAttributes));
	assertEquals("1", this.oFormatter.format("1e-0", mAttributes));
	assertEquals("1", this.oFormatter.format("1.0e+0", mAttributes));
	assertEquals("1", this.oFormatter.format("1.0e-0", mAttributes));
	assertEquals("0.01", this.oFormatter.format("1e-2", mAttributes));
	assertEquals("100", this.oFormatter.format("1.0e+2", mAttributes));
	assertEquals("0.01", this.oFormatter.format("1.0e-2", mAttributes));
	assertEquals("0.01", this.oFormatter.format("1.00e-2", mAttributes));
	assertEquals("0.011", this.oFormatter.format("1.10e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.01e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.001e-2", mAttributes));
	assertEquals("0.010", this.oFormatter.format("1.0001e-2", mAttributes));
};

TruncateDecimalFormatterTest.prototype.test_limitTinyValues = function() {
	var mAttributes = { "dp": "10" };
	assertEquals("0.1", this.oFormatter.format("1e-1", mAttributes));
	assertEquals("0.01", this.oFormatter.format("1e-2", mAttributes));
	assertEquals("0.001", this.oFormatter.format("1e-3", mAttributes));
	assertEquals("0.0001", this.oFormatter.format("1e-4", mAttributes));
	assertEquals("0.00001", this.oFormatter.format("1e-5", mAttributes));
	assertEquals("0.000001", this.oFormatter.format("1e-6", mAttributes));
};

TruncateDecimalFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
