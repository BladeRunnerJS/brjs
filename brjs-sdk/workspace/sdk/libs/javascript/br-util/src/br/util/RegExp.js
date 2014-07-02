br.util.RegExp = function() {
};

br.util.RegExp.SPECIALS = ["/", ".", "*", "+", "?", "|", "(", ")", "[", "]", "{", "}", "\\"];
br.util.RegExp.ESCAPE_REGEXP = new RegExp("(\\" + br.util.RegExp.SPECIALS.join("|\\") + ")", "g");

br.util.RegExp.escape = function(sRegExp) {
	return sRegExp.replace(br.util.RegExp.ESCAPE_REGEXP, "$1");
};
