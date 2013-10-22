describe("mapped-nodelist-subtemplates tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/subtemplates/mapped-nodelist-subtemplates");
	});
	
	it("has the correct default values", function(){
		given("example.opened = true");
		then("example.model.legs.nearLeg.currencyPair = 'GBPUSD'");
			and("example.model.legs.nearLeg.amount = '100000'");
			and("example.model.legs.farLeg.currencyPair = 'EURGBP'");
			and("example.model.legs.farLeg.amount = '150000'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(div span:eq(0)).text = 'GBPUSD'");
			and("example.view.(div span:eq(1)).text = '100000'");
			and("example.view.(div span:eq(2)).text = 'EURGBP'");
			and("example.view.(div span:eq(3)).text = '150000'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.legs.nearLeg.amount => '0'");
			and("example.model.legs.farLeg.amount => '1'");
		then("example.view.(div span:eq(1)).text = '0'");
			and("example.view.(div span:eq(3)).text = '1'");
	});
	
	it("updates the model when clicking the Clear NearLeg button", function(){
		given("example.viewOpened = true");
		when("example.view.(button).clicked => true");
		then("example.model.legs.nearLeg.currencyPair = 'GBPUSD'");
			and("example.model.legs.nearLeg.amount = '0'");
			and("example.model.legs.farLeg.currencyPair = 'EURGBP'");
			and("example.model.legs.farLeg.amount = '150000'");
	});
	
	it("updates the view when the Clear NearLeg button is clicked", function(){
		given("example.viewOpened = true");
		when("example.view.(button).clicked => true");
		then("example.view.(div span:eq(0)).text = 'GBPUSD'");
			and("example.view.(div span:eq(1)).text = '0'");
			and("example.view.(div span:eq(2)).text = 'EURGBP'");
			and("example.view.(div span:eq(3)).text = '150000'");
	});
});
