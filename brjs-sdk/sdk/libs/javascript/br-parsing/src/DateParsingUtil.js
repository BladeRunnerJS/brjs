/**
 * @module br/parsing/DateParsingUtil
 */

var moment = require('momentjs');

module.exports = {

	/**
	 * @static
	 * @param {string|Date} vDate The date to parse
	 * @param {string} sDateFormat The input format
	 * @param {object} [mAttributes] A map of options
	 * @param {boolean} [mAttributes.endOfUnit=false] Whether to parse ambiguous dates to the end of a month or year
	 * @returns {Date}
	 */
	parse: function(vDate, sDateFormat, mAttributes) {
		if (!vDate){
			return null;
		}
		
		if (vDate instanceof Date) {
			sDateFormat = "javascript";
		} else if (!sDateFormat) {
			sDateFormat = "DD-MM-YYYY HH:mm:ss";
		}

		switch (sDateFormat) {
			case "java":
				var oDate = new Date();
				oDate.setTime(Number(vDate));
				return oDate;
			case "javascript":
				return vDate;
			case "U":
				return moment(vDate*1000).toDate();
			default:
				var oMoment = moment(String(vDate), sDateFormat);
				if (mAttributes && mAttributes.endOfUnit === true && sDateFormat.toLowerCase().indexOf('d') === -1) {
					oMoment.endOf(sDateFormat === 'YYYY' ? 'year' : 'month');
				}
				var sValidationString = oMoment.format(sDateFormat);
				return (sValidationString.toLowerCase() == String(vDate).toLowerCase()) ? oMoment.toDate() : null;
		}
	}
	
};
