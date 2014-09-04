/**
 * @module br/presenter/property/EditableProperty
 */

/**
 * Constructs a new <code>EditableProperty</code> instance.
 * 
 * @class
 * @alias module:br/presenter/property/EditableProperty
 * @extends module:br/presenter/property/WritableProperty
 * @implements module:br/presenter/validator/ValidationResultListener
 * 
 * @classdesc
 * <code>EditableProperty</code> is identical to {@link module:br/presenter/property/WritableProperty},
 * except that it also has the ability to be edited by users.
 * 
 * <p>Because editable properties can be displayed using controls that allow unconstrained input (e.g
 * text input boxes), {@link #addValidator} can be used to add validators that provide user feedback
 * when invalid values are entered, and {@link #addParser} can be used to help convert user input into
 * valid forms.</p>
 * 
 * @param {Object} vValue (optional) The default value for this property.
 */
br.presenter.property.EditableProperty = function(vValue)
{
	// call super constructor
	br.presenter.property.WritableProperty.call(this, vValue);
	
	/** @private */
	this.m_pParsers = [];
	
	/** @private */
	this.m_pValidators = [];
	
	/** @private */
	this.m_oValidationResultCollator = null;

	/** @private */
	this.m_oValidationErrorListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onValidationError");

	/** @private */
	this.m_oValidationSuccessListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onValidationSuccess");
	
	/** @private */
	this.m_oValidationCompleteListenerFactory = new br.util.ListenerFactory(br.presenter.property.PropertyListener, "onValidationComplete");
};
br.Core.extend(br.presenter.property.EditableProperty, br.presenter.property.WritableProperty);
br.Core.implement(br.presenter.property.EditableProperty, br.presenter.validator.ValidationResultListener);

/**
 * Adds a {@link module:br/presenter/parser/Parser} that will be run each time the user enters a
 * new value.
 * 
 * <p>Parsers allow user input to be normalized prior to validation. For example, the
 * user may be allowed to enter '1M' into an amount field, and a parser might convert
 * this to '1000000' before it is validated by a numeric validator.</p>
 * 
 * <p>Any number of parsers can be added to an editable property, and will be applied
 * in the same way that production rules are applied in production rule systems:</p>
 * 
 * <ol>
 *   <li>The parsers will be iterated one by one in the same order in which they were
 *	added.</li>
 *   <li>If any parser is able to produce a new value from the input, then this
 *	value becomes the current value and the process restarts at step 1.</li>
 *   <li>Once a clean run through all the parsers is achieved (with none of them
 *	available to produce new input) the parsing phase is complete.</li>
 * </ol>
 * 
 * <p>By configuring a number of simple parsers in the same way as production
 * rules are used, complex input handling can be supported.</p>
 * 
 * @param {module:br/presenter/parser/Parser} oParser the {@link module:br/presenter/parser/Parser} being added.
 * @param {Object} mConfig (optional) Any additional configuration for the parser.
 * @type br.presenter.property.EditableProperty
 */
br.presenter.property.EditableProperty.prototype.addParser = function(oParser, mConfig)
{
	if(!br.Core.fulfills(oParser, br.presenter.parser.Parser))
	{
		throw new br.Errors.InvalidParametersError("oParser was not an instance of Parser");
	}

	this.m_pParsers.push({parser:oParser, config:mConfig});

	return this;
};

/**
 * Adds a {@link module:br/presenter/validator/Validator} that will be run each time the user enters a
 * new value.
 * 
 * <p>Validators allow users to be immediately informed when any of their input is
 * invalid. The {@link module:br/presenter/node/Field},
 * {@link module:br/presenter/node/SelectionField} and
 * {@link module:br/presenter/node/MultiSelectionField} classes all listen to the
 * validation call-backs on {@link module:br/presenter/property/PropertyListener} and
 * maintain <code>hasError</code> and <code>failureMessage</code> properties that can
 * be displayed within the view.</p>
 * 
 * @param {module:br/presenter/validator/Validator} oValidator the {@link module:br/presenter/validator/Validator} being added.
 * @param {Object} mConfig (optional) Any additional configuration for the validator.
 * @type br.presenter.property.EditableProperty
 */
