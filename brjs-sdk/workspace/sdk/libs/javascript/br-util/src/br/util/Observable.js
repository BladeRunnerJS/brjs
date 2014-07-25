'use strict';

/**
 * @module br/util/Observable
 */

var Errors = require('br/Errors');

/**
 * Constructs a new <code>Observable</code>.
 *
 * An <code>Observable</code> is a generic implementation of the Observer design pattern that allows an object that
 *  wants to notify other objects (the observers) of events that it raises to do so by using the
 *  <code>Observable</code> to handle the boiler plate code, such as the registration and management of the list of
 *  observers.
 *  <br/><br/>
 *  Example:
 *  <pre>
 *  function RecordDataProvider() {
 *   this.m_oObservable = new br.util.Observable();
 *  }
 *
 *  RecordDataProvider.prototype.addListener = function(oListener) {
 *   this.m_oObservable.addObserver(oListener);
 *  };
 *
 * RecordDataProvider.prototype.removeListener = function(oListener) {
 *  this.m_oObservable.removeObserver(oListener);
 * };
 *
 * RecordDataProvider.prototype.processRecordUpdate = function(sRecordName, mRecordDataMap) {
 *  this.m_oObservable.notifyObservers("recordUpdated", [ sRecordName, mRecordDataMap ]);
 * };
 * </pre>
 * @class
 * @alias module:br/util/Observable
 */
function Observable() {
	/**
	 * The observers that have been registered with this <code>Observable</code>.
	 * @private
	 */
	this.m_pObservers = [];
}

/**
 * Gets the number of listeners within the observer.
 * @return The number of listeners within the observer.
 * @type Number
 */
Observable.prototype.getCount = function() {
	return this.m_pObservers.length;
};

/**
 * Adds the specified observer to the list of those to be called when {@link br.util.Observable#notifyObservers} is
 *  invoked. This method will not prevent a particular observer from being added multiple times. The
 * {@link br.util.Observable#addUniqueObserver} method should be used for this behaviour. If an observer is added
 * multiple times it will receive every notification once for each time it has been registered.
 *
 * @param {Object} observer The object to be added as an observer.
 * @throws {Error} if the specified observer is not an <code>Object</code>, or if it is a native JavaScript
 *  <code>String</code>, <code>Number</code>, <code>Boolean</code> or <code>Function</code>.
 */
Observable.prototype.addObserver = function(observer) {
	if (!(observer instanceof Object) ||
		(observer instanceof String ||
		observer instanceof Number ||
		observer instanceof Boolean ||
		observer instanceof Function)) {

		throw new Errors.InvalidParametersError('An observer must be an object');
	}

	this.m_pObservers.push(observer);
};

/**
 * Adds the specified observer to the list of those to be called when {@link br.util.Observable#notifyObservers} is
 *  invoked. This method prevents a observer that has already been added to an <code>Observable</code> from being added
 *  again. The {@link br.util.Observable#addObserver} method should be used if duplicates are allowed.
 *
 * @param {Object} observer The object to be added as an observer.
 * @return {Boolean} <code>true</code> if the observer was successfully added or <code>false</code> if it failed
 *  because it had already been added before.
 * @throws {Error} if the specified observer is not an <code>Object</code>, or if it is a native JavaScript
 *  <code>String</code>, <code>Number</code>, <code>Boolean</code> or <code>Function</code>.
 */
Observable.prototype.addUniqueObserver = function(observer) {
	var observerNotAdded = (this._getObserverIndex(observer) == -1);

	if (observerNotAdded) {
		this.addObserver(observer);
	}

	return observerNotAdded;
};

/**
 * Gets the index of the specified observer within the <code>m_pObservers</code> array.
 * @private
 *
 * @param {Object} observer The observer the index is required for.
 * @return {Number} The index of the observer within the <code>m_pObservers</code> array or <code>-1</code> if the
 *  observer has not been registered.
 */
Observable.prototype._getObserverIndex = function(observer) {
	var index = -1;

	for (var idx = 0, len = this.m_pObservers.length; idx < len; idx++) {
		if (this.m_pObservers[idx] === observer) {
			index = idx;
			break;
		}
	}

	return index;
};

/**
 * Removes the specified observer from the list of registered observers. It will no longer be notified of any events
 *  that are raised on this <code>Observable</code>.
 *
 * @param {Object} observer The observer to be removed.
 * @return {Boolean} <code>true</code> if the observer has been removed, otherwise <code>false</code>, which indicates
 *  that the observer was not registered.
 */
Observable.prototype.removeObserver = function(observer) {
	var observerIndex = this._getObserverIndex(observer);

	if (observerIndex != -1) {
		this.m_pObservers.splice(observerIndex, 1);
		return true;
	} else {
		return false;
	}
};

/**
 * Removes all observers from this <code>Observable</code>. They will no longer be informed of any events that are
 *  raised on it.
 */
Observable.prototype.removeAllObservers = function() {
	this.m_pObservers = [];
};

/**
 * Gets a list of all the observers that have been registered with this <code>Observable</code>.
 * @private
 *
 * @return {Array} A list of the observers that have been registered.
 */
