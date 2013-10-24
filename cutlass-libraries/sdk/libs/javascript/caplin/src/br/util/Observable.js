/**
 * Constructs a new <code>Observable</code>.
 * 
 * @class
 * An <code>Observable</code> is a generic implementation of the Observer design pattern that
 * allows an object that wants to notify other objects (the observers) of events that it raises to
 * do so by using the <code>Observable</code> to handle the boiler plate code, such as the
 * registration and management of the list of observers.
 * <br/><br/>
 * Example:
 * <pre>
 * function RecordDataProvider()
 * {
 *	this.m_oObservable = new br.util.Observable();
 * }
 * 
 * RecordDataProvider.prototype.addListener = function(oListener)
 * {
 *	this.m_oObservable.addObserver(oListener);
 * };
 * 
 * RecordDataProvider.prototype.removeListener = function(oListener)
 * {
 *	this.m_oObservable.removeObserver(oListener);
 * };
 * 
 * RecordDataProvider.prototype.processRecordUpdate = function(sRecordName, mRecordDataMap)
 * {
 *	this.m_oObservable.notifyObservers("recordUpdated", [ sRecordName, mRecordDataMap ]);
 * };
 * </pre>
 */
br.util.Observable = function() {
	/**
	 * The observers that have been registered with this <code>Observable</code>.
	 * 
	 * @type Array
	 * @private
	 */
	this.m_pObservers = [];
};

/**
 * Gets the number of listeners within the observer.
 * @return The number of listeners within the observer.
 * @type Number
 */
br.util.Observable.prototype.getCount = function() {
	return this.m_pObservers.length;
};

/**
 * Adds the specified observer to the list of those to be called when
 * {@link br.util.Observable#notifyObservers} is invoked. This method will not prevent a
 * particular observer from being added multiple times. The
 * {@link br.util.Observable#addUniqueObserver} method should be used for this behaviour. If an
 * observer is added multiple times it will receive every notification once for each time it has
 * been registered.
 * 
 * @param {Object} oObserver The object to be added as an observer.
 * @throws {Error} if the specified observer is not an <code>Object</code>,
 *		 or if it is a native JavaScript <code>String</code>, <code>Number</code>,
 *		 <code>Boolean</code> or <code>Function</code>.
 */
br.util.Observable.prototype.addObserver = function(oObserver) {
	if (!(oObserver instanceof Object)
			|| (oObserver instanceof String || oObserver instanceof Number
					|| oObserver instanceof Boolean || oObserver instanceof Function)) {
		var Errors = require('br/Errors');
		throw new Errors.InvalidParametersError("An observer must be an object");
	}
	this.m_pObservers.push(oObserver);
};

/**
 * Adds the specified observer to the list of those to be called when
 * {@link br.util.Observable#notifyObservers} is invoked. This method prevents a observer that
 * has already been added to an <code>Observable</code> from being added again. The
 * {@link br.util.Observable#addObserver} method should be used if duplicates are allowed.
 * 
 * @param {Object} oObserver The object to be added as an observer.
 * @type boolean
 * @return <code>true</code> if the observer was successfully added or <code>false</code> if it
 *		 failed because it had already been added before.
 * @throws {Error} if the specified observer is not an <code>Object</code>,
 *		 or if it is a native JavaScript <code>String</code>, <code>Number</code>,
 *		 <code>Boolean</code> or <code>Function</code>.
 */
br.util.Observable.prototype.addUniqueObserver = function(oObserver) {
	var bObserverNotAdded = (this._getObserverIndex(oObserver) == -1);
	if (bObserverNotAdded) {
		this.addObserver(oObserver);
	}
	return bObserverNotAdded;
};

/**
 * Gets the index of the specified observer within the <code>m_pObservers</code> array.
 * 
 * @param {Object} oObserver The observer the index is required for.
 * @type int
 * @return The index of the observer within the <code>m_pObservers</code> array or <code>-1</code> if the observer has not been
 *		 registered.
 * @private
 */
br.util.Observable.prototype._getObserverIndex = function(oObserver) {
	var nIndex = -1;
	for ( var i = 0, nLength = this.m_pObservers.length; i < nLength; ++i) {
		if (this.m_pObservers[i] === oObserver) {
			nIndex = i;
			break;
		}
	}
	return nIndex;
};

/**
 * Removes the specified observer from the list of registered observers. It will no longer be
 * notified of any events that are raised on this <code>Observable</code>.
 * 
 * @param {Object} oObserver The observer to be removed.
 * @type boolean
 * @return <code>true</code> if the observer has been removed, otherwise <code>false</code>, which
 *		 indicates that the observer was not registered.
 */
br.util.Observable.prototype.removeObserver = function(oObserver) {
	var nObserverIndex = this._getObserverIndex(oObserver);
	if (nObserverIndex != -1) {
		this.m_pObservers.splice(nObserverIndex, 1);
		return true;
	} else {
		return false;
	}
};

/**
 * Removes all observers from this <code>Observable</code>. They will no longer be informed of any
 * events that are raised on it.
 */
br.util.Observable.prototype.removeAllObservers = function() {
	this.m_pObservers = [];
};

/**
 * Gets a list of all the observers that have been registered with this <code>Observable</code>.
 * 
 * @type Array
 * @return A list of the observers that have been registered.
 * @private
 */
br.util.Observable.prototype._$getAllObservers = function() {
	return this.m_pObservers;
};

