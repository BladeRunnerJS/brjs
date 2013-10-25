SnapshotTest = TestCase("SnapshotTest");

SnapshotTest.prototype.test_Apply = function()
{
	var oProperty1 = new br.presenter.property.WritableProperty("p1");
	var oProperty2 = new br.presenter.property.WritableProperty("p2");
	var props = [oProperty1, oProperty2];
	
	var properties = new br.presenter.property.Properties(props);
	var snapshot =  properties.snapshot();
	
	oProperty1.setValue("xxxxxx");
	oProperty2.setValue("yyyyyy");

	snapshot.apply();
	
	assertEquals("p1", oProperty1.getValue());
	assertEquals("p2", oProperty2.getValue());
};

SnapshotTest.prototype.test_ApplyArrays = function()
{
	var oProperty1 = new br.presenter.property.WritableProperty(["a", "b"]);
	
	var properties = new br.presenter.property.Properties([oProperty1]);
	var snapshot =  properties.snapshot();
	
	oProperty1.setValue(["x", "y"]);

	snapshot.apply();
	
	assertEquals(["a", "b"], oProperty1.getValue());
};

SnapshotTest.prototype.test_nonWritablePropertiesAreNotSnapshotted = function()
{
	var oProperty1 = new br.presenter.property.WritableProperty(["a", "b"]);
	var oProperty2 = new br.presenter.property.Property("p2");
	var oProperty3 = new br.presenter.property.EditableProperty("p3");
	
	var properties = new br.presenter.property.Properties([oProperty1, oProperty2, oProperty3]);
	var snapshot =  properties.snapshot();
	
	oProperty1.setValue(["x", "y"]);
	oProperty2._$setInternalValue("p22");
	oProperty3.setValue("p33");
	
	assertEquals(["x", "y"], oProperty1.getValue());
	assertEquals(["p22"], oProperty2.getValue());
	assertEquals(["p33"], oProperty3.getValue());
	
	snapshot.apply();
	
	assertEquals(["a", "b"], oProperty1.getValue());
	assertEquals(["p22"], oProperty2.getValue());
	assertEquals(["p3"], oProperty3.getValue());
};
