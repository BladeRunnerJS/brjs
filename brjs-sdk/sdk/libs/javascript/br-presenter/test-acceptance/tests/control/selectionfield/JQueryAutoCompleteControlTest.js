(function() {
    var GwtTestRunner = require("br/test/GwtTestRunner");
    GwtTestRunner.initialize();

    describe("View to model interactions for JQueryAutoCompleteControlAdapter", function() {
        fixtures( require("br/presenter/PresenterFixtureFactory") );

        it("starts enabled and visible by default", function() {
            given("demo.viewOpened = true");
            then("demo.view.(#jqueryAutoCompleteBox).enabled = true");
                and("demo.view.(#jqueryAutoCompleteBox).isVisible = true");
        });

        it("has the correct initial value", function() {
            given("demo.viewOpened = true");
            then("demo.view.(#jqueryAutoCompleteBox).value = 'BB'");
                and("demo.view.(#jqueryAutoCompleteBox).doesNotHaveClass = 'autocomplete-menu-open'");
        });

        it("correctly auto completes a valid input option", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
                and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'A'");
            then("demo.view.(#autocomplete-container li:eq(0)).text = 'AA'");
        });

        it("shows no options for invalid text", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
                and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'D'");
            then("demo.view.(#autocomplete-container li).count = '0'");
        });

        it("allows clicking on option to set the value", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
                and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'A'");
                and("demo.view.(#autocomplete-container li:eq(0) a).clicked => true");
            then("demo.model.jquerySelectionField.value = 'AA'");
                and("demo.view.(#jqueryAutoCompleteBox).value = 'AA'");
        });

        it("hides the dropdown when the page is scrolled", function() {
            given("test.continuesFrom = 'correctly auto completes a valid input option'");
            when("test.page.(div:first).mouseWheel => '20'");
            then("demo.view.(#autocomplete-container li).isVisible = false");
        });

        it("does not hide the dropdown when the menu is scrolled", function() {
            given("test.continuesFrom = 'correctly auto completes a valid input option'");
            when("demo.view.(#autocomplete-container .ui-menu-item:first).mouseWheel => '20'");
            then("demo.view.(#autocomplete-container li).isVisible = true");
        });

        it("adds a class to the input and when it opens", function() {
            given("test.continuesFrom = 'correctly auto completes a valid input option'");
            then("demo.view.(#jqueryAutoCompleteBox).hasClass = 'autocomplete-menu-open'");
        });

        it("removes a class from the input when it closes", function() {
            given("test.continuesFrom = 'hides the dropdown when the page is scrolled'");
            then("demo.view.(#jqueryAutoCompleteBox).doesNotHaveClass = 'autocomplete-menu-open'");
        });

        it("does not display any options if minCharAmount is set to 2", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
            and("demo.view.(#jqueryAutoCompleteBox2).typedValue => 'A'");
            then("demo.view.(#autocomplete-container2 li).count = '0'");
        });

        it("does display options if minCharAmount is set to 2 and typed text is at least 2 chars long", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
            and("demo.view.(#jqueryAutoCompleteBox2).typedValue => 'AA'");
            then("demo.view.(#autocomplete-container2 li:eq(0)).text = 'AA'");
        });

        it("does not blur the input after selection if blurAfterClick is not provided", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
                and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'A'");
                and("demo.view.(#autocomplete-container li:eq(0) a).clicked => true");
            then("demo.model.jquerySelectionField.value = 'AA'");
                and("demo.view.(#jqueryAutoCompleteBox).value = 'AA'");
                and("demo.view.(#jqueryAutoCompleteBox).focused = true");
        });

        it("does blur the input after selection is made by click if blurAfterClick is set to true", function() {
            given("demo.viewOpened = true");
            when("demo.model.jquerySelectionField.value => ''");
                and("demo.view.(#jqueryAutoCompleteBox3).typedValue => 'A'");
                and("demo.view.(#autocomplete-container3 li:eq(0) a).clicked => true");
            then("demo.model.jquerySelectionField.value = 'AA'");
                and("demo.view.(#jqueryAutoCompleteBox3).value = 'AA'");
                and("demo.view.(#jqueryAutoCompleteBox3).focused = false");
        });

        it("does not show the menu immediately when a delay option is given", function() {
            given("demo.viewOpened = true");
                and("time.timeMode = 'Manual'");
            when("demo.model.jquerySelectionField.value => ''");
                and("demo.view.(#jqueryAutoCompleteBox4).typedValue => 'A'");
            then("demo.view.(#autocomplete-container4 li).count = '0'");
        });

        it("shows the menu after some time when a delay option is given", function() {
            given("test.continuesFrom = 'does not show the menu immediately when a delay option is given'");
            when("time.passedBy => 200");
            then("demo.view.(#autocomplete-container4 li).isVisible = true");
                and("demo.view.(#autocomplete-container4 li:eq(0)).text = 'AA'");
        });

    });
})();
