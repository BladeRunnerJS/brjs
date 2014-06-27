BladesetNameValidatorTest = TestCase("BladesetNameValidatorTest");

BladesetNameValidatorTest.prototype.setUp = function()
{
	this.m_oValidator = new brjs.dashboard.app.model.dialog.validator.BladesetNameValidator();
};

BladesetNameValidatorTest.prototype.isValid = function(sBladesetName)
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.m_oValidator.validate(sBladesetName, {}, oValidationResult);
	
	return oValidationResult.isValid();
};

BladesetNameValidatorTest.prototype.testBladesetNameCanOnlyBeLowerCaseAlphas = function()
{
	assertFalse("1a", this.isValid("A"));
	assertFalse("1b", this.isValid("aBc"));
	assertFalse("1c", this.isValid("1a"));
	
	assertTrue("2a", this.isValid("a"));
	assertTrue("2b", this.isValid("abc"));
	assertTrue("2c", this.isValid("a1"));
};

BladesetNameValidatorTest.prototype.testThatEmptyStringIsAValidBladesetName = function()
{
	assertTrue("1a", this.isValid(""));
};

BladesetNameValidatorTest.prototype.testStringWithSpacesIsInvalid = function()
{
	assertFalse("1a", this.isValid("bla bla"));
};

