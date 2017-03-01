'use strict';

module.exports = {
	messageDefinitions: {},

	initialize: function(messageDefinitions, useLocale, defaultLocale) {
		this.defaultLocale = defaultLocale;
		this.locale = useLocale;

		Object
			.keys(messageDefinitions)
			.forEach(function (locale) {
				this.registerTranslations(locale, messageDefinitions[locale]);
			}, this);
	},

	getTranslation: function(token) {
		var lowerCasedToken = token.toLowerCase();

		return this.messages[lowerCasedToken];
	},

	getDefaultTranslation: function(token) {
		var lowerCasedToken = token.toLowerCase();

		return this.defaultMessages[lowerCasedToken];
	},

	get defaultMessages() {
		if (this.messageDefinitions[this.defaultLocale] === undefined) {
			this.messageDefinitions[this.defaultLocale] = {};
		}

		return this.messageDefinitions[this.defaultLocale];
	},

	get messages() {
		if (this.messageDefinitions[this.locale] === undefined) {
			this.messageDefinitions[this.locale] = {};
		}

		return this.messageDefinitions[this.locale];
	},

	registerTranslations: function(locale, translations) {
		if (this.messageDefinitions[locale] === undefined) {
			this.messageDefinitions[locale] = {};
		}

		for (var token in translations) {
			var lowerCasedToken = token.toLowerCase();

			if (this.messageDefinitions[locale][lowerCasedToken] === undefined) {
				this.messageDefinitions[locale][lowerCasedToken] = translations[token];
			}
		}
	},

	tokenExists: function(token) {
		var lowerCasedToken = token.toLowerCase();

		return lowerCasedToken in this.messages || lowerCasedToken in this.defaultMessages;
	}
};
