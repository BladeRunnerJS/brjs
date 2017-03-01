'use strict';

/**
 * @module br/presenter/property/PropertyHelper
 */

/**
 * @class
 * @alias module:br/presenter/property/PropertyHelper
 *
 * @classdesc
 * <code>PropertyHelper</code> is a utility that simplifies the attaching and removing of property listeners. Compared
 * with adding and removing listeners on [Property]{@link module:br/presenter/property/Property} directly, using the
 * equivalent <code>PropertyHelper</code> methods has a number of benefits:
 *
 * <ul>
 *   <li>It allows an observer's complete set of property listeners to be referred to using a single object reference.</li>
 *   <li>It allows every property listener to be removed using a single operation, without affecting the listeners of
 *     other property observers.</li>
 *   <li>It allows an observer's listeners for a single property to be removed.</li>
 * </ul>
 */
function PropertyHelper() {
	this.m_pListeners = [];
}

/**
 * Adds a change listener to the supplied property. The function handler will be called with three arguments: the new value
 * of the property, the previous value of the property and the property itself.
 * <p>You can use this method in case you want to receive in your change handler the property's old value as well as the new one.</p>
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} [bCallNow]
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.addChangeNotification = function(oProperty, oScope, vHandler, bCallNow) {
	if (typeof vHandler == 'string') {
		vHandler = oScope[vHandler];
	}

	var vLastValue = oProperty.getValue();
	var oHandler = {
		onChange: function() {
			var vOldValue = vLastValue;
			var vNewValue = vLastValue = oProperty.getValue();
			vHandler.call(oScope, vNewValue, vOldValue, oProperty);
		}
	};

	return this._addListener('addChangeListener', oProperty, oHandler, 'onChange', bCallNow);
};

/**
 * Adds a change listener to the supplied property.
 * Can accept either a String method name or a function reference as the handler callback
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} [bCallNow]
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.addChangeListener = function(oProperty, oScope, vHandler, bCallNow) {
	return this._addListener('addChangeListener', oProperty, oScope, vHandler, bCallNow);
};

/**
 * Adds an update listener to the supplied property.
 * Can accept either a String method name or a function reference as the handler callback
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} [bCallNow]
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.addUpdateListener = function(oProperty, oScope, vHandler, bCallNow) {
	return this._addListener('addUpdateListener', oProperty, oScope, vHandler, bCallNow);
};

/**
 * Adds a listener for the validation success event to the supplied property.
 * Can accept either a String method name or a function reference as the handler callback
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} [bCallNow]
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.addValidationSuccessListener = function(oProperty, oScope, vHandler, bCallNow) {
	return this._addListener('addValidationSuccessListener', oProperty, oScope, vHandler, bCallNow);
};

/**
 * Adds a listener for the validation error event to the supplied property.
 * Can accept either a String method name or a function reference as the handler callback
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} [bCallNow]
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.addValidationErrorListener = function(oProperty, oScope, vHandler, bCallNow) {
	return this._addListener('addValidationErrorListener', oProperty, oScope, vHandler, bCallNow);
};

/**
 * Adds a listener for the validation state changing on the supplied editable property.
 *
 * Can accept either a String method name or a function reference as the handler callback. Note that as this method
 * attaches multiple handlers, there is no return value; in order to remove listeners created by this method, use either
 * #removeAllListeners or #clearProperty.
 *
 * The function used as the handler will be called with two arguments, a boolean value that shows the validation status
 * (true for valid and false for error) and the property.
 *
 * @param {module:br/presenter/property/EditableProperty} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} [bCallNow]
 */
PropertyHelper.prototype.addValidationChangeListener = function(oProperty, oScope, vHandler, bCallNow) {
	if (typeof vHandler == 'string') {
		vHandler = oScope[vHandler];
	}

	var oHandler = {
		onSuccess: function() {
			vHandler.call(oScope, true, oProperty);
		},
		onError: function() {
			vHandler.call(oScope, false, oProperty);
		}
	};

	this._addListener('addValidationSuccessListener', oProperty, oHandler, 'onSuccess', false);
	this._addListener('addValidationErrorListener', oProperty, oHandler, 'onError', false);

	if (bCallNow) {
		oProperty.hasValidationError() ? oHandler.onError() : oHandler.onSuccess();
	}
};

/**
 * Utility function that will bind any type of listener and cache the property and listener for future removal
 *
 * @private
 * @param {String} listenerType
 * @param {module:br/presenter/property/Property} oProperty
 * @param {Object} oScope
 * @param {Function|String} vHandler
 * @param {Boolean} bCallNow
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype._addListener = function(listenerType, oProperty, oScope, vHandler, bCallNow) {
	if ('function' === typeof vHandler) {
		oScope = {
			handler: vHandler.bind(oScope)
		};
		vHandler = 'handler';
	}

	this.m_pListeners.push({
		type: listenerType,
		listener: oProperty[listenerType](oScope, vHandler, bCallNow),
		property: oProperty
	});

	return this;
};

/**
 * Removes any listener bound to the supplied property that was attached by this PropertyHelper
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @param {String} [sType]
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.clearProperty = function(oProperty, sType) {
	var pListeners = this.m_pListeners;
	for (var i = pListeners.length - 1; i >= 0; i--) {
		var oListener = pListeners[i];

		if (oListener.property === oProperty && (!sType || sType === oListener.type)) {
			oProperty.removeListener(oListener.listener);
			pListeners.splice(i, 1);
		}
	}

	return this;
};

/**
 * Remove change handlers from the selected property that have been attached through this PropertyHelper
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.removeChangeListeners = function(oProperty) {
	return this.clearProperty(oProperty, 'addChangeListener');
};

/**
 * Remove validation success handlers from the selected property that have been attached through this PropertyHelper
 *
 * @param {module:br/presenter/property/Property} oProperty
 * @return {module:br/presenter/property/PropertyHelper}
 */
PropertyHelper.prototype.removeValidationSuccessListeners = function(oProperty) {
	return this.clearProperty(oProperty, 'addValidationSuccessListener');
};

/**
 * Removes all listeners attached through this PropertyHelper
 */
PropertyHelper.prototype.removeAllListeners = function() {
	var pListeners = this.m_pListeners;
	for (var i = pListeners.length - 1; i >= 0; i--) {
		pListeners[i].property.removeListener(pListeners[i].listener);
	}

	this.m_pListeners = [];
	return this;
};

module.exports = PropertyHelper;
