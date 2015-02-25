/**
 * @module br/parsing/ThousandsParser
 */

/**
 * @class
 * @alias module:br/parsing/ThousandsParser
 * @implements module:br/parsing/Parser
 * 
 * @classdesc
 * 
 * Parses an amount and strips any thousands separators.
 * 
 * <p><code>ThousandsParser</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "8987551787.0":</p>
 * 
 * <pre>br.parsing.ThousandsParser.parse("8,987,551,787.0", {})</pre>
 * 
 * <p>The number grouping separator will change per locale. The english separator defaults to "," as in the 
 * example above, see the i18n property br.i18n.number.grouping.separator in other locales for their separators.</p>
 * 
 * See {@link module:br/formatting/ThousandsFormatter} for the complementary formatter.
 */
br.parsing.ThousandsParser = function() {
};

br.Core.implement(br.parsing.ThousandsParser, br.parsing.Parser);

/**
 * Parses an amount, strips any thousands separators and changes the local radix (decimal point) char
 * into a ".".  
 * 
 *  <p>
 * Attribute Options:
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>separator</td><td>  the token representing thousands (defaults to a comma in en_GB locale)</td></tr>
 * </table>
 * 
 * @param {Variant} vValue  the amount.
 * @param {Map} mAttributes  the map of attributes.
 * @return  the amount, with any number grouping separators removed
 * @type  String
 */
br.parsing.ThousandsParser.prototype.parse = function(vValue, mAttributes) {
	//if the value is already a number in js format, then we don't need to parse it
	if(this._isNumeric(vValue)) {
		return vValue;
	}
	var i18n = require("br/I18n");
	var sSeparator = mAttributes["separator"] || i18n("br.i18n.number.grouping.separator");
	if (!vValue) {
		return vValue;
	}
	var result = (vValue).replace(new RegExp("[" + sSeparator + "]", "g"), "");
	
	//turn the localised radix char back into a js native decimal point
	if (result) {
		var radixCharacter = i18n("br.i18n.decimal.radix.character");
		return result.replace(radixCharacter, ".");
	}
	else {
		return null;
	}
};

/**
 * @private
 */
br.parsing.ThousandsParser.prototype.toString = function() {
	return "br.parsing.ThousandsParser";
};

br.parsing.ThousandsParser.prototype._isNumeric = function(value) {
	return !isNaN(parseFloat(value)) && isFinite(value);
}

