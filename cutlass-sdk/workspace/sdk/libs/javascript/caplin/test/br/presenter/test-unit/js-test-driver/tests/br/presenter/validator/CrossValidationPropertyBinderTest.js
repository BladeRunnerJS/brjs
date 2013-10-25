br.thirdparty('mock4js');

CrossValidationPropertyBinderTest = TestCase("CrossValidationPropertyBinderTest");

CrossValidationPropertyBinderTest.prototype.setUp = function()
{
	this.field1 = new br.presenter.node.Field(1);
	this.field2 = new br.presenter.node.Field(2);

	this.property1 = new br.presenter.property.WritableProperty(1);
	this.property2 = new br.presenter.property.WritableProperty(2);

	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

CrossValidationPropertyBinderTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

CrossValidationPropertyBinderTest.prototype._getLessThanTenValidator = function()
{
	var fValidator = function()
	{
		// nothing
	};
	br.implement(fValidator, br.presenter.validator.Validator);

	fValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
	{
		if (vValue < 10)
		{
			oValidationResult.setResult(true, "");
		}
		else
		{
			oValidationResult.setResult(false, "Property is not less than 10.");
		}
	};

	return new fValidator();
};

CrossValidationPropertyBinderTest.prototype._getOrderedPropertiesValidator = function()
{
	var fCPValidator = function()
	{
		// nothing
	};
	br.implement(fCPValidator, br.presenter.validator.CrossPropertyValidator);

	fCPValidator.prototype.validate = function(mProperties, oValidationResult)
	{
		var nFirstValue = mProperties["first"].getValue();
		var nSecondValue = mProperties["second"].getValue();
		if ((typeof nFirstValue !== "number") || (typeof nFirstValue !== "number"))
		{
			oValidationResult.setResult(true, "did not check, properties were not numbers.");
		}
		else if (nFirstValue <= nSecondValue)
		{
			oValidationResult.setResult(true, "");
		}
		else
		{
			oValidationResult.setResult(false, "First property is not less than or equal to the second.");
		}
	};

	return new fCPValidator();
};

CrossValidationPropertyBinderTest.prototype._getSumOfPropertiesValidator = function(nSumToDislike)
{
	var fCPValidator = function()
	{
		// nothing
	};
	br.implement(fCPValidator, br.presenter.validator.CrossPropertyValidator);

	fCPValidator.prototype.validate = function(mProperties, oValidationResult)
	{
		var nFirstValue = mProperties["first"].getValue();
		var nSecondValue = mProperties["second"].getValue();
		if ((nFirstValue + nSecondValue) !== nSumToDislike)
		{
			oValidationResult.setResult(true, "");
		}
		else
		{
			oValidationResult.setResult(false, "Property values should not sum to " + nSumToDislike + ".");
		}
	};

	return new fCPValidator();
};

CrossValidationPropertyBinderTest.prototype.test_unbindingACrossValidatorStopsErrors = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	// confirm
	this.field1.value.setValue(5);
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "First property is not less than or equal to the second.");

	this.field1.value.setValue(1);
	assertFalse(this.field1.hasError.getValue());

	br.presenter.validator.CrossValidationPropertyBinder.unbindValidator(nCVId);
	this.field1.value.setValue(5);
	assertFalse(this.field1.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_changingANonEditablePropertyToBeCrossInvalidMakesFieldInvalid = function()
{
	var mProperties = {
		"first": this.property1,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.property1.setValue(5);
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");
};

CrossValidationPropertyBinderTest.prototype.test_canMakeCrossValidationSucceedByChangingANonEditableProperty = function()
{
	var mProperties = {
		"first": this.property1,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field2.value.setValue(0);
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");

	this.property1.setValue(-1); // should now be valid
	assertFalse(this.field2.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_changingAFieldValueToBeCrossInvalidAffectsOtherFields = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field2.value.setValue(0);
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");

	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "First property is not less than or equal to the second.");
};

CrossValidationPropertyBinderTest.prototype.test_whenThereIsAlreadyACrossValidationErrorChangingAnotherFieldValueStillCausesError = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field1.value.setValue(5);
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "First property is not less than or equal to the second.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");

	this.field2.value.setValue(2);
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "First property is not less than or equal to the second.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");

};

CrossValidationPropertyBinderTest.prototype.test_canChangeAnInvalidFieldValueToMakeTheCrossPropertyValidatorSucceed = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field1.value.setValue(5);
	assertTrue(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());

	this.field1.value.setValue(0); // should now be valid
	assertFalse(this.field1.hasError.getValue());
	assertFalse(this.field2.hasError.getValue());

};

