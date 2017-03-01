require('../../_resources-test-at/html/test-form.html');
(function() {
    var PresenterComponent = require('br-presenter/component/PresenterComponent');
    var OrderOfClickAndBlurPresentationModel = require('br-presenter/_test-src/OrderOfClickAndBlurPresentationModel');
    var GwtTestRunner = require('br-test/GwtTestRunner');

    GwtTestRunner.initialize();
    PresenterComponent.PRESENTATION_MODEL_CLASSES['br-presenter/_test-src/OrderOfClickAndBlurPresentationModel'] = OrderOfClickAndBlurPresentationModel;

    describe("Order Of Click & Blur Events", function() {
        fixtures( require('br-presenter/_test-src/OrderOfClickAndBlurFixtureFactory') );

        it("starts with an empty text box", function()
        {
            given("form.viewOpened = true");
            then("form.view.(#textInput).value = ''");
        });

        it("has the correct binding between the model and view", function()
        {
            given("form.viewOpened = true");
                and("form.model.textInput = 'abc'");
            then("form.view.(#textInput).value = 'abc'");
        });

        it("does not update the presentation model until the input box loses focus", function()
        {
            given("form.viewOpened = true");
            when("form.view.(#textInput).typedValue => 'abc'");
            then("form.model.textInput = ''");
                and("form.model.storedText = ''");
        });

        it("updates the presentation model once the input box has lost focus", function()
        {
            given("form.viewOpened = true");
            when("form.view.(#textInput).typedValue => 'abc'");
                and("form.view.(#textInput).blurred => true");
            then("form.model.textInput = 'abc'");
                and("form.model.storedText = ''");
        });

        it("updates the presentation model before invoking the storeCurrentText() method", function()
        {
            given("form.viewOpened = true");
            when("form.view.(#textInput).typedValue => 'abc'");
                and("form.view.(#storeTextButton).clicked => true");
            then("form.model.textInput = 'abc'");
                and("form.model.storedText = 'abc'");
        });
    });
})();
