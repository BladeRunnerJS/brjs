'use stric';

/**
 * @name br.util.ListenerFactory
 * Construct a new <code>ListenerFactory</code> that can create listeners for the given event, on the given interface.
 * 
 * @param {Function} interfaceFunc The listener interface that objects created with this factory will implement.
 * @param {String} eventName The particular event on the listener interface that will be proxied through.
 * 
 * @class
 * Utility class for creating listener objects that re-route incoming events to a method of your choice.
 * 
 * <p>This class is useful in the following scenarios:</p>
 * 
 * <ul>
 *   <li>Objects that are only interested in a single event, and don't want to implement the entire interface.</li>
 *   <li>Objects that need to implement the same interface multiple times so they can observe multiple objects
 *	 &mdash; this is only possible using proxy listeners like this.</li>
 * </ul>
 */
function ListenerFactory(interfaceFunc, eventName) {
	if(!(interfaceFunc.prototype[eventName] instanceof Function)) {
		throw new TypeError("'" + eventName + "' was not a valid call-back method on the given interface");
	}

	this.m_fInterface = interfaceFunc;
	this.m_sEvent = eventName;
}

/**
 * Returns a listener that will forward received events through to the given method on the given object.
 * 
 * @param {Object} target The object that will be invoked when events are received.
 * @param {String} methodName The name of the method to invoke on the target object.
 * @type Object
 */
ListenerFactory.prototype.createListener = function(target, methodName) {
	if(!target[methodName] || !(target[methodName] instanceof Function)) {
		throw new TypeError("No such method '" + methodName + "' on the target object.");
	}

	var listenerProxy = new this.m_fInterface();
	listenerProxy[this.m_sEvent] = target[methodName].bind(target);

	return listenerProxy;
};

module.exports = ListenerFactory;
