"use strict";

/**
* A simple in browser event hub.
* Consists of multiple 'channels' where each channel is an <code>Emitr</code>.
*
* @module br/EventHub
* @see {@link https://bladerunnerjs.github.io/emitr/}
* @see {@link http://bladerunnerjs.org/docs/concepts/event_hub/}
*/

var Emitter = require( 'emitr' );
var br = require( 'br/Core' );

/**
* @class
* @alias module:br/EventHub
* 
* @classdesc
* Create the event hub. This generally isn't required as it's already constructed
* and made available to apps via the [Service Registry]{@link module:br/ServiceRegistry}
*/
var EventHub = function() {
  Emitter.apply( this );
	this.channels = {};
};
br.extend( EventHub, Emitter );

/**
* Get a named channel from the event hub.
*
* @param {String} channelName The name of the channel
* @returns An [Emitr]{@link https://bladerunnerjs.github.io/emitr/} object.
* @see {@link https://bladerunnerjs.github.io/emitr/}
*/
EventHub.prototype.channel = function( channelName ) {
	if ( !this.channels[ channelName ] ) {
		this.channels[ channelName ] = new Channel( channelName );
    this.trigger( 'new-channel', this.channels[ channelName ] );	//TODO: make this a META event
	}
	return this.channels[ channelName ];
};

/** @private **/
function Channel( name ) {
  Emitter.apply( this );
	this.name = name;
}
br.extend( Channel, Emitter );


module.exports = EventHub;
