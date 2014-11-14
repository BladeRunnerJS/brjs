/**
 * @module br/presenter/validator/ValidSelectionValidator
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/validator/ValidSelectionValidator
 * @implements module:br/presenter/validator/Validator
 * 
 * @param {module:br/presenter/node/OptionsNodeList} oOptions The list of valid options.
 */
br.presenter.validator.ValidSelectionValidator = function(oOptions)
{
	if (!oOptions || !(oOptions instanceof br.presenter.node.OptionsNodeList))
	{
		throw new br.Errors.InvalidParametersError("You must provide an instance of OptionsNodeList");
	}
	this.m_oOptions = oOptions;
	this.m_bAllowInvalidSelections = false;
};
br.Core.implement(br.presenter.validator.ValidSelectionValidator, br.presenter.validator.Validator);

br.presenter.validator.ValidSelectionValidator.prototype.allowInvalidSelections = function(bAllowInvalidSelections)
{
	this.m_bAllowInvalidSelections = bAllowInvalidSelections;
};

br.presenter.validator.ValidSelectionValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	// default results, unless value is found in the selection options
	var bIsValid = false;
	var i18n = require("br/I18n");
	var sValidationMessage = i18n("br.presenter.validator.invalidSelection", {value: vValue});

	if(this.m_bAllowInvalidSelections)
	{
		bIsValid = true;
		sValidationMessage = i18n("br.presenter.validator.invalidSelectionsAllowed");
	}
	else
	{
		var pOptions = this.m_oOptions.getOptions();
		for(var i = 0; i < pOptions.length; i++)
		{
			var vOptionValue = pOptions[i].value.getValue().toUpperCase ? pOptions[i].value.getValue().toUpperCase() : pOptions[i].value.getValue();
			var vAssertionValue = vValue.toUpperCase ? vValue.toUpperCase() : vValue;
			if(vValue && vOptionValue === vAssertionValue)
			{
				bIsValid = true;
				sValidationMessage = "";
				break;
			}
		}
	}
	oValidationResult.setResult(bIsValid, sValidationMessage);
};

