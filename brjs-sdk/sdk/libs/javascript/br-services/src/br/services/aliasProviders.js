
module.exports = {
	'br.html-service': function() {
		return require('ct-services/html/ConfigurableHTMLResourceService').default;
	},
	'br.event-hub': function() {
		return require('br/EventHub');
	},
	'br.xml-service': function() {
		return require("ct-services/xml/ConfigurableXMLResourceService").default;
	},
	'br.app-meta-service': function() {
		return require('br-services/appmeta/BRAppMetaService');
	},
	'br.locale-provider': function() {
		return require('br-services/locale/BRLocaleProvider');
	},
	'br.locale-switcher': function() {
		return require('br-services/locale/BRLocaleForwardingSwitcher');
	},
	'br.locale-service': function() {
		return require('br-services/locale/BRLocaleService');
	}
};
