br.Core.thirdparty('jsunitextensions');
br.Core.thirdparty('mock4js');

ComponentTest = TestCase("ComponentTest");

ComponentTest.prototype.createSetFrameComponent = function()
{
	var fSetFrameComponent = function()
	{
		this.hasBeenInvoked = false;
	};
	br.Core.extend(fSetFrameComponent, br.component.Component);
	
	fSetFrameComponent.prototype.setFrame = function(oContainer)
	{
		this.hasBeenInvoked = true;
	};
	
	return new fSetFrameComponent();
};

ComponentTest.prototype.createSetContainerComponent = function()
{
	var fSetContainerComponent = function()
	{
		this.hasBeenInvoked = false;
	};
	br.Core.extend(fSetContainerComponent, br.component.Component);
	
	fSetContainerComponent.prototype.setContainer = function(oContainer)
	{
		this.hasBeenInvoked = true;
	};
	
	return new fSetContainerComponent();
};

ComponentTest.prototype.test_invokingSetFrameOnASetFrameComponentWorks = function()
{
	var oSetFrameComponent = this.createSetFrameComponent();
	assertFalse(oSetFrameComponent.hasBeenInvoked);
	
	oSetFrameComponent.setFrame();
	assertTrue(oSetFrameComponent.hasBeenInvoked);
};

// TODO: investigate these failures - setContainer() isnt defined in Component after CT->BRJS move
//ComponentTest.prototype.test_invokingSetContainerOnASetFrameComponentWorks = function()
//{
//	var oSetFrameComponent = this.createSetFrameComponent();
//	assertFalse(oSetFrameComponent.hasBeenInvoked);
//	
//	oSetFrameComponent.setContainer();
//	assertTrue(oSetFrameComponent.hasBeenInvoked);
//};
//
//ComponentTest.prototype.test_invokingSetContainerOnASetContainerComponentWorks = function()
//{
//	var oSetContainerComponent = this.createSetContainerComponent();
//	assertFalse(oSetContainerComponent.hasBeenInvoked);
//	
//	oSetContainerComponent.setContainer();
//	assertTrue(oSetContainerComponent.hasBeenInvoked);
//};

ComponentTest.prototype.test_invokingSetFrameOnASetContainerComponentWorks = function()
{
	var oSetContainerComponent = this.createSetContainerComponent();
	assertFalse(oSetContainerComponent.hasBeenInvoked);
	
	oSetContainerComponent.setFrame();
	assertTrue(oSetContainerComponent.hasBeenInvoked);
};