/**
 * Invokes the specified method with specified array of parameters on each of the observers that have been
 * added to this <code>Observable</code>. Please note that this method does not attempt to
 * catch any exceptions that may be thrown by the caller.  If this is an issue then the
 * {@link br.util.Observable#notifyObserversWithTryCatch} method should be used instead.
 * It is recommended that before adding an observer to the <code>Observable</code>, it should be
 * tested to ensure it conforms to the expected interface, and if not it should be rejected.
 *
 * @param {String} sMethodName The method to be invoked on each of the registered observers.
 * @param {Array} (Optional) pParameters An array of the parameters to be passed into the specified method.
 *		The first element of the array will be the first parameter in the callback method, the
 *		second element the second parameter, and so on.
 */
br.util.Observable.prototype.notifyObservers = function(sMethodName, pParameters) {
	if (!pParameters) {
		pParameters = [];
	}

	// operate on copy of the observers array in case an observer removes unsubscribes itself during the call-back
	var pObserversCopy = this.m_pObservers.slice();
	for ( var i = 0, nLength = pObserversCopy.length; i < nLength; ++i) {
		var oObserver = pObserversCopy[i];
		if (typeof oObserver[sMethodName] != "function") {
			var Errors = require('br/Errors');
			throw new Errors.NotSupportedError("Observer does not implement '" + sMethodName + "'");
		}

		oObserver[sMethodName].apply(oObserver, pParameters);
	}
};

/**
 * Invokes the specified method with specified parameters on each of the observers that have been
 * added to this <code>Observable</code>. Please note that this method does not attempt to
 * catch any exceptions that may be thrown by the caller.  
 * It is recommended that before adding an observer to the <code>Observable</code>, it should be
 * tested to ensure it conforms to the expected interface, and if not it should be rejected.
 *
 * @param {String} sMethodName The method to be invoked on each of the registered observers.
 * @param {...} (Optional) Additional parameters are passed to the observer
 */
br.util.Observable.prototype.notify = function(sMethodName) {
	this.notifyObservers(sMethodName, Array.prototype.slice.call(arguments, 1));
};

/**
 * Invokes the specified method with a specified array of parameters on each of the observers that have been
 * added to this <code>Observable</code>. This method wraps each call to the observer in a
 * <code>try..catch</code> block so that if any observer throws an exception it will get caught and
 * the execution will continue to the remaining observers. When exceptions occur, they are wrapped in 
 * {@link br.util.Observable.FailedNotification} and an array of these are returned to the caller.
 * 
 * @param {String} sMethodName The method to be invoked on each of the registered observers.
 * @param {Array} pParameters An array of the parameters to be passed into the specified method.
 *		The first element of the array will be the first parameter in the callback method, the
 *		second element the second parameter, and so on.
 * @param {boolean} bThrowExceptions (optional) You can use this parameter if you wish the exception array to be thrown rather
 * than returned. If no exceptions occur then nothing will be thrown and the method will return normally.
 * @return {Array} The list of <code>br.util.Observable.FailedNotification</code>s that occured or an empty
 * array if no exceptions occurred.
 * 
 * @see br.util.Observable#notifyObservers to notify without guarding against exceptions
 */
br.util.Observable.prototype.notifyObserversWithTryCatch = function(sMethodName, pParameters, bThrowExceptions) {
	if (!pParameters) {
		pParameters = [];
	}

	var pFailedNotifications = [];
	for ( var i = 0, nLength = this.m_pObservers.length; i < nLength; ++i) {
		var oObserver = this.m_pObservers[i];
		try {
			oObserver[sMethodName].apply(oObserver, pParameters);
		} catch (e) {
			pFailedNotifications.push(new br.util.Observable.FailedNotification(oObserver, sMethodName, e));
		}
	}

	if (!bThrowExceptions) {
		return pFailedNotifications;
	} else if (pFailedNotifications.length > 0 && bThrowExceptions) {
		throw pFailedNotifications;
	}
};

/**
 * Constructs a new <code>Observable.FailedNotification</code> with the specified observer, method
 * name and exception.
 * 
 * @param {Object} oObserver The observer that threw the exception whilst processing a
 *		notification.
 * @param {String} sMethodName The name of the method that was invoked.
 * @param {Object} oException The exception that was thrown.
 * 
 * @class
 * Represents all the information about why a particular observer failed to process a particular
 * event successfully. These are returned by the {@link br.util.Observable#notifyObserversWithTryCatch}
 * method when an exception occurs whilst processing a particular event for an observer.
 */
br.util.Observable.FailedNotification = function(oObserver, sMethodName, oException) {
	this.m_oObserver = oObserver;
	this.m_sMethodName = sMethodName;
	this.m_oException = oException;
};

/**
 * Gets the observer that threw the exception.
 * 
 * @type Object
 * @return The observer.
 */
br.util.Observable.FailedNotification.prototype.getObserver = function() {
	return this.m_oObserver;
};

/**
 * Gets the name of the method that was invoked.
 * 
 * @type String
 * @return The method name.
 */
br.util.Observable.FailedNotification.prototype.getMethodName = function() {
	return this.m_sMethodName;
};

/**
 * Gets the exception that was thrown.
 * 
 * @type String
 * @return The exception.
 */
br.util.Observable.FailedNotification.prototype.getException = function() {
	return this.m_oException;
};