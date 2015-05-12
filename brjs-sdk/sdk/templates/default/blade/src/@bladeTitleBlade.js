var KnockoutComponent = require('br/knockout/KnockoutComponent')

var @bladeTitleBlade = function() {
	
}

@bladeTitleBlade.prototype.getComponent = function() {
	var @bladeTitleViewModel = require( '@bladeRequirePrefix/@bladeTitleViewModel' )
	var @bladeTitleModel = new @bladeTitleViewModel();
	var @bladeTitleComponent = new KnockoutComponent( '@bladeNamespace.view-template', @bladeTitleModel );
	return @bladeTitleComponent;
}

module.exports = bladeTitleBlade;