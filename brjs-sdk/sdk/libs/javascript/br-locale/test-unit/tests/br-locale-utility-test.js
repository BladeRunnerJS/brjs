require('jasmine');

(function() {
	describe('switcher.getLocalizedPageUrl()', function() {
		var getLocalizedPageUrl = require('br-locale/switcher').getLocalizedPageUrl;

		it('appends the locale to the end end of simple page urls', function() {
			expect(getLocalizedPageUrl('http://acme.com:1337/app/', 'en_GB')).toBe('http://acme.com:1337/app/en_GB');
		});

		it('prepends the locale before any internal page anchor', function() {
			expect(getLocalizedPageUrl('http://acme.com:1337/app/#anchor', 'en_GB')).toBe('http://acme.com:1337/app/en_GB#anchor');
		});

		it('prepends the locale before any query string', function() {
			expect(getLocalizedPageUrl('http://acme.com:1337/app/?query=1', 'en_GB')).toBe('http://acme.com:1337/app/en_GB?query=1');
			expect(getLocalizedPageUrl('http://acme.com:1337/app?query=1', 'en_GB')).toBe('http://acme.com:1337/app/en_GB?query=1');
		});

		it('keeps the query string and anchor in the correct order', function() {
			expect(getLocalizedPageUrl('http://acme.com:1337/app/?query=1#anchor', 'en_GB')).toBe('http://acme.com:1337/app/en_GB?query=1#anchor');
		});

		it('prepends the locale before the html suffix for urls that contain an html suffix', function() {
			expect(getLocalizedPageUrl('http://acme.com:1337/app/index.html', 'en_GB')).toBe('http://acme.com:1337/app/en_GB.html');
		});

		it('keeps the query string and anchor in the correct order for urls that contain an html suffix', function() {
			expect(getLocalizedPageUrl('http://acme.com:1337/app/index.html?query=1#anchor', 'en_GB')).toBe('http://acme.com:1337/app/en_GB.html?query=1#anchor');
		});

        it('appends the locale to the end of page urls that have no folder', function() {
            expect(getLocalizedPageUrl('http://acme.com', 'en_GB')).toBe('http://acme.com/en_GB');
        });
	});
})();
