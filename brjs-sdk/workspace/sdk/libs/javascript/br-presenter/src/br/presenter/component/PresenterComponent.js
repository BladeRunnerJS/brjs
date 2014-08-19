"use strict";

/**
 * @module br/presenter/component/PresenterComponent
 */

br.Core.thirdparty("presenter-knockout");

var Utility = require('br/core/Utility');
var Errors = require('br/Errors');
var ServiceRegistry = require('br/ServiceRegistry');

/**
 * @class
 * @alias module:br/presenter/component/PresenterComponent
 * @implements module:br/component/Component
 * 
 * @description
 * Constructs a new instance of <code>PresenterComponent</code>.
 * 
 * <p>Instances of <code>PresenterComponent</code> can also be created from an XML snippet using the following
 * methods:</p>
 * 
 * <ul>
 *   <li>{@link PresenterComponentFactory#getViewerComponentFromXml}</li>
 * </ul>
 * 
 * <p>When component life cycle events are triggered on the <code>PresenterComponent</code> these 
 * are proxied through to the <code>PresentationModel</code> if they are defined in the <code>PresentationModel</code>.</p>
 * 
 * @param {String} sTemplateId The id of a template to render the presentation model with.
 * @param {Object} vPresentationModel A presentation model instance, or the name of a presentation model class that can be constructed.
 */
function PresenterComponent(sTemplateId, vPresentationModel) {
	this.m_sTemplateId = sTemplateId;
	this.m_eTemplate = this._getTemplate(sTemplateId);
	this.m_sPresentationModel = null;
	this.m_oPresentationModel = null;
	this.m_bViewBound = false;
	this.m_bViewAttached = false;
	this.m_oFrame = null;
	this.m_pLifecycleListeners = [];
	
	var oPresentationModel;
	if (typeof(vPresentationModel) == "string") {
		this.m_sPresentationModel = vPresentationModel;
		var fPresentationModel = Utility.locate(this.m_sPresentationModel);
		oPresentationModel = new fPresentationModel();
	} else {		
		oPresentationModel = vPresentationModel;
	}

	if (!(oPresentationModel instanceof br.presenter.PresentationModel)) {
		throw new Errors.InvalidParametersError("Presentation Model passed to PresenterComponent is not a br.presenter.PresentationModel");
	}
	this.m_oPresentationModel = oPresentationModel;
	
	PresenterComponent._initializePlugins();
	
	this.m_oPresentationModel._$setPath(this);
};

br.Core.implement(PresenterComponent, br.component.Component);
br.Core.implement(PresenterComponent, br.component.Serializable);

/**
 * @private
 */
PresenterComponent.TEMPLATE_NOT_FOUND = "TemplateNotFound";
PresenterComponent.TemplateNotFoundError = function(message, filename, lineNumber) {
	Errors.CustomError.call(this, PresenterComponent.TEMPLATE_NOT_FOUND, message, filename, lineNumber);
};
br.Core.extend(PresenterComponent.TemplateNotFoundError, Errors.CustomError);

/**
 * @private
 */
PresenterComponent._initializePlugins = function() {
	if (!presenter_ko.bindingHandlers.control) {
		presenter_ko.bindingHandlers.control = new br.presenter.view.knockout.ControlPlugin();
		presenter_ko.bindingHandlers.tooltip = new br.presenter.view.knockout.TooltipPlugin();
	}
};

/**
 * Retrieve the presentation model being displayed by this component.
 * 
 * @type br.presenter.PresentationModel
 */
PresenterComponent.prototype.getPresentationModel = function() {
	return this.m_oPresentationModel;
};

/**
 * Returns <code>true</code> once {@link br/component/Frame#onOpen} has
 * fired, and the display element has been attached to the page.
 * 
 * @type boolean
 */
PresenterComponent.prototype.isViewAttached = function() {
	return this.m_bViewAttached;
};

// *********************** Component Interface ***********************

