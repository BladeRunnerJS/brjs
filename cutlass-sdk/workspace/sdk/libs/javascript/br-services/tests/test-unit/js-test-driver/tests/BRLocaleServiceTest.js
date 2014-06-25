BRLocaleServiceTest = TestCase("BRLocaleServiceTest");

var BRLocaleService = require("br/services/locale/BRLocaleService");

//TODO: find a better way to test this instead of passing in functions

BRLocaleServiceTest.prototype.test_getActiveLocaleWhenCookieSet = function()
{
	var getCookieFn = function(){ return "de" }
	var getBrowserLocalesFn = function(){ return ['en'] }
	var appLocales = {'en' : true, 'de' : true}
	
	var localeService = new BRLocaleService( getCookieFn, getBrowserLocalesFn, appLocales );
	
	assertEquals("de", localeService.getLocale());
};

BRLocaleServiceTest.prototype.test_getActiveLocaleWhenCookieNotSet = function()
{
	var getCookieFn = function(){ return null }
	var getBrowserLocalesFn = function(){ return ['en'] }
	var appLocales = {'en' : true, 'de' : true}
	
	var localeService = new BRLocaleService( getCookieFn, getBrowserLocalesFn, appLocales );
	
	assertEquals("en", localeService.getLocale());
};

BRLocaleServiceTest.prototype.test_getActiveLocaleWhenBrowserLocaleDoesntMatch = function()
{
	var getCookieFn = function(){ return null }
	var getBrowserLocalesFn = function(){ return ['fr'] }
	var appLocales = {'en' : true, 'de' : true}
	
	var localeService = new BRLocaleService( getCookieFn, getBrowserLocalesFn, appLocales );
	
	assertEquals("en", localeService.getLocale());
};

BRLocaleServiceTest.prototype.test_getPageLocale = function()
{	
	var urlAccessorFn = function() { return "/someapp/en_GB/"; }
	var localeService = new BRLocaleService( null, null, null, urlAccessorFn );
	
	assertEquals("en_GB", localeService.getPageLocale());
};

BRLocaleServiceTest.prototype.test_getAndSetLocaleCookie = function()
{	
	var cookieName = "locale.key."+new Date().getTime();
	window.$BRJS_LOCALE_COOKIE_NAME = cookieName; //TODO: find a better way to do this
	
	var getBrowserLocalesFn = function(){ return ['en'] }
	var appLocales = {'en' : true, 'de' : true}
	
	var localeService = new BRLocaleService( null, getBrowserLocalesFn, appLocales );
	
	debugger;
	
	assertEquals("en", localeService.getLocale());
	localeService.setLocaleCookie( "de", 1 );
	assertEquals("de", localeService.getLocale());
};
