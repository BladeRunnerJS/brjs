'use strict';

var EventUtility = require('br/util/EventUtility');
var OptionsNodeList = require('br/presenter/node/OptionsNodeList');
var ElementUtility = require('br/util/ElementUtility');
var InvalidControlModelError = require('br/presenter/control/InvalidControlModelError');
var SelectionField = require('br/presenter/node/SelectionField');
var ControlAdaptor = require('br/presenter/control/ControlAdaptor');
var PropertyListener = require('br/presenter/property/PropertyListener');
var Core = require('br/Core');

/**
 * @module br/presenter/control/selectionfield/ToggleSwitchControl
 */

/**
 * @class
 * @alias module:br/presenter/control/selectionfield/ToggleSwitchControl
 * @implements module:br/presenter/control/ControlAdaptor
 * @implements module:br/presenter/property/PropertyListener
 * 
 * @classdesc
 * A provided toggle-switch control that can be used to render instances
 * of {@link module:br/presenter/node/SelectionField} within presenter.
 * This class is constructed by presenter automatically on your behalf.
 * 
 * <p>The toggle-switch control is aliased by <em>br.toggle-switch</em>,
 * and can be used within templates as follows:</p>
 * 
 * <pre>
 *   &lt;span data-bind="controlNode:selectionFieldProperty, control:'br.toggle-switch'"&gt;&lt;/span&gt;
 * </pre>
 * 
 * <p>The toggle-switch control can only be used to display <code>SelectionField</code> instances
 * having exactly two options.</p>
 */
function ToggleSwitchControl() {
}

Core.inherit(ToggleSwitchControl, PropertyListener);
Core.inherit(ToggleSwitchControl, ControlAdaptor);

// *********************** PropertyListener Interface ***********************

/**
 * @private
 * @see br.presenter.property.PropertyListener#onPropertyChanged
 */
ToggleSwitchControl.prototype.onPropertyChanged = function() {
	this._refresh();
};


// *********************** ControlAdaptor Interface ***********************

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
ToggleSwitchControl.prototype.setElement = function(eElement) {
	this.m_eElement = eElement;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setOptions
 */
ToggleSwitchControl.prototype.setOptions = function(newValue) {
	// do nothing -- this control doesn't support options to change its behaviour
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
ToggleSwitchControl.prototype.setPresentationNode = function(oPresentationNode) {
	if (!(oPresentationNode instanceof SelectionField)) {
		throw new InvalidControlModelError('ToggleSwitchControl', 'SelectionField');
	}

	this.m_oPresentationNode = oPresentationNode;

	if (!this.m_eElement) {
		this.m_eElement = document.createElement('div');
	}

	ElementUtility.addClassName(this.m_eElement, 'toggleSwitch');
	this.m_eFirstElementContainer = document.createElement('label');
	ElementUtility.addClassName(this.m_eFirstElementContainer, 'choiceA');

	this.m_eSecondElementContainer = document.createElement('label');
	ElementUtility.addClassName(this.m_eSecondElementContainer, 'choiceB');

	this._updateOptions();

	this.m_eElement.appendChild(this.m_eFirstElementContainer);
	this.m_eElement.appendChild(this.m_eSecondElementContainer);

	this._refresh();

	oPresentationNode.value.addListener(this);
	oPresentationNode.options.addChangeListener(this._updateOptions.bind(this));
	oPresentationNode.enabled.addChangeListener(this._updateEnabled.bind(this), true);
	oPresentationNode.visible.addChangeListener(this._updateVisible.bind(this), true);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#destroy
 */
ToggleSwitchControl.prototype.destroy = function() {
	this.m_oPresentationNode.removeChildListeners();
	ElementUtility.discardChild(this.m_eFirstElementContainer);
	ElementUtility.discardChild(this.m_eSecondElementContainer);
	ElementUtility.discardChild(this.m_eElement);

	this.m_eElement = null;
	this.m_fFirstClick = null;
	this.m_fSecondClick = null;
	this.m_nFirstClickListenerId = null;
	this.m_nSecondClickListenerId = null;
	this.m_oPresentationNode = null;
	this.m_eFirstElementContainer = null;
	this.m_eSecondElementContainer = null;
};


// *********************** Private Methods ***********************

/**
 * @private
 */
ToggleSwitchControl.prototype._updateOptions = function() {
	var oOptions = this.m_oPresentationNode.options;
	var pNewOptions = oOptions.getOptions();

	if (pNewOptions.length != 2) {
		throw new InvalidControlModelError('ToggleSwitchControl',
			'SelectionField (having exactly two elements)');
	}

	if (oOptions instanceof OptionsNodeList) {
		this.m_eFirstElementContainer.innerHTML = pNewOptions[0].label.getValue();
		this.m_eSecondElementContainer.innerHTML = pNewOptions[1].label.getValue();
	} else {
		this.m_eFirstElementContainer.innerHTML = pNewOptions[0];
		this.m_eSecondElementContainer.innerHTML = pNewOptions[1];
	}

	this._refresh();
};

/**
 * @private
 */
ToggleSwitchControl.prototype._updateEnabled = function() {
	var bIsEnabled = this.m_oPresentationNode.enabled.getValue();
	if (bIsEnabled) {
		ElementUtility.removeClassName(this.m_eElement, 'disabled');
		this._bindClickEventHandlers();
	} else {
		ElementUtility.addClassName(this.m_eElement, 'disabled');
		EventUtility.removeEventListener(this.m_nFirstClickListenerId);
		EventUtility.removeEventListener(this.m_nSecondClickListenerId);
	}
};

/**
 * @private
 */
ToggleSwitchControl.prototype._updateVisible = function() {
	var bIsVisible = this.m_oPresentationNode.visible.getValue();
	this.m_eElement.style.display = (bIsVisible) ? '' : 'none';
};

/**
 * @private
 */
ToggleSwitchControl.prototype._refresh = function() {
	if (this.m_oPresentationNode.value.getValue() === this.m_oPresentationNode.options.getOptions()[0].value.getValue()) {
		ElementUtility.addClassName(this.m_eElement, 'choiceASelected');
		ElementUtility.removeClassName(this.m_eElement, 'choiceBSelected');
	} else {
		ElementUtility.addClassName(this.m_eElement, 'choiceBSelected');
		ElementUtility.removeClassName(this.m_eElement, 'choiceASelected');
	}
};

/**
 * @private
 */
ToggleSwitchControl.prototype._bindClickEventHandlers = function() {
	var oSelf = this;

	this.m_fFirstClick = function() {
		ElementUtility.addClassName(oSelf.m_eElement, 'choiceASelected');
		ElementUtility.removeClassName(oSelf.m_eElement, 'choiceBSelected');
		oSelf.m_oPresentationNode.value.setValue(oSelf.m_oPresentationNode.options.getOptions()[0].value.getValue());
	};

	this.m_fSecondClick = function() {
		ElementUtility.addClassName(oSelf.m_eElement, 'choiceBSelected');
		ElementUtility.removeClassName(oSelf.m_eElement, 'choiceASelected');
		oSelf.m_oPresentationNode.value.setValue(oSelf.m_oPresentationNode.options.getOptions()[1].value.getValue());
	};

	// TODO: find out why tests fail when these events are added without the final "true" parameter (direct attachment), which attaches the events as .onclick attributes
	this.m_nFirstClickListenerId = EventUtility.addEventListener(this.m_eFirstElementContainer, 'click', this.m_fFirstClick, true);
	this.m_nSecondClickListenerId = EventUtility.addEventListener(this.m_eSecondElementContainer, 'click', this.m_fSecondClick, true);
};

module.exports = ToggleSwitchControl;
