(function() {
    var PresenterComponent = require('br/presenter/component/PresenterComponent');
    var MultiViewClickPresentationModel = require('br/presenter/MultiViewClickPresentationModel');
    var GwtTestRunner = require("br/test/GwtTestRunner");

    GwtTestRunner.initialize();
    PresenterComponent.PRESENTATION_MODEL_CLASSES['br/presenter/MultiViewClickPresentationModel'] = MultiViewClickPresentationModel;

    describe("The ability to click on one view of a model and it should change the other view", function() {

        fixtures( require("br/presenter/MultiViewClickFixtureFactory") );

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
})();
