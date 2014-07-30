br.Core.thirdparty('jsmockito');

(function() {

	"use strict";

	var toolTipField;

	var testCaseName = "ToolTipFieldTests";
	var testCase = {

		setUp: function() {
			toolTipField = new br.presenter.node.ToolTipField();
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
