br.test.GwtTestRunner.initialize();

describe("The ability to click on one view of a model and it should change the other view", function() {

	fixtures("MultiViewClickFixtureFactory");

	it("displays stuff correctly in multi view click", function()
	{
		given("multiViewClick.viewOpened = true");
		then("multiViewClick.view.(#selectBox option).count = 2");
			and("multiViewClick.view.(#selectBox option:selected).value = 'Cooking'");
			then("multiViewClick.view.(#radioButtons input).count = 2");
			and("multiViewClick.view.(#radioButtons input:checked).count = 1");
			and("multiViewClick.view.(#radioButtons input:first).checked = true");
			and("multiViewClick.view.(#radioButtons input:last).checked = false");
	});
	
	it("displays both views correctly after click", function()
	{
		given("test.continuesFrom = 'displays stuff correctly in multi view click'");
		when("multiViewClick.view.(#radioButtons :nth-child(4)).clicked => true");
		then("multiViewClick.view.(#selectBox option).count = 2");
			and("multiViewClick.view.(#selectBox option:selected).value = 'Extreme Ironing'");
			and("multiViewClick.view.(#radioButtons input:checked).count = 1");
			and("multiViewClick.view.(#radioButtons input:first).checked = false");
			and("multiViewClick.view.(#radioButtons input:last).checked = true");
	});
});