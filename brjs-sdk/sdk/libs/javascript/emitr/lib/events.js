"use strict";

var Event = require('./Event');

var MetaEvent = Event.extend(
		/**
		 * @memberOf Emitter.meta
		 * @class MetaEvent
		 * @param {*} event The event this MetaEvent is about
		 * @classdesc
		 * A parent class for all meta events.
		 */
				function(event) {
			/**
			 * Event provides the identifier of the event that this MetaEvent is about.
			 * @name Emitter.meta.MetaEvent#event
			 * @type {*}
			 */
			this.event = event;
		}
);
/**
 * @memberOf Emitter.meta
 * @extends Emitter.meta.MetaEvent
 * @class ListenerEvent
 * @classdesc
 * A parent class for all MetaEvents about listeners.
 */
var ListenerEvent = MetaEvent.extend(
		function(event, listener, context) {
			MetaEvent.call(this, event);
			/**
			 * The listener this ListenerEvent is about.
			 * @name Emitter.meta.ListenerEvent#listener
			 * @type {function}
			 */
			this.listener = listener;
			/**
			 * The context associated with the listener.
			 * @name Emitter.meta.ListenerEvent#context
			 * @type {?object}
			 */
			this.context = context;
		}
);
/**
 * @memberOf Emitter.meta
 * @class AddListenerEvent
 * @extends Emitter.meta.ListenerEvent
 */
var AddListenerEvent = ListenerEvent.extend();
/**
 * @memberOf Emitter.meta
 * @class RemoveListenerEvent
 * @extends Emitter.meta.ListenerEvent
 */
var RemoveListenerEvent = ListenerEvent.extend();
/**
 * @memberOf Emitter.meta
 * @class DeadEvent
 * @extends Emitter.meta.MetaEvent
 */
var DeadEvent = MetaEvent.extend(
		function(event, args) {
			MetaEvent.call(this, event);
			this.data = args;
		}
);

/**
 * Where the meta events live.
 * @memberOf Emitter
 * @namespace meta
 */
module.exports = {
	MetaEvent: MetaEvent,
	ListenerEvent: ListenerEvent,
	AddListenerEvent: AddListenerEvent,
	RemoveListenerEvent: RemoveListenerEvent,
	DeadEvent: DeadEvent
};
