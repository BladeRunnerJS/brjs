const brAliasProviders = require('br-services/aliasProviders');
const brPresenterAliasProviders = require('br-presenter/aliasProviders');

const appAliases = {};

module.exports = Object.assign(
	{},
	brAliasProviders,
	brPresenterAliasProviders,
	appAliases
);
