(function() {
    var jQuery = require("jquery");
    require('jsmockito');

    var JQueryDatePickerLocaleUtil = require('br/presenter/control/datefield/JQueryDatePickerLocaleUtil');

    var capturedDefaults;
	var originalSetDefaults = jQuery.datepicker.setDefaults;
    var testCaseName = 'JQueryDatePickerLocaleUtilTest';

    var testCase = {
		'setUp': function() {
			JsHamcrest.Integration.JsTestDriver();
			JsMockito.Integration.JsTestDriver();

			jQuery.datepicker.setDefaults = function(defaults) {
				capturedDefaults = defaults;
			}
		},

		'tearDown': function() {
			jQuery.datepicker.setDefaults = originalSetDefaults;
		},

		'test that JQueryDatePickerLocaleUtil.getDefaultLocales() provides valid defaults for date picker': function() {
			jQuery.datepicker.setDefaults(JQueryDatePickerLocaleUtil.getDefaultLocales());

			assertEquals('closeText should equal translation', "Done", capturedDefaults.closeText);
			assertEquals('prevText should equal translation', "Prev", capturedDefaults.prevText);
			assertEquals('nextText should equal translation', "Next", capturedDefaults.nextText);
			assertEquals('currentText should equal translation', "Today", capturedDefaults.currentText);
			assertEquals('weekHeader should equal translation', "Wk", capturedDefaults.weekHeader);
			assertEquals('dateFormat should equal translation', "mm/dd/yy", capturedDefaults.dateFormat);
			assertEquals('isRTL should equal translation', false, capturedDefaults.isRTL);
			assertEquals('showMonthAfterYear should equal translation', false, capturedDefaults.showMonthAfterYear);
			assertEquals('yearSuffix should equal translation', "", capturedDefaults.yearSuffix);

			assertEquals('monthNames array length', 12, capturedDefaults.monthNames.length);
			assertEquals('monthNamesShort array length', 12, capturedDefaults.monthNamesShort.length);
			assertEquals('dayNames array length', 7, capturedDefaults.dayNames.length);
			assertEquals('dayNamesShort array length', 7, capturedDefaults.dayNamesShort.length);
			assertEquals('dayNamesMin array length', 7, capturedDefaults.dayNamesMin.length);
		}

	};

    TestCase(testCaseName, testCase);
}());
