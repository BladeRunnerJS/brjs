(function() {
	describe("locale-forwarder", function() {

		require("jasmine");

		var getLocalizedPageUrl = require("br-locale-utility").getLocalizedPageUrl;
		var getActiveLocale = require("br-locale-utility").getActiveLocale;

	    function locale(localeInfo) {
	        var apps = {};
	        for(var i = 0, l = localeInfo.app.length; i < l; ++i) {
	            apps[localeInfo.app[i]] = true;
	        }

	        return getActiveLocale(localeInfo.cookie, localeInfo.user, apps);
	    }

	    it("uses the first matching user locale if no cookie is set.", function() {
	        expect(locale({
	            cookie:null,
	            user:['de_CH', 'de'],
	            app:['en', 'de']})).toBe("de");
	    });

	    it("uses the full locale if there is an available match.", function() {
	        expect(locale({
	            cookie:null,
	            user:['de_CH', 'de'],
	            app:['en', 'de', 'de_CH']})).toBe("de_CH");
	    });

	    it("uses the default locale if there are no other matches.", function() {
	        expect(locale({
	            cookie:null,
	            user:['fr_FR', 'fr'],
	            app:['en', 'de']})).toBe("en");
	    });

	    it("uses the cookie value in preference to the user-defined browser locales.", function() {
	        expect(locale({
	            cookie:'de',
	            user:['en_GB', 'en'],
	            app:['en', 'de']})).toBe("de");
	    });

	    it("ignores the cookie value if it is no longer one of the available locales.", function() {
	        expect(locale({
	            cookie:'en_GB',
	            user:['de_DE', 'de'],
	            app:['en', 'de']})).toBe("de");
	    });

	    it("appends the locale to the end end of simple page urls", function() {
	        expect(getLocalizedPageUrl("http://acme.com:1337/app/", "en_GB")).toBe("http://acme.com:1337/app/en_GB/");
	    });

	    it("prepends the locale before any internal page anchor", function() {
	        expect(getLocalizedPageUrl("http://acme.com:1337/app/#anchor", "en_GB")).toBe("http://acme.com:1337/app/en_GB/#anchor");
	    });

	    it("prepends the locale before any query string", function() {
	        expect(getLocalizedPageUrl("http://acme.com:1337/app/?query=1", "en_GB")).toBe("http://acme.com:1337/app/en_GB/?query=1");
	        expect(getLocalizedPageUrl("http://acme.com:1337/app?query=1", "en_GB")).toBe("http://acme.com:1337/app/en_GB/?query=1");
	    });

	    it("keeps the query string and anchor in the correct order", function() {
	        expect(getLocalizedPageUrl("http://acme.com:1337/app/?query=1#anchor", "en_GB")).toBe("http://acme.com:1337/app/en_GB/?query=1#anchor");
	    });

	    it("appends the locale before the requested resource for static files", function() {
	        expect(getLocalizedPageUrl("http://acme.com:1337/app/www/index.html?test1#test2", "en_GB")).toBe("http://acme.com:1337/app/www/en_GB/index.html?test1#test2");
	    });

	});
})();
