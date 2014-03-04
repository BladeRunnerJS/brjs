br.Core.thirdparty('mock4js');

PropertyTest = TestCase("PropertyTest");

PropertyTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

PropertyTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

PropertyTest.prototype._getTestFormatter = function(){
	
	var fFormatter = function(){};
	br.Core.implement(fFormatter, br.presenter.formatter.Formatter);
	fFormatter.prototype.format = function(sValue, mConfig){
		return sValue + mConfig.c;
	};
	return new fFormatter();
};

PropertyTest.prototype.test_getValueIsUndefinedByDefault = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	assertEquals(undefined, oProperty.getValue());
};

PropertyTest.prototype.test_initialValueCanBePassedInConstructor = function()
{
	var oProperty = new br.presenter.property.WritableProperty("value");
	assertEquals("value", oProperty.getValue());
};

PropertyTest.prototype.test_getValueEqualsSetValue = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	
	oProperty.setValue("1234");
	assertEquals("1a", oProperty.getValue(), "1234");
};

PropertyTest.prototype.test_getFormattedValueIsTheSameAsGetValueIfThereAreNoFormatters = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	
	oProperty.setValue("1234");
	assertEquals("1a", oProperty.getValue(), "1234");
	assertEquals("1a", oProperty.getFormattedValue(), "1234");
};

PropertyTest.prototype.test_weThrowAnExceptionIfTheProvidedFormatterIsNotAnInstanceOfFormatter = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	
	assertException("1a", function(){
		oProperty.addFormatter({});
	}, br.Errors.LEGACY);
};

PropertyTest.prototype.test_getFormattedValueProvidesADefaultAtributeMapIfNotProvided = function()
{
	var oFormatterMock = mock(br.presenter.formatter.Formatter);
	
	// passes through any config we do provide
	oFormatterMock.expects(atLeastOnce()).format("1.23456789", {key:"value"});
	var oProperty = new br.presenter.property.WritableProperty().addFormatter(oFormatterMock.proxy(), {key:"value"}).setValue("1.23456789");
	oProperty.getFormattedValue();
	
	// defaults if not provided
	oFormatterMock.expects(atLeastOnce()).format("1.23456789", {});
	var oProperty = new br.presenter.property.WritableProperty().addFormatter(oFormatterMock.proxy()).setValue("1.23456789");
	oProperty.getFormattedValue();
};

PropertyTest.prototype.test_getFormattedValueReturnsFormattedValue = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	var oFormatter = this._getTestFormatter();
	
	oProperty.addFormatter(oFormatter, {c:"b"}).setValue("a");
	assertEquals("1a", "ab", oProperty.getFormattedValue());
};

PropertyTest.prototype.test_getFormattedValueReturnsFormattedValueUsingMultipleFormatters = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	var oFormatter = this._getTestFormatter();
	
	oProperty.addFormatter(oFormatter, {c:"b"}).addFormatter(oFormatter, {c:"c"}).setValue("a");
	assertEquals("1a", "abc", oProperty.getFormattedValue());
};

PropertyTest.prototype.test_weThrowAnExceptionIfTheProvidedListenerIsNotAnInstanceOfListener = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	
	assertException("1a", function(){
		oProperty.addListener({});
	}, br.Errors.LEGACY);
};

PropertyTest.prototype.test_weCanSuccesfullyAddAndRemoveAListener = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	var oPropertyListener = new br.presenter.property.PropertyListener();
	oProperty.addListener(oPropertyListener);
	oProperty.removeListener(oPropertyListener);
	
};

PropertyTest.prototype.test_weCanAddMultipleListeners = function()
{
	var oProperty = new br.presenter.property.WritableProperty();
	var oPropertyListener1 = new br.presenter.property.PropertyListener();
	var oPropertyListener2 = new br.presenter.property.PropertyListener();
	oProperty.addListener(oPropertyListener1);
	oProperty.addListener(oPropertyListener2);
};

