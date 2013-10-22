describe("multi-selection-field tutorial test suite", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("tutorial/control-models/multi-selection-field");
	});
	
	it("has default select value", function(){
		given("example.opened = true");
		then("example.model.hobbies.value = ['Extreme Ironing', 'Films']");
			and("example.model.noExtremeSports.value = false");
	});	
	
	it("has valid view bindings", function(){
		given("example.viewOpened = true");
		then("example.view.(select option:eq(0)).value = 'Cooking'");
			and("example.view.(select).childrenCount = 5");
			and("example.view.(select option:eq(1)).value = 'Extreme Ironing'");
			and("example.view.(select option:eq(2)).value = 'Bungeejumping'");
			and("example.view.(select option:eq(3)).value = 'Films'");
			and("example.view.(select).value = ['Extreme Ironing', 'Films']");
			and("example.view.(input:eq(0)).checked = false");
			and("example.view.(input:eq(1)).checked = true");
			and("example.view.(input:eq(2)).checked = false");
			and("example.view.(input:eq(3)).checked = true");
			and("example.view.(span:last-child).text = 'Extreme Ironing,Films'");
	});
	
	it("updates the view when changing the model", function(){
		given("example.viewOpened = true");
		when("example.model.hobbies.value => ['Cooking', 'Bungeejumping']");
		then("example.view.(select).value = ['Cooking', 'Bungeejumping']");
		and("example.view.(input:eq(0)).checked = true");
		and("example.view.(input:eq(1)).checked = false");
		and("example.view.(input:eq(2)).checked = true");
		and("example.view.(input:eq(3)).checked = false");		
		and("example.view.(span:last-child).text = 'Cooking,Bungeejumping'");
	});
	
	it("removes the extreme sports when setting 'No Extreme Sports' to true", function() {
		given("example.viewOpened = true");
		when("example.model.noExtremeSports.value => true");
		then("example.view.(select).childrenCount = 3");
			and("example.view.(span:last-child).text = 'Films'");
	});
	
	// TODO this seems to be a timing issue?
//	it("removes the extreme sports when clicking the 'No Extreme Sports' checkbox", function() {
//		given("example.viewOpened = true");
//		when("example.view.(input:last-child).checked => true");
//		then("example.view.(select).childrenCount = 3");
//	});
	
});
