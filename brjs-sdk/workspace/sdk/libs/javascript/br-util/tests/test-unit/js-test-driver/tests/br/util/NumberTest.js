var Number = require('br/util/Number');

NumberTest = TestCase('NumberTest');

NumberTest.prototype.test_decimalPlaces = function() {
	assertEquals(2, Number.decimalPlaces('.05'));
	assertEquals(1, Number.decimalPlaces('.5'));
	assertEquals(0, Number.decimalPlaces('1'));
	assertEquals(100, Number.decimalPlaces('25e-100'));
	assertEquals(100, Number.decimalPlaces('2.5e-99'));
	assertEquals(0, Number.decimalPlaces('.5e1'));
	assertEquals(1, Number.decimalPlaces('.25e1'));
};
