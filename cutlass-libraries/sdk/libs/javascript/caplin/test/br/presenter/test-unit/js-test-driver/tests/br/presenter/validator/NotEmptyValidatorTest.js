NotEmptyValidatorTest = TestCase("NotEmptyValidatorTest");

NotEmptyValidatorTest.prototype.setUp = function() {
	var i18n = require("br/i18n")
	i18n.reset();
	i18n.initialise([{i18ntesttoken: "i18nErrorMessage"}]);
	
	this.oNotEmptyValidator = new br.presenter.validator.NotEmptyValidator("errorMessage");
	
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
	var oNotEmptyValidator =  new br.presenter.validator.NotEmptyValidator("i18ntesttoken");
	
	oNotEmptyValidator.validate("", {}, this.oValidationResult);
	
	assertFalse(this.oValidationResult.bResult);
	assertEquals("i18nErrorMessage", this.oValidationResult.sMessage);
}