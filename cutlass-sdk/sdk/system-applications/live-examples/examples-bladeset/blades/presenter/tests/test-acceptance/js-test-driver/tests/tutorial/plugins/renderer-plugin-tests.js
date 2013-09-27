describe("renderer-plugin tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/plugins/renderer-plugin");
	});
	
	it("has default starting value", function(){
		given("example.opened = true");
		then("example.model.renderer_text = 'This element has had its class applied by the ClassStyler'");
	});
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(span:eq(1)).text = 'This element has had its class applied by the ClassStyler'");
			and("example.view.(span:eq(1)).hasClass = 'renderer-plugin-demo'");
	});

	
});