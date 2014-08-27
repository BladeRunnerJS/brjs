ISODateValidatorTest = TestCase("ISODateValidatorTest");

ISODateValidatorTest.prototype.setUp = function()
{
	this.oValidator = new br.presenter.validator.ISODateValidator();
};

ISODateValidatorTest.prototype.tearDown = function()
{
	// nothing
};

ISODateValidatorTest.prototype.test_validISODateStringsPassValidation = function()
{
	var pValidDateStrings = ["2000-01-01", "2000-01-31", "2000-12-01", "2000-12-31"];
	for (var i = 0, max = pValidDateStrings.length; i < max; i++)
	{
		var oValidationResult = new br.presenter.validator.ValidationResult();
		this.oValidator.validate(pValidDateStrings[i], {}, oValidationResult);
		assertTrue("Valid YYYY-MM-DD string passes validation", oValidationResult.isValid());
	}
};

ISODateValidatorTest.prototype.test_validISODateStringsWithoutDashesPassValidation = function()
{
	var pValidDateStrings = ["20000101", "20000131", "20001201", "20001231"];
	for (var i = 0, max = pValidDateStrings.length; i < max; i++)
	{
		var oValidationResult = new br.presenter.validator.ValidationResult();
		this.oValidator.validate(pValidDateStrings[i], {}, oValidationResult);
		assertTrue("Valid YYYYMMDD string passes validation", oValidationResult.isValid());
	}
};

ISODateValidatorTest.prototype.test_ISODateStringRegexDoesNotAllowExtraCharacterSuffix = function()
{
	var pValidDateStrings = [
		"2000-01-01s", "2000-01-312", "2000-01-31$",
		"20000101s", "200001312", "20000131$"
	];

	for (var i = 0, max = pValidDateStrings.length; i < max; i++)
	{
		var oValidationResult = new br.presenter.validator.ValidationResult();
		this.oValidator.validate(pValidDateStrings[i], {}, oValidationResult);
		assertFalse("Invalid YYYY-MM-DD/YYYYMMDD string passes validation with suffix", oValidationResult.isValid());
	}
};

ISODateValidatorTest.prototype.test_ISODateStringRegexDoesNotAllowExtraCharacterPrefix = function()
{
	var pValidDateStrings = [
		"s2000-01-01", "%2000-01-31", "$1-2000-01-31",
		"s20000101", "%20000131", "$120000131"
	];
	for (var i = 0, max = pValidDateStrings.length; i < max; i++)
	{
		var oValidationResult = new br.presenter.validator.ValidationResult();
		this.oValidator.validate(pValidDateStrings[i], {}, oValidationResult);
		assertFalse("Invalid YYYY-MM-DD/YYYYMMDD string passes validation with prefix", oValidationResult.isValid());
	}
};

ISODateValidatorTest.prototype.test_invalidISODateStringsRegexDoesNotAllowExtraCharacters = function()
{
	var pValidDateStrings = [
		"2000-01-01s", "2000-01-312",
		"20000101s", "200001312"
	];
	for (var i = 0, max = pValidDateStrings.length; i < max; i++)
	{
		var oValidationResult = new br.presenter.validator.ValidationResult();
		this.oValidator.validate(pValidDateStrings[i], {}, oValidationResult);
		assertFalse("Invalid YYYY-MM-DD/YYYYMMDD string passes validation", oValidationResult.isValid());
	}
};

ISODateValidatorTest.prototype.test_incorrectDatesWithCorrectFormatFailValidation = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidator.validate("2011-02-30", {}, oValidationResult);
	assertFalse("Feb 30th fails validation", oValidationResult.isValid());

	this.oValidator.validate("2011-13-01", {}, oValidationResult);
	assertFalse("13th month fails validation", oValidationResult.isValid());

	this.oValidator.validate("2011-13-01", {}, oValidationResult);
	assertFalse("13th month fails validation", oValidationResult.isValid());

	this.oValidator.validate("2011-01-32", {}, oValidationResult);
	assertFalse("32nd day fails validation", oValidationResult.isValid());

	this.oValidator.validate("1234-56-78", {}, oValidationResult);
	assertFalse("32nd day fails validation", oValidationResult.isValid());
};

ISODateValidatorTest.prototype.test_incorrectDatesWithCorrectFormatWithoutDashesFailValidation = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();

	this.oValidator.validate("20110230", {}, oValidationResult);
	assertFalse("Feb 30th fails validation", oValidationResult.isValid());

	this.oValidator.validate("20111301", {}, oValidationResult);
	assertFalse("13th month fails validation", oValidationResult.isValid());

	this.oValidator.validate("20111301", {}, oValidationResult);
	assertFalse("13th month fails validation", oValidationResult.isValid());

	this.oValidator.validate("20110132", {}, oValidationResult);
	assertFalse("32nd day fails validation", oValidationResult.isValid());

	this.oValidator.validate("12345678", {}, oValidationResult);
	assertFalse("32nd day fails validation", oValidationResult.isValid());
};

ISODateValidatorTest.prototype.test_emptyValuesPassValidation = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidator.validate("", {}, oValidationResult);
	assertTrue("Empty string passes validation", oValidationResult.isValid());

	this.oValidator.validate(null, {}, oValidationResult);
	assertTrue("Null passes validation", oValidationResult.isValid());

	this.oValidator.validate(undefined, {}, oValidationResult);
	assertTrue("Undefined passes validation", oValidationResult.isValid());
};

ISODateValidatorTest.prototype.test_badFormattingFailsValidation = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();

	this.oValidator.validate("20th June 2011", {}, oValidationResult);
	assertFalse("Using worded dates fails validation", oValidationResult.isValid());

	this.oValidator.validate("201130", {}, oValidationResult);
	assertFalse("YYYYMM fails validation", oValidationResult.isValid());

	this.oValidator.validate("2011", {}, oValidationResult);
	assertFalse("YYYY fails validation", oValidationResult.isValid());
};
