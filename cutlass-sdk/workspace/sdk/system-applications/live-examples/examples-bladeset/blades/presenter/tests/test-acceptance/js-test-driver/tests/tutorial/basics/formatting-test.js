describe("formatting tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/formatting");
	});
	
	it("has an amount of 100.42", function(){
		given("example.opened = true");
		then("example.model.amount = 100.42");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(.formattedAmount).text = '100.42'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount => '200.49553'")
		then("example.view.(.formattedAmount).text  = '200.50'");
	});
	
});
