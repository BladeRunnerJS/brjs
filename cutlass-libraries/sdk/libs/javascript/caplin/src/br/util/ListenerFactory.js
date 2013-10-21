/**
 * Construct a new <code>ListenerFactory</code> that can create listeners for the given event, on the given interface.
 * 
 * @param {Function} fInterface The listener interface that objects created with this factory will implement.
 * @param {String} sEvent The particular event on the listener interface that will be proxied through.
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
br.util.ListenerFactory = function(fInterface, sEvent) {
	if(!(fInterface.prototype[sEvent] instanceof Function)) {
		throw new TypeError("'" + sEvent + "' was not a valid call-back method on the given interface");
	}
	
	/** @private */
	this.m_fInterface = fInterface;
	
	/** @private */
	this.m_sEvent = sEvent;
};

/**
 * Returns a listener that will forward received events through to the given method on the given object.
 * 
 * @param {Object} oTarget The object that will be invoked when events are received.
 * @param {String} sMethod The name of the method to invoke on the target object.
 * @type Object
 */
br.util.ListenerFactory.prototype.createListener = function(oTarget, sMethod) {
	if(!oTarget[sMethod] || !(oTarget[sMethod] instanceof Function)) {
		throw new TypeError("No such method '" + sMethod + "' on the target object.");
	}
	var oListenerProxy = new this.m_fInterface();
	oListenerProxy[this.m_sEvent] = oTarget[sMethod].bind(oTarget);
	
	return oListenerProxy;
};
