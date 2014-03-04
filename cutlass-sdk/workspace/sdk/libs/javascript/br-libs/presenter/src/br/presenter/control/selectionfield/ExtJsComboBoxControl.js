br.Core.thirdparty("extjs");

/**
 * This class is constructed by presenter automatically on your behalf.
 * 
 * @class
 * A control adaptor that allows the ExtJs ComboBox control to be used to render instances
 * of {@link br.presenter.node.SelectionField} within presenter.
 * 
 * <p>The ExtJs ComboBox control is aliased by <em>br.combo-box</em>, and can
 * be used within templates as follows:</p>
 * 
 * <pre>
 *   &lt;span data-bind="controlNode:selectionFieldProperty, control:'br.combo-box'"&gt;&lt;/span&gt;
 * </pre>
 * 
 * <p>By default {@link br.presenter.node.SelectionField#options} are used
 * to generate validation errors if the user types something other than one of
 * the available options, but you can invoke
 * {@link br.presenter.node.SelectionField#allowInvalidSelections} with
 * <code>true</code> if you'd prefer <code>options</code> to act as a list of
 * suggestions only.</p>
 * 
 * @constructor
 * @implements br.presenter.control.ControlAdaptor
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl = function()
{
	/** @private */
	this.m_oSelectionField = null;
	
	/** @private */
	this.m_oExtComboBox = null;
	
	/** @private */
	this.m_oExtStore = new Ext.data.ArrayStore({fields:["values"], data:[]});
	
	/** @private */
	this.m_eNode = {};
	
	/** @private */
	this.m_fChangeCallback = this._getChangeCallback(this);
	
	/** @private */
	this.m_mExtSettings = {
		mode:'local',
		displayField:'values',
		store:this.m_oExtStore,
		disableKeyFilter:true,
		listeners:{
			select:this.m_fChangeCallback,
			keyup:this.m_fChangeCallback,
			blur:this.m_fChangeCallback
		}
	};
};
br.Core.inherit(br.presenter.control.selectionfield.ExtJsComboBoxControl, br.presenter.control.ControlAdaptor);

// *********************** ControlAdaptor Interface ***********************

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.setElement = function(eElement)
{
	if(eElement.type && eElement.type === 'text') {
		this.m_eNode = eElement;
	} else {
		this.m_eNode = document.createElement("input");
		this.m_eNode.type = "text";

		eElement.appendChild(this.m_eNode);
	}
}

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setOptions
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.setOptions = function(mOptions)
{
	this.m_mExtSettings = br.util.MapUtility.mergeMaps([mOptions, this.m_mExtSettings]);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.setPresentationNode = function(oPresentationNode)
{
	this.m_oExtComboBox = new Ext.form.ComboBox(this.m_mExtSettings);
	if(!(oPresentationNode instanceof br.presenter.node.SelectionField)) {
		throw new br.presenter.control.InvalidControlModelError("ExtJsComboBoxControl", "SelectionField");
	}

	this.m_oSelectionField = oPresentationNode;
	this.m_oSelectionField.value.addChangeListener(this, "_updateValue", true);
	this.m_oSelectionField.options.addChangeListener(this, "_updateOptions", true);
	this.m_oSelectionField.enabled.addChangeListener(this, "_updateEnabled", true);
	this.m_oSelectionField.visible.addChangeListener(this, "_updateVisible", true);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#onViewReady
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.onViewReady = function()
{
	this.m_oExtComboBox.applyToMarkup(this.m_eNode);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#destroy
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.destroy = function()
{
	this.m_eNode = null; 
	this.m_oExtStore.destroy(); 
	this.m_oExtStore = null;
	this.m_oSelectionField.removeChildListeners();
	this.m_fChangeCallback(true);
	this.m_fChangeCallback = null;
	this.m_oExtComboBox.destroy();
	this.m_mExtSettings = null;
	this.m_oExtComboBox = null;
	this.m_oSelectionField = null;
};


// *********************** Private Methods ***********************

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._onKeyUp = function()
{
	this._onValueChanged();
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._onSelect = function()
{
	this._onValueChanged();
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._onBlur = function()
{
	this._onValueChanged();
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._onValueChanged = function()
{
	var vLabel = this.m_oExtComboBox.getValue();
	var vValue = this.m_oSelectionField.options.getOptionByLabel(vLabel);

	vValue = ( vValue ? vValue.value.getValue() : vLabel );

	this.m_oSelectionField.value.setUserEnteredValue(vValue);
	this.m_oExtComboBox.setValue(vLabel);
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateValue = function()
{
	var vValue = this.m_oSelectionField.value.getValue();
	var vLabel = this.m_oSelectionField.options.getOptionByValue(vValue);

	vLabel = ( vLabel ? vLabel.label.getValue() : vValue );

	this.m_oExtComboBox.setValue(vLabel);
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateOptions = function()
{
	var pOptions = this.m_oSelectionField.options.getOptions();
	var pData = [];
	
	for(var i = 0, l = pOptions.length; i < l; ++i) {
		pData.push([pOptions[i]]);
	}
	
	this.m_oExtStore.loadData(pData, false);
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateEnabled = function()
{
	var bIsEnabled = this.m_oSelectionField.enabled.getValue();
	this.m_oExtComboBox.setDisabled(!bIsEnabled);
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateVisible = function()
{
	var bIsVisible = this.m_oSelectionField.visible.getValue();
	this.m_eNode.style.display = (bIsVisible) ? "block" : "none";
};

/**
 * @private
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._getChangeCallback = function(self)
{
	return function()
	{
		self._onValueChanged();
	};
};
