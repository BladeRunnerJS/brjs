'use strict';

const aliasesConfig = require('../src/config/aliases');

module.exports = Object.assign(
	aliasesConfig,
	{
		'some.alias2': () => require('br/_test-src/b/AliasClass2'),
		'some.alias1': () => require('br/_test-src/a/AliasClass1'),
		'br.component.TestSerializableComponent': () => require('br-component/_test-src/TestSerializableComponent'),
		'br.app-meta-service': () => require('br-services/appmeta/JSTDAppMetaService')
	}
);

module.exports['some.alias1'].interfaceRef = require('br/_test-src/Alias1Interface');
