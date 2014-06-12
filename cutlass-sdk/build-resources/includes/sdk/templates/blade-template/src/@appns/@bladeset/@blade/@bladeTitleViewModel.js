'use strict';

var ko = require( 'ko' );
var i18n = require( 'br/I18n' );
var ServiceRegistry = require( 'br/ServiceRegistry' );

function @bladeTitleViewModel() {
	this.eventHub = ServiceRegistry.getService( 'br.event-hub' );
	this.message = ko.observable( 'Hello World!' );
	this.helloWorldI18n = ko.observable( i18n( '@appns.@bladeset.@blade.hello.world' ) );
}

@bladeTitleViewModel.prototype.buttonClicked = function() {
	console.log( 'button clicked' );
	var channel = this.eventHub.channel('@blade-channel');
	channel.trigger( 'hello-event', { some: 'Hello World!' } );
};

module.exports = @bladeTitleViewModel;
