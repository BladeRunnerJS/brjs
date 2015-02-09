(function() {
	I18nTest = TestCase("I18nTest");

	I18nTest.prototype.test_theSecondLocaleInJsTestDriverIsLoadedAutomatically = function()	{
		var i18n = require( 'br/I18n' );
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
	}

	I18nTest.prototype.test_setLocaleIsLoaded = function() {
		var i18n = require( 'br/I18n' );
		i18n.setLocale("de");
		assertEquals( 'Januar', i18n('br.i18n.date.month.january') );
	}

	I18nTest.prototype.test_correctLocaleIsLoadedIfChangedMidTest = function() {
		var i18n = require( 'br/I18n' );
		i18n.setLocale("en");
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
		i18n.setLocale("de");
		assertEquals( 'Januar', i18n('br.i18n.date.month.january') );
		i18n.setLocale("en");
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
	}

	I18nTest.prototype.test_propertyIsNotFoundIfLocaleDoesNotExist = function() {
		var i18n = require( 'br/I18n' );
		i18n.setLocale("fr");
		assertEquals( '??? br.i18n.date.month.january ???', i18n('br.i18n.date.month.january') );
		i18n.setLocale("en");
	}
})();