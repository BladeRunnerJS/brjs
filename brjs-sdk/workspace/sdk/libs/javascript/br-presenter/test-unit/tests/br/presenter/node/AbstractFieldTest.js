AbstractFieldTest = function()
{
};

AbstractFieldTest.inheritMethods = function(fFieldTest)
{
	for(var sMethod in AbstractFieldTest.prototype)
	{
		fFieldTest.prototype[sMethod] = AbstractFieldTest.prototype[sMethod];
	}
};

AbstractFieldTest.prototype._getTestValidator = function()
{
	var fValidator = function(){};
	br.Core.implement(fValidator, br.presenter.validator.Validator);

	fValidator.prototype.validate = function(sText, mConfig, oValidationResult)
	{
		var bIsValid = (sText == "fail") ? false : true;

		oValidationResult.setResult(bIsValid, "only 'pass' is valid");
	};

	return new fValidator();
};

AbstractFieldTest.prototype.test_fieldsContainANumberOfPredefinedProperties = function()
{
	var fField = this._$getFieldClass();
	var oField = new fField();

	assertTrue("1a", oField.label instanceof br.presenter.property.WritableProperty);
	assertTrue("1b", oField.value instanceof br.presenter.property.EditableProperty);
	assertTrue("1c", oField.hasError instanceof br.presenter.property.WritableProperty);
	assertTrue("1d", oField.failureMessage instanceof br.presenter.property.WritableProperty);
};

