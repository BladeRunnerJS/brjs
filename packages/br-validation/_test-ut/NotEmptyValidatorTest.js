require('br-presenter/_resources-test-at/html/test-form.html');
require('../_resources-test-ut/en.properties');
(function() {
	var NotEmptyValidator = require('br-presenter/validator/NotEmptyValidator');

	NotEmptyValidatorTest = TestCase("NotEmptyValidatorTest");

	NotEmptyValidatorTest.prototype.setUp = function() {
		this.oNotEmptyValidator = new NotEmptyValidator("errorMessage");

		this.oValidationResult =
		{
			setResult: function(bResult, sMessage)
			{
				this.bResult = bResult;
				this.sMessage = sMessage;
			}
		}
	}

	NotEmptyValidatorTest.prototype.test_NotEmptyPass = function() {
		this.oNotEmptyValidator.validate(1, {}, this.oValidationResult);

		assertTrue(this.oValidationResult.bResult);
	}

	NotEmptyValidatorTest.prototype.test_EmptyFails = function() {
		this.oNotEmptyValidator.validate("", {}, this.oValidationResult);

		assertFalse(this.oValidationResult.bResult);
		assertEquals("errorMessage", this.oValidationResult.sMessage);
	}

	NotEmptyValidatorTest.prototype.test_EmptyShowsi18nErrorMessage = function() {
		var oNotEmptyValidator =  new NotEmptyValidator("br.validation.i18ntesttoken");

		oNotEmptyValidator.validate("", {}, this.oValidationResult);

		assertFalse(this.oValidationResult.bResult);
		assertEquals("i18nErrorMessage", this.oValidationResult.sMessage);
	}
})();
