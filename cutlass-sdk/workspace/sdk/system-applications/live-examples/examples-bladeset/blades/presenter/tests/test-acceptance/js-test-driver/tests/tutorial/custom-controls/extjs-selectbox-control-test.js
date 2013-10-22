describe("extjs-selectbox-control tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/custom-controls/extjs-selectbox-control");
	});
	
	it("has default combobox values", function(){
		given("example.opened = true");
		then("example.model.tradeType.options = ['SWAP', 'FORWARD', 'SPOT']");
			and("example.model.tradeType.value = 'SWAP'");
	});	
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(input).value = 'SWAP'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.tradeType.value => 'FORWARD'");
		then("example.view.(input).value = 'FORWARD'");
	});
	
	it("updates the model when changing the view", function(){
		given("example.viewOpened = true");
		when("example.view.(input).value => 'SWAP'");
		then("example.model.tradeType.value = 'SWAP'");
	});


});
