(function() {
	var OptionsNodeList = require('br/presenter/node/OptionsNodeList');
	var ValidMultiSelectionValidator = require('br/presenter/validator/ValidMultiSelectionValidator');
	var ValidationResult = require('br/presenter/validator/ValidationResult');

	ValidMultiSelectionValidatorTest = TestCase("ValidMultiSelectionValidatorTest");

	ValidMultiSelectionValidatorTest.prototype.setUp = function()
	{
		this.oOptions = new OptionsNodeList(["a", "b", "c", "fOoBaR"]);
		this.oValidMultiSelectionValidator = new ValidMultiSelectionValidator(this.oOptions);
	};

	ValidMultiSelectionValidatorTest.prototype.tearDown = function()
	{
		// nothing
	};

	ValidMultiSelectionValidatorTest.prototype.test_cannotConstructValidMultiSelectionValidatorWithoutAnOptionsNodeList = function()
	{
		assertException("1a", function(){
			new ValidMultiSelectionValidator();
		}, br.Errors.INVALID_PARAMETERS);
		assertException("1b", function(){
			new ValidMultiSelectionValidator("a");
		}, br.Errors.INVALID_PARAMETERS);
		assertException("1c", function(){
			new ValidMultiSelectionValidator(["a", "b"]);
		}, br.Errors.INVALID_PARAMETERS);
	};

	ValidMultiSelectionValidatorTest.prototype.test_arraysOfKnownOptionValuesValidateSuccessfully = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidMultiSelectionValidator.validate(["b"], {}, oValidationResult);
		assertTrue("1a", oValidationResult.isValid());

		oValidationResult = new ValidationResult();
		this.oValidMultiSelectionValidator.validate(["a", "c"], {}, oValidationResult);
		assertTrue("1b", oValidationResult.isValid());
	};

	ValidMultiSelectionValidatorTest.prototype.test_nonExistentArraysOfOptionValuesFailValidationByDefault = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidMultiSelectionValidator.validate(["d"], {}, oValidationResult);
		assertFalse("1a", oValidationResult.isValid());

		oValidationResult = new ValidationResult();
		this.oValidMultiSelectionValidator.validate(["c", "d"], {}, oValidationResult);
		assertFalse("1b", oValidationResult.isValid());
	};

	ValidMultiSelectionValidatorTest.prototype.test_weCanAllowInvalidArraysOfSelectionsToPassValidations = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidMultiSelectionValidator.allowInvalidSelections(true);
		this.oValidMultiSelectionValidator.validate(["c", "d"], {}, oValidationResult);
		assertTrue("1a", oValidationResult.isValid());
	};

	ValidMultiSelectionValidatorTest.prototype.test_caseIsIgnoredWhenValidating = function()
	{
		var oValidationResult = new ValidationResult();
		this.oValidMultiSelectionValidator.validate(["FOObar"], {}, oValidationResult);
		assertTrue(oValidationResult.isValid());
	};
})();
