/**
 * @class
 * 
 * Parses an amount containing a thousands, millions or billions token into a number.
 * <p/>
 * <code>LocalisedAmountParser</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "4900000"
 * <p/>
 * <code>br.presenter.parser.LocalisedAmountParser.parse("4.9MM", {})</code>
 * <p/>
 * See {@link br.presenter.formatter.AmountFormatter} for the complementary formatter.
 * 
 * @implements br.presenter.parser.Parser
 * @singleton
 */
br.presenter.parser.LocalisedAmountParser = function() {
};

br.Core.implement(br.presenter.parser.LocalisedAmountParser, br.presenter.parser.Parser);

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
br.presenter.parser.LocalisedAmountParser.prototype.parse = function(sValue, mAttributes) 
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

br.presenter.parser.LocalisedAmountParser.prototype._getShortcutMultiplier = function(sShortcutSymbol)
{
	var sToken = "br.presenter.number.formatting.multiplier." + sShortcutSymbol.toLowerCase();
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
br.presenter.parser.LocalisedAmountParser.prototype.toString = function() {
	return "br.presenter.parser.LocalisedAmountParser";
};
