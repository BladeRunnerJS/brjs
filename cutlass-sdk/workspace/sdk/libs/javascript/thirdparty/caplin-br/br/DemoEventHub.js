define("br/DemoEventHub", /** @exports br/DemoEventHub */ function(require, module, exports) {
  "use strict";
  
  var br = require( 'br' );
  var ServiceRegistry = require( './ServiceRegistry' );

  // TODO: work out why 'Emitter' should be used here rather than 'emitter'
  var Emitter = require( 'Emitter' );

  var DemoEventHub = function() {
    this.channels = {};
  };
   
  DemoEventHub.prototype.channel = function( channel ) {
    if ( !this.channels[ channel ] ) {
      this.channels[channel] = new DemoEmitter(channel);
    }
   
    return this.channels[ channel ];
  }

  var DemoEmitter = function(name) {
    this.name = name;
  };
  br.extend( DemoEmitter, Emitter );

  DemoEmitter.prototype.trigger = function() {
    console.log( 'trigger', this.name, arguments );
    Emitter.prototype.trigger.apply( this, arguments );
  };

  module.exports = DemoEventHub;

});