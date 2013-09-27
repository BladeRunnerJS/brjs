describe("fields tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/fields", true);
	});
	
	it("has an amount of 42 as default", function(){
		given("example.opened = true");
		then("example.model.amount.value = 10000");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = '10000'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => '66'")
		then("example.view.(input).value = '66'");
	});
	
	it("displays failure message when invalid value is entered", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => 'abc'")
		then("example.view.(.error_msg).text = 'Invalid Number'");
	});
});
