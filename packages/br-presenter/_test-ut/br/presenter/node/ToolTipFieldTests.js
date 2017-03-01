require('../../../../_resources-test-at/html/test-form.html');
(function() {
    var ToolTipField = require('br-presenter/node/ToolTipField');
    var Core = require("br/Core");
    require('jsmockito');

    "use strict";

    var toolTipField;

    var testCaseName = "ToolTipFieldTests";
    var testCase = {

		setUp: function() {
			toolTipField = new ToolTipField();
		},

		tearDown: function() {},

		"test hasTooltip is true when a className is present": function() {

			//when
			toolTipField.tooltipClassName.setValue("a-tooltip");

			//then
			assertTrue("hasTooltip should be true", toolTipField.hasToolTip.getValue());
		},

		"test hasTooltip is false when className is empty": function() {

			//when
			toolTipField.tooltipClassName.setValue("a-tooltip");
			toolTipField.tooltipClassName.setValue("");

			//then
			assertFalse("hasTooltip should be false", toolTipField.hasToolTip.getValue());
		}


	};

    TestCase(testCaseName, testCase);
})();
