describe("date-field tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/control-models/date-field");
	});
	
	it("has default select value", function(){
		given("example.opened = true");
		then("example.model.dateField.value = '2011-01-01'");
	});	
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = '2011-01-01'");
	});

	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.dateField.value => '2012-02-02'");
		then("example.view.(input).value = '2012-02-02'");			
	});
});
