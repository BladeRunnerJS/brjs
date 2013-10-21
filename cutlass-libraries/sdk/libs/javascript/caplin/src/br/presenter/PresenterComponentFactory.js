
/**
 * @class
 * Allows instances of {@link br.presenter.component.PresenterComponent} to be constructed via XML snippets.
 * 
 * @constructor
 */
br.presenter.PresenterComponentFactory = function() {};

br.presenter.PresenterComponentFactory.prototype.createFromXml = function( sXml )
{
	var oPresenterNode = br.util.XmlParser.parse( sXml );
	var sPresenterNodeName = oPresenterNode.nodeName;
	
	if( sPresenterNodeName !== 'presenter' && sPresenterNodeName !== "br.presenter-component" ) {
		var sErrorMsg = "Nodename for Presenter Configuration XML must be either 'presenter' or 'br.presenter-component', but was:" + sPresenterNodeName;
		
		throw new br.Errors.InvalidParametersError(sErrorMsg);
	}
	
	var sTemplateId = oPresenterNode.getAttribute("templateId");
	var sPresentationModel = oPresenterNode.getAttribute("presentationModel");
	
	var oPresenterComponent = new br.presenter.component.PresenterComponent(sTemplateId, sPresentationModel);
	oPresenterComponent.deserialize(sXml);
	
	return oPresenterComponent;
};
