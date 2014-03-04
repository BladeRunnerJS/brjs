/**
 * Constructs a new instance of <code>SelectionField</code>.
 * 
 * @class
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model a multi-selection field on screen.
 * 
 * <p>Multi-Selection fields can be rendered using a number of different controls:</p>
 * 
 * <ul>
 *   <li>check boxes</li>
 *   <li>multi select box</li>
 * </ul>
 * 
 * <p>By default, multi-selection fields are automatically configured to update the
 * currently selected options (held within {@link #value}) if the list of available
 * {@link #options} changes &mdash; any selected options that are no longer available
 * will be automatically removed from the list. You can change this behaviour by invoking
 * {@link #automaticallyUpdateValueWhenOptionsChange} with <code>false</code>.</p>
 * 
 * @constructor
 * @param {Object} vOptions The list of available options, either using an array, a map of strings or as a {@link br.presenter.node.OptionsNodeList}.
 * @param {Object} vValues (optional) The list of currently selected options, either using an array or as a {@link br.presenter.property.EditableProperty} containing an array.
 * @extends br.presenter.node.PresentationNode
 */
br.presenter.node.MultiSelectionField = function(vOptions, vValues)
{
	if((vValues instanceof br.presenter.property.Property) && !(vValues instanceof br.presenter.property.EditableProperty))
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "MultiSelectionField constructor: can't pass non-editable property as parameter");
	}
	vValues = vValues || [];
	
	// allow arguments to be passed as either properties or primitives
	var oProperty = (vValues instanceof br.presenter.property.EditableProperty) ? vValues : new br.presenter.property.EditableProperty(vValues);
	/**
	 * The current list of options the user can select from.
	 * @type br.presenter.node.OptionsNodeList
	 */
	this.options = (vOptions instanceof br.presenter.node.OptionsNodeList) ? vOptions : new br.presenter.node.OptionsNodeList(vOptions);
	this.options.addChangeListener(this, "_automaticallyUpdateValueOnOptionsChange");
	
	/** @private */
	this.m_bAutomaticallyUpdateValueWhenOptionsChange = true;
	
	/** @private */
	this.m_oValidMultiSelectionValidator = new br.presenter.validator.ValidMultiSelectionValidator(this.options);
	
	/**
	 * The textual label associated with the multi-selection field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = new br.presenter.property.WritableProperty("");
	
	/**
	 * The list of currently selected options.
	 * @type br.presenter.property.WritableProperty
	 */
	this.value = oProperty;
	this.value.addValidator(this.m_oValidMultiSelectionValidator);
	
	/**
	 * A boolean property that is <code>true</code> if {@link #value} has any validation errors, and <code>false</code> otherwise.
	 * @type br.presenter.property.WritableProperty
	 */
	this.hasError = new br.presenter.property.WritableProperty(false);
	
	/**
	 * A textual description of the currently failing validation message when {@link #hasError} is <code>true</code>.
	 * @type br.presenter.property.WritableProperty
	 */
	this.failureMessage = new br.presenter.property.WritableProperty();
	
	/**
	 * A boolean property representing whether the multi-selection field is enabled or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.enabled = new br.presenter.property.WritableProperty(true);
	
	/**
	 * A boolean property representing whether the multi-selection field is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new br.presenter.property.WritableProperty(true);

	/**
	 * The logical control-name the multi-selection field is being bound to &mdash; this
	 * value will appear within the <code>name</code> attribute if being bound to a native HTML control.
	 * @type br.presenter.property.WritableProperty
	 */
	this.controlName = new br.presenter.property.WritableProperty("");

	/** @private */
	this.m_oValueListener = new br.presenter.node.FieldValuePropertyListener(this);
	// validate the initial values
	this.value.forceValidation();
};
br.Core.extend(br.presenter.node.MultiSelectionField, br.presenter.node.PresentationNode);

/**
 * Whether the multi-selection field displays a validation error if the selected values (within {@link #value})
 * contain items that are not members of the {@link #options} array.
 * 
 * <p>Invalid selections cause validation errors by default, but this rarely happens
 * with multi-selection fields since {@link #value} automatically updates if the underlying
 * {@link #options} change by default, and standard multi-selection controls don't allow
 * unconstrained user input.</p>
 * 
 * @param {boolean} bAllowInvalidSelections Invalid selections are allowed when set to <code>true</code>.
 */
br.presenter.node.MultiSelectionField.prototype.allowInvalidSelections = function(bAllowInvalidSelections)
{
	this.m_oValidMultiSelectionValidator.allowInvalidSelections(bAllowInvalidSelections);
};

/**
 * Whether the selection field automatically picks a new {@link #value} when the underlying {@link #options}
 * change.
 * 
 * <p>If the underlying {@link #options} change, so that {@link #value} still refers to options that no longer
 * exist, a validation error would be displayed if it were not for the fact that invalid selections are automatically
 * removed by default when this happens. Automatically updating the {@link #value} may not be desirable in all cases,
 * and it may preferable instead to display a validation error so the user can be made fully aware that their
 * selection requires change.</p>
 * 
 * @param {boolean} bAutomaticallyUpdate True to automatically update values
 * @see #allowInvalidSelections
 */
br.presenter.node.MultiSelectionField.prototype.automaticallyUpdateValueWhenOptionsChange = function(bAutomaticallyUpdate)
{
	this.m_bAutomaticallyUpdateValueWhenOptionsChange = bAutomaticallyUpdate;
};

/**
 * @private
 */
br.presenter.node.MultiSelectionField.prototype._automaticallyUpdateValueOnOptionsChange = function()
{
	if(!this.m_bAutomaticallyUpdateValueWhenOptionsChange)
	{
		this.value.forceValidation();
	}
	else
	{
		var pOptions = this.options.getOptionValues();
		var pCurrentlySelected = this.value.getValue();
		var pSelected = [];
		var Utility = require('br/core/Utility');
		var mOptions = Utility.addValuesToSet({}, pOptions);
		
		for(var i = 0, l = pCurrentlySelected.length; i < l; ++i)
		{
			var sOption = pCurrentlySelected[i];
			
			if(mOptions[sOption])
			{
				pSelected.push(sOption);
			}
		}
		
		var oValueProperty = this.value;
		oValueProperty.setUserEnteredValue(pSelected);
	}
};
