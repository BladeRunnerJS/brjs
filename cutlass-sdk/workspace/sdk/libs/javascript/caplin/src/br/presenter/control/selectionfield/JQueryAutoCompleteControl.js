
br.thirdparty("jquery");

/**
 * @constructor
 *
 * @class
 * Provides an input box that supports auto complete when used in conjunction with a 
 * {@link br.presenter.node.AutoCompleteSelectionField}.
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
 *
 * @extends br.presenter.control.ControlAdaptor
 * @extends br.presenter.property.PropertyListener
 */
br.presenter.control.selectionfield.JQueryAutoCompleteControl = function()
{
	br.presenter.control.ControlAdaptor.call(this);

	/** @private */
	this.m_eElement = {};
	this.m_bOpenOnFocus = false;
	this.m_sAppendTo = 'body';
};

br.inherit(br.presenter.control.selectionfield.JQueryAutoCompleteControl, br.presenter.control.ControlAdaptor);
br.inherit(br.presenter.control.selectionfield.JQueryAutoCompleteControl, br.presenter.property.PropertyListener);

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
br.presenter.control.selectionfield.JQueryAutoCompleteControl.prototype.setElement = function(eElement)
{
	if(eElement.type && eElement.type === 'text') {
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
br.presenter.control.selectionfield.JQueryAutoCompleteControl.prototype.setPresentationNode = function(oPresentationNode)
{
	if (!(oPresentationNode instanceof br.presenter.node.AutoCompleteSelectionField))
	{
		throw new br.presenter.control.InvalidControlModelError("JQueryAutoCompleteControl", "AutoCompleteSelectionField");
	}

	this.m_eElement.value = oPresentationNode.value.getValue();
	oPresentationNode.value.addUpdateListener(this, "_valueChanged");
	this.m_oPresentationNode = oPresentationNode;
};

br.presenter.control.selectionfield.JQueryAutoCompleteControl.prototype.onViewReady = function()
{
	var oJqueryInput = jQuery(this.m_eElement);
	var self = this;

	oJqueryInput.keydown(function(event, oUi) {
		if (event.which == 13) // Enter.
		{
			self._setValue(self.m_oPresentationNode, this, oUi);
			event.stopImmediatePropagation();
			event.preventDefault();
			this.blur();
			return false;
		}
	});

	oJqueryInput.autocomplete({
		minLength: self.m_nMinCharAmount || 0,
		autoFocus: true,
		appendTo: self.m_sAppendTo,
		source: function(request, response) {
			var sTerm = request.term;
			self.m_oPresentationNode.getAutoCompleteList(sTerm, function(pValues) {
				response(pValues);
			});
		},
		select: function(event, oUi) {
			self._setValue(self.m_oPresentationNode, this, oUi);
			// don't propagate this to the keydown (if it's triggered by an enter)
			event.stopImmediatePropagation();
			event.preventDefault();
			return false;
		}
	});

	if (this.m_bOpenOnFocus)
	{
		oJqueryInput.focus(function(e) {
			jQuery(this).autocomplete("search", oJqueryInput.val() || "");
		});
	}
};

br.presenter.control.selectionfield.JQueryAutoCompleteControl.prototype._setValue = function(oPresentationNode, oInput, oUi)
{
	if (oPresentationNode.isValidOption(oInput.value))
	{
		oPresentationNode.value.setValue(oInput.value);
		this.m_eElement.value = oInput.value;
	}
	else if (oUi)
	{
		oPresentationNode.value.setValue(oUi.item.value);
		this.m_eElement.value = oUi.item.value;
	}
};

br.presenter.control.selectionfield.JQueryAutoCompleteControl.prototype._valueChanged = function()
{
	this.m_eElement.value = this.m_oPresentationNode.value.getValue();
};

/**
 * @private
 */
br.presenter.control.selectionfield.JQueryAutoCompleteControl.prototype.setOptions = function(mOptions)
{
	if (mOptions && mOptions.openOnFocus !== undefined && mOptions.openOnFocus !== "false")
	{
		this.m_bOpenOnFocus = true;
	}
	if (mOptions && mOptions.appendTo !== undefined)
	{
		this.m_sAppendTo = mOptions.appendTo;
	}
	if (mOptions && mOptions.minCharAmount !== undefined)
	{
		this.m_nMinCharAmount = mOptions.minCharAmount;
	}
};