PropertyTest.prototype.test_onPropertyChangedIsInvokedWhenTheValueChanges = function()
{
	var oPropertyListenerMock = mock(br.presenter.property.PropertyListener);
	oPropertyListenerMock.stubs().onPropertyUpdated();

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");

	oProperty.addListener(oPropertyListenerMock.proxy());
	oPropertyListenerMock.expects(once()).onPropertyChanged();
	oProperty.setValue("new value");
};

PropertyTest.prototype.test_onPropertyChangedIsNeverInvokedWhenTheValueIsUpdatedButNotChanged = function()
{
	var oPropertyListenerMock = mock(br.presenter.property.PropertyListener);
	oPropertyListenerMock.stubs().onPropertyUpdated();

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");

	oProperty.addListener(oPropertyListenerMock.proxy());
	oPropertyListenerMock.expects(never()).onPropertyChanged();
	oProperty.setValue("initial value"); // setting to the same value as before
};

PropertyTest.prototype.test_onPropertyChangedIsInvokedForAllListenersWhenTheValueChanges = function()
{
	var oPropertyListenerMock1 = mock(br.presenter.property.PropertyListener);
	var oPropertyListenerMock2 = mock(br.presenter.property.PropertyListener);
	oPropertyListenerMock1.stubs().onPropertyUpdated();
	oPropertyListenerMock2.stubs().onPropertyUpdated();

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");
	
	oProperty.addListener(oPropertyListenerMock1.proxy()).addListener(oPropertyListenerMock2.proxy());
	oPropertyListenerMock1.expects(once()).onPropertyChanged();
	oPropertyListenerMock2.expects(once()).onPropertyChanged();
	oProperty.setValue("new value");
};

PropertyTest.prototype.test_onPropertyChangedCeasesToBeInvokedOnceListenersAreRemoved = function()
{
	var oPropertyListenerMock1 = mock(br.presenter.property.PropertyListener);
	var oPropertyListenerMock2 = mock(br.presenter.property.PropertyListener);
	oPropertyListenerMock1.stubs().onPropertyUpdated();
	oPropertyListenerMock2.stubs().onPropertyUpdated();

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");
	
	oProperty.addListener(oPropertyListenerMock1.proxy()).addListener(oPropertyListenerMock2.proxy());
	oPropertyListenerMock1.expects(once()).onPropertyChanged();
	oPropertyListenerMock2.expects(once()).onPropertyChanged();
	oProperty.setValue("value1");
	
	oProperty.removeListener(oPropertyListenerMock1.proxy());
	oPropertyListenerMock1.expects(never()).onPropertyChanged();
	oPropertyListenerMock2.expects(once()).onPropertyChanged();
	oProperty.setValue("value2");
};

PropertyTest.prototype.test_weCanRequestForTheListenerToInvokeCallbackImmediately = function()
{
	var oPropertyListenerMock = mock(br.presenter.property.PropertyListener);
	var oProperty = new br.presenter.property.WritableProperty();
	
	oPropertyListenerMock.expects(once()).onPropertyUpdated();
	oPropertyListenerMock.expects(once()).onPropertyChanged();
	oProperty.addListener(oPropertyListenerMock.proxy(), true);
};

PropertyTest.prototype._getListenerClass = function()
{
	var fListenerClass = function()
	{
	};
	
	fListenerClass.prototype.invocationMethod = function()
	{
	};
	
	return fListenerClass;
};

PropertyTest.prototype.test_weCanAddAndRemoveAChangeOnlyListener = function()
{
	var oListenerMock = mock(this._getListenerClass());
	var oProperty = new br.presenter.property.WritableProperty();
	var oPropertyListener = oProperty.addChangeListener(oListenerMock.proxy(), "invocationMethod");
	
	oListenerMock.expects(once()).invocationMethod();
	oProperty.setValue("newvalue");
	
	oProperty.removeListener(oPropertyListener);
};

PropertyTest.prototype.test_specifyingANonExistentChangeListenerMethodCausesAnException = function()
{
	var oListenerMock = mock(this._getListenerClass());
	var oProperty = new br.presenter.property.WritableProperty();
	
	assertException("1a", function(){
		oProperty.addChangeListener(oListenerMock.proxy(), "noSuchMethod");
	}, br.Errors.LEGACY);
};