Observable.prototype._$getAllObservers = function() {
	return this.m_pObservers;
};

/**
 * Invokes the specified method with specified array of parameters on each of the observers that have been added to
 *  this <code>Observable</code>. Please note that this method does not attempt to catch any exceptions that may be
 *  thrown by the caller.  If this is an issue then the {@link br.util.Observable#notifyObserversWithTryCatch} method
 *  should be used instead. It is recommended that before adding an observer to the <code>Observable</code>, it should
 *  be tested to ensure it conforms to the expected interface, and if not it should be rejected.
 *
 * @param {String} methodName The method to be invoked on each of the registered observers.
 * @param {Array} (Optional) parameters An array of the parameters to be passed into the specified method. The first
 *  element of the array will be the first parameter in the callback method, the second element the second parameter,
 *  and so on.
 */
Observable.prototype.notifyObservers = function(methodName, parameters) {
	if (!parameters) {
		parameters = [];
	}

	// operate on copy of the observers array in case an observer removes unsubscribes itself during the call-back
	var observersCopy = this.m_pObservers.slice();
	for (var idx = 0, len = observersCopy.length; idx < len; idx++) {
		var observer = observersCopy[idx];
		if (typeof observer[methodName] !== 'function') {
			throw new Errors.NotSupportedError("Observer does not implement '" + methodName + "'");
		}

		observer[methodName].apply(observer, parameters);
	}
};

/**
 * Invokes the specified method with specified parameters on each of the observers that have been added to this
 *  <code>Observable</code>. Please note that this method does not attempt to catch any exceptions that may be thrown
 *  by the caller. It is recommended that before adding an observer to the <code>Observable</code>, it should be
 * tested to ensure it conforms to the expected interface, and if not it should be rejected.
 *
 * @param {String} methodName The method to be invoked on each of the registered observers.
 * @param \{...\} (Optional) Additional parameters are passed to the observer.
 */
Observable.prototype.notify = function(methodName) {
	this.notifyObservers(methodName, Array.prototype.slice.call(arguments, 1));
};

/**
 * Invokes the specified method with a specified array of parameters on each of the observers that have been added to
 *  this <code>Observable</code>. This method wraps each call to the observer in a <code>try..catch</code> block so
 *  that if any observer throws an exception it will get caught and the execution will continue to the remaining
 *  observers. When exceptions occur, they are wrapped in {@link br.util.Observable.FailedNotification} and an array of
 *  these are returned to the caller.
 *
 * @param {String} methodName The method to be invoked on each of the registered observers.
 * @param {Array} parameters An array of the parameters to be passed into the specified method. The first element of
 *  the array will be the first parameter in the callback method, the second element the second parameter, and so on.
 * @param {boolean} throwExceptions (optional) You can use this parameter if you wish the exception array to be thrown
 *  rather than returned. If no exceptions occur then nothing will be thrown and the method will return normally.
 * @return {Array} The list of <code>br.util.Observable.FailedNotification</code>s that occured or an empty array if no
 *  exceptions occurred.
 *
 * @see br.util.Observable#notifyObservers to notify without guarding against exceptions.
 */
Observable.prototype.notifyObserversWithTryCatch = function(methodName, parameters, throwExceptions) {
	if (!parameters) {
		parameters = [];
	}

	var failedNotifications = [];
	for (var idx = 0, len = this.m_pObservers.length; idx < len; idx++) {
		var observer = this.m_pObservers[idx];

		try {
			observer[methodName].apply(observer, parameters);
		} catch (e) {
			failedNotifications.push(new Observable.FailedNotification(observer, methodName, e));
		}
	}

	if (!throwExceptions) {
		return failedNotifications;
	} else if (failedNotifications.length > 0 && throwExceptions) {
		throw failedNotifications;
	}
};

/**
 * Constructs a new <code>Observable.FailedNotification</code> with the specified observer, method name and exception.
 *
 * @param {Object} observer The observer that threw the exception whilst processing a notification.
 * @param {String} methodName The name of the method that was invoked.
 * @param {Object} exception The exception that was thrown.
 *
 * @class
 * Represents all the information about why a particular observer failed to process a particular event successfully.
 *  These are returned by the {@link br.util.Observable#notifyObserversWithTryCatch} method when an exception occurs
 *  whilst processing a particular event for an observer.
 */
Observable.FailedNotification = function(observer, methodName, exception) {
	this.m_observer = observer;
	this.m_sMethodName = methodName;
	this.m_oException = exception;
};

/**
 * Gets the observer that threw the exception.
 *
 * @return {Object} The observer.
 */
Observable.FailedNotification.prototype.getObserver = function() {
	return this.m_observer;
};

/**
 * Gets the name of the method that was invoked.
 *
 * @return {String} The method name.
 */
Observable.FailedNotification.prototype.getMethodName = function() {
	return this.m_sMethodName;
};

/**
 * Gets the exception that was thrown.
 *
 * @return {String} The exception.
 */
Observable.FailedNotification.prototype.getException = function() {
	return this.m_oException;
};

module.exports = Observable;
