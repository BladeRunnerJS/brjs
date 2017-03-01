var APP_VERSION = process.env.VERSION || 'dev';

module.exports = {
	APP_VERSION: APP_VERSION,
	VERSIONED_BUNDLE_PATH: 'public/' + APP_VERSION,
	LOCALE_COOKIE_NAME: 'BRJS.LOCALE',
	APP_LOCALES: {
		en: true
	}
};
