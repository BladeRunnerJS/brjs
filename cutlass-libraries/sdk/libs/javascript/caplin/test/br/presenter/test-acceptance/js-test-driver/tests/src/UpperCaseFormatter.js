br.presenter.testing.UpperCaseFormatter = function()
{
	// nothing
};
br.implement(br.presenter.testing.UpperCaseFormatter, br.presenter.formatter.Formatter);

br.presenter.testing.UpperCaseFormatter.prototype.format = function(vValue, mAttributes)
{
	return vValue.toUpperCase();
};

/**
 * @private
 */
br.presenter.testing.UpperCaseFormatter.prototype.toString = function()
{
	return "br.presenter.testing.UpperCaseFormatter";
};

br.presenter.testing.UpperCaseFormatter = new br.presenter.testing.UpperCaseFormatter();
