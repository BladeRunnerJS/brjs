
/**
* A workbench tool to display events triggered on the Event Hub
*
* @module br/workbench/tools/EventHubViewer
*/

var br = require( 'br/Core' );
var emitr = require( 'emitr' );
var ko = require( 'ko' );
var moment = require( 'momentjs' );

var WorkbenchComponent = require( 'br/workbench/ui/WorkbenchComponent' );
var KnockoutComponent = require( 'br/knockout/KnockoutComponent' );

/**
 * @alias module:br/workbench/tools/EventHubViewer
 * @param module:br/EventHub eventHub The event hub to use
 */
function EventHubViewer( eventHub ) {
  if( !eventHub ) {
    throw new Error( 'an eventHub must be provided' );
  }

  // used in view model
  this.messages = ko.observableArray();

  this._eventHub = eventHub;

  this._component = new KnockoutComponent( 'br.workbench.event-logger', this );

  this._el = document.createElement("div");
  this._el.className = "presentation-model-viewier";

  var messagesEl = this._component.getElement();
  this._el.appendChild( messagesEl );

  this._eventHub.on( 'new-channel', this._newChannel, this );
}
br.implement( EventHubViewer, WorkbenchComponent );

/** WorkbenchComponent */
EventHubViewer.prototype.getElement = function() {
  return this._el;
};

/** ViewModel function */
EventHubViewer.prototype.clear = function() {
  this.messages.removeAll();
};

/** @private */
EventHubViewer.prototype._log = function( toLog ) {
  var msg = moment().format('H:mm:ss') + ': ' + toLog;
  this.messages.unshift( msg );
};

/** @private */
EventHubViewer.prototype._newChannel = function( channel ) {
  this._log( 'Subscribed: "' + channel.name + '"' );
  channel.on( emitr.meta.AddListenerEvent, this._addListenerEventBindingWrapper( channel ), this );
  channel.on( emitr.meta.DeadEvent, this._eventBindingWrapper( "DeadEvent", channel ), this );
};

/** @private */
EventHubViewer.prototype._addListenerEventBindingWrapper = function( channel ) {
  var wrapper = function() {
    // augment the events so that the channel is passed as the first arg
    var args = Array.prototype.slice.call( arguments, 0 );
    args.unshift( channel );
    this._addListenerEvent.apply( this, args );
  }.bind( this );

  return wrapper;
};

/** @private */
EventHubViewer.prototype._addListenerEvent = function( channel, ev ) {
  // Ignore some meta events as we bind to them
  if( ev.context === this ) {
    return;
  }

  var eventName = ev.event;
  this._log( 'Bound: "' + eventName + '" on "' + channel.name + '"' );

  channel.on( ev.event, this._eventBindingWrapper( eventName, channel ), this );
};

/** @private */
EventHubViewer.prototype._eventBindingWrapper = function( eventName, channel ) {
  var wrapper = function() {
    // augment to pass (channel, eventName, event)
    var args = Array.prototype.slice.call( arguments, 0 );
    args.unshift( eventName );
    args.unshift( channel );
    this._logEventTrigger.apply( this, args );
  }.bind( this );

  return wrapper;
};

function getSafeJsonValue( obj ) {
  var val = obj;
  try {
    val = JSON.stringify( obj );
  }
  catch( e ){}
  return val;
}

/** @private */
EventHubViewer.prototype._logEventTrigger = function( channel, eventName, ev ) {
  var msg = 'Triggered: "' + eventName + '" on "' + channel.name + '" with data "' + getSafeJsonValue( ev ) + '"';
  this._log( msg );
};

EventHubViewer.prototype.close = function() {
  this._el.parentNode.removeChild(this._el);
};


module.exports = EventHubViewer;
