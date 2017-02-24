(function() {
    var Errors = require("br/Errors");
    var PresenterComponent = require("br/presenter/component/PresenterComponent");
    var SerializablePresentationModel = require("br/presenter/SerializablePresentationModel");
    var PresentationModel = require("br/presenter/PresentationModel");
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

    PresenterComponentTest = TestCase("PresenterComponentTest");

    PresenterComponentTest.prototype.setUp = function()
    {
        Mock4JS.addMockSupport(window);
        Mock4JS.clearMocksToVerify();

        this.m_oOrigKo = presenter_knockout;
        presenter_knockout = {
            applyBindings: function() {},
            bindingHandlers: {}
        };

    };

    PresenterComponentTest.prototype.tearDown = function()
    {
        presenter_knockout = this.m_oOrigKo;
        Mock4JS.verifyAllMocks();
    };

    PresenterComponentTest.FakePresentationModel = function()
    {
    };
    Core.extend(PresenterComponentTest.FakePresentationModel, PresentationModel);

    PresenterComponentTest.MockPresentationModel = function()
    {
    };
    Core.extend(PresenterComponentTest.MockPresentationModel, PresentationModel);
    Core.implement(PresenterComponentTest.MockPresentationModel, SerializablePresentationModel);

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
            new PresenterComponent("template-does-not-exist", new PresenterComponentTest.MockPresentationModel()).getElement();
        });
    };

    PresenterComponentTest.prototype.test_anExceptionIsThrownIfThePresentationModelIsNotAnInstanceOfPresentationModel = function()
    {
        assertException("1a", function(){
            new PresenterComponent("presenter-template", new PresenterComponentTest.BadPresentationModel());
        }, Errors.INVALID_PARAMETERS);
        assertException("1b", function(){
            new PresenterComponent("presenter-template", "PresenterComponentTest.BadPresentationModel");
        });
    };

    PresenterComponentTest.prototype.test_getElementReturnsCorrectTemplate = function()
    {
        var oPresenterComponent = new PresenterComponent("presenter-template", new PresenterComponentTest.MockPresentationModel());
        var eTemplate = oPresenterComponent.getElement();
        assertEquals('<div></div>', eTemplate.innerHTML.toLowerCase());
    };

    PresenterComponentTest.prototype.test_getElementReturnsTemplateWithAttributesPreserved = function()
    {
        var oPresenterComponent = new PresenterComponent("presenter-template-with-attributes", new PresenterComponentTest.MockPresentationModel());
        var eTemplate = oPresenterComponent.getElement();
        assertEquals('<div></div>', eTemplate.innerHTML.toLowerCase());
        assertEquals('my-class', eTemplate.getAttribute('class'));
        assertEquals('custom-attribute', eTemplate.getAttribute('data-custom'));
        assertEquals(null, eTemplate.getAttribute('id'));
    };

    PresenterComponentTest.prototype.test_presentationModelCanBePassedAsAClassName = function()
    {
        var oPresenterComponent = new PresenterComponent("presenter-template", new PresenterComponentTest.MockPresentationModel());
        var eTemplate = oPresenterComponent.getElement();
        assertEquals('<div></div>', eTemplate.innerHTML.toLowerCase());
    };

    PresenterComponentTest.prototype.test_getSerializedStateReturnsCorrectXMLString = function()
    {
        var oPresenterComponent = new PresenterComponent("presenter-template", new PresenterComponentTest.MockPresentationModel());
        var sSerializedState = oPresenterComponent.serialize();
        var sExpectedSerializedForm = '<br.presenter.component.PresenterComponent templateId="presenter-template" presentationModel="PresenterComponentTest.MockPresentationModel">some serialization</br.presenter.component.PresenterComponent>';
        assertEquals(sExpectedSerializedForm, sSerializedState);
    };

    PresenterComponentTest.prototype.test_presentationModelReceivesCorrectDataToDeserialize= function()
    {
        var oMockpresentationModel = mock(PresenterComponentTest.MockPresentationModel);

        oMockpresentationModel.stubs().getClassName().will(returnValue("PresenterComponentTest.MockPresentationModel"));

        var oPresenterComponent = new PresenterComponent("presenter-template", oMockpresentationModel.proxy());
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
        var oPresenterComponent = new PresenterComponent("presenter-template", new PresenterComponentTest.MockPresentationModel());
        var sSerializedState = oPresenterComponent.serialize();

        oPresenterComponent.deserialize(sSerializedState);

        assertEquals("some serialization", oPresenterComponent.getPresentationModel().serializedState);
    };

    PresenterComponentTest.prototype.test_getSerializedStateIsOnlyPossibleIfThePresentationModelClassNameIsProvided = function()
    {
        var oPresenterComponent = new PresenterComponent("presenter-template", new PresenterComponentTest.FakePresentationModel());

        assertException("1a", function(){
            oPresenterComponent.serialize();
        }, Errors.UNIMPLEMENTED_ABSTRACT_METHOD);
    };
})();
