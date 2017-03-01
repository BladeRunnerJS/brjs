require('../../_resources-test-at/html/test-form.html');
(function() {
    var PresenterComponent = require('br-presenter/component/PresenterComponent');
    var MultiViewPresentationModel = require('br-presenter/_test-src/MultiViewPresentationModel');
    var GwtTestRunner = require('br-test/GwtTestRunner');

    GwtTestRunner.initialize();
    PresenterComponent.PRESENTATION_MODEL_CLASSES['br-presenter/_test-src/MultiViewPresentationModel'] = MultiViewPresentationModel;

    describe("The ability to bind several view templates to the same presentation model", function() {

        fixtures( require('br-presenter/_test-src/MultiViewFixtureFactory') );

        it("displays both views correctly", function()
        {
            given("multiView.viewOpened = true");
            then("multiView.view.(#selectBox option).count = 3");
                and("multiView.view.(#selectBox option:selected).value = 'b'");
                and("multiView.view.(#alt-selectBox input:checked).count = 1");
                and("multiView.view.(#alt-selectBox input:first).checked = false");
                and("multiView.view.(#alt-selectBox :nth-child(2)).checked = true");
                and("multiView.view.(#alt-selectBox :nth-child(2)).value = 'b'");
                and("multiView.view.(#alt-selectBox input:last).checked = false");

                and("multiView.view.(#multiSelectBox option).count = 3");
                and("multiView.view.(#multiSelectBox option:selected).count = 2");
                and("multiView.view.(#multiSelectBox option:not(:selected)).value = 'B'");
                and("multiView.view.(#alt-multiSelectBox input:checked).count = 2");
                and("multiView.view.(#alt-multiSelectBox input:first).checked = true");
                and("multiView.view.(#alt-multiSelectBox :nth-child(2)).checked = false");
                and("multiView.view.(#alt-multiSelectBox :nth-child(2)).value = 'B'");
                and("multiView.view.(#alt-multiSelectBox input:last).checked = true");
        });
    });
})();
