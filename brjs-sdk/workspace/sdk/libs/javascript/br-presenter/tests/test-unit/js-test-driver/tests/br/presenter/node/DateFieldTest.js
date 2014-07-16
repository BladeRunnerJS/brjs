DateFieldTest = TestCase("DateFieldTest");
AbstractFieldTest.inheritMethods(DateFieldTest);

DateFieldTest.prototype._$getFieldClass = function()
{
	return br.presenter.node.DateField;
};

DateFieldTest.prototype.test_dateFieldsCanHaveValidDates = function()
{
	var oDateField = new br.presenter.node.DateField("2000-01-01");
	assertFalse("1a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-01-31");
	assertFalse("2a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-12-01");
	assertFalse("3a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-12-31");
	assertFalse("4a", oDateField.hasError.getValue());
};

DateFieldTest.prototype.test_canConstructDateFieldsWithDateObjects = function()
{
	var oDateField = new br.presenter.node.DateField(new Date(2000, 0, 1));
	assertEquals("1a", "2000-01-01", oDateField.value.getValue());
	assertFalse("2a", oDateField.hasError.getValue());
};

DateFieldTest.prototype.test_dateFieldsCanHaveNullUndefinedOrEmptyValues = function()
{
	var oDateField = new br.presenter.node.DateField("");
	assertFalse("1a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField(null);
	assertFalse("2a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField(undefined);
	assertFalse("3a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField();
	assertFalse("4a", oDateField.hasError.getValue());
};

DateFieldTest.prototype.test_dateFieldsThatContainNonStringsHaveValidationErrors = function()
{
	var oDateField = new br.presenter.node.DateField(23);
	assertTrue("1a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField({});
	assertTrue("2a", oDateField.hasError.getValue());
};

DateFieldTest.prototype.test_dateFieldsWithInvalidDatesHaveValidationErrors = function()
{
	var oDateField = new br.presenter.node.DateField("1234-56-78");
	assertTrue("1a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-01-00");
	assertTrue("2a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-01-32");
	assertTrue("3a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-00-01");
	assertTrue("4a", oDateField.hasError.getValue());
	
	oDateField = new br.presenter.node.DateField("2000-13-01");
	assertTrue("5a", oDateField.hasError.getValue());
};

DateFieldTest.prototype.test_constructingDateFieldWithStartAndEndDateTheWrongWayAroundThrowsException = function()
{
	assertException("1a", function(){
		var oDateField = new br.presenter.node.DateField(null,"2001-01-01","2000-01-01");
	}, br.Errors.INVALID_PARAMETERS);
};
