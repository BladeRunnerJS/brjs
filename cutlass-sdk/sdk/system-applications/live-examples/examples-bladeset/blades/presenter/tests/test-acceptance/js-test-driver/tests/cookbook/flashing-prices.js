describe("flashing prices", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		caplin.testing.TimeUtility.bCaptureTimeoutAndIntervals = false;
		loadExample("cookbook/flashing-prices");
	});
	
	it("has the correct initial value", function(){
		given("example.opened = true");
		then("example.model.amount = '0.420'");
	});
	
	it("has valid view bindings", function(){
		given("test.continuesFrom = 'has the correct initial value'")
			and("example.viewOpened = true");
			and("example.view.(span:first).text => '0.420'");
		when("example.model.amount => '0.450'");
		then("example.view.(span:first).value = '0.450'");
	});
	
	it("has the correct class when the amount increases", function(){
		given("example.viewOpened = true");
			and("example.model.amount = '0.420'");
		when("example.model.amount => '0.450'");
		then("example.view.(span:last).hasClass = 'flashing-up'");
	});
	
	it("has the correct class when the amount decreases", function(){
		given("test.continuesFrom = 'has the correct class when the amount increases'")
		when("example.model.amount => '0.300'");
		then("example.view.(span:last).hasClass = 'flashing-down'");
	});
	
	it("formats numbers to 3 decimal places", function(){
		given("example.viewOpened = true");
		when("example.model.amount => '0.3333333'");
		then("example.view.(span:first).text = '0.333'");
	});
	
});
