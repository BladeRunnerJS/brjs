br.Core.thirdparty('mock4js');

PropertiesTest = TestCase("PropertiesTest");

PropertiesTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

PropertiesTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

PropertiesTest.prototype.test_Construction = function()
{
	var oProperty1 = new br.presenter.property.Property("p1");
	var oProperty2 = new br.presenter.property.Property("p2");
	var props = [oProperty1, oProperty2];
	
	var propertiesA = new br.presenter.property.Properties(props);
	assertEquals(2, propertiesA.getSize());
	
};

PropertiesTest.prototype.test_AddProperty = function()
{
	var oProperty1 = new br.presenter.property.Property("p1");
	var oProperty2 = new br.presenter.property.Property("p2");
	var propertiesA = new br.presenter.property.Properties([oProperty1]);
	
	propertiesA.add(oProperty2);
	assertEquals(2, propertiesA.getSize());
};

PropertiesTest.prototype.test_AddProperties = function()
{
	var oProperty1 = new br.presenter.property.Property("p1");
	var oProperty2 = new br.presenter.property.Property("p2");
	var propertiesA = new br.presenter.property.Properties([oProperty1]);
	var propertiesB = new br.presenter.property.Properties([oProperty2]);
	
	propertiesA.add(propertiesB);
	assertEquals(2, propertiesA.getSize());
};

PropertiesTest.prototype.test_setValueCanBeInvokedOnAllWritableProperties = function()
{
	var oProperty = new br.presenter.property.Property(1);
	var oWritableProperty = new br.presenter.property.WritableProperty(1);
	var oEditableProperty = new br.presenter.property.WritableProperty(1);
	var oProperties = new br.presenter.property.Properties([oProperty, oWritableProperty, oEditableProperty]);
	
	assertEquals("1a", 1, oProperty.getValue());
	assertEquals("1b", 1, oWritableProperty.getValue());
	assertEquals("1c", 1, oEditableProperty.getValue());
	
	oProperties.setValue(2);
	
	assertEquals("2a", 1, oProperty.getValue());
	assertEquals("2b", 2, oWritableProperty.getValue());
	assertEquals("2c", 2, oEditableProperty.getValue());
};

PropertiesTest.prototype.test_snapshottingWorksForAllWritableProperties = function()
{
	var oProperty = new br.presenter.property.Property(1);
	var oWritableProperty = new br.presenter.property.WritableProperty(1);
	var oEditableProperty = new br.presenter.property.WritableProperty(1);
	var oProperties = new br.presenter.property.Properties([oProperty, oWritableProperty, oEditableProperty]);
	
	assertEquals("1a", 1, oProperty.getValue());
	assertEquals("1b", 1, oWritableProperty.getValue());
	assertEquals("1c", 1, oEditableProperty.getValue());
	
	var oSnapshot = oProperties.snapshot();
	
	assertEquals("2a", 1, oProperty.getValue());
	assertEquals("2b", 1, oWritableProperty.getValue());
	assertEquals("2c", 1, oEditableProperty.getValue());
	
	oProperty._$setInternalValue(2);
	oWritableProperty.setValue(2);
	oEditableProperty.setValue(2);
	
	assertEquals("3a", 2, oProperty.getValue());
	assertEquals("3b", 2, oWritableProperty.getValue());
	assertEquals("3c", 2, oEditableProperty.getValue());
	
	oSnapshot.apply();
	
	assertEquals("4a", 2, oProperty.getValue());
	assertEquals("4b", 1, oWritableProperty.getValue());
	assertEquals("4c", 1, oEditableProperty.getValue());
};

PropertiesTest.prototype.test_addListenerIsProxiedThroughToAllProperties = function()
{
	var oMockProperty1 = mock(br.presenter.property.Property);
	var oMockProperty2 = mock(br.presenter.property.Property);
	var oProperties = new br.presenter.property.Properties([oMockProperty1.proxy(), oMockProperty2.proxy()]);
	var oMockPropertyListener = mock(br.presenter.property.PropertyListener);
	
	oMockProperty1.expects(once()).addListener(oMockPropertyListener.proxy(), false);
	oMockProperty2.expects(once()).addListener(oMockPropertyListener.proxy(), false);
	oProperties.addListener(oMockPropertyListener.proxy(), false);
};

PropertiesTest.prototype.test_removeAllListenersIsProxiedThroughToAllProperties = function()
{
	var oMockProperty1 = mock(br.presenter.property.Property);
	var oMockProperty2 = mock(br.presenter.property.Property);
	var oProperties = new br.presenter.property.Properties([oMockProperty1.proxy(), oMockProperty2.proxy()]);
	var oMockPropertyListener = mock(br.presenter.property.PropertyListener);
	
	oMockProperty1.expects(once()).addListener(oMockPropertyListener.proxy(), false);
	oMockProperty2.expects(once()).addListener(oMockPropertyListener.proxy(), false);
	oProperties.addListener(oMockPropertyListener.proxy(), false);
	
	Mock4JS.verifyAllMocks();
	
	oMockProperty1.expects(once()).removeAllListeners();
	oMockProperty2.expects(once()).removeAllListeners();
	oProperties.removeAllListeners();
};

PropertiesTest.prototype.test_addChangeListenerIsProxiedThroughToAllProperties = function()
{
	var oMockProperty1 = mock(br.presenter.property.Property);
	var oMockProperty2 = mock(br.presenter.property.Property);
	var oProperties = new br.presenter.property.Properties([oMockProperty1.proxy(), oMockProperty2.proxy()]);
	
	oMockProperty1.expects(once()).addListener(ANYTHING, false);
	oMockProperty2.expects(once()).addListener(ANYTHING, false);
	oProperties.addChangeListener({onChange:function(){}}, "onChange", false);
};

PropertiesTest.prototype.test_addUpdateListenerIsProxiedThroughToAllProperties = function()
{
	var oMockProperty1 = mock(br.presenter.property.Property);
	var oMockProperty2 = mock(br.presenter.property.Property);
	var oProperties = new br.presenter.property.Properties([oMockProperty1.proxy(), oMockProperty2.proxy()]);
	
	oMockProperty1.expects(once()).addListener(ANYTHING, false);
	oMockProperty2.expects(once()).addListener(ANYTHING, false);
	oProperties.addUpdateListener({onUpdate:function(){}}, "onUpdate", false);
};

PropertiesTest.prototype.test_addListenerWithImmediateNotificationOnlyNotifiesOnce = function()
{
	var oMockProperty1 = mock(br.presenter.property.Property);
	var oMockProperty2 = mock(br.presenter.property.Property);
	var oProperties = new br.presenter.property.Properties([oMockProperty1.proxy(), oMockProperty2.proxy()]);
	var oMockPropertyListener = mock(br.presenter.property.PropertyListener);
	
	oMockProperty1.expects(once()).addListener(oMockPropertyListener.proxy(), false);
	oMockProperty2.expects(once()).addListener(oMockPropertyListener.proxy(), true);
	oProperties.addListener(oMockPropertyListener.proxy(), true);
};
