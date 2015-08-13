(function() {
    var ControlPluginComponentLifecycleListener = require("br/presenter/view/knockout/ControlPluginComponentLifecycleListener");
    var ControlAdaptor = require("br/presenter/control/ControlAdaptor");
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

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
        var oMockControlAdaptor = mock(ControlAdaptor);
        var oControlPluginComponentLifecycleListener = new ControlPluginComponentLifecycleListener(oMockControlAdaptor.proxy());
        
        oMockControlAdaptor.expects(once()).destroy();
        
        oControlPluginComponentLifecycleListener.onClose();
    };
})();
