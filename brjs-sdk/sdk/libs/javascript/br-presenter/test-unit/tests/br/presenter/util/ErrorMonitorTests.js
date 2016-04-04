(function() {
    var ToolTipField = require("br/presenter/node/ToolTipField");
    var Field = require("br/presenter/node/Field");
    var ErrorMonitor = require("br/presenter/util/ErrorMonitor");
    var ToolTipNode = require("br/presenter/node/ToolTipNode");
    var Core = require("br/Core");
    require('jsmockito');

    "use strict";

    var errorMonitor;
    var tooltipNode;

    var testCaseName = "ErrorMonitorTests";
    var testCase = {

		setUp: function() {
			JsHamcrest.Integration.JsTestDriver();
			JsMockito.Integration.JsTestDriver();
			
			tooltipNode = new ToolTipNode();
			errorMonitor = new ErrorMonitor(tooltipNode);
		},

		tearDown: function() {},

		"test addErrorListeners ignores fields that are not ToolTipFields": function() {
			//given
			var field = new Field();
			field.hasError = spy(field.hasError);

			//when
			errorMonitor.addErrorListeners([field]);

			//then
			verify(field.hasError, times(0)).addChangeListener();
		},

		"test addErrorListeners monitors fields that are ToolTipFields": function() {
			//given
			var toolTipField = new ToolTipField();
			toolTipField.hasError = spy(toolTipField.hasError);

			//when
			errorMonitor.addErrorListeners([toolTipField]);

			//then
			verify(toolTipField.hasError, times(1)).addChangeListener();
		},

		"test monitorField fails when field passed in is not a ToolTipField": function() {
			//given
			var field = new Field();

			//when
			assertFails("A ToolTipField must be passed in.", function() { errorMonitor.monitorField(field) });
		},

		"test if hasError is set when any of the fields has error": function() {
			//given
			var toolTipField1 = new ToolTipField();
			var toolTipField2 = new ToolTipField();

			errorMonitor.hasError = spy(errorMonitor.hasError);

			//when
			errorMonitor.addErrorListeners([toolTipField1, toolTipField2]);
			toolTipField2.hasError.setValue(true);

			//then
			assertTrue(errorMonitor.hasError.getValue(), true);
		},

		"test if hasError isn't set when field hasn't error": function() {
			//given
			var toolTipField = new ToolTipField();
			errorMonitor.hasError = spy(errorMonitor.hasError);

			//when
			errorMonitor.addErrorListeners([toolTipField]);

			//then
			assertFalse(errorMonitor.hasError.getValue(), false);
		}


	};

    TestCase(testCaseName, testCase);
})();
