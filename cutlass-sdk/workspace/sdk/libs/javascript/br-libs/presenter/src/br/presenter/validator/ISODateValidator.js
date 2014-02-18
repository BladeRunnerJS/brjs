/**
 * @private
 * @class
 * @constructor
 * @implements br.presenter.validator.Validator
 */
br.presenter.validator.ISODateValidator = function()
{
	/** @private */
	this.m_oSplitterRegex = /^(\d{4})-(\d{2})-(\d{2})$/;

	/** @private */
	this.m_oSplitterNoDashesRegex = /^(\d{4})(\d{2})(\d{2})$/;
};

/**
 * @private
 * @param vValue
 * @param mAttributes
 * @param oValidationResult
 */
br.presenter.validator.ISODateValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	var bIsValid = false;
	var i18n = require("br/I18n");
	var sValidationMessage = i18n("br.presenter.validator.invalidISODateFormat", {value: vValue});

	if (vValue === null || vValue === undefined || vValue === "")
	{
		bIsValid = true;
		sValidationMessage = i18n("br.presenter.validator.valueNullUndefinedOrEmptyString");
	}
	else
	{
		if (vValue.match)
		{
			var pFormatMatch = vValue.match(this.m_oSplitterRegex);
			if (!pFormatMatch)
			{
				// Try with regex that has dashes optional
				pFormatMatch = vValue.match(this.m_oSplitterNoDashesRegex);
			}

			if (pFormatMatch)
			{
				// pFormatMatch will look like this:
				// ["YYYY-MM-DD", "YYYY", "MM", "DD"] or ["YYYYMMDD", "YYYY", "MM", "DD"].

				// Convert captured results to numbers for convenience:
				pFormatMatch[1] = Number(pFormatMatch[1]);
				pFormatMatch[2] = Number(pFormatMatch[2]);
				pFormatMatch[3] = Number(pFormatMatch[3]);

				// Use built-in JavaScript date object to check the validity of the value
				var oTempDate = new Date(pFormatMatch[1], pFormatMatch[2]-1, pFormatMatch[3]);

				if( ( oTempDate.getFullYear() === pFormatMatch[1] ) &&
					( oTempDate.getMonth() === (pFormatMatch[2]-1) ) &&
					( oTempDate.getDate() === pFormatMatch[3] ) )
				{
					bIsValid = true;
					sValidationMessage = "";
				}
			}
		}
	}

	oValidationResult.setResult(bIsValid, sValidationMessage);
};

/**
 * @private
 * @param {String} sISODate
 * @type Boolean
 */
br.presenter.validator.ISODateValidator.prototype.isValidISODate = function (sISODate)
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.validate(sISODate, {}, oValidationResult);
	return oValidationResult.isValid();
};

br.Core.implement(br.presenter.validator.ISODateValidator, br.presenter.validator.Validator);