/**
 * @module br/presenter/validator/DateRangeCrossPropertyValidator
 */

var CrossPropertyValidator = require('br/presenter/validator/CrossPropertyValidator');

/**
 * @private
 * @class
 * @alias module:br/presenter/validator/DateRangeCrossPropertyValidator
 */
function DateRangeCrossPropertyValidator()
{
	// nothing
}
br.Core.implement(DateRangeCrossPropertyValidator, CrossPropertyValidator);

/**
 * @private
 */
DateRangeCrossPropertyValidator.prototype.validate = function(mProperties, oValidationResult)
{
	var bIsValid = true;
	var sValidationMessage = "";

	var sDate = mProperties.selectedDate.getValue();

	if (sDate !== null && sDate !== undefined && sDate !== "")
	{
		var oDate = new Date(Number(sDate.substr(0, 4)), Number(sDate.substr(5, 2))-1, Number(sDate.substr(8, 2)));
		var oStart = mProperties.startDate.getDateValue();
		var oEnd = mProperties.endDate.getDateValue();

		var bIsLessThanStart = false;
		if (oStart)
		{
			bIsLessThanStart = (oDate.getTime() < oStart.getTime());
		}
		var bIsGreaterThanEnd = false;
		if (oEnd)
		{
			bIsGreaterThanEnd = (oDate.getTime() > oEnd.getTime());
		}

		if(bIsGreaterThanEnd || bIsLessThanStart)
		{
			bIsValid = false;
			sValidationMessage = "Cross validation of the selected date, start date and end date did not pass";
		}
	}
	oValidationResult.setResult(bIsValid, sValidationMessage);
};

module.exports = DateRangeCrossPropertyValidator;
