describe("toggle-switch-control tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/custom-controls/toggle-switch-control");
	});
	
	it("has default combobox values", function(){
		given("example.opened = true");
		then("example.model.buysell.options = ['GBP', 'USD']");
			and("example.model.buysell.value = 'gbp'");
	});	
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(.toggleSwitch).hasClass = 'choiceBSelected'");
			and("example.view.(.toggleSwitch).doesNotHaveClass = 'choiceASelected'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.view.(.choiceA).clicked => true");
		then("example.view.(.toggleSwitch).hasClass = 'choiceASelected'");
			and("example.view.(.toggleSwitch).doesNotHaveClass = 'choiceBSelected'");
	});
	
});

