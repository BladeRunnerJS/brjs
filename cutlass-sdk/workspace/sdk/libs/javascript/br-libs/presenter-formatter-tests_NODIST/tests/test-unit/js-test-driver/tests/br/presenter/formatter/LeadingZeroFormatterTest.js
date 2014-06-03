LeadingZeroFormatterTest = TestCase("LeadingZeroFormatterTest");

require('br/presenter/formatter/LeadingZeroFormatter');

LeadingZeroFormatterTest.prototype.setUp = function() {
	this.oFormatter = new br.presenter.formatter.LeadingZeroFormatter();
	this.mAttributesSize0 = {length:0};
	this.mAttributesSize1 = {length:1};
	this.mAttributesSize2 = {length:2};
	this.mAttributesSize5 = {length:5};
};

/*
 * PASS THROUGH TESTS
 */

LeadingZeroFormatterTest.prototype.test_NullPassthrough = function() {
	assertEquals(null, this.oFormatter.format(null, this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_UndefinedPassthrough = function() {
	assertEquals(undefined, this.oFormatter.format(undefined, this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_EmptyPassthrough = function() {
	assertEquals("", this.oFormatter.format("", this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_HyphenPassthrough = function() {
	assertEquals("-", this.oFormatter.format("-", this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_AlphaPassthrough = function() {
	assertEquals("ABC", this.oFormatter.format("ABC", this.mAttributesSize2));
};

/*
 * TESTS ON POSITIVE VALUES
 */

LeadingZeroFormatterTest.prototype.test_OneWith0 = function() {
	assertEquals("1", this.oFormatter.format(1, this.mAttributesSize0));
};

LeadingZeroFormatterTest.prototype.test_OneWith1 = function() {
	assertEquals("1", this.oFormatter.format(1, this.mAttributesSize1));
};

LeadingZeroFormatterTest.prototype.test_OneWith2 = function() {
	assertEquals("01", this.oFormatter.format(1, this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_OneWith5 = function() {
	assertEquals("00001", this.oFormatter.format(1, this.mAttributesSize5));
};

/*
 * TESTS ON ZERO
 */
LeadingZeroFormatterTest.prototype.test_ZeroWith0 = function() {
	assertEquals("0", this.oFormatter.format(0, this.mAttributesSize0));
};

LeadingZeroFormatterTest.prototype.test_ZeroWith1 = function(){
	assertEquals("0", this.oFormatter.format(0, this.mAttributesSize1));
};

LeadingZeroFormatterTest.prototype.test_ZeroWith2 = function() {
	assertEquals("00", this.oFormatter.format(0, this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_ZeroWith5 = function() {
	assertEquals("00000", this.oFormatter.format(0, this.mAttributesSize5));
};

/*
 * TESTS ON NEGATIVE VALUES
 */

LeadingZeroFormatterTest.prototype.test_MinusOneWith0 = function() {
	assertEquals("-1", this.oFormatter.format(-1, this.mAttributesSize0));
};

LeadingZeroFormatterTest.prototype.test_MinusOneWith1 = function() {
	assertEquals("-1", this.oFormatter.format(-1, this.mAttributesSize1));
};

LeadingZeroFormatterTest.prototype.test_MinusOneWith2 = function() {
	assertEquals("-01", this.oFormatter.format(-1, this.mAttributesSize2));
};

LeadingZeroFormatterTest.prototype.test_MinusOneWith5 = function() {
	assertEquals("-00001", this.oFormatter.format(-1, this.mAttributesSize5));
};

LeadingZeroFormatterTest.prototype.test_toString = function() {
	assertEquals("string", typeof this.oFormatter.toString());
};
