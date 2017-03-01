require('br-presenter/_resources-test-at/html/test-form.html');
var br = require('br/Core');
var Alias1Interface = require('br/_test-src/Alias1Interface');
var Alias1AlternateInterface = require('br/_test-src/Alias1AlternateInterface');

function AliasClass1() {
};

AliasClass1.prototype.interfaceFunction = function(){
};

AliasClass1.prototype.alternateInterfaceFunction = function(){
};

br.hasImplemented(AliasClass1, Alias1Interface);
br.hasImplemented(AliasClass1, Alias1AlternateInterface);

module.exports = AliasClass1;
