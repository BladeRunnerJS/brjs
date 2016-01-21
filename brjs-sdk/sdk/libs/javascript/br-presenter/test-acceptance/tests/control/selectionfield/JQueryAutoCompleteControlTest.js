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

		it("does blur the input after selection is made by click if blurAfterClick is set to true", function() {
			given("demo.viewOpened = true");
			when("demo.model.jquerySelectionField.value => ''");
				and("demo.view.(#jqueryAutoCompleteBox3).typedValue => 'A'");
				and("demo.view.(#autocomplete-container li:eq(0) a).clicked => true");
			then("demo.model.jquerySelectionField.value = 'AA'");
				and("demo.view.(#jqueryAutoCompleteBox3).value = 'AA'");
				and("demo.view.(#jqueryAutoCompleteBox3).focused = false");
		});

    });
})();
