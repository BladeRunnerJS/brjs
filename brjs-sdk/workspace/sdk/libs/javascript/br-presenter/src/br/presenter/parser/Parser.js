/**
 * @module br/presenter/parser/Parser
 */

var Errors = require('br/Errors');

/**
 * This is an interface and should not be constructed.
 * @classdesc
 * @interface
 * Parses a value according to some attributes.
 */
br.presenter.parser.Parser = function() {};

/**
 * Parses a value and either returns the parsed value upon success, otherwise returns <code>null</code>.
 *
 * @param {String} sValue the unparsed value.  May be null.
 * @param {Object} mAttributes the attributes appropriate to the <code>Parser</code> implementation.
 * @return the parsed value, or <code>null</code> if the value was not recognised.
 */
br.presenter.parser.Parser.prototype.parse = function(sValue, mAttributes)
{
	throw new Errors.UnimplementedInterfaceError("Parser.parse() has not been implemented.");
};

/**
 * Allows parsers that should only run a single time, and that should not repeatedly re-parse their own output.
 * 
 * <p>This method is optional. Parsers that don't implement it are not considered to be single-use parsers by
 * default.</p>
 * 
 * @type boolean
 */
br.presenter.parser.Parser.prototype.isSingleUseParser = function()
{
	return false;
};
