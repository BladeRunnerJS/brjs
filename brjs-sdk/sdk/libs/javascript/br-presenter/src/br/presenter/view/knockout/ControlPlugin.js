'use strict';

var ControlAdaptor = require('br/presenter/control/ControlAdaptor');
var ControlPluginComponentLifecycleListener = require('br/presenter/view/knockout/ControlPluginComponentLifecycleListener');
var Core = require('br/Core');

/**
 * @module br/presenter/view/knockout/ControlPlugin
 */

var presenter_knockout = require('presenter-knockout');

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/ControlPlugin
 */
function ControlPlugin() {
}

/** @private */
ControlPlugin.prototype.init = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel) {
	var sControlName = fValueAccessor();
	var oPresentationNode = fAllBindingsAccessor().controlNode || fAllBindingsAccessor().value;
	var mControlOptions = fAllBindingsAccessor().controloptions || fAllBindingsAccessor().controlOptions || {};
	var oPresenterComponent = oViewModel.__oPresenterComponent;
	
	var ControlAdaptorFactory = require('br/presenter/control/ControlAdaptorFactory');
	var oControlAdaptor = ControlAdaptorFactory.createControlAdaptor(sControlName);

	if (ControlPlugin.hasSetElement(oControlAdaptor)) {
		oControlAdaptor.setElement(eElement);
	} else {
		// this is to support deprecated components that use getElement instead.
		eElement.appendChild(oControlAdaptor.getElement());
	}

	oControlAdaptor.setOptions(mControlOptions);
	oControlAdaptor.setPresentationNode(oPresentationNode);

	// If we're currently being added to a presenter component that is not yet attached
	// then our view will become ready when the presenter component is attached.
	var oControlPluginComponentLifecycleListener = new ControlPluginComponentLifecycleListener(oControlAdaptor);
	oPresenterComponent.addLifeCycleListener(oControlPluginComponentLifecycleListener);

	var fCleanUpFunction = presenter_knockout.bindingHandlers.control._destroyWrapper(oControlAdaptor, oPresenterComponent, oControlPluginComponentLifecycleListener);
	presenter_knockout.utils.domNodeDisposal.addDisposeCallback(eElement, fCleanUpFunction);

	// if the control is being added dynamically by knockout,
	// we won't receive an onOpen from the presenter component.
	if (oPresenterComponent.isViewAttached()) {
		oControlPluginComponentLifecycleListener.ensureViewReady();
	}
};

ControlPlugin.prototype.update = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel) {
	// this method doesn't provide us anything useful we don't already get in init()
};

ControlPlugin.hasSetElement = function(oControlAdaptor) {
	return (oControlAdaptor.setElement && (oControlAdaptor.setElement !== ControlAdaptor.prototype.setElement));
};

ControlPlugin.prototype._destroyWrapper = function(oControlAdaptor, oPresenterComponent, oControlPluginComponentLifecycleListener) {
	return function() {
		if (!oControlAdaptor.bDestroyed) {
			oControlAdaptor.destroy();
			oPresenterComponent.removeLifeCycleListener(oControlPluginComponentLifecycleListener);
		}
	};
};

module.exports = ControlPlugin;
