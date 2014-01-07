define("br/EventHub", /** @exports br/EventHub */ function(require, exports, module) {
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
});
