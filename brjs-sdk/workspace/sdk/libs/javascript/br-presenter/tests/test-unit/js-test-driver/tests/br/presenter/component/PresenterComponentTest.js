br.Core.thirdparty('mock4js');

PresenterComponentTest = TestCase("PresenterComponentTest");

PresenterComponentTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
	
	this.m_oOrigKo = presenter_ko;
	presenter_ko = {
		applyBindings: function() {},
		bindingHandlers: {}
	};

};

PresenterComponentTest.prototype.tearDown = function()
{
	presenter_ko = this.m_oOrigKo;
	Mock4JS.verifyAllMocks();
};

PresenterComponentTest.FakePresentationModel = function()
{
};
br.Core.extend(PresenterComponentTest.FakePresentationModel, br.presenter.PresentationModel);

PresenterComponentTest.MockPresentationModel = function()
{
};
br.Core.extend(PresenterComponentTest.MockPresentationModel, br.presenter.PresentationModel);
br.Core.implement(PresenterComponentTest.MockPresentationModel, br.presenter.SerializablePresentationModel);

PresenterComponentTest.MockPresentationModel.prototype.serialize = function()
{
	return "some serialization";
};

PresenterComponentTest.MockPresentationModel.prototype.deserialize = function(serializedState)
{
	this.serializedState = serializedState;
};

PresenterComponentTest.MockPresentationModel.prototype.getClassName = function()
{
	return "PresenterComponentTest.MockPresentationModel";
};

PresenterComponentTest.BadPresentationModel = function()
{
	// nothing
};

PresenterComponentTest.prototype.test_anExceptionIsThrownIfTheTemplateDoesNotExist = function()
{
	assertException("1a", function() {
		new br.presenter.component.PresenterComponent("template-does-not-exist", new PresenterComponentTest.MockPresentationModel());
	}, br.presenter.component.PresenterComponent.TEMPLATE_NOT_FOUND);
};

PresenterComponentTest.prototype.test_anExceptionIsThrownIfThePresentationModelIsNotAnInstanceOfPresentationModel = function()
{
	assertException("1a", function(){
		new br.presenter.component.PresenterComponent("presenter-template", new PresenterComponentTest.BadPresentationModel());
	}, br.Errors.INVALID_PARAMETERS);
	assertException("1b", function(){
		new br.presenter.component.PresenterComponent("presenter-template", "PresenterComponentTest.BadPresentationModel");
	}, br.Errors.INVALID_PARAMETERS);
};

PresenterComponentTest.prototype.test_getElementReturnsCorrectTemplate = function()
{
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template", new PresenterComponentTest.MockPresentationModel());
	var eTemplate = oPresenterComponent.getElement();
	assertEquals('<div></div>', eTemplate.innerHTML.toLowerCase());
};

PresenterComponentTest.prototype.test_getElementReturnsTemplateWithAttributesPreserved = function()
{
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template-with-attributes", new PresenterComponentTest.MockPresentationModel());
	var eTemplate = oPresenterComponent.getElement();
	assertEquals('<div></div>', eTemplate.innerHTML.toLowerCase());
	assertEquals('my-class', eTemplate.getAttribute('class'));
	assertEquals('custom-attribute', eTemplate.getAttribute('data-custom'));
	assertEquals(null, eTemplate.getAttribute('id'));
};

PresenterComponentTest.prototype.test_presentationModelCanBePassedAsAClassName = function()
{
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template", "PresenterComponentTest.MockPresentationModel");
	var eTemplate = oPresenterComponent.getElement();
	assertEquals('<div></div>', eTemplate.innerHTML.toLowerCase());
};

PresenterComponentTest.prototype.test_getSerializedStateReturnsCorrectXMLString = function()
{
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template", "PresenterComponentTest.MockPresentationModel");
	var sSerializedState = oPresenterComponent.serialize();
	var sExpectedSerializedForm = '<br.presenter.component.PresenterComponent templateId="presenter-template" presentationModel="PresenterComponentTest.MockPresentationModel">some serialization</br.presenter.component.PresenterComponent>';
	assertEquals(sExpectedSerializedForm, sSerializedState);
};

PresenterComponentTest.prototype.test_presentationModelReceivesCorrectDataToDeserialize= function()
{
	var oMockpresentationModel = mock(PresenterComponentTest.MockPresentationModel);
	
	oMockpresentationModel.stubs().getClassName().will(returnValue("PresenterComponentTest.MockPresentationModel"));
	
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template", oMockpresentationModel.proxy());
	var sSerializedForm1 = '<br.presenter.component.PresenterComponent templateId="presenter-template" presentationModel="PresenterComponentTest.MockPresentationModel"/>';
	var sSerializedForm2 = '<br.presenter.component.PresenterComponent templateId="presenter-template" presentationModel="PresenterComponentTest.MockPresentationModel"></br.presenter.component.PresenterComponent>';
	var sSerializedForm3 = '<br.presenter.component.PresenterComponent templateId="presenter-template" presentationModel="PresenterComponentTest.MockPresentationModel">some serialization</br.presenter.component.PresenterComponent>';

	oMockpresentationModel.expects(once()).deserialize("");
	oPresenterComponent.deserialize(sSerializedForm1);
	
	oMockpresentationModel.expects(once()).deserialize("");
	oPresenterComponent.deserialize(sSerializedForm2);
	
	oMockpresentationModel.expects(once()).deserialize("some serialization");
	oPresenterComponent.deserialize(sSerializedForm3);


};

PresenterComponentTest.prototype.test_presentationModelCanSerializeAndDeserializeTheSameState = function()
{
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template", "PresenterComponentTest.MockPresentationModel");
	var sSerializedState = oPresenterComponent.serialize();
	
	oPresenterComponent.deserialize(sSerializedState);
	
	assertEquals("some serialization", oPresenterComponent.getPresentationModel().serializedState);
};

PresenterComponentTest.prototype.test_getSerializedStateIsOnlyPossibleIfThePresentationModelClassNameIsProvided = function()
{
	var oPresenterComponent = new br.presenter.component.PresenterComponent("presenter-template", new PresenterComponentTest.FakePresentationModel());
	
	assertException("1a", function(){
		oPresenterComponent.serialize();
	}, br.Errors.UNIMPLEMENTED_ABSTRACT_METHOD);
};