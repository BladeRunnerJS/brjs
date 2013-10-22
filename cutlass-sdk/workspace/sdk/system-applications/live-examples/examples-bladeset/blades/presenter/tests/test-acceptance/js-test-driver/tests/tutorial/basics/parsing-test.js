describe("parsing tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/parsing");
	});
	
	it("has an amount of TYPE LOWERCASE TEXT as default", function(){
		given("example.opened = true");
		then("example.model.text = 'TEXT'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = 'TEXT'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.text => 'NEW'");
		then("example.view.(input).value = 'NEW'");
	});
	
	it("parses lowercase value", function(){
		given("example.viewOpened = true");
		when("example.view.(input).value => 'value'");
		then("example.model.text = 'VALUE'");
	});
});
