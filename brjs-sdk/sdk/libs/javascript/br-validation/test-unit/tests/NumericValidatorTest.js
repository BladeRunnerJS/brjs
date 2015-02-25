(function() {
	var NumericValidator = require('br/presenter/validator/NumericValidator');

	NumericValidatorTest = TestCase("NumericValidatorTest");

	NumericValidatorTest.prototype.setUp = function() {
		this.oNumericValidator = new NumericValidator("errorMessage");

		this.oValidationResult =
		{
			setResult: function(bResult, sMessage)
			{
				this.bResult = bResult;
				this.sMessage = sMessage;
			}
		}
	};

	NumericValidatorTest.prototype.testValue = function(value) {
		this.oNumericValidator.validate(value, {}, this.oValidationResult);
		return this.oValidationResult.bResult;
	};

	NumericValidatorTest.prototype.test_ValidIntegers = function() {
		assertTrue('1.1', this.testValue(1));
		assertTrue('1.2', this.testValue("1"));
	};

	NumericValidatorTest.prototype.test_ValidFloats = function() {
		assertTrue('2.1', this.testValue(.1));
		assertTrue('2.2', this.testValue(1.));
		assertTrue('2.3', this.testValue(1.1));
		assertTrue('2.4', this.testValue(".1"));
		assertTrue('2.5', this.testValue("1."));
		assertTrue('2.6', this.testValue("1.1"));
	};

	NumericValidatorTest.prototype.test_ValidSigns = function() {
		assertTrue('3.1', this.testValue(-1));
		assertTrue('3.2', this.testValue("-1"));
		assertTrue('3.3', this.testValue("+1"));
	};

	NumericValidatorTest.prototype.test_InvalidStrings = function() {
		assertFalse('4.1', this.testValue("1a"));
		assertFalse('4.2', this.testValue("1..1"));
		assertFalse('4.3', this.testValue("."));
	};

	NumericValidatorTest.prototype.test_otherTypes = function() {
		assertFalse('5.1', this.testValue(true));
		assertFalse('5.2', this.testValue(NaN));
		assertFalse('5.3', this.testValue({'1': 1}));
		assertFalse('5.4', this.testValue([1]));
		assertFalse('5.5', this.testValue(undefined));
		assertFalse('5.6', this.testValue(null));
		assertFalse('5.7', this.testValue("abc"));
	};

	NumericValidatorTest.prototype.testInvalidNumberShowsErrorMessage = function() {
		this.oNumericValidator.validate("abc", {}, this.oValidationResult);

		assertFalse(this.oValidationResult.bResult);
		assertEquals("errorMessage", this.oValidationResult.sMessage);
	};

	NumericValidatorTest.prototype.testInvalidNumberShowsi18nErrorMessage = function() {
		var oNumericValidator =  new NumericValidator("br.validation.i18ntesttoken");
		oNumericValidator.validate("abc", {}, this.oValidationResult);

		assertFalse(this.oValidationResult.bResult);
		assertEquals("i18nErrorMessage", this.oValidationResult.sMessage);
	};
})();
