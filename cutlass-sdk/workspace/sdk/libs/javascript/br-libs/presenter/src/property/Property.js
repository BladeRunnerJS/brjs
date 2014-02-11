/**
 * Constructs a new <code>Property</code> instance &mdash; you will probably never want to
 * construct a <code>Property</code> yourself since they are not writable.
 *
 * @class
 * Instances of <code>Property</code> are used to store all the values held within a
 * presentation model.
 *
 * <p>Each value within a presentation model is stored within a single <code>Property</code>
 * instance, and updates to properties are displayed immediately within the view. The displayed
 * value may be formatted prior to display if any formatters have been added using the
 * {@link #addFormatter} method.</p>
 *
 * <p>A few varieties of property are available:</p>
 *
 * <dl>
 *   <dt>{@link br.presenter.property.Property}:</dt>
 *   <dd>Used for properties that are not writable by the end-developer, and whose value is
 *	 updated automatically on behalf of the end-developer.</dd>
 *   <dt>{@link br.presenter.property.WritableProperty}:</dt>
 *   <dd>Used for properties that are completely accessible to the end-developer, but which
 *	 are not editable by the end-user.</dd>
 *   <dt>{@link br.presenter.property.EditableProperty}:</dt>
 *   <dd>Used for properties that need to be editable by the end-user, and writable by the
 *	 end-developer.</dd>
 * </dl>
 *
 * @constructor
 * @param {Object} vValue (optional) The default value for this property.
 */
br.presenter.property.Property = function(vValue)
{
	br.presenter.view.knockout.KnockoutProperty.call(this);

	if (vValue instanceof Array) {
		if (this._containsPresentationNode(vValue)) {
			throw new br.Errors.CustomError(br.Errors.LEGACY, "Array passed into property instance contains a PresentationNode, use NodeList instead.");
		}
	}

	/** @private */
	this.m_vValue = vValue;

	/** @private */
	this.m_pFormatters = [];

	/** @private */
	this.m_oObservable = new br.util.Observable();

	/** @private */
	this.m_oChangeListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onPropertyChanged");

	/** @private */
	this.m_oUpdateListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onPropertyUpdated");
};
br.Core.extend(br.presenter.property.Property, br.presenter.view.knockout.KnockoutProperty);

/**
 * Returns the unformatted value for this property.
 * @type Object
 */
br.presenter.property.Property.prototype.getValue = function()
{
	return this.m_vValue;
};

/**
 * @private
 * Sets the internal value for this property and notifies listeners of the
 * change.
 *
 * @param vValue The new value for this property.
 * @type br.presenter.property.Property
 */
br.presenter.property.Property.prototype._$setInternalValue = function(vValue)
{
	var vOldValue = this.m_vValue;
	this.m_vValue = vValue;

	this.updateView(this.getFormattedValue());

	this.m_oObservable.notifyObservers("onPropertyUpdated");
	if (vOldValue !== this.m_vValue)
	{
		this.m_oObservable.notifyObservers("onPropertyChanged");
	}
	return this;
};

/**
 * Returns the formatted value for this property if any formatters have been attached,
 * otherwise returns the raw property value.
 *
 * @type Object
 */
br.presenter.property.Property.prototype.getFormattedValue = function()
{
	var sFormattedValue = this.m_vValue;

	for(var i = 0, l = this.m_pFormatters.length; i < l; ++i)
	{
		var oFormatterPair = this.m_pFormatters[i];
		sFormattedValue = oFormatterPair.formatter.format(sFormattedValue, oFormatterPair.config);
	}

	return sFormattedValue;
};

/**
 * Returns the rendered value after applying any active formatters,
 * otherwise returns the raw property value.
 *
 * @type Object
 */
br.presenter.property.Property.prototype.getRenderedValue = function()
{
	return this.getValue();
};

/**
 * Returns the path that would be required to bind this property from the view.
 *
 * <p>This method is used internally, but might also be useful in allowing the dynamic
 * construction of views for arbitrary presentation models.</p>
 *
 * @type String
 */
br.presenter.property.Property.prototype.getPath = function()
{
	return this.m_sPath;
};

