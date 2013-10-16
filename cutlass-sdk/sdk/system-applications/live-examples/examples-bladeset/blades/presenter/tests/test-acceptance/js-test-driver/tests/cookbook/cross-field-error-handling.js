describe("cross field error handling", function(){
	fixtures("PresenterLiveExamplesFixtureFactory");
	
	beforeEach(function(){
		loadExample("cookbook/validation");
	});
	
	it("has the correct initial field values", function(){
		given("example.opened = true");
		then("example.model.min.value = '50'");
			and("example.model.max.value = '200'");
			and("example.model.amount.value = '100'");
	});
	
	it("has valid view bindings", function(){
		given("test.continuesFrom = 'has the correct initial field values'");
			and("example.viewOpened = true");
		then("example.view.(input:eq(0)).value = '50'");
			and("example.view.(input:eq(1)).value = '200'");
			and("example.view.(input:eq(2)).value = '100'");
			and("example.view.(input:eq(0)).doesNotHaveClass = 'error'");
			and("example.view.(input:eq(1)).doesNotHaveClass = 'error'");
			and("example.view.(input:eq(2)).doesNotHaveClass = 'error'");
	});
	
	it("validates the model when changing the initial amounts via the model", function(){
		given("example.opened = true");
		when("example.model.min.value => '200'");
			and("example.model.max.value => '500'");
			and("example.model.amount.value => '100'");
		then("example.model.amount.hasError = true");
			and("example.model.min.hasError = true");
			and("example.model.max.hasError = true");
	});
	
	it("validates when the user inputs an invalid range number", function(){
		given("example.viewOpened = true");
		when("example.view.(input:eq(2)).value => 10")
		then("example.view.(input:eq(0)).hasClass = 'error'");
			and("example.view.(input:eq(1)).hasClass = 'error'");
			and("example.view.(input:eq(2)).hasClass = 'error'");
			and("example.view.(.error_msg).text = 'The amount should be between the minimum and maximum values'");
	});
	
	//TODO
//	it("has correct class when validation fails", function(){
//		given("test.continuesFrom = 'validates the model when changing the initial amounts'");		
//		then("example.view.(.error_msg).text = 'The amount should be between the minimum and maximum values'");
//			and("example.view.(#min).hasClass = 'error'");
//			and("example.view.(#max).class = 'error'");
//			and("example.view.(.field:last input).class = 'error'");
//	});

	it("validates the fields when amount changes and amount becomes out of range", function(){
		given("test.continuesFrom = 'has the correct initial field values'");
		when("example.model.amount.value => '300'");
		then("example.model.amount.hasError = true");
			and("example.model.min.hasError = true");
			and("example.model.max.hasError = true");
			and("example.model.amount.failureMessage = 'The amount should be between the minimum and maximum values'");
			and("example.model.min.failureMessage = 'The amount should be between the minimum and maximum values'");
			and("example.model.max.failureMessage = 'The amount should be between the minimum and maximum values'");
	});

	//TODO
//	it("has correct class when cross field validation fails", function(){
//		given("test.continuesFrom = 'validates the fields when amount changes and amount becomes out of range'");
//		then("example.view.(.error_msg).text = 'The amount should be between the minimum and maximum values'");
//			and("example.view.(#min).hasClass = 'error'");
//			and("example.view.(#max).class = 'error'");
//			and("example.view.(.field:last input).class = 'error'");
//	});
	
	it("validates the fields when min changes and amount becomes out of range", function(){
		given("test.continuesFrom = 'has the correct initial field values'");
		when("example.model.min.value => '150'");
		then("example.model.amount.hasError = true");
			and("example.model.min.hasError = true");
			and("example.model.max.hasError = true");
			and("example.model.amount.failureMessage = 'The amount should be between the minimum and maximum values'");
			and("example.model.min.failureMessage = 'The amount should be between the minimum and maximum values'");
			and("example.model.max.failureMessage = 'The amount should be between the minimum and maximum values'");
	});
	
	it("validates the fields when max changes and amount becomes out of range", function(){
		given("test.continuesFrom = 'has the correct initial field values'");
		when("example.model.max.value => '80'");
		then("example.model.amount.hasError = true");
			and("example.model.min.hasError = true");
			and("example.model.max.hasError = true");
			and("example.model.amount.failureMessage = 'The amount should be between the minimum and maximum values'");
			and("example.model.min.failureMessage = 'The amount should be between the minimum and maximum values'");
			and("example.model.max.failureMessage = 'The amount should be between the minimum and maximum values'");
	});
	
	it("validates the fields when amount is changed and amount comes back in range", function(){
		given("example.opened = true");
			and("example.model.min.value = '100'");
			and("example.model.max.value = '200'");
			and("example.model.amount.value = '300'");
			and("example.model.amount.hasError = true");
			and("example.model.amount.failureMessage = 'The amount should be between the minimum and maximum values'");
		when("example.model.amount.value => '150'");
		then("example.model.amount.hasError = false");
			and("example.model.amount.failureMessage = ''");
	});

	it("validates the fields when min is changed and amount comes back in range", function(){
		given("example.opened = true");
			and("example.model.min.value = '100'");
			and("example.model.max.value = '200'");
			and("example.model.amount.value = '80'");
			and("example.model.amount.hasError = true");
			and("example.model.amount.failureMessage = 'The amount should be between the minimum and maximum values'");
		when("example.model.min.value => '50'");
		then("example.model.amount.hasError = false");
			and("example.model.amount.failureMessage = ''");
	});

	it("validates the fields when max is changed and amount comes back in range", function(){
		given("example.opened = true");
			and("example.model.min.value = '100'");
			and("example.model.max.value = '200'");
			and("example.model.amount.value = '300'");
			and("example.model.amount.hasError = true");
			and("example.model.amount.failureMessage = 'The amount should be between the minimum and maximum values'");
		when("example.model.max.value => '500'");
		then("example.model.amount.hasError = false");
			and("example.model.amount.failureMessage = ''");
	});

	//view elements do not have error class when value out of range 

});
