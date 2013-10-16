describe("calendar-date-field tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/control-models/calendar-date-field");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(button).hasClass = 'ui-datepicker-trigger'");
	});

});