/**
 * Add a {@link br.presenter.formatter.Formatter} that will be applied to the property before it's
 * rendered to screen.
 *
 * <p>Any number of formatters can be added to a property, and will be applied in the same
 * order in which the formatters were added.</p>
 *
 * @param {br.presenter.formatter.Formatter} oFormatter The formatter being added.
 * @param {Object} mConfig (optional) Any additional configuration for the formatter.
 * @type br.presenter.property.Property
 */
br.presenter.property.Property.prototype.addFormatter = function(oFormatter, mConfig)
{
	if(!br.Core.fulfills(oFormatter, br.presenter.formatter.Formatter))
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "oFormatter was not an instance of Formatter");
	}

	var oFormatterPair = {formatter:oFormatter, config:mConfig};
	this.m_pFormatters.push(oFormatterPair);
	return this;
};

/**
 * Add a {@link br.presenter.property.PropertyListener} that will be notified
 * each time the property is updated.
 *
 * @param {br.presenter.property.PropertyListener} oListener The listener to be added.
 * @param {boolean} bNotifyImmediately Whether to invoke the listener immediately using the current value.
 * @type br.presenter.property.Property
 */
br.presenter.property.Property.prototype.addListener = function(oListener, bNotifyImmediately)
{
	if(!br.Core.fulfills(oListener, br.presenter.property.PropertyListener))
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "oListener was not an instance of PropertyListener");
	}

	this.m_oObservable.addObserver(oListener);

	if(bNotifyImmediately) {
		oListener.onPropertyUpdated();
		oListener.onPropertyChanged();
	}

	return this;
};

/**
 * Remove a previously added {@link br.presenter.property.PropertyListener}.
 *
 * @param {br.presenter.property.PropertyListener} oListener The listener being removed.
 * @type br.presenter.property.Property
 */
br.presenter.property.Property.prototype.removeListener = function(oListener)
{
	this.m_oObservable.removeObserver(oListener);
	return this;
};

/**
 * Remove all previously added {@link br.presenter.property.PropertyListener} instances.
 *
 * @type br.presenter.property.Property
 */
br.presenter.property.Property.prototype.removeAllListeners = function()
{
	this.m_oObservable.removeAllObservers();
	return this;
};

/**
 * Convenience method that allows a change listener to be added to added for objects
 * that do not themselves implement {@link br.presenter.property.PropertyListener}.
 *
 * <p>Listeners added using <code>addChangeListener()</code> will only be notified
 * when {@link br.presenter.property.PropertyListener#onPropertyChanged} fires, and
 * will not be notified if any of the other
 * {@link br.presenter.property.PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on multiple
 * properties.</p>
 *
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time the property changes.
 * @param {boolean} [bNotifyImmediately] (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.Property.prototype.addChangeListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oChangeListenerFactory.createListener(oListener, sMethod);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * Convenience method that allows an update listener to be added to added for objects
 * that do not themselves implement {@link br.presenter.property.PropertyListener}.
 *
 * <p>Listeners added using <code>addUpdateListener()</code> will only be notified
 * when {@link br.presenter.property.PropertyListener#onPropertyUpdated} fires, and
 * will not be notified if any of the other
 * {@link br.presenter.property.PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on multiple
 * properties.</p>
 *
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time the property changes.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.Property.prototype.addUpdateListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oUpdateListenerFactory.createListener(oListener, sMethod);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * @private
 */
br.presenter.property.Property.prototype._$setPath = function(sPath)
{
	this.m_sPath = sPath;
};

/**
 * @private
 * @type br.util.Observable
 */
br.presenter.property.Property.prototype._$getObservable = function()
{
	return this.m_oObservable;
};

/**
 * @private
 * Return true if the array pValues contains a {@link br.presenter.node.PresentationNode}.
 * This is used to reject the instantiation of a Property object with an array
 * of PresentationNodes, because the correct way to have a collection of them
 * is to use {@link br.presenter.node.NodeList}.
 *
 * @param {Array} pValues Array of values to become part of the property.
 * @type boolean
 */
br.presenter.property.Property.prototype._containsPresentationNode = function(pValues)
{
	for (var idx = 0, max = pValues.length; idx < max; idx++) {
		if (pValues[idx] instanceof br.presenter.node.PresentationNode) {
			return true;
		}
	}
	return false;
};