PresenterComponent.prototype.setDisplayFrame = function(frame) {
	this.m_oFrame = frame;
	
	this.m_oPresentationModel.setComponentFrame(frame);

	function getEventHandler(event) {
		var handlerName = "on"+event.charAt(0).toUpperCase()+event.substring(1); 
		if (this[handlerName]) {
			return function() {
				this[handlerName].apply(this, arguments);
			}
		}
		return function() {
			this._propagateComponentEvent(handlerName, arguments);
		};
	}
	
	br.component.Frame.EVENTS.forEach(function(event) {
		frame.on(event, getEventHandler.call(this, event), this);
	}, this);

	frame.setContent(this.getElement());

};

PresenterComponent.prototype.getElement = function() {
	if (!this.m_bViewBound) {
		this.m_bViewBound = true;
		presenter_ko.applyBindings(this.m_oPresentationModel, this.m_eTemplate);
	}
	
	return this.m_eTemplate;
};

// It is the responsibility of the containing system to call serialize and then persist the resultant string.
// It can then call deserialize with that string to (re)create an instance of the component. 
// We write the component class into the serialized form so this component can throw an exception
// if an attempt is made to deserialize it with a string that was not created by this class.
// This class identifier should NOT be used by the containing system to map its serialized blobs to component type as 
// it is a private concern of this class and liable to change.
PresenterComponent.prototype.serialize = function() {
	var sSerializedState = "";
	
	if (!this.m_sPresentationModel) {
		this.m_sPresentationModel = this.m_oPresentationModel.getClassName();
	}
	
	if (br.Core.fulfills(this.m_oPresentationModel, br.presenter.SerializablePresentationModel)) {
		sSerializedState = this.m_oPresentationModel.serialize();
	}
	
	var sSerializedString = '<br.presenter.component.PresenterComponent templateId="' + this.m_sTemplateId + '" presentationModel="' + this.m_sPresentationModel + '">'
							+ sSerializedState + 
							'</br.presenter.component.PresenterComponent>';
	
	return sSerializedString;
};

/**
 * Extracts the data inside the presenter tag and gives it to the PresentationModel for deserialization.
 * Only has affect if the Presentation Model implements {@link module:br/presenter/SerializablePresentationModel}.
 * 
 * @param {String} sPresenterData The presenter xml node in string format
 */
PresenterComponent.prototype.deserialize = function(sPresenterData) {
	if (br.Core.fulfills(this.m_oPresentationModel, br.presenter.SerializablePresentationModel)) {
		var vOffsetPresenterOpeningTag = sPresenterData.indexOf('>');
		var vOffsetPresenterClosingTag = sPresenterData.indexOf('</br.presenter.component.PresenterComponent>');

		//If it doesn't have a closing presenter tag means it has no serialized data
		var sPresenterTagData = (vOffsetPresenterClosingTag !== -1) ? sPresenterData.substring(vOffsetPresenterOpeningTag+1, vOffsetPresenterClosingTag) : "";
		this.m_oPresentationModel.deserialize(sPresenterTagData);
	}
};

PresenterComponent.deserialize = function(sXml) 
{
	var oPresenterNode = br.util.XmlParser.parse( sXml );
	var sPresenterNodeName = oPresenterNode.nodeName;
	
	if(sPresenterNodeName !== "br.presenter.component.PresenterComponent" ) {
		var sErrorMsg = "Nodename for Presenter Configuration XML must be 'br.presenter.component.PresenterComponent', but was:" + sPresenterNodeName;
		
		throw new Errors.InvalidParametersError(sErrorMsg);
	}
	
	var sTemplateId = oPresenterNode.getAttribute("templateId");
	var sPresentationModel = oPresenterNode.getAttribute("presentationModel");
	
	var oPresenterComponent = new br.presenter.component.PresenterComponent(sTemplateId, sPresentationModel);
	oPresenterComponent.deserialize(sXml);
	
	return oPresenterComponent;
};

/**
 * When invoked, the onOpen component life cycle event is propagated to the 
 * <code>PresentationModel</code>.
 * 
 * @private
 */
PresenterComponent.prototype.onAttach = function() {
	if (this.m_bViewAttached === true) {
		return;
	}
	this.m_bViewAttached = true;
	this._propagateComponentEvent("onOpen", [this.m_oFrame.width, this.m_oFrame.height]);
};

