var br = require('br/Core');

function AliasInterfaceError(aliasName, classRef, interfaceRef) {
	this.name = 'AliasInterfaceError';
	this.message = "Class '" + classRef + "' does not implement interface '" + interfaceRef + "', as required by alias 'alias!" + aliasName + "'.";
}
br.extend(AliasInterfaceError, Error);

module.exports = AliasInterfaceError;

