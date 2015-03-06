/**
 * @module br/presenter/validator/ValidMultiSelectionValidator
 */

var Validator = require('br/validation/Validator');
var Errors = require('br/Errors');

/**
 * @private
 * @class
 * @alias module:br/presenter/validator/ValidMultiSelectionValidator
 * @implements module:br/validation/Validator
 *
 * @param {module:br/presenter/node/OptionsNodeList} oOptions The list of valid options.
 */
function ValidMultiSelectionValidator(oOptions)
{
	if (!oOptions || !(oOptions instanceof br.presenter.node.OptionsNodeList))
	{
		throw new Errors.InvalidParametersError("You must provide an instance of OptionsNodeList");
	}
	this.m_oOptions = oOptions;
	this.m_bAllowInvalidSelections = false;
}
br.Core.implement(ValidMultiSelectionValidator, Validator);

ValidMultiSelectionValidator.prototype.allowInvalidSelections = function(bAllowInvalidSelections)
{
	this.m_bAllowInvalidSelections = bAllowInvalidSelections;
};

/**
 * @private
 * @see br.validation.Validator#validate
 */
ValidMultiSelectionValidator.prototype.validate = function(pValues, mAttributes, oValidationResult)
{
	var bIsValid = true;
	var sValidationMessage = "";
	var i18n = require("br/I18n");
	if(this.m_bAllowInvalidSelections)
	{
		sValidationMessage = i18n("br.presenter.validator.invalidSelectionsAllowed");
	}
	else
	{
		var mOptionsAsMap = this._getOptionsAsMap(this.m_oOptions);
		for(var i = 0, l = pValues.length; i < l; ++i)
		{
			var vValue = pValues[i].toUpperCase();
			if(!mOptionsAsMap[vValue])
			{
				bIsValid = false;
				sValidationMessage = i18n("br.presenter.validator.invalidSelection", {value: vValue});
				break;
			}
		}
	}
	oValidationResult.setResult(bIsValid, sValidationMessage);
};

/**
 * @private
 * @param oOptions
 */
ValidMultiSelectionValidator.prototype._getOptionsAsMap = function(oOptions)
{
	var oResult = {};
	var pOptions = oOptions.getOptions();
	for(var i = 0; i < pOptions.length; i++)
	{
		oResult[pOptions[i].value.getValue().toUpperCase()] = pOptions[i].label.getValue();
	}
	return oResult;
};

module.exports = ValidMultiSelectionValidator;
