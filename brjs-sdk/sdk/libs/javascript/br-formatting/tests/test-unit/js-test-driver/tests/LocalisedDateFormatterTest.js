(function() {
	var LocalisedDateFormatter = require('br/formatting/LocalisedDateFormatter');
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

		'test formatting failure should return original string': function() {
			var result = formatter.format('abc', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'DDMMYYYY'
			});

			assertEquals('abc', result);
		},

		'test english localised format': function() {
			var result = formatter.format('20150125', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'L',
				outputLocale: 'en'
			});

			assertEquals('01/25/2015', result);
		},

		'test format to UK localised date format': function() {
			var result = formatter.format('20150125', {
				inputFormat: 'YYYYMMDD',
				outputFormat: 'L',
				outputLocale: 'en-gb'
			});

			assertEquals('25/01/2015', result);
		}

	};

	return new TestCase('LocalisedDateParserTest', testCase);
}());
