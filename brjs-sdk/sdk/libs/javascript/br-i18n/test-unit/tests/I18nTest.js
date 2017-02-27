require('../_resources/de.properties');
(function() {
	I18nTest = TestCase("I18nTest");

	I18nTest.prototype.setUp = function() {
		require('service!br.app-meta-service').setVersion('1.2.3');
	};

	I18nTest.prototype.tearDown = function() {
		require('service!br.app-meta-service').resetAllValues();
	};

	I18nTest.prototype.test_defaultsToFinalLocale = function()	{
		var i18n = require( 'br/I18n' );
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
	};

	I18nTest.prototype.test_setLocaleIsLoaded = function() {
		var i18n = require( 'br/I18n' );
		i18n.setLocale("de");
		assertEquals( 'Januar', i18n('br.i18n.date.month.january') );
	};

	I18nTest.prototype.test_correctLocaleIsLoadedIfChangedMidTest = function() {
		var i18n = require( 'br/I18n' );
		i18n.setLocale("en");
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
		i18n.setLocale("de");
		assertEquals( 'Januar', i18n('br.i18n.date.month.january') );
		i18n.setLocale("en");
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
	};

	I18nTest.prototype.test_defaultReturnedIfLocaleDoesNotExistInProd = function() {
		var i18n = require( 'br/I18n' );
		i18n.setLocale("fr");
		assertEquals( 'January', i18n('br.i18n.date.month.january') );
		i18n.setLocale("en");
	};

	I18nTest.prototype.test_tokenReturnedIfLocaleDoesNotExistInDev = function() {
		require('service!br.app-meta-service').setVersion('dev');
		var i18n = require( 'br/I18n' );
		i18n.setLocale("fr");
		assertEquals( '??? br.i18n.date.month.january ???', i18n('br.i18n.date.month.january') );
		i18n.setLocale("en");
	};
})();