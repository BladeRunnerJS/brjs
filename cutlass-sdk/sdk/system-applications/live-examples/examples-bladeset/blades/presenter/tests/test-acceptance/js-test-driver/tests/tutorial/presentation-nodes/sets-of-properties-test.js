describe("sets-of-properties tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/presentation-nodes/sets-of-properties");
	});
	
	it("has default starting values", function(){
		given("example.opened = true");
		then("example.model.amount.value = '10000'");
			and("example.model.account.value = 'acct1'");
	});
	
	it("has enabled input fields by default", function(){
		given("example.opened = true");
		then("example.model.amount.enabled = true");
			and("example.model.account.enabled = true");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(.amount).value = '10000'");
			and("example.view.(.account).value = 'acct1'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.amount.value => '100'");
			and("example.model.account.value => 'acct2'");
		then("example.view.(.amount).value = '100'");
			and("example.view.(.account).value = 'acct2'");
	});
	
	it("disables the fields when clicking the Disable Fields button", function(){
		given("example.viewOpened = true");
		when("example.view.(button:eq(0)).clicked => true")
		then("example.model.amount.enabled = false");
			and("example.model.account.enabled = false");
	});
	
	it("re-enables the fields when clicking the Enable Fields button", function(){
		given("test.continuesFrom = 'disables the fields when clicking the Disable Fields button'");
		when("example.view.(button:eq(1)).clicked => true")
		then("example.model.amount.enabled = true");
			and("example.model.account.enabled = true");
	});
	
	it("disables the fields for the second time when clicking the Disable Fields button", function(){
		given("test.continuesFrom = 're-enables the fields when clicking the Enable Fields button'");
		when("example.view.(button:eq(0)).clicked => true")
		then("example.model.amount.enabled = false");
			and("example.model.account.enabled = false");
	});
	
});
