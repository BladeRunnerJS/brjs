(function() {
	var Component = require('br-component/Component');
	var br = require("br/Core");

	ComponentTest = TestCase("ComponentTest");

	ComponentTest.prototype.createSetDisplayFrameComponent = function()
	{
		var fSetDisplayFrameComponent = function()
		{
			this.hasBeenInvoked = false;
		};
		br.extend(fSetDisplayFrameComponent, Component);

		fSetDisplayFrameComponent.prototype.setDisplayFrame = function(oContainer)
		{
			this.hasBeenInvoked = true;
		};
		
		return new fSetDisplayFrameComponent();
	};

	ComponentTest.prototype.test_invokingSetDisplayFrameOnASetFrameComponentWorks = function()
	{
		var oSetDisplayFrameComponent = this.createSetDisplayFrameComponent();
		assertFalse(oSetDisplayFrameComponent.hasBeenInvoked);

		oSetDisplayFrameComponent.setDisplayFrame();
		assertTrue(oSetDisplayFrameComponent.hasBeenInvoked);
	};
	
}());
