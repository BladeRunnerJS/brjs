br.test.GwtTestRunner.initialize();

describe("Backwards compatibility with templates that are still surrounded by script tags", function() {

	fixtures("LegacyTemplateFixtureFactory");

	it("loads the template", function()
	{
		given("legacy.viewOpened = true");
		then("legacy.view.(span#prop-plain).text = 'a'");
			and("legacy.view.(span#prop-formatted).text = 'A'");
	});
});
