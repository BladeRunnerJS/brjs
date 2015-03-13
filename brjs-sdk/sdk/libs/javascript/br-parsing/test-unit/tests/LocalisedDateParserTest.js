(function() {

	require('jsunitextensions');

	var LocalisedDateParser = require('br/parsing/LocalisedDateParser');
	var parser;

	var testCase = {
		setUp: function() {
			parser = new LocalisedDateParser();
		},

		tearDown: function() {},

		'test parser implements isSingleUseParser': function() {
			assertFalse(parser.isSingleUseParser());
		},

		'test parsing YYYYMMDD to DDMMYYYY': function() {
			var result = parser.parse('20150125', {
				inputFormats: ['YYYYMMDD'],
				outputFormat: 'DDMMYYYY'
			});

			assertEquals('25012015', result);
		},

		'test parsing failure': function() {
			var result = parser.parse('', {
				inputFormats: ['YYYYMMDD'],
				outputFormat: 'DDMMYYYY'
			});

			assertUndefined(result);
		},

		'test parsing to US localised format': function() {
			var result = parser.parse('20150125', {
				inputFormats: ['YYYYMMDD'],
				outputFormat: 'L',
				outputLocale: 'en'
			});

			assertEquals('01/25/2015', result);
		},

		'test parsing to a UK localised format': function() {
			var result = parser.parse('20150125', {
				inputFormats: ['YYYYMMDD'],
				outputFormat: 'L',
				outputLocale: 'en-gb'
			});

			assertEquals('25/01/2015', result);
		},

		'test parsing from a localised format': function() {
			var result = parser.parse('25/01/2015', {
				inputFormats: ['L'],
				outputFormat: 'YYYYMMDD',
				inputLocale: 'en-gb'
			});

			assertEquals('20150125', result);
		},

		'test parsing with multiple input formats': function() {
			var result = parser.parse('20150125', {
				inputFormats: ['YYYY-MM-DD', 'YYYY/MM/DD', 'YYYY MM DD', 'YYYYMMDD'],
				outputFormat: 'DDMMYYYY'
			});

			assertEquals('25012015', result);
		},

		'test parsing ambiguous date to end of year': function() {
			var result = parser.parse('2015', {
				inputFormats: ['YYYY'],
				outputFormat: 'YYYYMMDD',
				endOfUnit: true
			});

			assertEquals('20151231', result);
		},

		'test parsing ambiguous date to end of month': function() {
			var result = parser.parse('012015', {
				inputFormats: ['MMYYYY'],
				outputFormat: 'YYYYMMDD',
				endOfUnit: true
			});

			assertEquals('20150131', result);
		},

		'test parsing unambiguous date with endOfUnit flag set': function() {
			var result = parser.parse('05012015', {
				inputFormats: ['DDMMYYYY'],
				outputFormat: 'YYYYMMDD',
				endOfUnit: true
			});

			assertEquals('20150105', result);
		}

	};

	return new TestCase('LocalisedDateParserTest', testCase);
}());
