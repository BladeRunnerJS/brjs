'use strict';

var jQuery = require('jquery');
var InvalidControlModelError = require('br/presenter/control/InvalidControlModelError');
var DateField = require('br/presenter/node/DateField');
var ControlAdaptor = require('br/presenter/control/ControlAdaptor');
var Core = require('br/Core');

/**
 * @module br/presenter/control/datefield/JQueryDatePickerControl
 */

Core.thirdparty('jquery');

var MapUtility = require('br/util/MapUtility');

/**
 * @class
 * @alias module:br/presenter/control/datefield/JQueryDatePickerControl
 * @implements module:br/presenter/control/ControlAdaptor
 * 
 * @classdesc
 * A control adaptor that allows the JQuery Calendar control to be used to render instances
 * of {@link module:br/presenter/node/DateField} within presenter.
 * This class is constructed by presenter automatically on your behalf.
 * 
 * <p>The jQuery date picker control is aliased by <em>br.date-picker</em>, and can
 * be used within templates as follows:</p>
 * 
 * <pre>
 *   &lt;span data-bind="controlNode:dateFieldProperty, control:'br.date-picker'"&gt;&lt;/span&gt;
 * </pre>
 *
 * <p>You can also use a hidden input element and avoid having an extra container element:</p>
 *
 * <pre>
 *   &lt;input type="hidden" data-bind="controlNode:dateFieldProperty, control:'br.date-picker'"/&gt;
 * </pre>
 */
function JQueryDatePickerControl() {
	/** @private */
	this.m_oJQueryNode = {};

	/** @private */
	this.m_mOptions = {};

	/** @private */
	this.m_eElement = null;
}

Core.inherit(JQueryDatePickerControl, ControlAdaptor);

// *********************** ControlAdaptor Interface ***********************

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
JQueryDatePickerControl.prototype.setElement = function(eElement) {
	this.m_eElement = eElement;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setOptions
 */
JQueryDatePickerControl.prototype.setOptions = function(mOptions) {
	this.m_mOptions = mOptions;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
JQueryDatePickerControl.prototype.setPresentationNode = function(oPresentationNode) {
	if (!(oPresentationNode instanceof DateField)) {
		throw new InvalidControlModelError('JQueryDatePickerControl', 'DateField');
	}

	this.m_oPresentationNode = oPresentationNode;
	this.m_oPresentationNode.enabled.addChangeListener(this, '_setDisabled');
	this.m_oPresentationNode.visible.addChangeListener(this, '_setVisible');
	this.m_oPresentationNode.value.addChangeListener(this, '_setValue');
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#destroy
 */
JQueryDatePickerControl.prototype.destroy = function() {
	this.m_oJQueryNode.remove();
	this.m_oPresentationNode.removeChildListeners();

	this.m_oPresentationNode = null;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#onViewReady
 */
JQueryDatePickerControl.prototype.onViewReady = function() {
	if (this.m_mOptions.inline || this.m_eElement.type && this.m_eElement.type === 'hidden') {
		// if the passed element is a hidden input box use that to bind to it
		this.m_oJQueryNode = jQuery(this.m_eElement);
	} else {
		// otherwise use it as a container
		this.m_oJQueryNode = jQuery('<input type="hidden" />');
		this.m_oJQueryNode.appendTo(this.m_eElement);
	}

	this._generateCalendarHtml();

	this._setVisible();
	this._setValue();
};


// *********************** Private Methods ***********************

/**
 * @private
 */
JQueryDatePickerControl.prototype._setDisabled = function() {
	this.m_oJQueryNode.datepicker('option', 'disabled', !this.m_oPresentationNode.enabled.getValue());
};

/**
 * @private
 */
JQueryDatePickerControl.prototype._setVisible = function() {
	var bVisible = this.m_oPresentationNode.visible.getValue();
	this.m_oJQueryNode[0].parentNode.style.display = (bVisible) ? 'block' : 'none';
};

/**
 * @private
 */
JQueryDatePickerControl.prototype._setValue = function() {
	if (this.m_oPresentationNode.value.hasValidationError() === false) {
		this.m_oJQueryNode.datepicker('setDate', this.m_oPresentationNode.value.getValue());
	}
};

/**
 * @private
 */
JQueryDatePickerControl.prototype._generateCalendarHtml = function() {
	var oThis = this;
	var oOptions = MapUtility.mergeMaps([{
		showOn: 'button',
		dateFormat: 'yy-mm-dd',
		disabled: !this.m_oPresentationNode.enabled.getValue(),
		onSelect: function(dateText) {
			oThis.m_oPresentationNode.value.setValue(dateText);
		},
		beforeShow: function() {
			oThis._setValue();
		}
	}, this.m_mOptions], true);

	this.m_oJQueryNode.datepicker(oOptions);
};

module.exports = JQueryDatePickerControl;
