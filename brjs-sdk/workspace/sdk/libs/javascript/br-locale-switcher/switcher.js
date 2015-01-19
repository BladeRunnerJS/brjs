var localeSwitcher = {
	switch: function switchLocale() {
		var localeCookie = LocaleUtility.getCookie(window.$BRJS_LOCALE_COOKIE_NAME);
		var browserAcceptedLocales = LocaleUtility.getBrowserAcceptedLocales();
		var appLocales = window.$BRJS_APP_LOCALES;
		var activeLocale = LocaleUtility.getActiveLocale(localeCookie, browserAcceptedLocales, appLocales);
		window.location = LocaleUtility.getLocalizedPageUrl(window.location.href, activeLocale);
	}
};

