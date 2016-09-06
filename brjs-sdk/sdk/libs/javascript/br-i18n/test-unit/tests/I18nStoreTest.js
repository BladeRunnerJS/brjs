(function() {
	'use strict';

	var I18nStore = require('br/i18n/I18nStore');

	var I18nStoreTest = TestCase("I18nStoreTest");

	I18nStoreTest.prototype.setUp = function() {
		I18nStore.locale = 'locale';
	};

	I18nStoreTest.prototype.test_registerTranslations = function() {
		var translations = {
			key: 'value',
			upperCaseKey: 'Value'
		};

		I18nStore.registerTranslations('locale', translations);

		assertEquals(I18nStore.getTranslation('key'), 'value');
		assertEquals(I18nStore.getTranslation('upperCaseKey'), 'Value');
	};

	I18nStoreTest.prototype.test_registerTranslationsDoesNotAllowOverridingMessages = function() {
		var translations = {
			key: 'newvalue',
			upperCaseKey: 'newValue'
		};
		this.test_registerTranslations();

		I18nStore.registerTranslations('locale', translations);

		assertEquals(I18nStore.getTranslation('key'), 'value');
		assertEquals(I18nStore.getTranslation('upperCaseKey'), 'Value');
	};
})();
