describe("dynamic-nodelist-subtemplates tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/subtemplates/dynamic-nodelist-subtemplates");
	});
	
	it("has the correct default values", function(){
		given("example.opened = true");
		then("example.model.legs.0.currencyPair = 'GBPUSD'");
			and("example.model.legs.0.amount = '100000'");
			and("example.model.legs.1.currencyPair = 'EURGBP'");
			and("example.model.legs.1.amount = '150000'");
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
		when("example.model.legs.0.amount => '0'");
			and("example.model.legs.1.amount => '1'");
		then("example.view.(div span:eq(1)).text = '0'");
			and("example.view.(div span:eq(3)).text = '1'");
	});
});
