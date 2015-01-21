(function() {
	var Component = require('br/component/Component');

	br.Core.thirdparty('jsunitextensions');
	br.Core.thirdparty('mock4js');

	ComponentTest = TestCase("ComponentTest");

	ComponentTest.prototype.createSetDisplayFrameComponent = function()
	{
		var fSetDisplayFrameComponent = function()
		{
			this.hasBeenInvoked = false;
		};
		br.Core.extend(fSetDisplayFrameComponent, Component);

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
