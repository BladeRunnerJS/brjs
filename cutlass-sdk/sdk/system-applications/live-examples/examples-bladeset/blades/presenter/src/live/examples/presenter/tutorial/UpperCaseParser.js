live.examples.presenter.tutorial.UpperCaseParser = function() {
};

caplin.implement(live.examples.presenter.tutorial.UpperCaseParser, caplin.core.Parser);

/**
 * Converts all lower case letters to upper case.
 * 
 * @param {Variant} vValue  the input.
 * @param {Map} mAttributes  (unused)
 * @return  the input with all lower case letters replaced with their upper case equivalents.
 * @type  String
 */
live.examples.presenter.tutorial.UpperCaseParser.prototype.parse = function(vValue, mAttributes) {
	return typeof(vValue) == "string" ? vValue.toUpperCase() : vValue
};

live.examples.presenter.tutorial.UpperCaseParser = new live.examples.presenter.tutorial.UpperCaseParser();
