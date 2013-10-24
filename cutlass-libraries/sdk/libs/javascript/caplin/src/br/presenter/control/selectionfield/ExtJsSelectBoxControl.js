/**
 * This class is constructed by presenter automatically on your behalf.
 * 
 * @class
 * A control adaptor that allows the ExtJs SelectBox control to be used to render instances
 * of {@link br.presenter.node.SelectionField} within presenter.
 * 
 * <p>The ExtJs SelectBox control is aliased by <em>br.select-box</em>, and can
 * be used within templates as follows:</p>
 * 
 * <pre>
 *   &lt;span data-bind="value:selectionFieldProperty, control:'br.select-box'"&gt;&lt;/span&gt;
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
 * @extends br.presenter.control.selectionfield.ExtJsComboBoxControl
 */
br.presenter.control.selectionfield.ExtJsSelectBoxControl = function()
{
	// call super constructor
	br.presenter.control.selectionfield.ExtJsComboBoxControl.call(this);
	
	// modify ext settings (select boxes are just combo boxes that aren't editable in ext)
	this.m_mExtSettings.editable = false;
	this.m_mExtSettings.triggerAction = "all";
	delete this.m_mExtSettings.listeners.keyup;
	delete this.m_mExtSettings.listeners.blur;
};

br.extend(br.presenter.control.selectionfield.ExtJsSelectBoxControl, br.presenter.control.selectionfield.ExtJsComboBoxControl);
