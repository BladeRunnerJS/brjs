"use strict";
var br = require( 'br/Core' );
var TestingInterface = require('itapp/itbladeset/itblade/TestingInterface');

var AppAliasesOverride = function() {
};

br.implement(AppAliasesOverride, TestingInterface);


AppAliasesOverride.prototype.implementMe = function(message) {
	return "Hello from an app level aliased class";
};

module.exports = AppAliasesOverride;