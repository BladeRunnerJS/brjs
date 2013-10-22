describe("simple-properties tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/simple-properties");
	});
	
	it("has an amount of 10000", function(){
		given("example.opened = true");
		then("example.model.amount = 10000");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(span:last).text = '10000'");
	});
});
