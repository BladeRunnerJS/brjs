describe("snapshots tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/presentation-nodes/snapshots");
	});
	
	it("has default starting values", function(){
		given("example.opened = true");
		then("example.model.amount.value = '10000'");
			and("example.model.account.value = 'acct1'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(.amount).value = '10000'");
			and("example.view.(.account).value = 'acct1'");
	});
	
	it("updates the snapshots tutorial view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => '100'");
			and("example.model.account.value => 'acct2'");
		then("example.view.(.amount).value = '100'");
			and("example.view.(.account).value = 'acct2'");
	});
	
	it("resets to the snapshot values", function(){
		given("test.continuesFrom = 'updates the snapshots tutorial view when changing the model'");
		when("example.view.(button).clicked => true")
		then("example.model.amount.value = '10000'");
			and("example.model.account.value = 'acct1'");
	});
	
});
