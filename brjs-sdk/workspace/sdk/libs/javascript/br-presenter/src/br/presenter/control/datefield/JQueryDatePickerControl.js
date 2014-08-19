/**
 * @module br/presenter/control/datefield/JQueryDatePickerControl
 */

br.Core.thirdparty("jquery");

/**
 * @class
 * @alias module:br/presenter/control/datefield/JQueryDatePickerControl
 * @implements module:br/presenter/control/ControlAdaptor
 * 
 * @description
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
br.presenter.control.datefield.JQueryDatePickerControl = function()
{
	/** @private */
	this.m_oJQueryNode = {};

	/** @private */
	this.m_mOptions = {};
};

br.Core.inherit(br.presenter.control.datefield.JQueryDatePickerControl, br.presenter.control.ControlAdaptor);

// *********************** ControlAdaptor Interface ***********************

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setElement
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype.setElement = function(eElement)
{
	if(eElement.type && eElement.type === 'hidden') {
		// if the passed element is a hidden input box use that to bind to it
		this.m_oJQueryNode = jQuery(eElement);
	} else {
		// otherwise use it as a container
		this.m_oJQueryNode = jQuery('<input type="hidden" />');
		this.m_oJQueryNode.appendTo(eElement);
	}
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setOptions
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype.setOptions = function(mOptions)
{
	this.m_mOptions = mOptions;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#setPresentationNode
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype.setPresentationNode = function(oPresentationNode)
{
	if(!(oPresentationNode instanceof br.presenter.node.DateField)) {
		throw new br.presenter.control.InvalidControlModelError("JQueryDatePickerControl", "DateField");
	}

	this.m_oPresentationNode = oPresentationNode;
	this.m_oPresentationNode.enabled.addChangeListener(this, "_setDisabled", true);
	this.m_oPresentationNode.visible.addChangeListener(this, "_setVisible");
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#destroy
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype.destroy = function()
{
	this.m_oJQueryNode.remove();
	this.m_oPresentationNode.removeChildListeners();

	this.m_oPresentationNode = null;
};

/**
 * @private
 * @see br.presenter.control.ControlAdaptor#onViewReady
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype.onViewReady = function()
{
	this._generateCalendarHtml();
	this.m_oJQueryNode.datepicker("option", this.m_mOptions);
	this._setVisible();
};


// *********************** Private Methods ***********************

/**
 * @private
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype._setDisabled = function()
{
	var sAction = this.m_oPresentationNode.enabled.getValue() ? "enable" : "disable";
	this.m_oJQueryNode.datepicker(sAction);
};

/**
 * @private
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype._setVisible = function()
{
	var bVisible = this.m_oPresentationNode.visible.getValue();
	this.m_oJQueryNode[0].parentNode.style.display = (bVisible) ? "block" : "none";
};

/**
 * @private
 */
br.presenter.control.datefield.JQueryDatePickerControl.prototype._generateCalendarHtml = function()
{
	var oThis = this;

	this.m_oJQueryNode.datepicker(
	{
		showOn: 'button',
		dateFormat: "yy-mm-dd",

		onSelect: function(dateText)
		{
			oThis.m_oPresentationNode.value.setValue(dateText);
		},
		beforeShow:function()
		{
			oThis.m_oJQueryNode.datepicker("setDate", oThis.m_oPresentationNode.value.getValue());
		}
	});
};