CrossValidationPropertyBinderTest.prototype.test_whenOneFieldCausesACrossValidationErrorCanChangeAnotherFieldToMakeItSucceed = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field2.value.setValue(0);
	assertTrue(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());

	this.field1.value.setValue(-1); // should now be valid
	assertFalse(this.field1.hasError.getValue());
	assertFalse(this.field2.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_whenOneFieldCausesACrossValidationErrorCanChangeAnotherFieldToSomethingAlsoInvalidStillCausesError = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field2.value.setValue(0);
	assertTrue(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());

	this.field1.value.setValue(2); // no different, still invalid
	assertTrue(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_orderingOfTwoCrossPropertyValidatorsOnTheSamePropertiesIsRespected = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oSumCrossValidator = this._getSumOfPropertiesValidator(10);
	var oOrderingCrossValidator = this._getOrderedPropertiesValidator();
	var nSCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oSumCrossValidator);
	var nOCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oOrderingCrossValidator);

	this.field1.value.setValue(8); // makes both validators fail (8 + 2 = 10, and 8 not less than or equal to 2)
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "Property values should not sum to 10.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "Property values should not sum to 10.");

	this.field2.value.setValue(9); // should now be valid for both validators
	assertFalse(this.field1.hasError.getValue());
	assertFalse(this.field2.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_anInvalidValueInOnePropertyTriggersCrossValidationOnOtherProperty = function()
{
	var mProperties = {
		"first": this.field1.value,
		"second": this.field2.value
	};

	var oValidator = this._getLessThanTenValidator();
	this.field1.value.addValidator(oValidator);
	var oCrossValidator = this._getOrderedPropertiesValidator();
	var nSCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties, oCrossValidator);

	this.field1.value.setValue(11);
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "Property is not less than 10.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");
};

CrossValidationPropertyBinderTest.prototype.test_twoCrossPropertyValidatorsThatHaveACommonFieldBehaveWellTogether = function()
{
	var oCommonField = new br.presenter.node.Field(2);
	var mProperties1 = {
		"first": this.field1.value,
		"second": oCommonField.value
	};

	var mProperties2 = {
		"first": oCommonField.value,
		"second": this.field2.value
	};

	var oSumCrossValidator = this._getSumOfPropertiesValidator(10);
	var oOrderingCrossValidator = this._getOrderedPropertiesValidator();
	var nSCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties1, oSumCrossValidator);
	var nOCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties2, oOrderingCrossValidator);

	oCommonField.value.setValue(9); // will make both validators fail
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "Property values should not sum to 10.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");
	assertTrue(oCommonField.hasError.getValue());
	assertEquals(oCommonField.failureMessage.getValue(), "Property values should not sum to 10."); // respectful of validator ordering

	oCommonField.value.setValue(8); // fixes one validation error but not the other
	assertFalse(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");
	assertTrue(oCommonField.hasError.getValue());
	assertEquals(oCommonField.failureMessage.getValue(), "First property is not less than or equal to the second.");

	oCommonField.value.setValue(1); // fixes both
	assertFalse(this.field1.hasError.getValue());
	assertFalse(this.field2.hasError.getValue());
	assertFalse(oCommonField.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_twoCrossPropertyValidatorsThatHaveACommonPropertyBehaveWellTogether = function()
{
	var oCommonProperty = new br.presenter.property.WritableProperty(2);
	var mProperties1 = {
		"first": this.field1.value,
		"second": oCommonProperty
	};

	var mProperties2 = {
		"first": oCommonProperty,
		"second": this.field2.value
	};

	var oSumCrossValidator = this._getSumOfPropertiesValidator(10);
	var oOrderingCrossValidator = this._getOrderedPropertiesValidator();
	var nSCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties1, oSumCrossValidator);
	var nOCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties2, oOrderingCrossValidator);

	oCommonProperty.setValue(9); // will make both validators fail
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "Property values should not sum to 10.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");

	oCommonProperty.setValue(8); // fixes one validation error but not the other
	assertFalse(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");

	oCommonProperty.setValue(1); // fixes both
	assertFalse(this.field1.hasError.getValue());
	assertFalse(this.field2.hasError.getValue());
};

CrossValidationPropertyBinderTest.prototype.test_aNormalValidatorsErrorOnAPropertyCommonToTwoCrossValidatorsCausesCrossValidationErrorsOnTheOtherProperties = function()
{
	var oCommonField = new br.presenter.node.Field(2);
	var mProperties1 = {
		"first": this.field1.value,
		"second": oCommonField.value
	};

	var mProperties2 = {
		"first": oCommonField.value,
		"second": this.field2.value
	};

	var oValidator = this._getLessThanTenValidator();
	oCommonField.value.addValidator(oValidator);
	var oSumCrossValidator = this._getSumOfPropertiesValidator(12);
	var oOrderingCrossValidator = this._getOrderedPropertiesValidator();
	var nSCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties1, oSumCrossValidator);
	var nOCVId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator(mProperties2, oOrderingCrossValidator);

	oCommonField.value.setValue(11); // will make all validators fail
	assertTrue(this.field1.hasError.getValue());
	assertEquals(this.field1.failureMessage.getValue(), "Property values should not sum to 12.");
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");
	assertTrue(oCommonField.hasError.getValue());
	assertEquals(oCommonField.failureMessage.getValue(), "Property is not less than 10.");

	oCommonField.value.setValue(8); // fixes one validation error but not the other
	assertFalse(this.field1.hasError.getValue());
	assertTrue(this.field2.hasError.getValue());
	assertEquals(this.field2.failureMessage.getValue(), "First property is not less than or equal to the second.");
	assertTrue(oCommonField.hasError.getValue());
	assertEquals(oCommonField.failureMessage.getValue(), "First property is not less than or equal to the second.");

	oCommonField.value.setValue(1); // fixes both
	assertFalse(this.field1.hasError.getValue());
	assertFalse(this.field2.hasError.getValue());
	assertFalse(oCommonField.hasError.getValue());
};
