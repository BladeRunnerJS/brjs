(function() {
    var GwtTestRunner = require("br/test/GwtTestRunner");
    GwtTestRunner.initialize();

    describe("Backwards compatibility with templates that are still surrounded by script tags", function() {

        fixtures( require("br/presenter/LegacyTemplateFixtureFactory") );

        it("loads the template", function()
        {
            given("legacy.viewOpened = true");
            then("legacy.view.(span#prop-plain).text = 'a'");
                and("legacy.view.(span#prop-formatted).text = 'A'");
        });
    });
})();
