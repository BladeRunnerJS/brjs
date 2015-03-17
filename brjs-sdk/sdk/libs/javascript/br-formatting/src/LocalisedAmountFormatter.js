/**
 * @module br/formatting/LocalisedAmountFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');

/**
 * @class
 * @alias module:br/formatting/LocalisedAmountFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Formats a number into a localised string representation.
 * <p/>
 * <code>LocalisedAmountFormatter</code> Formats a number to a configuarble number of decimal placess
 * according to current locale of browser. It automatically detects the current locale of the the
 * browser and uses that to format numbers. For example<br/>
 *  1234567.89 = 1,234,567.89 in english locale<br/>
 * 	1234567.89 = 1.234.567,89 in french locale<br/>
 * It is typically used with Presenter, but can be invoked programmatically:
 * <p/>
 * <code>br.formatting.LocalisedAmountFormatter.format(1234567890, {})</code>
 * <code>br.formatting.LocalisedAmountFormatter.format(1234567890, {dp: 4})</code>
 */
function LocalisedAmountFormatter() {}

topiarist.implement(LocalisedAmountFormatter, Formatter);

/**
 * Formats a number into an localised string representation.
 *
 * @param {Variant} vValue  the numeric amount (Number type).
 * 	if a string is passed it is returned unchanged.
 * @param {Map} mAttributes  a list of attributes, as specified here and in.
 * 		dp: number of decinal places to be displayed. Rounding occurs according
 * 		to standard javascript behaviour.
 * @return  the tokenized amount.
 * @type  String
 */
LocalisedAmountFormatter.prototype.format = function(vValue, mAttributes) {

	//the field may want to display a message like "please enter " so
	if (typeof vValue == "string") {
		return vValue;
	}

	if(typeof vValue != "number"){
		throw "LocalisedAmountFormatter.format(value): value must be numeric";
	}

	if(vValue < 0){
		throw "LocalisedAmountFormatter.format(value): value must be positive ";
	}


	var oAmount = new Number(vValue);
	var decimalPlaces = mAttributes.dp || 0;

	var oRounded = new Number(oAmount.toFixed(decimalPlaces));
	var result = oRounded.toLocaleString();
	return result;
};


/**
 * @private
 */
LocalisedAmountFormatter.prototype.toString = function() {
	return "br.formatting.LocalisedAmountFormatter";
};

module.exports = LocalisedAmountFormatter;
