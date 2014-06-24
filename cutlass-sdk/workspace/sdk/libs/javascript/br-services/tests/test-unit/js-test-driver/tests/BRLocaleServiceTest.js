BRLocaleServiceTest = TestCase("BRLocaleServiceTest");

var BRLocaleService = require("br/services/locale/BRLocaleService");

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