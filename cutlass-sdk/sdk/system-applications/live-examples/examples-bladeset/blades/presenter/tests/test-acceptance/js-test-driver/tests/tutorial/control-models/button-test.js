describe("button tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/control-models/button");
	});
	
	it("has default value", function(){
		given("example.viewOpened = true");
		then("example.view.(button).text = 'Click Me'");
	});	
	
	it("can change the button text", function(){
		given("example.viewOpened = true");
		when("example.model.myButton.label => '1234'");
		then("example.view.(button).text = '1234'");
	});	
	
	it("can hide the button", function(){
		given("example.viewOpened = true");
		when("example.model.myButton.visible => false");
		then("example.view.(button).isVisible = false");
	});	
	
	it("can show the button", function(){
		given("test.continuesFrom = 'can hide the button'")
		when("example.model.myButton.visible => true");
		then("example.view.(button).isVisible = true");
	});	
	
	it("can click the button", function(){
		given("example.viewOpened = true");
		when("example.view.(button).clicked => true");
		then("alert.triggered = 'Hello World!'");
	});	
	
	it("cant click the button if its disabled", function(){
		given("example.viewOpened = true");
		when("example.model.myButton.enabled => false");
			and("example.view.(button).clicked => true")
		then("example.view.(button).enabled = false");;
			// and we dont get an alert
	});
	
	it("can click the button again once its been enabled", function(){
		given("test.continuesFrom = 'cant click the button if its disabled'")
		when("example.model.myButton.enabled => true");
			and("example.view.(button).clicked => true");
		then("example.view.(button).enabled = true");
			and("alert.triggered = 'Hello World!'");
	});
	
	
});
