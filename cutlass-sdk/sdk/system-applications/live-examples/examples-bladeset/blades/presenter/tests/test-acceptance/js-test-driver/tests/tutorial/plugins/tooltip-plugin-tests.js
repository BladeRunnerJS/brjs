describe("tooltip-plugin tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/plugins/tooltip-plugin", true);
		document.body.appendChild(caplin.presenter.view.knockout.TooltipPlugin.element[0]);
	});
	
	it("doesnt display tooltip on load", function(){
		given("example.viewOpened = true");
		then("test.page.(.ui-tooltip-content).isVisible = false");
	});
	
	it("displays tooltip for invalid amount", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => 'NotANumber'");
		and("example.view.(.amount_input).focusIn => true");
		then("test.page.(.ui-tooltip-content).text = 'Invalid Number'");
			and("test.page.(.ui-tooltip-content).isVisible = true");
	});
	
	it("removes tooltip for valid amount", function(){
		given("test.continuesFrom = 'displays tooltip for invalid amount'");
		when("example.model.amount.value => '1234'");
			and("example.view.(.amount_input).focusOut => true");
			and("example.view.(.amount_input).focusIn => true");
		then("test.page.(.ui-tooltip-content).isVisible = false");
	});
	
});