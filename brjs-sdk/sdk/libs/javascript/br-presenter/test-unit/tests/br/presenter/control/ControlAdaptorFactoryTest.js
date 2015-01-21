
ControlAdaptorFactoryTest = TestCase("ControlAdaptorFactoryTest");	

ControlAdaptorFactoryTest.prototype.setUp = function(){
	var oSelf = this;
	this.TestControlAdaptorClass=function(){
		this.setOptions=function(mOptions)
		{
			oSelf.optionsRecieved = mOptions;
		};
	};
	this.originalControlAdaptorFactory = br.presenter.control.ControlAdaptorFactory;
	
	this.fAliasRegistryGetClass = br.AliasRegistry.getClass;
	this.fAliasRegistryisAliasAssigned = br.AliasRegistry.isAliasAssigned;
	br.AliasRegistry.isAliasAssigned = function(sAlias)
	{
		return sAlias === "control_testControl";
	}
	
	br.AliasRegistry.getClass = function(sAlias)
	{
		return (sAlias == "control_testControl") ? oSelf.TestControlAdaptorClass : null;
	}
	
};

ControlAdaptorFactoryTest.prototype.tearDown = function(){
	br.presenter.control.ControlAdaptorFactory = this.originalControlAdaptorFactory;
	
	br.AliasRegistry.getClass = this.fAliasRegistryGetClass;
	br.AliasRegistry.isAliasAssigned = this.fAliasRegistryisAliasAssigned;
};


ControlAdaptorFactoryTest.prototype.test_createControlAdaptorReturnsControlAdaptorFromAlias = function()
{
	var myControlAdaptor = br.presenter.control.ControlAdaptorFactory.createControlAdaptor('control_testControl');
	assertInstanceOf(this.TestControlAdaptorClass,myControlAdaptor);
};

ControlAdaptorFactoryTest.prototype.test_createControlAdaptorReturnsRegisteredControlAdaptorIfNoAliasIsFound = function()
{
	br.presenter.control.ControlAdaptorFactory.registerConfiguredControlAdaptor('TestControlAdaptor',this.TestControlAdaptorClass);
	var myControlAdaptor = br.presenter.control.ControlAdaptorFactory.createControlAdaptor('TestControlAdaptor');
	assertInstanceOf(this.TestControlAdaptorClass,myControlAdaptor);
};
