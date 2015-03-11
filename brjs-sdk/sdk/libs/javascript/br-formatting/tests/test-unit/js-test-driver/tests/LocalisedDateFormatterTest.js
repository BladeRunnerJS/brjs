(function() {

	require('jsunitextensions');

	var LocalisedDateFormatter = require('br/formatting/LocalisedDateFormatter');
	var moment = require('momentjs');
	var formatter;

	var testCase = {
		setUp: function() {
			formatter = new LocalisedDateFormatter();
		},

		tearDown: function() {},

		'test formatting YYYYMMDD to DDMMYYYY': function() {
			var result = formatter.format('20150125', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'DDMMYYYY'
			});

			assertEquals('25012015', result);
		},

		'test formatting failure': function() {
			var result = formatter.format('', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'DDMMYYYY'
			});

			assertUndefined(result);
		},

		'test english localised format': function() {
			moment.lang('en');
			var result = formatter.format('20150125', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'L'
			});

			assertEquals('01/25/2015', result);
		},

		'test formatting to a locale other than the global one': function() {
			moment.lang('en');
			var result = formatter.format('20150125', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'L',
				locale: 'en-gb'
			});

			assertEquals('25/01/2015', result);
		}

	};

	return new TestCase('LocalisedDateParserTest', testCase);
}());
