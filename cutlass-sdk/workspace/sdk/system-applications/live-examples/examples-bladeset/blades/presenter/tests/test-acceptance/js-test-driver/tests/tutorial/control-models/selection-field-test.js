describe("selection-field tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/control-models/selection-field");
	});
	
	it("has default select value", function(){
		given("example.opened = true");
		then("example.model.hobbies.value = 'Cooking'");
	});	
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(select).value = 'Cooking'");
			and("example.view.(input:eq(0)).checked = true")
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.hobbies.value => 'Extreme Ironing'");
		then("example.view.(select).value = 'Extreme Ironing'");
			and("example.view.(input:eq(1)).checked = true");			
	});
	
});
