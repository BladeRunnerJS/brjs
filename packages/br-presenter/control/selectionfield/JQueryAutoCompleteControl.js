'use strict';

var jQuery = require('jquery');
var InvalidControlModelError = require('br-presenter/control/InvalidControlModelError');
var AutoCompleteSelectionField = require('br-presenter/node/AutoCompleteSelectionField');
var PropertyListener = require('br-presenter/property/PropertyListener');
var ControlAdaptor = require('br-presenter/control/ControlAdaptor');
var Core = require('br/Core');

/**
 * @module br/presenter/control/selectionfield/JQueryAutoCompleteControl
 */

/**
 * @class
 * @alias module:br/presenter/control/selectionfield/JQueryAutoCompleteControl
 * @extends module:br/presenter/control/ControlAdaptor
 * @extends module:br/presenter/property/PropertyListener
 *
 * @classdesc
 * Provides an input box that supports auto complete when used in conjunction with a
 * {@link module:br/presenter/node/AutoCompleteSelectionField}.
 *
 * <p>The jQuery auto complete control is aliased by <em>br.autocomplete-box</em>, and can
 * be used within templates as follows:</p>
 *
 * <pre>
 *   &lt;span data-bind="controlNode:autoCompleteSelectionFieldProperty, control:'br.autocomplete-box'"&gt;&lt;/span&gt;
 * </pre>
 *
 * <p>You can also use an exsisting text input element and avoid having an extra container element around the control:</p>
 *
 * <pre>
 *   &lt;input type="text" data-bind="controlNode:autoCompleteSelectionFieldProperty, control:'br.autocomplete-box'"/&gt;
 * </pre>
 *
 * <p>
 * Options: <em>openOnFocus</em> - Show the auto complete selection on input focus (true|false). Defaults to false.
 * <em>appendTo</em> - Specify the jquery selector of the element that the menu should be appended to.
 * <em>minCharAmount</em> - Specify the minimun amount of characters to be typed before the autocomplete menu is displayed. Default is 0.
 * </p>
 *
 * @see br.presenter.node.AutoCompleteSelectionField
 */
function JQueryAutoCompleteControl() {
	ControlAdaptor.call(this);

	/** @private */
	this.m_eElement = {};
	this.m_jQueryInput = null;
	this.m_bOpenOnFocus = false;
	this.m_sAppendTo = 'body';
	this._viewOpened = false;
}