br.presenter.property.EditableProperty.prototype.addValidator = function(oValidator, mConfig)
{
	if(!br.Core.fulfills(oValidator, br.presenter.validator.Validator))
	{
		throw new br.Errors.InvalidParametersError("oValidator was not an instance of Validator");
	}

	this.m_pValidators.push({validator:oValidator, config:mConfig});

	return this;
};

/**
 * @private
 * @see br.presenter.property.Property#addListener
 */
br.presenter.property.EditableProperty.prototype.addListener = function(oListener, bNotifyImmediately)
{
	br.presenter.property.WritableProperty.prototype.addListener.call(this, oListener, bNotifyImmediately);
	
	if(bNotifyImmediately)
	{
		this.forceValidation();
	}
	
	return this;
};

/**
 * A convenience method that allows <em>validation error</em> listeners to be added for objects that do
 * not themselves implement {@link module:br/presenter/property/PropertyListener}.
 * 
 * <p>Listeners added using <code>addValidationErrorListener()</code> will only be
 * notified when {@link module:br/presenter/property/PropertyListener#onValidationError}
 * fires, and will not be notified if any of the other
 * {@link module:br/presenter/property/PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on
 * multiple properties.</p>
 * 
 * <p>The invoked method will be passed two arguments:</p>
 * 
 * <ul>
 *   <li><code>vPropertyValue</code> &mdash; The current value of the property.</li>
 *   <li><code>sErrorMessage</code> &mdash; The failure message.</li>
 * </ul>
 * 
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time there is a validation error.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.EditableProperty.prototype.addValidationErrorListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oValidationErrorListenerFactory.createListener(oListener, sMethod);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * A convenience method that allows <em>validation success</em> listeners to be added for objects that do
 * not themselves implement {@link module:br/presenter/property/PropertyListener}.
 * 
 * <p>Listeners added using <code>addValidationSuccessListener()</code> will only be
 * notified when {@link module:br/presenter/property/PropertyListener#onValidationSuccess}
 * fires, and will not be notified if any of the other
 * {@link module:br/presenter/property/PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on
 * multiple properties.</p>
 *
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time validation is successful.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.EditableProperty.prototype.addValidationSuccessListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oValidationSuccessListenerFactory.createListener(oListener, sMethod);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * A convenience method that allows <em>validation complete</em> listeners to be added for objects that do
 * not themselves implement {@link module:br/presenter/property/PropertyListener}.
 * 
 * <p>Listeners added using <code>addValidationCompleteListener()</code> will only be
 * notified when {@link module:br/presenter/property/PropertyListener#onValidationComplete}
 * fires, and will not be notified if any of the other
 * {@link module:br/presenter/property/PropertyListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on
 * multiple properties.</p>
 *
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time validation is successful.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.property.PropertyListener
 */
br.presenter.property.EditableProperty.prototype.addValidationCompleteListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oPropertyListener = this.m_oValidationCompleteListenerFactory.createListener(oListener, sMethod);
	this.addListener(oPropertyListener, bNotifyImmediately);

	return oPropertyListener;
};

/**
 * Sets the unformatted value for this property and notifies listeners of the
 * change.
 * 
 * <p>This method is the same as {@link module:br/presenter/property/WritableProperty#setValue},
 * except that validation will also be performed.</p>
 * @param {Variant} vValue The value to set. 
 * @see br.presenter.property.WritableProperty#setValue
 */
br.presenter.property.EditableProperty.prototype.setValue = function(vValue)
{
	// call super method
	br.presenter.property.WritableProperty.prototype.setValue.call(this, vValue);
	this.forceValidation();
	
	return this;
};

/**
 * Accepts a user entered value that may need to be parsed before calling {@link #setValue}.
 * 
 * @param {Object} vUserEnteredValue The unparsed value to set.
 * @type br.presenter.property.EditableProperty
 */
