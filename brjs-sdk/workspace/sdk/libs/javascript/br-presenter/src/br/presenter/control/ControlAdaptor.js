"use strict";

/**
 * @module br/presenter/control/ControlAdaptor
 */

var Errors = require('br/Errors');

/**
 * @class
 * @interface
 * @alias module:br/presenter/control/ControlAdaptor
 * 
 * @classdesc
 * Interface implemented by control adaptor classes, that allow external controls to be used within presenter.
 */
function ControlAdaptor() {};

/**
 * Sets the element in which the control will be rendered in.
 *
 * @param  {Element} eElement DOM element to use as the container for the control. Some controls will use this
 * element as the actual control (progressive enhancement).
 */
ControlAdaptor.prototype.setElement = function(eElement) {
	throw new Errors.UnimplementedInterfaceError("ControlAdaptor.setElement() has not been implemented.");
};

/**
 * Sets the configuration options of the control.
 * 
 * <p>Control configuration options passed within the HTML template are made available using this
 * method. Control options can be passed within the HTML template like this:</p>
 * 
 * <pre>
 * &lt;div id="dropDownSelectBox"
 *  data-bind="controlNode:selectionField, control:'ext-select-box', controlOptions:{width:95}"&gt;
 * &lt;/div&gt;
 * </pre>
 * 
 * @param {Object} mOptions A map of options
 */
ControlAdaptor.prototype.setOptions = function(mOptions)
{
	throw new Errors.UnimplementedInterfaceError("ControlAdaptor.setOptions() has not been implemented.");
};

/**
 * Sets the presentation node the control is being bound to.
 * 
 * <p>It is the control adaptors responsibility to synchronize the presentation node with the control
 * being adapted; any changes to the presentation node properties must be reflected in the control,
 * and any user interactions with the control must be reflected in the presentation node.</p>
 * 
 * <p>Each control adaptor will typically be designed to work with a single type of presentation
 * node, for example:</p>
 * 
 * <ul>
 *   <li>{@link module:br/presenter/node/Button}</li>
 *   <li>{@link module:br/presenter/node/Field}</li>
 *   <li>{@link module:br/presenter/node/SelectionField}</li>
 *   <li>{@link module:br/presenter/node/MultiSelectionField}</li>
 * </ul>
 * 
 * <p>Authors are encouraged specify the presentation node type the control adaptor accepts within the
 * package name (e.g.
 * <code>br.presenter.control.<b>selectionfield</b>.JQueryAutoCompleteControl</code>), and to fail
 * fast by throwing a {@link module:br/presenter/control/InvalidControlModelError} if they are bound
 * to the wrong type of presentation node.</p>
 * 
 * @param {module:br/presenter/node/PresentationNode} oPresentationNode The presentation node the control
 * is being bound to.
 * @throws {br.presenter.control.InvalidControlModelError} if the wrong type of presentation
 * node is used.
 */
ControlAdaptor.prototype.setPresentationNode = function(oPresentationNode)
{
		// do nothing -- this method is optional.
};

/**
 * Called after the view is rendered to screen.
 * 
 * <p>Controls that need to perform some additional work once the element they are contained within
 * has been added to the document (via {@link #getElement}),  can do so within this call-back.</p>
 */
ControlAdaptor.prototype.onViewReady = function()
{
		// do nothing -- this method is optional.
};

/**
 * Called when the view is being disposed of to allow cleaning up of listeners and controls.
 */
ControlAdaptor.prototype.destroy = function()
{
	// do nothing -- this method is optional.
};

br.presenter.control.ControlAdaptor = ControlAdaptor;
