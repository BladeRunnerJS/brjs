var br = require( 'br/Core' );
var WorkbenchComponent = require( 'br/workbench/ui/WorkbenchComponent' );
var emitr = require( 'emitr' );
var fell = require('fell');
var Log = fell.Log;
var KnockoutComponent = require( 'br/knockout/KnockoutComponent' );
var ko = require( 'ko' );

function EventHubViewer( eventHub ) {
  if( !eventHub ) {
    throw new Error( 'an eventHub must be provided' );
  }
  this.constructor.superclass.apply( this, [ 50 ] );
  Log.addDestination( this );

  // View Model properties
  this.messages = ko.observableArray();
  this.clear = function(){};


  this._eventHub = eventHub;

  this._component = new KnockoutComponent( 'br.workbench-event-logger', this );
  
  this._el = document.createElement("div");
  this._el.className = "presentation-model-viewier";

  var messagesEl = this._component.getElement();
  this._el.appendChild( messagesEl );

  this._eventHub.on( 'new-channel', this._newChannel, this );
}
br.extend( EventHubViewer, fell.destination.LogStore );
br.implement( EventHubViewer, WorkbenchComponent );

EventHubViewer.prototype.onLog = function(time, component, level, data) {
  this.constructor.superclass.prototype.onLog.apply( this, arguments );

  this.messages.unshift( this.logRecords.newest().toString() );
};

EventHubViewer.prototype.getElement = function() {
  return this._el;
};

EventHubViewer.prototype._newChannel = function( channel ) {
  Log.info( 'new channel subscription "{0}"', channel.name );
  channel.on( emitr.meta.AddListenerEvent, this._logAddListenerEvent, this );
};

EventHubViewer.prototype._logAddListenerEvent = function( ev ) {
  if( ev.context === this ) {
    return;
  }

  console.log( ev );
  Log.info( ev );
};

EventHubViewer.prototype.close = function() {
  this._el.parentNode.removeChild(this._el);
};


module.exports = EventHubViewer;