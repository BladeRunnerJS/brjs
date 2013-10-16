describe("writable-property tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/writable-properties");
	});
	
	it("has an amount of 42 as default", function(){
		given("example.opened = true");
		then("example.model.amount = 42");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(span:last).text = '42'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount => '50'")
		then("example.view.(span:last).text = '50'");
	});
});
