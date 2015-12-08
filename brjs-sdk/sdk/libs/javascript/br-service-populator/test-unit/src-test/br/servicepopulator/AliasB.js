
var AliasA = require('br/servicepopulator/AliasA');
var AliasB = require('br/servicepopulator/AliasC');

function AliasB() {
	this.aliasA = new AliasA();
	this.aliasB = new AliasB();
};

module.exports = AliasB;
