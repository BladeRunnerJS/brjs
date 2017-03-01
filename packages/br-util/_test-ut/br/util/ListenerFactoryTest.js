(function() {
	require('jsmockito');

	var ListenerFactory = require('br-util/ListenerFactory');

	var ListenerFactoryTest = TestCase("ListenerFactoryTest");

	ListenerFactoryTest.prototype.setUp = function()
	{
		JsHamcrest.Integration.JsTestDriver();
		JsMockito.Integration.JsTestDriver();
		
		this.m_fListenerInterface = function() {};
		this.m_fListenerInterface.prototype.onEvent1 = function() {};
		this.m_fListenerInterface.prototype.onEvent2 = function() {};
		
		this.m_fTargetClass = function() {};
		this.m_fTargetClass.prototype.targetMethod = function() {};
	};

	ListenerFactoryTest.prototype.test_creatingAFactoryForAnEventThatDoesntExistCausesAnException = function()
	{
		var oThis = this;
		assertException("1a", function() {
			new ListenerFactory(oThis.m_fListenerInterface, "no-such-event");
		}, "TypeError");
	};

	ListenerFactoryTest.prototype.test_createdListenersAreOfTheRightType = function()
	{
		var oListenerFactory = new ListenerFactory(this.m_fListenerInterface, "onEvent1");
		var oListener = oListenerFactory.createListener(new this.m_fTargetClass(), function() {});
		
		assertTrue("1a", oListener instanceof this.m_fListenerInterface);
	};

	ListenerFactoryTest.prototype.test_invokingTheSpecifiedEventCausesItToBeProxiedThrough = function()
	{
		var callback = spy(function() {});
		var oListenerFactory = new ListenerFactory(this.m_fListenerInterface, "onEvent1");
		var oListener = oListenerFactory.createListener(callback);
		
		oListener.onEvent1("abc");
		verify(callback)('abc');
	};

	ListenerFactoryTest.prototype.test_invokingOneOfTheOtherEventsHasNoEffect = function()
	{
		var callback = spy(function() {});
		var oListenerFactory = new ListenerFactory(this.m_fListenerInterface, "onEvent1");
		var oListener = oListenerFactory.createListener(callback);
		
		oListener.onEvent2();
		verify(callback, never())(anything());
	};
})();
