/**
 * @module br/parsing/Parser
 */

var Errors = require('br/Errors');

/**
 * This is an interface and should not be constructed.
 *
 * @class
 * @interface
 * @alias module:br/parsing/Parser
 *
 * @classdesc
 * Parsers allow you to convert arbitrary user-input into some normalized form, and so provide additional
 * flexibility to the end-user as to how data entry is performed. Parsers can be designed to work in 
 * co-operation with each other, allowing complex user input to be normalized using a series of 
 * progressive transformations.

 * For example, the input -(3 + (4 * 5) + 2) could be normalized to -25 via the following steps:

 * -(3 + (4 * 5) + 2) -> -(3 + (20) + 2) (operator-parser)
 * -(3 + (20) + 2) -> -(3 + 20 + 2) (bracket-parser)
 * -(3 + 20 + 2) -> -(23 + 2) (operator-parser)
 * -(23 + 2) -> -(25) (operator-parser)
 * -(25) -> -25 (bracket-parser)
 * Notice here how the input is slowly transformed into it's a final value by repeatedly applying
 * a couple of simple parser transformations, and that parsing only stops when none of the parsers 
 * are able to further simplify the input text.

 * For this to work, parsers must obey the following two rules:

 * 1. A parser's output must be closer to normal form than it's input.
 * 2. Parsers must be able to disambiguate the meaning of the text they are parsing (e.g. whether '12/01/2000' is the UK date "12th of January, 2000" or the US date "December the 1st, 2000"?).
 */
function Parser() {
}

/**
 * Parses a value and either returns the parsed value upon success, otherwise returns <code>null</code>.
 *
 * @param {String} sValue the unparsed value.  May be null.
 * @param {Object} mAttributes the attributes appropriate to the <code>Parser</code> implementation.
 * @return the parsed value, or <code>null</code> if the value was not recognised.
 */
Parser.prototype.parse = function(sValue, mAttributes) {
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
Parser.prototype.isSingleUseParser = function() {
	return false;
};

module.exports = Parser;
