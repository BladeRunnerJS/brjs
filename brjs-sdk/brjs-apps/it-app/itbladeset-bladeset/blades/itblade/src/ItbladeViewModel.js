'use strict';

var ko = require( 'ko' );

function ItbladeViewModel() {
	this.eventHub = require('service!br.event-hub');
	this.bladeMessage = ko.observable('Hello from the Itblade View Model');
}

module.exports = ItbladeViewModel;
