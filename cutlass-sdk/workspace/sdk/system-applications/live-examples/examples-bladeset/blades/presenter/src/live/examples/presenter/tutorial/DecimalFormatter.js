live.examples.presenter.tutorial.DecimalFormatter = function() {
};

caplin.implement(live.examples.presenter.tutorial.DecimalFormatter, caplin.core.Formatter);

/**
 * Formats the value to the specified number of decimal places.
 * @param vValue the value to format
 * @param mAttributes {'dp':x} where x is the number of decimal places to format to
 * @returns the number formatted to the required number of decimal places
 */
live.examples.presenter.tutorial.DecimalFormatter.prototype.format = function(vValue, mAttributes) {
	return caplin.core.Number.isNumber(vValue) ? caplin.core.Number.toFixed(vValue, mAttributes["dp"]) : vValue;
};

live.examples.presenter.tutorial.DecimalFormatter.prototype.toString = function() {
	return "live.examples.presenter.tutorial.DecimalFormatter";
};

live.examples.presenter.tutorial.DecimalFormatter = new live.examples.presenter.tutorial.DecimalFormatter();