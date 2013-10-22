novobank.example.DemoCrossPropertyValidator = function()
{
};
caplin.implement(novobank.example.DemoCrossPropertyValidator, caplin.presenter.validator.CrossPropertyValidator);

// This is the only method required by the CrossPropertyValidator interface:
novobank.example.DemoCrossPropertyValidator.prototype.validate = function(mProperties, oValidationResult)
{
	// Default validation results
	var bIsValid = false;
	var sFailureMessage = "The amount should be between the minimum and maximum values";
	
	// This validator is expecting the following mappings to be available in mProperties.
	// N.B. dot-notation can be used for conciseness.
	var oMin = mProperties["minimum"];
	var oMax = mProperties["maximum"];
	var oValue = mProperties["value"];
	
	// This is a guard against the case when the values are actually strings as opposed
	// to numbers and comparing them would give incorrect results e.g. "100" instead 100
	var oMin = Number(oMin.getValue());
	var oMax = Number(oMax.getValue());
	var oValue = Number(oValue.getValue());
	
	// This is the check for "min <= value <= max"
	if ((oMin <= oValue) && (oValue <= oMax))
	{
		bIsValid = true;
		sFailureMessage = "";
	}
	oValidationResult.setResult(bIsValid, sFailureMessage);
};

novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.node.Field(100);
	this.amount.label.setValue("Amount");
	this.min = new caplin.presenter.node.Field(50);
	this.min.label.setValue("Minimum amount");
	this.max = new caplin.presenter.node.Field(200);
	this.max.label.setValue("Maximum amount");
	
	// Here we construct the necessary property map for the validator. Note that we
	// reference the "value" properties of the fields, not the fields themselves, and 
	// that the map keys match those expected by the cross validator.
	var mProperties = {
		"value": this.amount.value,
		"minimum": this.min.value,
		"maximum": this.max.value
	};
	
	caplin.presenter.validator.CrossValidationPropertyBinder.bindValidator(
		mProperties,
		new novobank.example.DemoCrossPropertyValidator() // We pass a newly created cross validator to the binder
	);
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
