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
br.presenter.control.selectionfield.ToggleSwitchControl = function()
{
};

br.Core.inherit(br.presenter.control.selectionfield.ToggleSwitchControl, br.presenter.property.PropertyListener);
br.Core.inherit(br.presenter.control.selectionfield.ToggleSwitchControl, br.presenter.control.ControlAdaptor);

// *********************** PropertyListener Interface ***********************

/**
 * @private
 * @see br.presenter.property.PropertyListener#onPropertyChanged
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype.onPropertyChanged = function()
{
	this._refresh();
};


// *********************** ControlAdaptor Interface ***********************

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype.setElement = function(eElement)
{
	this.m_eElement = eElement;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setOptions
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype.setOptions = function(newValue)
{
	// do nothing -- this control doesn't support options to change its behaviour
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype.setPresentationNode = function(oPresentationNode)
{
	if(!(oPresentationNode instanceof br.presenter.node.SelectionField)) {
		throw new br.presenter.control.InvalidControlModelError("ToggleSwitchControl", "SelectionField");
	}

	this.m_oPresentationNode = oPresentationNode;

	if(!this.m_eElement) {
		this.m_eElement = document.createElement("div");
	}

	br.util.ElementUtility.addClassName(this.m_eElement, "toggleSwitch");
	this.m_eFirstElementContainer = document.createElement("label");
	br.util.ElementUtility.addClassName(this.m_eFirstElementContainer, "choiceA");

	this.m_eSecondElementContainer = document.createElement("label");
	br.util.ElementUtility.addClassName(this.m_eSecondElementContainer, "choiceB");

	this._updateOptions();
	
	this.m_eElement.appendChild(this.m_eFirstElementContainer);
	this.m_eElement.appendChild(this.m_eSecondElementContainer);
	
	this._refresh();
	
	oPresentationNode.value.addListener(this);
	oPresentationNode.options.addChangeListener(this,"_updateOptions");
	oPresentationNode.enabled.addChangeListener(this, "_updateEnabled", true);
	oPresentationNode.visible.addChangeListener(this, "_updateVisible", true);
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#destroy
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype.destroy = function()
{
	this.m_oPresentationNode.removeChildListeners();
	br.util.ElementUtility.discardChild(this.m_eFirstElementContainer);
	br.util.ElementUtility.discardChild(this.m_eSecondElementContainer);
	br.util.ElementUtility.discardChild(this.m_eElement);
	
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
br.presenter.control.selectionfield.ToggleSwitchControl.prototype._updateOptions = function()
{
	var oOptions = this.m_oPresentationNode.options;
	var pNewOptions = oOptions.getOptions();

	if(pNewOptions.length != 2) {
		throw new br.presenter.control.InvalidControlModelError("ToggleSwitchControl",
			"SelectionField (having exactly two elements)");
	}

	if(oOptions instanceof br.presenter.node.OptionsNodeList)
	{
		this.m_eFirstElementContainer.innerHTML = pNewOptions[0].label.getValue();
		this.m_eSecondElementContainer.innerHTML = pNewOptions[1].label.getValue();
	}
	else
	{
		this.m_eFirstElementContainer.innerHTML = pNewOptions[0];
		this.m_eSecondElementContainer.innerHTML = pNewOptions[1];
	}

	this._refresh();
};

/**
 * @private
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype._updateEnabled = function() {
	var bIsEnabled = this.m_oPresentationNode.enabled.getValue();
	if(bIsEnabled) {
		br.util.ElementUtility.removeClassName(this.m_eElement, "disabled");
		this._bindClickEventHandlers();
	}
	else {
		br.util.ElementUtility.addClassName(this.m_eElement, "disabled");
		br.util.EventUtility.removeEventListener(this.m_nFirstClickListenerId);
		br.util.EventUtility.removeEventListener(this.m_nSecondClickListenerId);
	}
};

/**
 * @private
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype._updateVisible = function() {
	var bIsVisible = this.m_oPresentationNode.visible.getValue();
	this.m_eElement.style.display = (bIsVisible) ? "" : "none";
};

/**
 * @private
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype._refresh = function()
{
	if(this.m_oPresentationNode.value.getValue() === this.m_oPresentationNode.options.getOptions()[0].value.getValue())
	{
		br.util.ElementUtility.addClassName(this.m_eElement, "choiceASelected");
		br.util.ElementUtility.removeClassName(this.m_eElement, "choiceBSelected");
	}
	else
	{
		br.util.ElementUtility.addClassName(this.m_eElement, "choiceBSelected");
		br.util.ElementUtility.removeClassName(this.m_eElement, "choiceASelected");
	}
};

/**
 * @private
 */
br.presenter.control.selectionfield.ToggleSwitchControl.prototype._bindClickEventHandlers = function() {
	var oSelf = this;
	
	this.m_fFirstClick = function()
	{
		br.util.ElementUtility.addClassName(oSelf.m_eElement, "choiceASelected");
		br.util.ElementUtility.removeClassName(oSelf.m_eElement, "choiceBSelected");
		oSelf.m_oPresentationNode.value.setValue(oSelf.m_oPresentationNode.options.getOptions()[0].value.getValue());
	};
	
	this.m_fSecondClick = function()
	{
		br.util.ElementUtility.addClassName(oSelf.m_eElement, "choiceBSelected");
		br.util.ElementUtility.removeClassName(oSelf.m_eElement, "choiceASelected");
		oSelf.m_oPresentationNode.value.setValue(oSelf.m_oPresentationNode.options.getOptions()[1].value.getValue());
	};

	// TODO: find out why tests fail when these events are added without the final "true" parameter (direct attachment), which attaches the events as .onclick attributes
	this.m_nFirstClickListenerId = br.util.EventUtility.addEventListener(this.m_eFirstElementContainer, "click", this.m_fFirstClick, true);
	this.m_nSecondClickListenerId = br.util.EventUtility.addEventListener(this.m_eSecondElementContainer, "click", this.m_fSecondClick, true);
};