Core.inherit(JQueryAutoCompleteControl, ControlAdaptor);
Core.inherit(JQueryAutoCompleteControl, PropertyListener);

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
JQueryAutoCompleteControl.prototype.setElement = function(eElement) {
	if (eElement.type && eElement.type === 'text') {
		this.m_eElement = eElement;
	} else {
		this.m_eElement = document.createElement('input');
		eElement.appendChild(this.m_eElement);
	}
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
JQueryAutoCompleteControl.prototype.setPresentationNode = function(oPresentationNode) {
	if (!(oPresentationNode instanceof AutoCompleteSelectionField)) {
		throw new InvalidControlModelError('JQueryAutoCompleteControl', 'AutoCompleteSelectionField');
	}

	this.m_eElement.value = oPresentationNode.value.getFormattedValue();
	this._valueChangedListener = oPresentationNode.value.addUpdateListener(this, '_valueChanged');
	this.m_oPresentationNode = oPresentationNode;
};

JQueryAutoCompleteControl.prototype.onViewReady = function() {
	this.m_jQueryInput = jQuery(this.m_eElement);
	var self = this;

	this.m_jQueryInput.autocomplete({
		delay: this.delay || 0,
		minLength: self.m_nMinCharAmount || 0,
		autoFocus: true,
		appendTo: self.m_sAppendTo,
		open: function() {
			// ensure menu is on top of elements
			self.m_jQueryInput.autocomplete('widget').css('z-index', 999999);

			self.m_jQueryInput.addClass('autocomplete-menu-open');

			return false;
		},
		close: function() {
			self.m_jQueryInput.removeClass('autocomplete-menu-open');
		},
		source: function(request, response) {
			var sTerm = request.term;
			self.m_oPresentationNode.getAutoCompleteList(sTerm, function(pValues) {
				response(pValues);
			});
		},
		select: function(event, oUi) {
			self._setValue(self.m_oPresentationNode, this, oUi);
			// if the selection is triggered by a click, not by pressing enter, then blur
			if (self.m_bBlurAfterClick === true) {
				var ie8LeftClick = !event.button && !event.which;

				if (event.which === 1 || ie8LeftClick) {
					this.blur();
				}
			}

			return false;
		}
	});

	this._onDocumentFocus = this._onDocumentFocus.bind(this);
	this.m_jQueryInput.on('focus', this._onDocumentFocus);

	this._onScroll = this._onScroll.bind(this);
	jQuery( document.body ).on('mousewheel wheel', this._onScroll);

	this._viewOpened = true;
};

JQueryAutoCompleteControl.prototype._onScroll = function(wheelEvent) {
	var isEventTargetChildOfAutoComplete = this.m_jQueryInput.autocomplete('widget')[0].contains(wheelEvent.target);

	if( isEventTargetChildOfAutoComplete === false ) {
		this.m_jQueryInput.autocomplete('close');
	}
};

JQueryAutoCompleteControl.prototype._onDocumentFocus = function() {
	this.m_jQueryInput.select();

	if (this.m_bOpenOnFocus) {
		this.m_jQueryInput.autocomplete('search', this.m_jQueryInput.val() || '');
	}
};

JQueryAutoCompleteControl.prototype._setValue = function(oPresentationNode, oInput, oUi) {
	var isValidOption = oPresentationNode.isValidOption( oInput.value );

	if ( isValidOption || oUi ) {
		var presentationNodeValue = isValidOption ? oInput.value : oUi.item.value;

		oPresentationNode.value.setValue( presentationNodeValue );
		this.m_eElement.value = ( this.clearTextAfterValidInput ? '' : oPresentationNode.value.getFormattedValue());
	}
};

JQueryAutoCompleteControl.prototype._valueChanged = function() {
	this.m_eElement.value = this.m_oPresentationNode.value.getFormattedValue();
};

/**
 * @private
 */
JQueryAutoCompleteControl.prototype.setOptions = function(mOptions) {
	mOptions = mOptions || {};

	if (mOptions.openOnFocus !== undefined && mOptions.openOnFocus !== 'false')	{
		this.m_bOpenOnFocus = true;
	}
	if (mOptions.appendTo !== undefined) {
		this.m_sAppendTo = mOptions.appendTo;
	}
	if (mOptions.minCharAmount !== undefined) {
		this.m_nMinCharAmount = mOptions.minCharAmount;
	}
	if (mOptions.blurAfterClick !== undefined) {
		this.m_bBlurAfterClick = mOptions.blurAfterClick;
	}
	if (mOptions.delay !== undefined) {
		this.delay = mOptions.delay;
	}
	if (mOptions.clearTextAfterValidInput !== undefined) {
		this.clearTextAfterValidInput = mOptions.clearTextAfterValidInput;
	}
};

/**
 * Destroy created listeners and jQuery autocomplete plugin
 */
JQueryAutoCompleteControl.prototype.destroy = function() {
	// if onOpen is never called the control wouldn't be initialised, hence we must guard against that
	if(this._viewOpened) {
		this.m_jQueryInput.off('focus', this._onDocumentFocus);
		jQuery( document.body ).off('mousewheel wheel', this._onScroll);
		this.m_jQueryInput.autocomplete('destroy');
		this.m_jQueryInput.off();
	}

	if (this._valueChangedListener) {
		this.m_oPresentationNode.value.removeListener(this._valueChangedListener);
	}

	this._valueChangedListener = null;
	this.m_jQueryInput = null;
	this.m_eElement = null;
};

module.exports = JQueryAutoCompleteControl;
