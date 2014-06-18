br.test.GwtTestRunner.initialize();

describe("View to model interactions for Control Plugin", function() {
	fixtures("ControlPluginFixtureFactory");
	
	it("invokes onViewReady() on the initial body node-list controls", function() {
		given("component.viewOpened = true");
		then("component.view.(.control).count = 2");
			and("component.view.(.control.body span).text = 'control #1: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList span).text = 'control #2: onViewReady() invoked 1 time(s)'");
	});
	
	it("invokes onViewReady() on dynamically added node-list controls", function() {
		given("component.viewOpened = true");
		when("component.model.showTwoNodeListItems.invoked => true");
		then("component.view.(.control).count = 3");
			and("component.view.(.control.body span).text = 'control #1: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList:first span).text = 'control #2: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList:last span).text = 'control #3: onViewReady() invoked 1 time(s)'");
	});
	
	it("allows initial and dynamically added node-list controls to be removed from the view", function() {
		given("test.continuesFrom = 'invokes onViewReady() on dynamically added node-list controls'");
		when("component.model.showZeroNodeListItems.invoked => true");
		then("component.view.(.control).count = 1");
			and("component.view.(.control.body span).text = 'control #1: onViewReady() invoked 1 time(s)'");
	});
	
	it("freshly invokes onViewReady() on a new control for re-added node-list control view models", function() {
		given("test.continuesFrom = 'allows initial and dynamically added node-list controls to be removed from the view'");
		when("component.model.showTwoNodeListItems.invoked => true");
		then("component.view.(.control).count = 3");
			and("component.view.(.control.body span).text = 'control #1: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList:first span).text = 'control #2: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList:last span).text = 'control #3: onViewReady() invoked 1 time(s)'");
	});

	it("freshly invokes onViewReady() on a new control for re-added node-list control view models in reverse order", function() {
		given("test.continuesFrom = 'allows initial and dynamically added node-list controls to be removed from the view'");
		when("component.model.showTwoNodeListItemsInReverseOrder.invoked => true");
		then("component.view.(.control).count = 3");
			and("component.view.(.control.body span).text = 'control #1: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList:first span).text = 'control #3: onViewReady() invoked 1 time(s)'");
			and("component.view.(.control.nodeList:last span).text = 'control #2: onViewReady() invoked 1 time(s)'");
	});

});