PropertyTest.prototype.test_weCanRequestForTheListenerToInvokeCallbackImmediatelyForChangeOnlyListener = function()
{
	var oListenerMock = mock(this._getListenerClass());
	var oProperty = new br.presenter.property.WritableProperty();
	
	oListenerMock.expects(once()).invocationMethod();
	oProperty.addChangeListener(oListenerMock.proxy(), "invocationMethod", true);
};

PropertyTest.prototype.test_weCanAddAndRemoveAnUpdateOnlyListener = function()
{
	var oListenerMock = mock(this._getListenerClass());
	var oProperty = new br.presenter.property.WritableProperty();
	var oPropertyListener = oProperty.addUpdateListener(oListenerMock.proxy(), "invocationMethod");

	oListenerMock.expects(once()).invocationMethod();
	oProperty.setValue("newvalue");

	oProperty.removeListener(oPropertyListener);
};

PropertyTest.prototype.test_weCanRequestForTheListenerToInvokeCallbackImmediatelyForUpdateOnlyListener = function()
{
	var oListenerMock = mock(this._getListenerClass());
	var oProperty = new br.presenter.property.WritableProperty();

	oListenerMock.expects(once()).invocationMethod();
	oProperty.addUpdateListener(oListenerMock.proxy(), "invocationMethod", true);
};

PropertyTest.prototype.test_instantiatingPropertyWithArrayOfPresentationNodesThrowsAnException = function()
{
	// a Field extends PresentationNode
	var oPresNodeField = new br.presenter.node.Field();
	var oPresNode = new br.presenter.node.PresentationNode();

	assertException("1a", function() {
		var oBadProperty = new br.presenter.property.WritableProperty([oPresNodeField, oPresNode]);
	}, br.Errors.LEGACY);
};

PropertyTest.prototype.test_onlyOnPropertyUpdatedIsInvokedWhenTheValueIsUpdatedButNotChanged = function()
{
	var oPropertyListenerMock = mock(br.presenter.property.PropertyListener);

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");

	oProperty.addListener(oPropertyListenerMock.proxy());
	oPropertyListenerMock.expects(never()).onPropertyChanged();
	oPropertyListenerMock.expects(once()).onPropertyUpdated();
	oProperty.setValue("initial value");
};

PropertyTest.prototype.test_onPropertyUpdatedIsInvokedForAllListenersWhenTheValueIsUpdated = function()
{
	var oPropertyListenerMock1 = mock(br.presenter.property.PropertyListener);
	var oPropertyListenerMock2 = mock(br.presenter.property.PropertyListener);
	oPropertyListenerMock1.stubs().onPropertyChanged();
	oPropertyListenerMock2.stubs().onPropertyChanged();

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");

	oProperty.addListener(oPropertyListenerMock1.proxy()).addListener(oPropertyListenerMock2.proxy());
	oPropertyListenerMock1.expects(once()).onPropertyUpdated();
	oPropertyListenerMock2.expects(once()).onPropertyUpdated();
	oProperty.setValue("initial value");
};

PropertyTest.prototype.test_onPropertyUpdatedCeasesToBeInvokedOnceListenersAreRemoved = function()
{
	var oPropertyListenerMock1 = mock(br.presenter.property.PropertyListener);
	var oPropertyListenerMock2 = mock(br.presenter.property.PropertyListener);
	oPropertyListenerMock1.stubs().onPropertyChanged();
	oPropertyListenerMock2.stubs().onPropertyChanged();

	// no listeners have been added yet, so won't be informed about 'initial value'
	var oProperty = new br.presenter.property.WritableProperty().setValue("initial value");

	oProperty.addListener(oPropertyListenerMock1.proxy()).addListener(oPropertyListenerMock2.proxy());
	oPropertyListenerMock1.expects(once()).onPropertyUpdated();
	oPropertyListenerMock2.expects(once()).onPropertyUpdated();
	oProperty.setValue("value1");

	oProperty.removeListener(oPropertyListenerMock1.proxy());
	oPropertyListenerMock1.expects(never()).onPropertyUpdated();
	oPropertyListenerMock2.expects(once()).onPropertyUpdated();
	oProperty.setValue("value2");
};
