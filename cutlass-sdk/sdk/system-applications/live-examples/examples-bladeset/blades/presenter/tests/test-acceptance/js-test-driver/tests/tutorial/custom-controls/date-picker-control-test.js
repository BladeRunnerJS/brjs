describe("date-picker-control tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/custom-controls/date-picker-control");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(button).hasClass = 'ui-datepicker-trigger'");
	});

});
