br.Core.thirdparty('mock4js');

ControlPluginTest = TestCase("ControlPluginTest");

ControlPluginTest.prototype.setUp = function() 
{
	br.presenter.component.PresenterComponent._initializePlugins();

	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
	
	this.originalControlAdaptorFactory = br.presenter.control.ControlAdaptorFactory;
	this.mockControlAdaptorFactory = Mock4JS.mockObject(br.presenter.control.ControlAdaptorFactory);
	br.presenter.control.ControlAdaptorFactory = this.mockControlAdaptorFactory.proxy();

	this.oPresenterComponent = {
		'addLifeCycleListener':function() {
		},
		
		'isViewAttached':function() {
			return false;
		}
	};
	var self = this;
	this.allBindingsAccessor = function() {
		return {
			controlOptions: {"color":"pink", "font":"bold"},
			controlNode : self.oPresenterComponent
		};
	};
	this.oViewModel = { 
		__oPresenterComponent : self.oPresenterComponent
	}
	
	this.mockControlAdaptor = mock(br.presenter.control.ControlAdaptor);
	var controlAdaptor = this.mockControlAdaptor.proxy();
	this.mockControlAdaptorFactory.stubs().createControlAdaptor("testClass").will(returnValue(controlAdaptor));
	
	this.mockControlAdaptor.stubs().setOptions(ANYTHING);
	this.mockControlAdaptor.stubs().setPresentationNode(ANYTHING);
	this.mockControlAdaptor.stubs().setElement(ANYTHING);
	this.mockControlAdaptor.stubs().onViewReady();
};

ControlPluginTest.prototype.tearDown = function() 
{
	br.presenter.control.ControlAdaptorFactory = this.originalControlAdaptorFactory;
	Mock4JS.verifyAllMocks();
};

ControlPluginTest.prototype.testsetsCorrectOptionsOnControlAdapter = function() 
{	
	//expectations
	this.mockControlAdaptor.expects(once()).setOptions({"color":"pink", "font":"bold"});
	
	//Whens
	var ControlPlugin = new br.presenter.view.knockout.ControlPlugin();
	var oMockElem = {appendChild:function(){}};
	ControlPlugin.init(oMockElem, function(){return "testClass";}, this.allBindingsAccessor, this.oViewModel);
};

ControlPluginTest.prototype.testsetsCorrectPresentationNodeOnControlAdapter = function() 
{	
	//expectations
	this.mockControlAdaptor.expects(once()).setPresentationNode(this.oPresenterComponent);
	
	//Whens
	var ControlPlugin = new br.presenter.view.knockout.ControlPlugin();
	var oMockElem = {appendChild:function(){}};
	ControlPlugin.init(oMockElem, function(){return "testClass";}, this.allBindingsAccessor, this.oViewModel);
};

ControlPluginTest.prototype.test_canUseOldControlOptionsNameAndValueName = function ()
{
	// expectations
	this.mockControlAdaptor.expects(once()).setOptions({"color":"pink", "font":"bold"});

	// Whens
	var ControlPlugin = new br.presenter.view.knockout.ControlPlugin();
	var oMockElem = {appendChild:function(){}};
	var allBindingsAccessor = function() {
		return {
			controloptions: {"color":"pink", "font":"bold"},
			value : self.oPresenterComponent
		};
	};
	ControlPlugin.init(oMockElem, function(){return "testClass";}, allBindingsAccessor, this.oViewModel);
};

ControlPluginTest.prototype.test_usesSetElementIfItHasBeenImplemented = function ()
{
	var oControlAdaptor = new br.presenter.testing.TestControlAdaptor();
	var bElementSet = false;
	oControlAdaptor.setElement = function(eElement) {
		bElementSet = true;
	};
	var oMockDoc = mock(br.presenter.testing.ElementInterface);
	
	//expectations
	oMockDoc.expects(never()).appendChild();
	
	//Whens
	var ControlPlugin = new br.presenter.view.knockout.ControlPlugin();
	this.mockControlAdaptorFactory.stubs().createControlAdaptor("real-adaptor").will(returnValue(oControlAdaptor));
	ControlPlugin.init(oMockDoc, function(){return "real-adaptor";}, this.allBindingsAccessor, this.oViewModel);
	
	assertTrue(bElementSet);
};

ControlPluginTest.prototype.test_errorsInSetElementShouldStillBubbleUp = function ()
{
	var fSomeError = function() {};
	var oControlAdaptor = new br.presenter.testing.TestControlAdaptor();
	oControlAdaptor.setElement = function(eElement) {
		throw new fSomeError();
	};
	var oMockDoc = mock(br.presenter.testing.ElementInterface);
	
	//expectations
	oMockDoc.expects(never()).appendChild();
	
	//Whens
	var ControlPlugin = new br.presenter.view.knockout.ControlPlugin();
	this.mockControlAdaptorFactory.stubs().createControlAdaptor("real-adaptor").will(returnValue(oControlAdaptor));
	
	var oThis = this;
	assertException(fSomeError, function() {
		ControlPlugin.init(oMockDoc, function(){return "real-adaptor";}, oThis.allBindingsAccessor, oThis.oViewModel);
	})
};
