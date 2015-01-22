DisplayFieldTest = TestCase("DisplayFieldTest");

DisplayFieldTest.prototype.test_defaultValuesCanBePassedInViaConstructor = function()
{
	var oDisplayField = new br.presenter.node.DisplayField();
	assertEquals("1a", "", oDisplayField.label.getValue());
	assertEquals("1b", undefined, oDisplayField.value.getValue());
	
	var oDisplayField = new br.presenter.node.DisplayField("value");
	oDisplayField.label.setValue("label");
	assertEquals("2a", "label", oDisplayField.label.getValue());
	assertEquals("2b", "value", oDisplayField.value.getValue());
};

DisplayFieldTest.prototype.test_nonEditablePropertyCanBePassedForValueInConstructor = function() {
	var oDisplayField = new br.presenter.node.DisplayField(new br.presenter.property.WritableProperty("value"));
	assertEquals("Expect DisplayField has label ''", "", oDisplayField.label.getValue());
	assertEquals("Expect DisplayField has value 'value'", "value", oDisplayField.value.getValue());
};
