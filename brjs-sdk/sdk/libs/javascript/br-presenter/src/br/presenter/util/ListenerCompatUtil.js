'use strict';

function ListenerCompatUtil() {
}

ListenerCompatUtil.enhance = function(listenerMethod) {
	return function(listener, method, notifyImmediately) {
		var callback;

		if(typeof(listener) == 'function') {
			callback = listener;
			notifyImmediately = method;
		}
		else {
			if(!listener[method] || !(listener[method] instanceof Function)) {
				throw new TypeError("No such method '" + method + "' on the listener object.");
			}

			callback = listener[method].bind(listener);
		}

		return listenerMethod.call(this, callback, notifyImmediately);
	}
};

module.exports = ListenerCompatUtil;
