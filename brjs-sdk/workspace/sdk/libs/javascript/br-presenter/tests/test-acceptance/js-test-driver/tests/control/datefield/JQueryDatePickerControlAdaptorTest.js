br.test.GwtTestRunner.initialize();

describe("View to model interactions for JQueryDatePickerControlAdaptor", function() {

	fixtures("PresenterFixtureFactory");
	
	it("loads the presentation model and view model correctly", function() 
	{
		given("demo.viewOpened = true");
		then("demo.model.date.value = '20111014'");
			and("demo.model.date.enabled = true");
			and("demo.model.date.visible = true");
			and("demo.view.(#datePicker).isVisible = true");
			then("demo.view.(#datePicker input).enabled = true");
	});
	
	it("changes the visibility in the presentation model if it's changed in the view", function() 
	{
		given("test.continuesFrom = 'loads the presentation model and view model correctly'");
		when("demo.model.date.visible => false");
		then("demo.view.(#datePicker).isVisible = false");
	});

	it("changes the enabled state in the presentation model if it's changed in the view", function() {
		given("test.continuesFrom = 'loads the presentation model and view model correctly'");
		when("demo.model.date.enabled => false");
		then("demo.view.(#datePicker input).enabled = false");
	});
	
	// TODO: get this fully working (requires async runner support)
	it("can start up disabled and/or invisible", function() 
	{
		given("demo.viewOpened = true");
		then("demo.view.(#datePicker2).isVisible = false");
//			and("demo.view.(#datePicker2 input).enabled = false");
	});
	
	// TODO: get these tests working (requires async runner support)
//	it("changes the current chosen date in the presentation model if it's changed in the view", function() {
//		given("test.continuesFrom = 'loads the presentation model and view model correctly'");
//		when("demo.model.date.value => '20132502'");
//			and("demo.view.(#datePicker .ui-datepicker-trigger).clicked => true");
//		then("demo.view.(#datePicker .ui-datepicker-today .ui-state-default).text = '25'");
//	});
//	
//	it("selects the current chosen date in the view model it is changed in the presentation model", function() {
//		given("test.continuesFrom = 'loads the presentation model and view model correctly'");
//		when("demo.view.(#datePicker .ui-datepicker-trigger).clicked => true");
//			and("demo.view.(#datePicker .ui-datepicker-today ~ .ui-datepicker-week-end).clicked => true");
//		then("demo.model.date.value = '20111015'");
//	});
//
//	it("passes the controls within the options in html template", function() {
//		given("test.continuesFrom = 'loads the presentation model and view model correctly'");
//		when("demo.view.(#datePicker .ui-datepicker-trigger).clicked => true");
//		then();
//	});
});
