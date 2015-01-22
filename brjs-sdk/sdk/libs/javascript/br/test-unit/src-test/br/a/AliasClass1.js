var br = require('br/Core');
var Alias1Interface = require('br/Alias1Interface');
var Alias1AlternateInterface = require('br/Alias1AlternateInterface');

function AliasClass1() {
};

AliasClass1.prototype.interfaceFunction = function(){
};

AliasClass1.prototype.alternateInterfaceFunction = function(){
};

br.hasImplemented(AliasClass1, Alias1Interface);
br.hasImplemented(AliasClass1, Alias1AlternateInterface);

module.exports = AliasClass1;
