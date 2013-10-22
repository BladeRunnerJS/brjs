describe("editable-properties tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/editable-properties");
	});
	
	it("has an amount of 1000", function(){
		given("example.opened = true");
		then("example.model.amount = '100000'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = '100000'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount => '999'")
		then("example.view.(input).value = '999'");
			and("alert.triggered = 'You changed the amount to: 999'")
	});
	
	it("updates the model when changing the view", function(){
		given("example.viewOpened = true");
		when("example.view.(input).value => 333")
		then("example.model.amount = 333");
			and("alert.triggered = 'You changed the amount to: 333'")
	});
	
});
