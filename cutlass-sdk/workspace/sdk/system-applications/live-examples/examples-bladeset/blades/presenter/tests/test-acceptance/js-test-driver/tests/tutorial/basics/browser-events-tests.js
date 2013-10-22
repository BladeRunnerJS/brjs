describe("browser-events tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/browser-events");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.view.(button).clicked  => 'true'");
		then("alert.triggered = 'The Execute button was clicked'");
	});
	
});
