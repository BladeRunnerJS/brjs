PresenterComponentFactoryTest = TestCase("PresenterComponentFactoryTest");

PresenterComponentFactoryTest.prototype.setUp = function()
{
	this.m_ePresenterTemplate = document.createElement("presenter-template");
	this.m_ePresenterTemplate.id = "presenter-template";
	document.body.appendChild(this.m_ePresenterTemplate);
};

PresenterComponentFactoryTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_ePresenterTemplate);
};

PresenterComponentFactoryTest.MockPresentationModel = function()
{
	// nothing
};
br.extend(PresenterComponentFactoryTest.MockPresentationModel, br.presenter.PresentationModel);

PresenterComponentFactoryTest.BadPresentationModel = function()
{
	// nothing
};

PresenterComponentFactoryTest.prototype.test_anExceptionIsThrownIfTheAliasGroupIdentfierIsNotComponent = function()
{
	assertException("An Error (INVALID_PARAMETERS) is thrown if the alias group identifer is not 'component'", function(){

		var presenterXML = '<notbr.presenter-component templateId="presenter-template" presentationModel="PresenterComponentFactoryTest.MockPresentationModel"/>';
		var oPCF = new br.presenter.PresenterComponentFactory();

		var oError = oPCF.createFromXml(presenterXML);
	}, br.Errors.INVALID_PARAMETERS);
};

PresenterComponentFactoryTest.prototype.test_anExceptionIsThrownIfThePresentationModelIsNotAnInstanceOfPresentationModel = function()
{
	assertException("An Error (LEGACY_ERROR) is thrown if the provided presentation model is not an instance of PresentationModel", function(){

		var presenterXML = '<br.presenter-component templateId="presenter-template" presentationModel="PresenterComponentFactoryTest.BadPresentationModel" />';
		var oPCF = new br.presenter.PresenterComponentFactory();

		var oError = oPCF.createFromXml(presenterXML);

	}, br.Errors.LEGACY_ERROR);
};

PresenterComponentFactoryTest.prototype.test_aPresenterComponentIsReturnedWhenValidXmlIsProvided = function()
{
	var presenterXML = '<br.presenter-component templateId="presenter-template" presentationModel="PresenterComponentFactoryTest.MockPresentationModel" />';
	var oPCF = new br.presenter.PresenterComponentFactory();

	var oPresenterComponent = oPCF.createFromXml(presenterXML);

	assertInstanceOf("A PresenterComponent is returned when PresenterCompnentFactory's createXML method is provided with valid XML", br.presenter.component.PresenterComponent, oPresenterComponent);
};


