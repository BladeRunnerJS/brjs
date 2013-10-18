br.thirdparty('mock4js');

ControlPluginComponentLifecycleListenerTest = TestCase("ControlPluginComponentLifecycleListenerTest");

ControlPluginComponentLifecycleListenerTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

ControlPluginComponentLifecycleListenerTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

ControlPluginComponentLifecycleListenerTest.prototype.test_destroyIsCalledWhenOnCloseIsCalled = function()
{
	var oMockControlAdaptor = mock(br.presenter.control.ControlAdaptor);
	var oControlPluginComponentLifecycleListener = new br.presenter.view.knockout.ControlPluginComponentLifecycleListener(oMockControlAdaptor.proxy());
	
	oMockControlAdaptor.expects(once()).destroy();
	
	oControlPluginComponentLifecycleListener.onClose();
};
