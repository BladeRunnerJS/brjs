"use strict";

var Emitter = require( 'emitr' );
var br = require( 'br/Core' );

var EventHub = function() {
  Emitter.apply( this );
	this.channels = {};
};
br.extend( EventHub, Emitter );

EventHub.prototype.channel = function( channelName ) {
	if ( !this.channels[ channelName ] ) {
		this.channels[ channelName ] = new Channel( channelName );
    this.trigger( 'new-channel', this.channels[ channelName ] );	//TODO: make this a META event
	}
	return this.channels[ channelName ];
};

function Channel( name ) {
  Emitter.apply( this );
	this.name = name;
}
br.extend( Channel, Emitter );


module.exports = EventHub;