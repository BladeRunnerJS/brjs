describe("simple-subtemplates tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/subtemplates/simple-subtemplates");
	});
	
	it("has an amount of 10000", function(){
		given("example.opened = true");
		then("example.model.amount.value = '10000'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = '10000'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => '555'");
		then("example.view.(input).value = '555'");
	});
});
