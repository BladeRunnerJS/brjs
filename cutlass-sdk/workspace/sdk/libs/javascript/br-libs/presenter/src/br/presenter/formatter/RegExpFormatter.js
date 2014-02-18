/**
 * @class
 * 
 * Transforms a string using a standard JavaScript regular expression.
 * <p/>
 * <code>RegExpFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following examples which evaluate to "8987551787.0" and "Buy USD" respectively:
 * <p/>
 * <code>br.presenter.formatter.RegExpFormatter.format("8987551787.0", { match:"[A-Z]" })</code><br/>
 * <code>br.presenter.formatter.RegExpFormatter.format("Buy.USD", { match:"(\\.", replace:" " })</code>
 * 
 * @singleton
 *
 * @implements br.presenter.formatter.Formatter
 */
br.presenter.formatter.RegExpFormatter = function()
{
	this.m_oRegExps = {};
};

/**
 * Transforms a string using a standard JavaScript regular expression.
 * 
 * By default, only the first match is substituted with the replacement string (unless the "g" option is supplied)
 * and the match is case sensitive (unless the "i" option is supplied).
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
 * <td>match</td><td>  string to match (a standard JavaScript RegExp).</td></tr>
 * <tr><td>replace</td><td>  string to use as replacement (optional, defaults to the empty string).</td></tr>
 * <tr><td>flags</td><td>  regular expression flags (optional, standard JavaScript RegExp flags).</td></tr>
 * </table>
 * 
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  the map of attributes.
 * @return  the string, converted by the regular expression.
 * @type  String
 */
br.presenter.formatter.RegExpFormatter.prototype.format = function(vValue, mAttributes) {
	if (typeof(vValue) == "string") {
		var oSearch = this.getRegExp(mAttributes["match"], mAttributes["flags"]);
		var sReplace = mAttributes["replace"] != null ? mAttributes["replace"] : "$&";
		vValue = vValue.replace(oSearch, sReplace);
	}
	return vValue;
};

/**
 * @private
 */
br.presenter.formatter.RegExpFormatter.prototype.getRegExp = function(sMatch, sFlags) {
	if (this.m_oRegExps[sMatch] == null) {
		this.m_oRegExps[sMatch] = {};
		if (this.m_oRegExps[sMatch][sFlags] == null) {
			this.m_oRegExps[sMatch][sFlags] = new RegExp(br.util.RegExp.escape(sMatch), sFlags);
		}
	}
	return this.m_oRegExps[sMatch][sFlags];
};

/**
 * @private
 */
br.presenter.formatter.RegExpFormatter.prototype.toString = function() {
	return "br.presenter.formatter.RegExpFormatter";
};

br.Core.implement(br.presenter.formatter.RegExpFormatter, br.presenter.formatter.Formatter);