br.presenter.property.EditableProperty.prototype.setUserEnteredValue = function(vUserEnteredValue)
{
	var vParsedValue = this._parse(vUserEnteredValue);
	this.setValue(vParsedValue);
	return this;
};

/**
 * Force the property to be re-validated.
 * 
 * <p>This method is useful for code that wishes to perform cross-property validation &mdash; it is
 * used by the {@link module:br/presenter/validator/CrossValidationPropertyBinder} class for
 * example.</p>
 */
br.presenter.property.EditableProperty.prototype.forceValidation = function()
{
	var vValue = this.getValue();
	
	if(this.m_oValidationResultCollator)
	{
		this.m_oValidationResultCollator.cancelValidationResults();
	}
	
	// No validators means any value is valid so send success
	if (this.m_pValidators.length === 0)
	{
		var oValidationResult = new br.presenter.validator.ValidationResult(this);
		oValidationResult.setResult(true, "");
	}
	else
	{
		this.m_oValidationResultCollator = new br.presenter.property.ValidationResultCollator(this, this.m_pValidators.length);
		
		// shoot off validate commands for each validator with their own ValidationResult object
		for(var i = 0, max = this.m_pValidators.length; i < max; ++i)
		{
			var oValidationResult = this.m_oValidationResultCollator.createValidationResult(i);
			var oValidator = this.m_pValidators[i];
			oValidator.validator.validate(vValue, oValidator.config, oValidationResult);

			// Handle early failure of *synchronous* validators
			if(oValidationResult.hasResult() && !oValidationResult.isValid())
			{
				break;
			}
		}
	}
};


/**
 * This method provides a synchronous way of checking the validation state. 
 * 
 */
br.presenter.property.EditableProperty.prototype.hasValidationError = function()
{ 
	var vValue = this.getValue(); 
	return this._hasValidationError(vValue); 
};

/**
 * @private
 */
br.presenter.property.EditableProperty.prototype._hasValidationError = function(vValue)
{ 
	for (var i = 0, l = this.m_pValidators.length; i < l; ++i) { 
		var oValidator = this.m_pValidators[i]; 
		var oValidationResult = new br.presenter.validator.ValidationResult(); 
		oValidator.validator.validate(vValue, oValidator.config, oValidationResult); 
		if (!oValidationResult.isValid()) return true; 
	} 
	return false; 
}; 


// *********************** ValidationResultListener Interface ***********************

/**
 * @private
 * @param {module:br/presenter/validator/ValidationResult} oValidationResult
 * @see br.presenter.validator.ValidationResultListener#onValidationResultReceived
 */
br.presenter.property.EditableProperty.prototype.onValidationResultReceived = function(oValidationResult)
{
	if(oValidationResult.isValid())
	{
		this._$getObservable().notifyObservers("onValidationSuccess", []);
	}
	else
	{
		var vValue = this.getValue();
		this._$getObservable().notifyObservers("onValidationError", [vValue, oValidationResult.getFailureMessage()]);
	}
	
	this._$getObservable().notifyObservers("onValidationComplete", []);
};


// *********************** Private Methods ***********************

/**
 * @private
 * @param {Object} vValue
 * @type Object
 */
br.presenter.property.EditableProperty.prototype._parse = function(vValue)
{
	var vParsedValue = vValue;
	var bValueChanged;
	var parsers = this.m_pParsers.slice();

	do
	{
		bValueChanged = false;

		for(var i = 0, l = parsers.length; i < l; ++i)
		{
			var oParser = parsers[i];
			var vNewValue = oParser.parser.parse(vParsedValue, oParser.config);

			if(vNewValue !== null && vNewValue !== undefined && vNewValue !== vParsedValue)
			{
				vParsedValue = vNewValue;
				bValueChanged = true;
				
				if(oParser.parser.isSingleUseParser()) {
					parsers.splice(i, 1);
				}
				break;
			}
		}
	} while(bValueChanged == true);

	return vParsedValue;
};
