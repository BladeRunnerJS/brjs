"use strict";

var I18N = function() {
};

I18N.create = function(translator) {
	var i18n = function(thingToFormat, mTemplateArgs) {
		return translator.getMessage(thingToFormat, mTemplateArgs);
	};
	
	i18n.number = function(thingToFormat) {
		return translator.formatNumber(thingToFormat);
	};
	
	i18n.date = function(thingToFormat) {
		return translator.formatDate(thingToFormat);
	};
	
	i18n.time = function(thingToFormat) {
		return translator.formatTime(thingToFormat);
	};
	
	i18n.getTranslator = function() {
		return translator;
	};
	
	return i18n;
};

module.exports = I18N;