BRLocaleServiceTest = TestCase("BRLocaleServiceTest");

var BRLocaleService = require("br/services/locale/BRLocaleService");

//TODO: find a better way to test this instead of passing in functions

BRLocaleServiceTest.prototype.test_getActiveLocaleWhenCookieSet = function()
{	
	var localeUtility = new MockLocaleUtility("de", ['en'], {'en' : true, 'de' : true});
	var localeService = new BRLocaleService( localeUtility );
	
	assertEquals("de", localeService.getLocale());
};

BRLocaleServiceTest.prototype.test_getActiveLocaleWhenCookieNotSet = function()
{
	var localeUtility = new MockLocaleUtility(null, ['en'], {'en' : true, 'de' : true});
	var localeService = new BRLocaleService( localeUtility );
	
	assertEquals("en", localeService.getLocale());
};

BRLocaleServiceTest.prototype.test_getActiveLocaleWhenBrowserLocaleDoesntMatch = function()
{
	var localeUtility = new MockLocaleUtility(null, ['fr'], {'en' : true, 'de' : true});
	var localeService = new BRLocaleService( localeUtility );
	
	assertEquals("en", localeService.getLocale());
};

BRLocaleServiceTest.prototype.test_getPageLocale = function()
{	
	var localeUtility = new MockLocaleUtility(null, ['fr'], {'en' : true, 'de' : true}, "/someapp/en_GB/");
	var localeService = new BRLocaleService( localeUtility );
	
	assertEquals("en_GB", localeService.getPageLocale());
};

BRLocaleServiceTest.prototype.test_getAndSetLocaleCookie = function()
{	
	//TODO: find a better way to do this
	window.$BRJS_LOCALE_COOKIE_NAME = "locale.key."+new Date().getTime(); 
	window.$BRJS_APP_LOCALES = {'en' : true, 'de' : true}; 
	
	var localeService = new BRLocaleService();
	
	assertEquals("en", localeService.getLocale());
	localeService.setLocaleCookie( "de", 1 );
	assertEquals("de", localeService.getLocale());
};



var MockLocaleUtility = function( localeCookieValue, browserLocales, appLocales, windowUrl ) {
	window.$BRJS_APP_LOCALES = appLocales;
	
	var LocaleUtility = require("br-locale-utility");
	this.setCookie = LocaleUtility.setCookie;
	if (localeCookieValue) {
		this.getCookie = function() { return localeCookieValue ; }	
	} else {
		this.getCookie = LocaleUtility.getCookie
	}
	if (browserLocales) {	
		this.getBrowserAcceptedLocales = function() { return browserLocales ; }
	} else {
		this.getBrowserAcceptedLocales = LocaleUtility.getBrowserAcceptedLocales;
	}
	if (windowUrl) {	
		this.getWindowUrl = function() { return windowUrl ; }
	} else {
		this.getWindowUrl = LocaleUtility.getWindowUrl;
	}
}


