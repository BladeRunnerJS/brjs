
module.exports = {
	'br.html-service': function() {
		return require('./html/ConfigurableHTMLResourceService').default;
	},
	'br.event-hub': function() {
		return require('br/EventHub');
	},
	'br.xml-service': function() {
		return require('./xml/ConfigurableXMLResourceService').default;
	},
	'br.app-meta-service': function() {
		return require('./appmeta/BRAppMetaService');
	},
	'br.locale-provider': function() {
		return require('./locale/BRLocaleProvider');
	},
	'br.locale-switcher': function() {
		return require('./locale/BRLocaleForwardingSwitcher');
	},
	'br.locale-service': function() {
		return require('./locale/BRLocaleService');
	}
};
