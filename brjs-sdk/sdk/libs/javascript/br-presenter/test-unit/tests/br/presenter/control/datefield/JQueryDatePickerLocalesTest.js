(function() {
	require('jsmockito');
	require('jstestdriverextensions');

	var subrealm;
	var spyI18n;
	var capturedDefaults;
	var translation = 'stock translation';

	var JQueryDatePickerLocales;

	var testCaseName = 'JQueryDatePickerLocalesTest';
	var testCase = {
		'setUp': function() {
			JsHamcrest.Integration.JsTestDriver();
			JsMockito.Integration.JsTestDriver();

			subrealm = realm.subrealm();
			subrealm.install();

			spyI18n = mockFunction();
			when(spyI18n)().thenReturn(translation);

			define('br/I18n', function(require, exports, module) {
				module.exports = spyI18n;
			});

			define('jquery', function(require, exports, module) {
				module.exports = {
					datepicker: {
						setDefaults: function(defaults) {
							capturedDefaults = defaults;
						}
					}
				};
			});

			JQueryDatePickerLocales = require('br/presenter/control/datefield/JQueryDatePickerLocales');
		},

		'tearDown': function() {
			subrealm.uninstall();
			subrealm = null;
			globalizeSourceModules();
		},

		'test given setLocalePropertiesToI18n is called, jQuery.datepicker.setDefaults() is called': function() {
			JQueryDatePickerLocales.setLocalePropertiesToI18n();

			assertEquals('closeText should equal translation', translation, capturedDefaults.closeText);
			assertEquals('prevText should equal translation', translation, capturedDefaults.prevText);
			assertEquals('nextText should equal translation', translation, capturedDefaults.nextText);
			assertEquals('currentText should equal translation', translation, capturedDefaults.currentText);
			assertEquals('weekHeader should equal translation', translation, capturedDefaults.weekHeader);
			assertEquals('dateFormat should equal translation', translation, capturedDefaults.dateFormat);
			assertEquals('firstDay should equal translation', 0, capturedDefaults.firstDay);
			assertEquals('isRTL should equal translation', false, capturedDefaults.isRTL);
			assertEquals('showMonthAfterYear should equal translation', false, capturedDefaults.showMonthAfterYear);
			assertEquals('yearSuffix should equal translation', translation, capturedDefaults.yearSuffix);

			assertEquals('monthNames array length', 12, capturedDefaults.monthNames.length);
			assertEquals('monthNamesShort array length', 12, capturedDefaults.monthNamesShort.length);
			assertEquals('dayNames array length', 7, capturedDefaults.dayNames.length);
			assertEquals('dayNamesShort array length', 7, capturedDefaults.dayNamesShort.length);
			assertEquals('dayNamesMin array length', 7, capturedDefaults.dayNamesMin.length);
		}

	};

	TestCase(testCaseName, testCase);

}());
