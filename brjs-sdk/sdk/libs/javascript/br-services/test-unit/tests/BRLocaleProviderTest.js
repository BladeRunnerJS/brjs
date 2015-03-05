require('jasmine');

(function() {
	'use strict';
	
	var BRLocaleProvider = require('br/services/locale/BRLocaleProvider');
	
	describe('BRLocaleProvider.getActiveLocaleGiven()', function() {
		var localeProvicer = new BRLocaleProvider();

		function locale(localeInfo) {
			var apps = {};
			for(var i = 0, l = localeInfo.app.length; i < l; ++i) {
				apps[localeInfo.app[i]] = true;
			}

			return localeProvicer.getActiveLocaleGiven(localeInfo.cookie, localeInfo.user, apps);
		}

		it('uses the first matching user locale if no cookie is set.', function() {
			expect(locale({
				cookie:null,
				user:['de_CH', 'de'],
				app:['en', 'de']})).toBe('de');
		});

		it('uses the full locale if there is an available match.', function() {
			expect(locale({
				cookie:null,
				user:['de_CH', 'de'],
				app:['en', 'de', 'de_CH']})).toBe('de_CH');
		});

		it('uses the default locale if there are no other matches.', function() {
			expect(locale({
				cookie:null,
				user:['fr_FR', 'fr'],
				app:['en', 'de']})).toBe('en');
		});

		it('uses the cookie value in preference to the user-defined browser locales.', function() {
			expect(locale({
				cookie:'de',
				user:['en_GB', 'en'],
				app:['en', 'de']})).toBe('de');
		});

		it('ignores the cookie value if it is no longer one of the available locales.', function() {
			expect(locale({
				cookie:'en_GB',
				user:['de_DE', 'de'],
				app:['en', 'de']})).toBe('de');
		});
	});
})();
