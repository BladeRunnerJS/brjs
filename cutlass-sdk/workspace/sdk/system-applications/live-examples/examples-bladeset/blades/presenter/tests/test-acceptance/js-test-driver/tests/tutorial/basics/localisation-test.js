describe("localisation tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/basics/localisation", true);
	});
	
	it("has an amount of 10000", function(){
		given("example.opened = true");
		then("example.model.amount = '10000'");
	});
	
	it("has a view amount of 10000", function(){
		given("example.viewOpened = true");
		then("example.view.(*:eq(2)).value = '10000'");
	});
	
	it("has translated all i18n strings", function(){
		given("example.viewOpened = true");
		then("example.view.(*:eq(0)).text = 'The amount is below'");
		then("example.view.(*:eq(1)).text = 'Amount label'");
	});
	
	it("has alternative i18n strings", function(){
		// TODO: add a way to test i18n using a fixture
		assertTrue(ct.i18n('novobank.example.amount.instruction.alt').indexOf('?') == -1);
		assertTrue(ct.i18n('novobank.example.invalid.number.alt').indexOf('?') == -1);
		assertTrue(ct.i18n('novobank.example.amount.label').indexOf('?') == -1);
	});
	
});
