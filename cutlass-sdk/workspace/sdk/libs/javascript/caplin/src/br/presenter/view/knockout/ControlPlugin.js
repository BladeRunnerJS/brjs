br.Core.thirdparty("knockout");

/**
 * @private
 * @constructor
 */
br.presenter.view.knockout.ControlPlugin = function()
{
};

/** @private */
br.presenter.view.knockout.ControlPlugin.prototype.init = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel)
{
	var sControlName = fValueAccessor();
	var oPresentationNode = fAllBindingsAccessor().controlNode || fAllBindingsAccessor().value;
	var mControlOptions = fAllBindingsAccessor().controloptions || fAllBindingsAccessor().controlOptions || {};
	var oPresenterComponent = oViewModel.__oPresenterComponent;
	var oControlAdaptor = br.presenter.control.ControlAdaptorFactory.createControlAdaptor(sControlName);
	
	if (br.presenter.view.knockout.ControlPlugin.hasSetElement(oControlAdaptor)) {
		oControlAdaptor.setElement(eElement);
	} else {
		// this is to support deprecated components that use getElement instead.
		eElement.appendChild(oControlAdaptor.getElement());
	}
	
	oControlAdaptor.setOptions(mControlOptions);
	oControlAdaptor.setPresentationNode(oPresentationNode);
	
	// If we're currently being added to a presenter component that is not yet attached
	// then our view will become ready when the presenter component is attached.
	var oControlPluginComponentLifecycleListener = new br.presenter.view.knockout.ControlPluginComponentLifecycleListener(oControlAdaptor);
	oPresenterComponent.addLifeCycleListener(oControlPluginComponentLifecycleListener);
	
	var fCleanUpFunction = ko.bindingHandlers.control._destroyWrapper(oControlAdaptor, oPresenterComponent, oControlPluginComponentLifecycleListener);
	ko.utils.domNodeDisposal.addDisposeCallback(eElement,fCleanUpFunction);

	// if the control is being added dynamically by knockout,
	// we won't receive an onOpen from the presenter component.
	if (oPresenterComponent.isViewAttached()) {
		oControlPluginComponentLifecycleListener.ensureViewReady();
	}
};

br.presenter.view.knockout.ControlPlugin.prototype.update = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel) {
	// this method doesn't provide us anything useful we don't already get in init()
};

br.presenter.view.knockout.ControlPlugin.hasSetElement = function(oControlAdaptor) {
	return (oControlAdaptor.setElement && (oControlAdaptor.setElement !== br.presenter.control.ControlAdaptor.prototype.setElement));
};

br.presenter.view.knockout.ControlPlugin.prototype._destroyWrapper = function(oControlAdaptor, oPresenterComponent, oControlPluginComponentLifecycleListener) {
	return function() {
		if(!oControlAdaptor.bDestroyed) {
			oControlAdaptor.destroy();
			oPresenterComponent.removeLifeCycleListener(oControlPluginComponentLifecycleListener);
		}
	};
};
