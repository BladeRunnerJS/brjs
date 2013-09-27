describe("date picker", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("cookbook/dates");
	});
	
	it('displays the component', function() {
		given("example.viewOpened = true");
		then("example.model.date.value = '2012-03-21'");
			and("example.model.rangedDate.value = '2012-03-21'");	
	});
			
	it('has valid view bindings', function() {
		given("example.viewOpened = true");
		then("example.view.(input:eq(0)).value = '2012-03-21'");	
		then("example.view.(input:eq(2)).value = '2012-03-21'");		
	});
	

//	it('can select a single date via the date picker', function() {
//		given("test.continuesFrom = 'displays the component'");
//		when("example.view.(#single-date #date button ).clicked => 'true'");
//			and("test.page.(#ui-datepicker-div tr:eq(1) td:last).clicked => 'true'");
//		then("example.model.date.value = '2012/03/03'");
//	});
//	
//	it('can select a ranged date via the date picker', function() {
//		given("example.viewOpened = true");
//		when("example.view.(#ranged-date #date button ).clicked => 'true'");
//			and("test.page.(#ui-datepicker-div tr:eq(1) td:last).clicked => 'true'");
//		then("example.model.date.rangedDate = '2012/03/03'");
//	});
	
	it('can select a single date via the input field', function() {
		given("example.viewOpened = true");
		when("example.view.(input:eq(0)).value => '2012-01-01'");
		then("example.model.date.value = '2012-01-01'");
	});
	
});
