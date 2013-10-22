describe("messageService property factory tests", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	it("has default starting values", function(){
		given("example.opened = true");
		then("example.view.(.bestBid).value = '4.44'");
			and("example.view.(.bestAsk).value = '5.55'");
	});
	
});
