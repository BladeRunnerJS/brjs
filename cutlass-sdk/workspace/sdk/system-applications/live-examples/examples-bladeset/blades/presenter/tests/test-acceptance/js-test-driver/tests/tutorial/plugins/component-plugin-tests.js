describe("component-plugin tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/plugins/component-plugin");
	});
	
	it("contains the correct row name within the grid", function(){
		given("example.viewOpened = true");
		then("test.page.(.message).text = 'This is a simple Presenter component'");
	});
	
});