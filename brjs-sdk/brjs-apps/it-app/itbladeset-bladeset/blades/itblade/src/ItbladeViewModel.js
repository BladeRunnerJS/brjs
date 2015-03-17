'use strict';

var ko = require( 'ko' );
var i18n = require( 'br/I18n' );

function ItbladeViewModel() {
	this.eventHub = require('service!br.event-hub');
	this.welcomeMessage = ko.observable('itblade is used for testing BRJS fundamental features');
	this.messagei18n = ko.observable( i18n( 'itapp.itbladeset.itblade.messagei18n' ) );
	this.logWelcome();
}


ItbladeViewModel.prototype.buttonClicked = function() {
	var channel = this.eventHub.channel('itblade-channel');
	channel.trigger( 'hello-event', { some: 'Hello World!' } );
};

ItbladeViewModel.prototype.logWelcome = function() {
	console.log(this.welcomeMessage());
};

module.exports = ItbladeViewModel;
