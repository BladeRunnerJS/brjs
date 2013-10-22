describe("field suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/control-models/field");
	});
	
	it("has default value", function(){
		given("example.opened = true");
		then("example.model.amount.value = '100'");
	});	
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = '100'");
	});

	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => '200'");
		then("example.view.(input).value = '200'");			
	});
});
