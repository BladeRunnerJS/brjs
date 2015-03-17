/**
 * @module br/parsing/LocalisedAmountParser
 */

var topiarist = require('topiarist');
var Parser = require('br/parsing/Parser');

/**
 * @class
 * @alias module:br/parsing/LocalisedAmountParser
 * @implements module:br/parsing/Parser
 * 
 * @classdesc
 * Parses an amount containing a thousands, millions or billions token into a number.
 * 
 * <p><code>LocalisedAmountParser</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "4900000":</p>
 * 
 * <pre>LocalisedAmountParser.parse("4.9MM", {})</pre>
 * 
 * See {@link module:br/formatting/AmountFormatter} for the complementary formatter.
 */
function LocalisedAmountParser() {}

topiarist.implement(LocalisedAmountParser, Parser);

/**
 * Parses an amount containing a thousands, millions or billions token into a number.
 *
 * If the amount does not match, then null is returned.
 * 
 * <p>
 * Attribute Options:
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>thousands</td><td> the token representing thousands (defaults to K)</td></tr>
 * <td>millions</td><td> the token representing millions (defaults to M)</td></tr>
 * <td>billions</td><td> the token representing billions (defaults to B)</td></tr>
 * </table>
 *
 * @param {Variant} sValue  the amount with tokens
 * @param {Object} mAttributes  the map of attributes.
 * @return  the numeric amount, or null if the value was not recognized.
 * @type  String
 */
LocalisedAmountParser.prototype.parse = function(sValue, mAttributes) 
{
	if(typeof sValue != "string")
	{
		return sValue;
	}
	
	if(sValue.length < 1)
	{
		return null;
	}
	
	var nMultiplier = 1;
	var sLastChar = sValue.charAt( (sValue.length - 1));
	if(isNaN(sLastChar))
	{
		nMultiplier = this._getShortcutMultiplier(sLastChar);
		
		if(nMultiplier != null)
		{
			sValue = sValue.substr(0, (sValue.length - 1));		
		}
		else
		{
			return null;
		}
	}

	var oTranslator = require("br/I18n").getTranslator();
	var sValue = oTranslator.parseNumber(sValue);
	
	if(!sValue)
	{
		return null;
	}
	
	var nResult =  sValue * nMultiplier; //coerces the result to a number
	
	return nResult;
};

LocalisedAmountParser.prototype.isSingleUseParser = function() {
	return false;
};

LocalisedAmountParser.prototype._getShortcutMultiplier = function(sShortcutSymbol)
{
	var sToken = "br.parsing.number.formatting.multiplier." + sShortcutSymbol.toLowerCase();
	var oTranslator = require("br/I18n").getTranslator();
	if (oTranslator.tokenExists(sToken)) 
	{
		var multiplier = oTranslator.getMessage(sToken);
		if (!isNaN(multiplier)) 
		{
			return multiplier*1;
		}
	}
	
	return null;
};

/**
 * @private
 */
LocalisedAmountParser.prototype.toString = function() {
	return "br/parsing/LocalisedAmountParser";
};

module.exports = LocalisedAmountParser;
