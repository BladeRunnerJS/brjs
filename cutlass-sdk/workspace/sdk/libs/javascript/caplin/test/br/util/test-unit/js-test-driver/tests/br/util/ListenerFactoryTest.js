br.thirdparty('mock4js');

ListenerFactoryTest = TestCase("ListenerFactoryTest");

ListenerFactoryTest.prototype.setUp = function()
{
	this.m_fListenerInterface = function() {};
	this.m_fListenerInterface.prototype.onEvent1 = function() {};
	this.m_fListenerInterface.prototype.onEvent2 = function() {};
	
	this.m_fTargetClass = function() {};
	this.m_fTargetClass.prototype.targetMethod = function() {};
	
	Mock4JS.addMockSupport(window);
};

ListenerFactoryTest.prototype.tearDown = function()
{
	Mock4JS.verifyAndClearAllMocks();
};

ListenerFactoryTest.prototype.test_creatingAFactoryForAnEventThatDoesntExistCausesAnException = function()
{
	var oThis = this;
	assertException("1a", function() {
		new br.util.ListenerFactory(oThis.m_fListenerInterface, "no-such-event");
	}, "TypeError");
};

ListenerFactoryTest.prototype.test_creatingAListenerWithANonExistentMethodOnTheTargetCausesException = function()
{
	var oThis = this;
	assertException("Non-existent target method causes exception", function() {
		var oFactory = new br.util.ListenerFactory(oThis.m_fListenerInterface, "onEvent1");
		oFactory.createListener(new oThis.m_fTargetClass(), "no-such-method");
	}, "TypeError");
};

ListenerFactoryTest.prototype.test_createdListenersAreOfTheRightType = function()
{
	var oListenerFactory = new br.util.ListenerFactory(this.m_fListenerInterface, "onEvent1");
	var oListener = oListenerFactory.createListener(new this.m_fTargetClass(), "targetMethod");
	
	assertTrue("1a", oListener instanceof this.m_fListenerInterface);
};

ListenerFactoryTest.prototype.test_invokingTheSpecifiedEventCausesItToBeProxiedThrough = function()
{
	var oTargetMock = mock(this.m_fTargetClass);
	var oListenerFactory = new br.util.ListenerFactory(this.m_fListenerInterface, "onEvent1");
	var oListener = oListenerFactory.createListener(oTargetMock.proxy(), "targetMethod");
	
	oTargetMock.expects(once()).targetMethod("abc");
	oListener.onEvent1("abc");
};

ListenerFactoryTest.prototype.test_invokingOneOfTheOtherEventsHasNoEffect = function()
{
	var oTargetMock = mock(this.m_fTargetClass);
	var oListenerFactory = new br.util.ListenerFactory(this.m_fListenerInterface, "onEvent1");
	var oListener = oListenerFactory.createListener(oTargetMock.proxy(), "targetMethod");
	
	oTargetMock.expects(never()).targetMethod();
	oListener.onEvent2();
};
