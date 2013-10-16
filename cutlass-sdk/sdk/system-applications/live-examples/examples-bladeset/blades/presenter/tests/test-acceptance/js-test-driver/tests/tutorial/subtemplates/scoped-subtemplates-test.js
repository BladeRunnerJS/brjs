describe("scoped-subtemplates tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/subtemplates/scoped-subtemplates");
	});
	
	it("has the correct default currency pair amounts", function(){
		given("example.opened = true");
		then("example.model.account.value = 'acct1'");
			and("example.model.leg1.amount.value = '10000'");
			and("example.model.leg2.amount.value = '20000'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input:eq(0)).value = 'acct1'");
			and("example.view.(input:eq(1)).value = '10000'");
			and("example.view.(input:eq(2)).value = '20000'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.account.value => 'acct2'");
			and("example.model.leg1.amount.value => '1'");
			and("example.model.leg2.amount.value => '2'");
		then("example.view.(input:eq(0)).value = 'acct2'");
			and("example.view.(input:eq(1)).value = '1'");
			and("example.view.(input:eq(2)).value = '2'");
	});
});
