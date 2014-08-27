'use strict';

/**
 * @module br/util/RegExp
 */

/**
 * @class
 * @alias module:br/util/RegExp
 */
function RegExpUtil() {
}

RegExpUtil.SPECIALS = ['/', '.', '*', '+', '?', '|', '(', ')', '[', ']', '{', '}', '\\'];
RegExpUtil.ESCAPE_REGEXP = new RegExp('(\\' + RegExpUtil.SPECIALS.join('|\\') + ')', 'g');

RegExpUtil.escape = function(expression) {
	return expression.replace(RegExpUtil.ESCAPE_REGEXP, '$1');
};

module.exports = RegExpUtil;
