AliasTest = TestCase("AliasTest");

AliasTest.prototype.setUp = function()
{
	// nothing
};

AliasTest.prototype.tearDown = function()
{
	// nothing
};

AliasTest.prototype._getFormatter = function()
{
	var fFormatter = function(){};
	br.Core.implement(fFormatter, br.presenter.formatter.Formatter);
	fFormatter.prototype.format = function(vValue, mAttributes)
	{
		return vValue.toUpperCase();
	}
	return new fFormatter();
}

AliasTest.prototype.test_canConstructAnAliasWithAPropertyInstance = function()
{
	var oProperty = new br.presenter.property.WritableProperty("test");
	var oAlias = new br.presenter.property.Alias(oProperty);
	assertEquals("test", oAlias.getValue());
};

AliasTest.prototype.test_cannotConstructAnAliasWithANonProperty = function()
{
	assertException("Non-property throws exception", function(){
		var oAlias = new br.presenter.property.Alias("test");
	}, br.Errors.INVALID_PARAMETERS);
};

AliasTest.prototype.test_changesInValueOfAliasedPropertyAreReflectedByTheAlias = function()
{
	var oProperty = new br.presenter.property.WritableProperty("test1");
	var oAlias = new br.presenter.property.Alias(oProperty);
	assertEquals("test1", oAlias.getValue());

	oProperty.setValue("test2");
	assertEquals("test2", oAlias.getValue());
};

AliasTest.prototype.test_anAliasRespectsTheFormattersOnItsWrappedProperty = function()
{
	var oProperty = new br.presenter.property.WritableProperty("test");
	oProperty.addFormatter(this._getFormatter(), {});
	var oAlias = new br.presenter.property.Alias(oProperty);

	assertEquals("TEST", oAlias.getFormattedValue());
};
