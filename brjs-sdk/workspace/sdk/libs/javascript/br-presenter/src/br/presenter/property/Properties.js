/**
 * @module br/presenter/property/Properties
 */

/**
 * Constructs a new <code>Properties</code> instance containing the given list
 * of {@link module:br/presenter/property/Property} objects.
 * 
 * @description
 * A class used to hold collections of properties, and providing utility methods for
 * performing operations over those collections.
 * 
 * @class
 * @param {Array} pProperties (optional) The initial set of properties.
 */
br.presenter.property.Properties = function(pProperties)
{
	/** @private */
	this.m_pProperties = pProperties || [];
	
	/** @private */
	this.m_oChangeListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onPropertyChanged");
	
	/** @private */
	this.m_oUpdateListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onPropertyUpdated");
};

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
br.presenter.property.Properties.prototype.add = function(vProperties)
{
	if( vProperties instanceof Array){
		this._addPropertyArray(vProperties);
	}else if(vProperties instanceof br.presenter.property.Property){
		this._addPropertyArray([vProperties]);
	}else if(vProperties instanceof br.presenter.property.Properties){
		this._addPropertyArray(vProperties.m_pProperties);
	}else{
		throw "br.presenter.property.Properties.prototype.add() unknown type";
	}
};

/**
 * Returns the size of the collection.
 * 
 * @type int
 */
br.presenter.property.Properties.prototype.getSize = function()
{
	return this.m_pProperties.length;
};

/**
 * Invoke <code>setValue()</code> on all writable properties within the collection.
 * 
 * @param {Object} vValue The value that all property instances will be set to.
 */
br.presenter.property.Properties.prototype.setValue = function(vValue)
{
	for(var i = 0; i < this.m_pProperties.length; i++)
	{
		var oProperty = this.m_pProperties[i];
		
		if(oProperty instanceof br.presenter.property.WritableProperty)
		{
			oProperty.setValue(vValue);
		}
	}
};

/**
 * Returns a snapshot of the current collection that can be restored at a later date.
 * 
 * @type br.presenter.property.Snapshot
 */
br.presenter.property.Properties.prototype.snapshot = function()
{
	return new br.presenter.property.Snapshot(this.m_pProperties);
};

/**
 * Add a listener to all properties
 */
br.presenter.property.Properties.prototype.addListener = function(oListener, bNotifyImmediately)
{
	for(var i = 0, l = this.m_pProperties.length; i < l; i++)
	{
		var bLastProperty = i == (l - 1);
		this.m_pProperties[i].addListener(oListener, bNotifyImmediately && bLastProperty);
	}
};

/**
 * Removes all the listeners attached to the properties.
 */
br.presenter.property.Properties.prototype.removeAllListeners = function()
{
	for(var i = 0; i < this.m_pProperties.length; i++)
	{
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
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time the property changes.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.Properties.prototype.addChangeListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oChangeListenerFactory.createListener(oListener, sMethod);
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
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time the property changes.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.Properties.prototype.addUpdateListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oUpdateListenerFactory.createListener(oListener, sMethod);
	this.addListener(oPropertyListener, bNotifyImmediately);
	
	return oPropertyListener;
};

/**
 * @private
 * @param pProperties
 */
br.presenter.property.Properties.prototype._addPropertyArray = function(pProperties)
{
	for(var i = 0; i < pProperties.length; i++){
		this.m_pProperties.push(pProperties[i]);
	}
};
