NumericValidatorTest = TestCase("NumericValidatorTest");

NumericValidatorTest.prototype.setUp = function() {
	this.oNumericValidator = new br.presenter.validator.NumericValidator("errorMessage");
	
	this.oValidationResult = 
	{
		setResult: function(bResult, sMessage)
		{
			this.bResult = bResult;
			this.sMessage = sMessage;
		}
	}
}


NumericValidatorTest.prototype.test_ValidNumberPass = function() {
	this.oNumericValidator.validate(1, {}, this.oValidationResult);
	
	assertTrue(this.oValidationResult.bResult);
}

NumericValidatorTest.prototype.test_InvalidNumber = function() {
	this.oNumericValidator.validate("1a", {}, this.oValidationResult);
	
	assertFalse(this.oValidationResult.bResult);
	assertEquals("errorMessage", this.oValidationResult.sMessage);
}

NumericValidatorTest.prototype.testInvalidNumberShowsi18nErrorMessage = function() {
	var oNumericValidator =  new br.presenter.validator.NumericValidator("br.presenter.i18ntesttoken");
	
	oNumericValidator.validate("1a", {}, this.oValidationResult);
	
	assertFalse(this.oValidationResult.bResult);
	assertEquals("i18nErrorMessage", this.oValidationResult.sMessage);
}