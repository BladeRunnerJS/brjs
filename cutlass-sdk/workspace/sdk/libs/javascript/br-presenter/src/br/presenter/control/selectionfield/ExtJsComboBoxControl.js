br.Core.thirdparty("extjs");

/**
 * This class is constructed by presenter automatically on your behalf.
 *
 * @class
 * A control adaptor that allows the ExtJs ComboBox control to be used to render instances of 
 *  {@link br.presenter.node.SelectionField} within presenter.
 * <p>The ExtJs ComboBox control is aliased by <em>br.combo-box</em>, and can be used within templates as follows:</p>
 * <pre>
 *   &lt;span data-bind="controlNode:selectionFieldProperty, control:'br.combo-box'"&gt;&lt;/span&gt;
 * </pre>
 * <p>By default {@link br.presenter.node.SelectionField#options} are used to generate validation errors if the 
 *  user types something other than one of the available options, but you can invoke 
 *  {@link br.presenter.node.SelectionField#allowInvalidSelections} with <code>true</code> if you'd prefer 
 *  <code>options</code> to act as a list of suggestions only.</p>
 *
 * @constructor
 * @implements br.presenter.control.ControlAdaptor
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl = function() {
	this.m_oSelectionField = null;
	this.m_oExtComboBox = null;
	this.m_oExtStore = new Ext.data.ArrayStore({fields: ['values'], data:[]});
	this.m_eNode = {};

	this.m_fChangeCallback = function() {
		this._onValueChanged();
	}.bind(this);

	this.m_mExtSettings = {
		mode: 'local',
		displayField: 'values',
		store: this.m_oExtStore,
		disableKeyFilter: true,
		listeners: {
			select: this.m_fChangeCallback,
			keyup: this.m_fChangeCallback,
			blur: this.m_fChangeCallback
		}
	};
};
br.Core.inherit(br.presenter.control.selectionfield.ExtJsComboBoxControl, br.presenter.control.ControlAdaptor);

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.setElement = function(element) {
	if (typeof element.type !== 'undefined' && element.type === 'text') {
		this.m_eNode = element;
	} else {
		this.m_eNode = document.createElement('input');
		this.m_eNode.type = 'text';

		element.appendChild(this.m_eNode);
	}
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setOptions
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.setOptions = function(options) {
	this.m_mExtSettings = br.util.MapUtility.mergeMaps([options, this.m_mExtSettings]);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.setPresentationNode = function(presentationNode) {
	this.m_oExtComboBox = new Ext.form.ComboBox(this.m_mExtSettings);

	if (!(presentationNode instanceof br.presenter.node.SelectionField)) {
		throw new br.presenter.control.InvalidControlModelError('ExtJsComboBoxControl', 'SelectionField');
	}

	this.m_oSelectionField = presentationNode;
	this.m_oSelectionField.value.addChangeListener(this, '_updateValue', true);
	this.m_oSelectionField.enabled.addChangeListener(this, '_updateEnabled', true);
	this.m_oSelectionField.visible.addChangeListener(this, '_updateVisible', true);

	// cache listener to options so we can remove it later
	this._optionsListener = this.m_oSelectionField.options.addChangeListener(this, '_updateOptions', true);

};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#onViewReady
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.onViewReady = function() {
	this.m_oExtComboBox.applyToMarkup(this.m_eNode);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#destroy
 */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype.destroy = function() {
	this.m_eNode = null;
	this.m_oExtStore.destroy();
	this.m_oExtStore = null;

	// remove listeners to all children that are instances of br.presenter.property.Property
	this.m_oSelectionField.removeChildListeners();

	// options is a NodeList not a Property so listeners need to be removed explicitly
	this.m_oSelectionField.options.removeListener(this._optionsListener);

	this.m_fChangeCallback = null;
	this.m_oExtComboBox.destroy();
	this.m_mExtSettings = null;
	this.m_oExtComboBox = null;
	this.m_oSelectionField = null;
};

/** @private */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._onValueChanged = function() {
	var label = this.m_oExtComboBox.getValue(),
		value = this.m_oSelectionField.options.getOptionByLabel(label);

	value = value ? value.value.getValue() : label;

	this.m_oSelectionField.value.setUserEnteredValue(value);
	this.m_oExtComboBox.setValue(label);
};

/** @private */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateValue = function() {
	var value = this.m_oSelectionField.value.getValue(),
		label = this.m_oSelectionField.options.getOptionByValue(value);

	label = label ? label.label.getValue() : value;

	this.m_oExtComboBox.setValue(label);
};

/** @private */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateOptions = function() {
	var options = this.m_oSelectionField.options.getOptions(),
		data = [];

	for (var i = 0, len = options.length; i < len; i++) {
		data.push([options[i]]);
	}

	this.m_oExtStore.loadData(data, false);
};

/** @private */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateEnabled = function() {
	var isEnabled = this.m_oSelectionField.enabled.getValue();
	this.m_oExtComboBox.setDisabled(!isEnabled);
};

/** @private */
br.presenter.control.selectionfield.ExtJsComboBoxControl.prototype._updateVisible = function() {
	var isVisible = this.m_oSelectionField.visible.getValue();
	this.m_eNode.style.display = (isVisible) ? 'block' : 'none';
};
/**/