/**
 * When invoked, the onClose component life cycle event is propagated to the 
 * <code>PresentationModel</code>.
 *
 * @private
 * @see br/component/Component#onClose
 */
PresenterComponent.prototype.onClose = function() {
	this._propagateComponentEvent("onClose", arguments);
	presenter_ko.cleanNode(this.m_eTemplate);
	this.m_oPresentationModel.removeChildListeners();
	this._nullObject(this.m_oPresentationModel);
	this.m_oPresentationModel = null;
	this.m_oComponentFrame = null;
	this.m_eTemplate = null;
	if (this.m_oFrame) {
		this.m_oFrame.clearListeners(this);
	}
};

/**
 * When invoked, the onResize component life cycle event is propagated to the 
 * <code>PresentationModel</code>.
 * 
 * @private
 * @param {int} nWidth
 * @param {int} nHeight
 * @see br/component/Component#onResize
 */
PresenterComponent.prototype.onResize = function() {
	this._propagateComponentEvent("onResize", [this.m_oFrame.width, this.m_oFrame.height]);
};

/**
 * When invoked, the onActivate component life cycle event is propagated to the 
 * <code>PresentationModel</code>.
 * 
 * @private
 * @see br/component/Component#onActivate
 */
PresenterComponent.prototype.onFocus = function() {
	this._propagateComponentEvent("onActivate", arguments);
};

/**
 * When invoked, the onDeactivate component life cycle event is propagated to the 
 * <code>PresentationModel</code>.
 * 
 * @private
 * @param {int} nWidth
 * @param {int} nHeight
 * @see br/component/Component#onDeactivate
 */
PresenterComponent.prototype.onBlur = function(nWidth, nHeight) {
	this._propagateComponentEvent("onDeactivate", arguments);
};

// *********************** Private Methods ***********************

/**
 * @private
 * @param {Object} oObjectToBeCleaned
 */
PresenterComponent.prototype._nullObject = function(oObjectToBeCleaned) {
	for(var sChildToBeCleaned in oObjectToBeCleaned) {
		var oChildToBeCleaned = oObjectToBeCleaned[sChildToBeCleaned];
		
		if (typeof oChildToBeCleaned === "object" && oChildToBeCleaned !== null) {
			oObjectToBeCleaned[sChildToBeCleaned] = null;
			
			if (oChildToBeCleaned instanceof br.presenter.node.PresentationNode) {
				this._nullObject(oChildToBeCleaned);
			}
		}
	}
};

/**
 * @private
 * @param {String} sEvent
 * @param {Array} pArguments
 */
PresenterComponent.prototype._propagateComponentEvent = function(sEvent, pArguments) {
	if (this.m_oPresentationModel[sEvent]) {
		this.m_oPresentationModel[sEvent].apply(this.m_oPresentationModel, pArguments);
	}
	this.m_pLifecycleListeners.forEach(function(listener){
		if (listener[sEvent]) {
			listener[sEvent].apply(listener, pArguments);
		}
	});
};

PresenterComponent.prototype.addLifeCycleListener = function(listener) {
	this.m_pLifecycleListeners.push(listener);
};

PresenterComponent.prototype.removeLifeCycleListener = function(listener) {
	var index = this.m_pLifecycleListeners.indexOf(listener);
	if (index >= 0) {
		this.m_pLifecycleListeners.splice(index, 1);
	}
}

/**
 * @private
 * @param {String} sTemplateId
 * @type Element
 */
PresenterComponent.prototype._getTemplate = function(sTemplateId) {
	var eTemplateHolder;
	var eTemplateNode = ServiceRegistry.getService("br.html-service").getHTMLTemplate(sTemplateId);

	if (!eTemplateNode)
	{
		throw new PresenterComponent.TemplateNotFoundError("Template with ID "+sTemplateId+" couldn't be found");		
	}

	eTemplateHolder = eTemplateNode.cloneNode(true);
	eTemplateHolder.removeAttribute('id');

	return eTemplateHolder;
};
