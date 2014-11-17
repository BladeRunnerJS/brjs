ValidSelectionValidatorTest = TestCase("ValidSelectionValidatorTest");

ValidSelectionValidatorTest.prototype.setUp = function()
{
	this.oOptions = new br.presenter.node.OptionsNodeList(["a", "b", "fOoBaR"]);
	this.oValidSelectionValidator = new br.presenter.validator.ValidSelectionValidator(this.oOptions);
};

ValidSelectionValidatorTest.prototype.tearDown = function()
{
	// nothing
};

ValidSelectionValidatorTest.prototype.test_cannotConstructValidSelectionValidatorWithoutAnOptionsNodeList = function()
{
	assertException("1a", function(){
		new br.presenter.validator.ValidSelectionValidator();
	}, br.Errors.INVALID_PARAMETERS);
	assertException("1b", function(){
		new br.presenter.validator.ValidSelectionValidator("a");
	}, br.Errors.INVALID_PARAMETERS);
	assertException("1c", function(){
		new br.presenter.validator.ValidSelectionValidator(["a", "b"]);
	}, br.Errors.INVALID_PARAMETERS);
};

ValidSelectionValidatorTest.prototype.test_knownOptionValuesValidateSuccessfully = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidSelectionValidator.validate("a", {}, oValidationResult);
	assertTrue("1a", oValidationResult.isValid());

	oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidSelectionValidator.validate("b", {}, oValidationResult);
	assertTrue("1b", oValidationResult.isValid());
};

ValidSelectionValidatorTest.prototype.test_nonExistentOptionValuesFailValidationByDefault = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidSelectionValidator.validate("c", {}, oValidationResult);
	assertFalse("1a", oValidationResult.isValid());
};

ValidSelectionValidatorTest.prototype.test_weCanAllowInvalidSelectionsToPassValidations = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidSelectionValidator.allowInvalidSelections(true);
	this.oValidSelectionValidator.validate("c", {}, oValidationResult);
	assertTrue("1a", oValidationResult.isValid());
};

ValidSelectionValidatorTest.prototype.test_caseIsIgnoredWhenValidating = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidSelectionValidator.validate("FOObar", {}, oValidationResult);
	assertTrue(oValidationResult.isValid());
};

ValidSelectionValidatorTest.prototype.test_canValidateNumericOptions = function()
{
	var oNumericOptions = new br.presenter.node.OptionsNodeList([1, 2, 3]);
	var oValidSelectionValidator = new br.presenter.validator.ValidSelectionValidator(oNumericOptions);
	
	var oValidationResult = new br.presenter.validator.ValidationResult();
	oValidSelectionValidator.validate(1, {}, oValidationResult);
	assertTrue(oValidationResult.isValid());
};

ValidSelectionValidatorTest.prototype.test_canValidateNumericOptionsWhenComparisonValueIsZero = function()
{
	var oNumericOptions = new br.presenter.node.OptionsNodeList([0, 1, 2]);
	var oValidSelectionValidator = new br.presenter.validator.ValidSelectionValidator(oNumericOptions);
	
	var oValidationResult = new br.presenter.validator.ValidationResult();
	oValidSelectionValidator.validate(0, {}, oValidationResult);
	assertTrue(oValidationResult.isValid());
};
