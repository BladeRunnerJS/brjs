ValidMultiSelectionValidatorTest = TestCase("ValidMultiSelectionValidatorTest");

ValidMultiSelectionValidatorTest.prototype.setUp = function()
{
	this.oOptions = new br.presenter.node.OptionsNodeList(["a", "b", "c"]);
	this.oValidMultiSelectionValidator = new br.presenter.validator.ValidMultiSelectionValidator(this.oOptions);
};

ValidMultiSelectionValidatorTest.prototype.tearDown = function()
{
	// nothing
};

ValidMultiSelectionValidatorTest.prototype.test_cannotConstructValidMultiSelectionValidatorWithoutAnOptionsNodeList = function()
{
	assertException("1a", function(){
		new br.presenter.validator.ValidMultiSelectionValidator();
	}, br.Errors.LEGACY);
	assertException("1b", function(){
		new br.presenter.validator.ValidMultiSelectionValidator("a");
	}, br.Errors.LEGACY);
	assertException("1c", function(){
		new br.presenter.validator.ValidMultiSelectionValidator(["a", "b"]);
	}, br.Errors.LEGACY);
};

ValidMultiSelectionValidatorTest.prototype.test_arraysOfKnownOptionValuesValidateSuccessfully = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidMultiSelectionValidator.validate(["b"], {}, oValidationResult);
	assertTrue("1a", oValidationResult.isValid());

	oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidMultiSelectionValidator.validate(["a", "c"], {}, oValidationResult);
	assertTrue("1b", oValidationResult.isValid());
};

ValidMultiSelectionValidatorTest.prototype.test_nonExistentArraysOfOptionValuesFailValidationByDefault = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidMultiSelectionValidator.validate(["d"], {}, oValidationResult);
	assertFalse("1a", oValidationResult.isValid());

	oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidMultiSelectionValidator.validate(["c", "d"], {}, oValidationResult);
	assertFalse("1b", oValidationResult.isValid());
};

ValidMultiSelectionValidatorTest.prototype.test_weCanAllowInvalidArraysOfSelectionsToPassValidations = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oValidMultiSelectionValidator.allowInvalidSelections(true);
	this.oValidMultiSelectionValidator.validate(["c", "d"], {}, oValidationResult);
	assertTrue("1a", oValidationResult.isValid());
};
