'use strict';

function RegExpUtil() {
}

RegExpUtil.SPECIALS = ['/', '.', '*', '+', '?', '|', '(', ')', '[', ']', '{', '}', '\\'];
RegExpUtil.ESCAPE_REGEXP = new RegExp('(\\' + RegExpUtil.SPECIALS.join('|\\') + ')', 'g');

RegExpUtil.escape = function(expression) {
	return expression.replace(RegExpUtil.ESCAPE_REGEXP, '$1');
};

module.exports = RegExpUtil;
