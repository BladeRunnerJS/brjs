"use strict";

var Emitter = require( 'emitr' );

var EventHub = function() {
	this.channels = {};
};

EventHub.prototype.channel = function( channel ) {
	if ( !this.channels[ channel ] ) {
		this.channels[channel] = new Emitter();
	}
	
	return this.channels[ channel ];
};

module.exports = EventHub;
