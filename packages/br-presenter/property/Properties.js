'use strict';

var Snapshot = require('br-presenter/property/Snapshot');
var WritableProperty = require('br-presenter/property/WritableProperty');
var Property = require('br-presenter/property/Property');
var PropertyListener = require('br-presenter/property/PropertyListener');
var ListenerFactory = require('br-util/ListenerFactory');
var ListenerCompatUtil = require('../util/ListenerCompatUtil');

/**
 * @module br/presenter/property/Properties
 */

/**
 * Constructs a new <code>Properties</code> instance containing the given list
 * of {@link module:br/presenter/property/Property} objects.
 * 
 * @class
 * @alias module:br/presenter/property/Properties
 * 
 * @classdesc
 * A class used to hold collections of properties, and providing utility methods for
 * performing operations over those collections.
 * 
 * @param {Array} pProperties (optional) The initial set of properties.
 */
function Properties(pProperties) {
	/** @private */
	this.m_pProperties = pProperties || [];

	/** @private */
	this.m_oChangeListenerFactory = new ListenerFactory(PropertyListener, 'onPropertyChanged');

	/** @private */
	this.m_oUpdateListenerFactory = new ListenerFactory(PropertyListener, 'onPropertyUpdated');
}

/**
 * Add the given properties to this collection.
 * 
 * The single argument passed to <code>add()</code> can be any of the following types:
 * 
 * <ul>
 *   <li>A single {@link module:br/presenter/property/Property} instance.</li>
 *   <li>An array of {@link module:br/presenter/property/Property} instances.</li>
 *   <li>Another <code>Properties</code> object.</li>
 * </ul>
 * 
 * @param {Object} vProperties The new properties to add.
 */
Properties.prototype.add = function(vProperties) {
	if (vProperties instanceof Array) {
		this._addPropertyArray(vProperties);
	} else if (vProperties instanceof Property) {
		this._addPropertyArray([vProperties]);
	} else if (vProperties instanceof Properties) {
		this._addPropertyArray(vProperties.m_pProperties);
	} else {
		throw 'br.presenter.property.Properties.prototype.add() unknown type';
	}
};

/**
 * Returns array of properties in the collection.
 *
 * @type Array
 */
Properties.prototype.getProperties = function() {
	return this.m_pProperties.slice(0);
};

/**
 * Returns the size of the collection.
 * 
 * @type int
 */
Properties.prototype.getSize = function() {
	return this.m_pProperties.length;
};

/**
 * Invoke <code>setValue()</code> on all writable properties within the collection.
 * 
 * @param {Object} vValue The value that all property instances will be set to.
 */
Properties.prototype.setValue = function(vValue) {
	for (var i = 0; i < this.m_pProperties.length; i++) {
		var oProperty = this.m_pProperties[i];

		if (oProperty instanceof WritableProperty) {
			oProperty.setValue(vValue);
		}
	}
};

/**
 * Returns a snapshot of the current collection that can be restored at a later date.
 * 
 * @type br.presenter.property.Snapshot
 */
Properties.prototype.snapshot = function() {
	return new Snapshot(this.m_pProperties);
};

/**
 * Add a listener to all properties
 */
Properties.prototype.addListener = function(oListener, bNotifyImmediately) {
	for (var i = 0, l = this.m_pProperties.length; i < l; i++) {
		var bLastProperty = i == (l - 1);
		this.m_pProperties[i].addListener(oListener, bNotifyImmediately && bLastProperty);
	}
};

/**
 * Removes all the listeners attached to the properties.
 */
Properties.prototype.removeAllListeners = function() {
	for (var i = 0; i < this.m_pProperties.length; i++) {
		this.m_pProperties[i].removeAllListeners();
	}
};

/**
 * Convenience method that allows a change listener to be added to added for objects
 * that do not themselves implement {@link module:br/presenter/property/PropertyListener}.
 * 
 * <p>Listeners added using <code>addChangeListener()</code> will only be notified
 * when {@link module:br/presenter/property/PropertyListener#onPropertyChanged} fires, and
 * will not be notified if any of the other
 * {@link module:br/presenter/property/PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on multiple
 * properties.</p>
 * 
 * @param {Function} fCallback The call-back that will be invoked each time the property changes.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
Properties.prototype.addChangeListener = function(fCallback, bNotifyImmediately) {
	var oPropertyListener = this.m_oChangeListenerFactory.createListener(fCallback);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * Convenience method that allows an update listener to be added to added for objects
 * that do not themselves implement {@link module:br/presenter/property/PropertyListener}.
 *
 * <p>Listeners added using <code>addUpdateListener()</code> will only be notified
 * when {@link module:br/presenter/property/PropertyListener#onPropertyUpdated} fires, and
 * will not be notified if any of the other
 * {@link module:br/presenter/property/PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on multiple
 * properties.</p>
 *
 * @param {Function} fCallback The call-back that will be invoked each time the property is updated.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
Properties.prototype.addUpdateListener = function(fCallback, bNotifyImmediately) {
	var oPropertyListener = this.m_oUpdateListenerFactory.createListener(fCallback);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * @private
 * @param pProperties
 */
Properties.prototype._addPropertyArray = function(pProperties) {
	for (var i = 0; i < pProperties.length; i++) {
		this.m_pProperties.push(pProperties[i]);
	}
};

Properties.prototype.addChangeListener = ListenerCompatUtil.enhance(Properties.prototype.addChangeListener);
Properties.prototype.addUpdateListener = ListenerCompatUtil.enhance(Properties.prototype.addUpdateListener);

module.exports = Properties;
