/**
 * @private
 * @constructor
 * @param {br.presenter.node.OptionsNodeList} oOptions The list of valid options.
 * @implements br.presenter.validator.Validator
 */
br.presenter.validator.ValidMultiSelectionValidator = function(oOptions)
{
	if (!oOptions || !(oOptions instanceof br.presenter.node.OptionsNodeList))
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "You must provide an instance of OptionsNodeList");
	}
	this.m_oOptions = oOptions;
	this.m_bAllowInvalidSelections = false;
};
br.Core.implement(br.presenter.validator.ValidMultiSelectionValidator, br.presenter.validator.Validator);

br.presenter.validator.ValidMultiSelectionValidator.prototype.allowInvalidSelections = function(bAllowInvalidSelections)
{
	this.m_bAllowInvalidSelections = bAllowInvalidSelections;
};

/**
 * @private
 * @see br.presenter.validator.Validator#validate
 */
br.presenter.validator.ValidMultiSelectionValidator.prototype.validate = function(pValues, mAttributes, oValidationResult)
{
	var bIsValid = true;
	var sValidationMessage = "";
	var i18n = require("br/i18n");
	if(this.m_bAllowInvalidSelections)
	{
		sValidationMessage = i18n("br.presenter.validator.invalidSelectionsAllowed");
	}
	else
	{
		var mOptionsAsMap = this._getOptionsAsMap(this.m_oOptions);
		for(var i = 0, l = pValues.length; i < l; ++i)
		{
			var vValue = pValues[i];
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
br.presenter.validator.ValidMultiSelectionValidator.prototype._getOptionsAsMap = function(oOptions)
{
	var oResult = {};
	var pOptions = oOptions.getOptions();
	for(var i = 0; i < pOptions.length; i++)
	{
		oResult[pOptions[i].value.getValue()] = pOptions[i].label.getValue();
	}
	return oResult;
};


