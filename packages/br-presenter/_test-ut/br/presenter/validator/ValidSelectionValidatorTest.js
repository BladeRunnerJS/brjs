(function() {
    var Errors = require("br/Errors");
    var OptionsNodeList = require('br-presenter/node/OptionsNodeList');
    var ValidSelectionValidator = require('br-presenter/validator/ValidSelectionValidator');
    var ValidationResult = require('br-presenter/validator/ValidationResult');

    ValidSelectionValidatorTest = TestCase("ValidSelectionValidatorTest");

    ValidSelectionValidatorTest.prototype.setUp = function()
	{
		this.oOptions = new OptionsNodeList(["a", "b", "fOoBaR"]);
		this.oValidSelectionValidator = new ValidSelectionValidator(this.oOptions);
	};

    ValidSelectionValidatorTest.prototype.tearDown = function()
	{
		// nothing
	};

    ValidSelectionValidatorTest.prototype.test_cannotConstructValidSelectionValidatorWithoutAnOptionsNodeList = function()
	{
		assertException("1a", function(){
			new ValidSelectionValidator();
		}, Errors.INVALID_PARAMETERS);
		assertException("1b", function(){
			new ValidSelectionValidator("a");
		}, Errors.INVALID_PARAMETERS);
		assertException("1c", function(){
			new ValidSelectionValidator(["a", "b"]);
		}, Errors.INVALID_PARAMETERS);
	};

    ValidSelectionValidatorTest.prototype.test_knownOptionValuesValidateSuccessfully = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidSelectionValidator.validate("a", {}, oValidationResult);
		assertTrue("1a", oValidationResult.isValid());

		oValidationResult = new ValidationResult();
		this.oValidSelectionValidator.validate("b", {}, oValidationResult);
		assertTrue("1b", oValidationResult.isValid());
	};

    ValidSelectionValidatorTest.prototype.test_nonExistentOptionValuesFailValidationByDefault = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidSelectionValidator.validate("c", {}, oValidationResult);
		assertFalse("1a", oValidationResult.isValid());
	};

    ValidSelectionValidatorTest.prototype.test_canCorrectlyValidateWhenValueIsUndefined = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidSelectionValidator.validate(undefined, {}, oValidationResult);
		assertFalse("1a", oValidationResult.isValid());
	};

    ValidSelectionValidatorTest.prototype.test_weCanAllowInvalidSelectionsToPassValidations = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidSelectionValidator.allowInvalidSelections(true);
		this.oValidSelectionValidator.validate("c", {}, oValidationResult);
		assertTrue("1a", oValidationResult.isValid());
	};

    ValidSelectionValidatorTest.prototype.test_caseIsIgnoredWhenValidating = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidSelectionValidator.validate("FOObar", {}, oValidationResult);
		assertTrue(oValidationResult.isValid());
	};

    ValidSelectionValidatorTest.prototype.test_canValidateNumericOptions = function()
	{
		var oNumericOptions = new OptionsNodeList([1, 2, 3]);
		var oValidSelectionValidator = new ValidSelectionValidator(oNumericOptions);

		var oValidationResult = new ValidationResult();
		oValidSelectionValidator.validate(1, {}, oValidationResult);
		assertTrue(oValidationResult.isValid());
	};

    ValidSelectionValidatorTest.prototype.test_canValidateNumericOptionsWhenComparisonValueIsZero = function()
	{
		var oNumericOptions = new OptionsNodeList([0, 1, 2]);
		var oValidSelectionValidator = new ValidSelectionValidator(oNumericOptions);

		var oValidationResult = new ValidationResult();
		oValidSelectionValidator.validate(0, {}, oValidationResult);
		assertTrue(oValidationResult.isValid());
	};
})();
