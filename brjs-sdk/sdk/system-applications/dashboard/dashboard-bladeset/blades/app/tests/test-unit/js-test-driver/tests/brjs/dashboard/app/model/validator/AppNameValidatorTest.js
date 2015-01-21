AppNameValidatorTest = TestCase("AppNameValidatorTest");

AppNameValidatorTest.prototype.setUp = function()
{
	this.m_oValidator = new brjs.dashboard.app.model.dialog.validator.AppNameValidator();
};

AppNameValidatorTest.prototype.isValid = function(sAppName)
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.m_oValidator.validate(sAppName, {}, oValidationResult);
	
	
	return oValidationResult.isValid();
};

AppNameValidatorTest.prototype.testAppNameCanBeLowerCaseOrUpperCaseAlphas = function()
{
	assertTrue("1a", this.isValid("a"));
	assertTrue("1b", this.isValid("abc"));
	
	assertTrue("2a", this.isValid("A"));
	assertTrue("2b", this.isValid("aBc"));
	
	assertTrue("3a", this.isValid("a1"));
	assertTrue("3b", this.isValid("1a"));
};

AppNameValidatorTest.prototype.testThatEmptyStringIsNotAValidAppName = function()
{
	assertTrue("1a", this.isValid(""));
};

AppNameValidatorTest.prototype.testStringWithSpacesIsInvalid = function()
{
	assertFalse("1a", this.isValid("bla bla"));
};

