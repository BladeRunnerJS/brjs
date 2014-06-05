ISODatePropertyTest = TestCase("ISODatePropertyTest");

ISODatePropertyTest.prototype.setUp = function()
{
	this.pValidDateStrings = ["2000-01-01", "2000-01-31", "2000-12-01", "2000-12-31"];
	this.pInvalidDateStrings = ["1234-56-78", "2000-01-00", "2000-01-32", "2000-00-01", "2000-13-01"];
};

ISODatePropertyTest.prototype.tearDown = function()
{
	// nothing
};

ISODatePropertyTest.prototype.test_canConstructISODatePropertyWithValidISODateStrings = function()
{
	for (var i = 0, max = this.pValidDateStrings.length; i < max; i++)
	{
		var oISODateProperty = new br.presenter.property.ISODateProperty(this.pValidDateStrings[i]);
		assertEquals(this.pValidDateStrings[i], oISODateProperty.getValue());
	}
};

ISODatePropertyTest.prototype.test_canConstructISODatePropertyWithNullUndefinedOrEmptyValues = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty("");
	assertEquals("", oISODateProperty.getValue());

	oISODateProperty = new br.presenter.property.ISODateProperty(null);
	assertNull("", oISODateProperty.getValue());

	oISODateProperty = new br.presenter.property.ISODateProperty(undefined);
	assertUndefined(oISODateProperty.getValue());

	oISODateProperty = new br.presenter.property.ISODateProperty();
	assertUndefined(oISODateProperty.getValue());
};

ISODatePropertyTest.prototype.test_constructingISODatePropertiesWithInvalidDatesThrowsException = function()
{
	var oThis = this;
	for (var i = 0, max = oThis.pInvalidDateStrings.length; i < max; i++)
	{
		assertException("1a", function(){
			var oISODateProperty = new br.presenter.property.ISODateProperty(oThis.pInvalidDateStrings[i]);
		}, br.Errors.INVALID_PARAMETERS);
	}
};

ISODatePropertyTest.prototype.test_canSetISODatePropertyWithValidISODateStrings = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty();
	for (var i = 0, max = this.pValidDateStrings.length; i < max; i++)
	{
		oISODateProperty.setValue(this.pValidDateStrings[i]);
		assertEquals(this.pValidDateStrings[i], oISODateProperty.getValue());
	}
};

ISODatePropertyTest.prototype.test_canSetISODatePropertyWithNullUndefinedOrEmptyValues = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty();

	oISODateProperty.setValue("");
	assertEquals("", oISODateProperty.getValue());

	oISODateProperty.setValue(null);
	assertNull("", oISODateProperty.getValue());

	oISODateProperty.setValue(undefined);
	assertUndefined(oISODateProperty.getValue());

	oISODateProperty.setValue();
	assertUndefined(oISODateProperty.getValue());
};

ISODatePropertyTest.prototype.test_cannotSetISODatePropertiesWithInvalidDatesThrowsException = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty();

	var oThis = this;
	for (var i = 0, max = this.pInvalidDateStrings.length; i < max; i++)
	{
		assertException("1a", function(){
			oISODateProperty.setValue(oThis.pInvalidDateStrings[i]);
		}, br.Errors.INVALID_PARAMETERS);
	}
};

ISODatePropertyTest.prototype.test_canGetCorrectDateObjectFromISODateProperty = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty("2001-03-03");
	var oDate = oISODateProperty.getDateValue();
	assertEquals(2001, oDate.getFullYear());
	assertEquals(2, oDate.getMonth()); // the range for months is 0-11, as opposed to 1-12
	assertEquals(3, oDate.getDate());
};

ISODatePropertyTest.prototype.test_canConstructISODatePropertyWithADateObject = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty(new Date(2000, 2, 3));
	assertEquals("2000-03-03", oISODateProperty.getValue());
};

ISODatePropertyTest.prototype.test_canSetISODateWithADateObject = function()
{
	var oISODateProperty = new br.presenter.property.ISODateProperty();
	oISODateProperty.setValue(new Date(2000, 2, 3));
	assertEquals("2000-03-03", oISODateProperty.getValue());
};
