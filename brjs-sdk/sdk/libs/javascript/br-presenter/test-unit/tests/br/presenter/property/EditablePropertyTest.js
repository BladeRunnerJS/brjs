(function() {
    var Errors = require("br/Errors");
    var PropertyListener = require("br/presenter/property/PropertyListener");
    var Parser = require("br/presenter/parser/Parser");
    var EditableProperty = require("br/presenter/property/EditableProperty");
    var Validator = require("br/presenter/validator/Validator");
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

    var EditablePropertyTest = TestCase("EditablePropertyTest");

    EditablePropertyTest.prototype.setUp = function()
    {
        Mock4JS.addMockSupport(window);
        Mock4JS.clearMocksToVerify();
    };

    EditablePropertyTest.prototype.tearDown = function()
    {
        Mock4JS.verifyAllMocks();
    };

    EditablePropertyTest.prototype._getTestValidator = function(bAsync)
    {
        var fValidator = function(bAsync)
        {
            this.m_bAsync = (bAsync === true);
            this.m_sResultMessage = bAsync ? "async" : "sync";
            this.m_pValidationRequests = [];
        };
        Core.implement(fValidator, Validator);
        
        fValidator.prototype.validate = function(sText, mConfig, oValidationResult)
        {
            var bIsValid = (sText.match(mConfig.failText)) ? false : true;
            this.m_pValidationRequests.push({isValid:bIsValid, result:oValidationResult});
            
            //only set results *now* if a synchronous validator, otherwise async
            // and done manually later by calling provideValidationResult().
            if(!this.m_bAsync)
            {
                this.provideValidationResult();
            }
        };

        fValidator.prototype.provideValidationResult = function()
        {
            var oValidationRequest = this.m_pValidationRequests.pop();
            
            oValidationRequest.result.setResult(oValidationRequest.isValid, this.m_sResultMessage);
        };

        return new fValidator(bAsync);
    };

    EditablePropertyTest.prototype.test_getValueIsUndefinedByDefault = function()
    {
        var oEditableProperty = new EditableProperty();
        assertEquals(undefined, oEditableProperty.getValue());
    };

    EditablePropertyTest.prototype.test_initialValueCanBePassedInConstructor = function()
    {
        var oEditableProperty = new EditableProperty("value");
        assertEquals("value", oEditableProperty.getValue());
    };

    EditablePropertyTest.prototype.test_setUserEnteredValueSetsValue = function()
    {
        var oEditableProperty = new EditableProperty();
        oEditableProperty.setUserEnteredValue("value");
        assertEquals("value", oEditableProperty.getValue());
    };

    EditablePropertyTest.prototype.test_onlyRunsEachParserOnceIfParserSpecifiesThisBehaviour = function()
    {
        var fParser = function(parseOperation){
            this.parseOperation = parseOperation;
        };
        Core.implement(fParser, Parser);

        fParser.prototype.parse = function(sValue, mConfig){
            return isNaN(sValue) ? null : this.parseOperation(sValue);
        };
        fParser.prototype.isSingleUseParser = function(){
            return true;
        };
        var oParser = new fParser(function(nValue){return nValue / 10});
        assertEquals("1", oParser.parse("10", {}));
        
        var oParser2 = new fParser(function(nValue){return nValue + "K"});
        var oEditableProperty = new EditableProperty().addParser(oParser, {}).addParser(oParser2, {});
        oEditableProperty.setUserEnteredValue("10");
        assertEquals("1K", oEditableProperty.getValue());
    };

    EditablePropertyTest.prototype.test_setUserEnteredValueSetsParsedValueWhenParsersAreAdded = function()
    {
        var fParser = function(){};
        Core.implement(fParser, Parser);

        fParser.prototype.parse = function(sText, mConfig){
            return sText.replace(mConfig.find, mConfig.replace);
        };
        fParser.prototype.isSingleUseParser = function(){
            return false;
        };
        var oParser = new fParser();
        var mParser1 = {find:"y", replace:"z"};
        var mParser2 = {find:"x", replace:"y"};

        assertEquals("xx", oParser.parse("xx", mParser1));
        assertEquals("yx", oParser.parse("xx", mParser2));

        var oEditableProperty = new EditableProperty().addParser(oParser, mParser1).addParser(oParser, mParser2);
        oEditableProperty.setUserEnteredValue("xx");
        assertEquals("zz", oEditableProperty.getValue());
    };

    EditablePropertyTest.prototype.test_whenThereAreNoValidatorsValidationAlwaysSucceed = function()
    {
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty();
        oEditableProperty.addListener(oMockListener.proxy());

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("whatever");
    };

    EditablePropertyTest.prototype.test_asyncValidatorCausesValidationError = function()
    {
        var oAsyncValidator = this._getTestValidator(true);
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty();
        oEditableProperty.addValidator(oAsyncValidator, {failText:"fail"}).addListener(oMockListener.proxy());

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oEditableProperty.setUserEnteredValue("fail");
        Mock4JS.verifyAllMocks();

        oMockListener.expects(once()).onValidationError(ANYTHING, ANYTHING);
        oMockListener.expects(once()).onValidationComplete();
        oAsyncValidator.provideValidationResult();
    };

    EditablePropertyTest.prototype.test_whenAnAsyncValidatorIsFirstInAListOfValidatorsItsErrorIsObservedFirstEvenIfItHappensSecond = function()
    {
        var oAsyncValidator = this._getTestValidator(true);
        var oSyncValidator = this._getTestValidator(false);
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty();
        oEditableProperty
            .addValidator(oAsyncValidator, {failText:"fail"})
            .addValidator(oSyncValidator, {failText:"fail"})
            .addListener(oMockListener.proxy());

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError("fail", "async");
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("fail");
        oAsyncValidator.provideValidationResult();
        Mock4JS.verifyAllMocks();
    };

    EditablePropertyTest.prototype.test_listenersAreNotNotifiedOfAsyncValidationsWhenPreviousValidatorsHaveNoResults = function()
    {
        var oAsyncValidatorFirst = this._getTestValidator(true);
        var oAsyncValidatorSecond = this._getTestValidator(true);
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty().addListener(oMockListener.proxy());
        oEditableProperty
            .addValidator(oAsyncValidatorFirst, {failText:"fails first validator"})
            .addValidator(oAsyncValidatorSecond, {failText:"fails second validator"});

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(never()).onValidationSuccess();
        oMockListener.expects(never()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("fails first validator");
        oAsyncValidatorSecond.provideValidationResult();
        Mock4JS.verifyAllMocks();

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(never()).onValidationSuccess();
        oMockListener.expects(never()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("fails second validator");
        oAsyncValidatorSecond.provideValidationResult();
        Mock4JS.verifyAllMocks();

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(never()).onValidationSuccess();
        oMockListener.expects(never()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("passes both");
        oAsyncValidatorSecond.provideValidationResult();
    };

    EditablePropertyTest.prototype.test_validationErrorInAnAsyncValidatorEarlyInTheListNotifiesListenersOfErrorImmediately = function()
    {
        var oAsyncValidatorFirst = this._getTestValidator(true);
        var oAsyncValidatorSecond = this._getTestValidator(true);
        var oAsyncValidatorThird = this._getTestValidator(true);
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty().addListener(oMockListener.proxy());
        oEditableProperty
            .addValidator(oAsyncValidatorFirst, {failText:"fails first validator"})
            .addValidator(oAsyncValidatorSecond, {failText:"fails second validator"})
            .addValidator(oAsyncValidatorThird, {failText:"fails third validator"});

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError("fails second validator", "async");
        oMockListener.expects(never()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("fails second validator");
        oAsyncValidatorSecond.provideValidationResult();
        oAsyncValidatorFirst.provideValidationResult();
    };

    EditablePropertyTest.prototype.test_validationSuccessForAsyncValidatorsHappensOnlyWhenTheyAllSucceed = function()
    {
        var oAsyncValidatorFirst = this._getTestValidator(true);
        var oAsyncValidatorSecond = this._getTestValidator(true);
        var oAsyncValidatorThird = this._getTestValidator(true);
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty().addListener(oMockListener.proxy());
        oEditableProperty
            .addValidator(oAsyncValidatorFirst, {failText:"fails first validator"})
            .addValidator(oAsyncValidatorSecond, {failText:"fails second validator"})
            .addValidator(oAsyncValidatorThird, {failText:"fails third validator"});

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(never()).onValidationSuccess(); // "never" succeed with only 2/3 passes
        oMockListener.expects(never()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("pass1");
        oAsyncValidatorFirst.provideValidationResult();
        oAsyncValidatorSecond.provideValidationResult();
        Mock4JS.verifyAllMocks();

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("pass2");
        oAsyncValidatorSecond.provideValidationResult();
        oAsyncValidatorFirst.provideValidationResult();
        oAsyncValidatorThird.provideValidationResult(); // three passes this time
    };

    EditablePropertyTest.prototype.test_previousValidationResultsAreIgnoredIfTheUserModifiesThePropertyBeforeValidationIsComplete = function()
    {
        var oAsyncValidatorFirst = this._getTestValidator(true);
        var oAsyncValidatorSecond = this._getTestValidator(true);
        var oMockListener = mock(PropertyListener);
        var oEditableProperty = new EditableProperty().addListener(oMockListener.proxy());
        
        oEditableProperty
            .addValidator(oAsyncValidatorFirst, {failText:"fail"})
            .addValidator(oAsyncValidatorSecond, {failText:"fail"});
        
        oMockListener.expects(exactly(2)).onPropertyUpdated();
        oMockListener.expects(exactly(1)).onPropertyChanged();
        oMockListener.expects(never()).onValidationSuccess();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(never()).onValidationComplete();
        
        oEditableProperty.setUserEnteredValue("pass");
        oAsyncValidatorFirst.provideValidationResult();
        oEditableProperty.setUserEnteredValue("pass");
        oAsyncValidatorSecond.provideValidationResult();
        Mock4JS.verifyAllMocks();
        
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(never()).onValidationError();
        oMockListener.expects(once()).onValidationComplete();
        
        oAsyncValidatorFirst.provideValidationResult();
        oAsyncValidatorSecond.provideValidationResult();
    };

    EditablePropertyTest.prototype.test_validatorsAreNotQueriedIfAnEarlierValidatorFailsSynchronously = function()
    {
        var oMockValidator1 = new Validator();
        var oMockValidator2 = mock(Validator);
        var oEditableProperty = new EditableProperty().addValidator(oMockValidator1).addValidator(oMockValidator2.proxy());
        
        oMockValidator1.validate = function(vValue, mAttributes, oValidationResult)
        {
            // by synchronously failing validation, the next validator shouldn't ever be queried
            oValidationResult.setResult(false, "validation failed!");
        };
        
        oMockValidator2.expects(never()).validate();
        oEditableProperty.setUserEnteredValue("some value");
    };

    EditablePropertyTest.prototype.test_invalidValueCausesOnPropertyChangedError = function()
    {
        var oValidator = this._getTestValidator();
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty().addValidator(oValidator, {failText:"fail"}).addListener(oMockListener.proxy());

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("pass");
        Mock4JS.verifyAllMocks();

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError("fail", "sync");
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("fail");
    };

    EditablePropertyTest.prototype.test_multipleSynchronousValidatorsAreHandledCorrectly = function()
    {
        var oValidator = this._getTestValidator();
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty().addListener(oMockListener.proxy());
        oEditableProperty.addValidator(oValidator, {failText:"x"}).addValidator(oValidator, {failText:"y"});
        
        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError(ANYTHING, ANYTHING);
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("x");
        Mock4JS.verifyAllMocks();
        
        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError(ANYTHING, ANYTHING);
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("y");
        Mock4JS.verifyAllMocks();
        
        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("z");
    };

    EditablePropertyTest.prototype.test_removingAValidatorStopsValidationErrors = function()
    {
        var oValidator = this._getTestValidator();
        var oMockListener = mock(PropertyListener);
        var mValidatorInfo = {};
        var oEditableProperty = new EditableProperty().addValidator(oValidator, {failText:"fail"},
            mValidatorInfo).addListener(oMockListener.proxy());

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError("fail", "sync");
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("fail");
        Mock4JS.verifyAllMocks();

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.removeValidator(mValidatorInfo);
        oEditableProperty.setUserEnteredValue("fail");
    };

    EditablePropertyTest.prototype.test_onValidationErrorOnlyEverFiresOnce = function()
    {
        var oValidator = this._getTestValidator();
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty().addListener(oMockListener.proxy());
        oEditableProperty.addValidator(oValidator, {failText:"x"}).addValidator(oValidator, {failText:"y"});

        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError(ANYTHING, ANYTHING);
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.setUserEnteredValue("xy");
        Mock4JS.verifyAllMocks();
    };

    EditablePropertyTest.prototype.test_immediateNotificationListenerCallbackCanFireOnValidationSuccess = function()
    {
        var oValidator = this._getTestValidator();
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty("pass").addValidator(oValidator, {failText:"fail"});
        
        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationSuccess();
        oMockListener.expects(once()).onValidationComplete();
        
        oEditableProperty.addListener(oMockListener.proxy(), true);
    };

    EditablePropertyTest.prototype.test_immediateNotificationListenerCallbackCanFireOnValidationError = function()
    {
        var oValidator = this._getTestValidator();
        var oMockListener = mock(PropertyListener);

        var oEditableProperty = new EditableProperty("fail").addValidator(oValidator, {failText:"fail"});
        
        oMockListener.expects(once()).onPropertyUpdated();
        oMockListener.expects(once()).onPropertyChanged();
        oMockListener.expects(once()).onValidationError(ANYTHING, ANYTHING);
        oMockListener.expects(once()).onValidationComplete();
        oEditableProperty.addListener(oMockListener.proxy(), true);
    };

    EditablePropertyTest.prototype.test_exceptionIsThrownIfParserDoesNotImplementParserInterface = function()
    {
        var oEditableProperty = new EditableProperty("value");
        
        assertException("1a", function(){
            oEditableProperty.addParser({});
        }, Errors.INVALID_PARAMETERS);
    };

    EditablePropertyTest.prototype.test_exceptionIsThrownIfValidatorDoesNotImplementValidatorInterface = function()
    {
        var oEditableProperty = new EditableProperty("value");
        
        assertException("1a", function(){
            oEditableProperty.addValidator({});
        }, Errors.INVALID_PARAMETERS);
    };

    EditablePropertyTest.prototype.test_setUserEnteredValueProvidesADefaultParserAttributeMapIfNotProvided = function()
    {
        var oParserMock = mock(Parser);
        
        // passes through any config we do provide
        oParserMock.expects(once()).parse("1.23456789", {key:"value"});
        var oEditableProperty = new EditableProperty().addParser(oParserMock.proxy(), {key:"value"});
        oEditableProperty.setUserEnteredValue("1.23456789");
        
        // defaults if not provided
        oParserMock.expects(once()).parse("1.23456789", {});
        var oEditableProperty = new EditableProperty().addParser(oParserMock.proxy());
        oEditableProperty.setUserEnteredValue("1.23456789");
    };

    EditablePropertyTest.prototype.test_setUserEnteredValueProvidesADefaultValidatorAttributeMapIfNotProvided = function()
    {
        var oValidatorMock = mock(Validator);
        
        // passes through any config we do provide
        oValidatorMock.expects(once()).validate("1.23456789", {key:"value"}, and(NOT_NULL, NOT_UNDEFINED)).will(returnValue({isValid:function(){return true;}}));
        var oEditableProperty = new EditableProperty().addValidator(oValidatorMock.proxy(), {key:"value"});
        oEditableProperty.setUserEnteredValue("1.23456789");
        
        // defaults if not provided
        oValidatorMock.expects(once()).validate("1.23456789", {}, and(NOT_NULL, NOT_UNDEFINED)).will(returnValue({isValid:function(){return true;}}));
        var oEditableProperty = new EditableProperty().addValidator(oValidatorMock.proxy());
        oEditableProperty.setUserEnteredValue("1.23456789");
    };

    EditablePropertyTest.prototype.getListenerClass = function()
    {
        var fListenerClass = function()
        {
        };
        
        fListenerClass.prototype.invocationMethod = function()
        {
        };
        
        return fListenerClass;
    };

    EditablePropertyTest.prototype.test_weCanAddAndRemoveAValidationErrorOnlyListener = function()
    {
        var oListenerMock = mock(this.getListenerClass());
        var oProperty = new EditableProperty();
        var oValidator = this._getTestValidator();
        var oPropertyListener = oProperty.addValidationErrorListener(oListenerMock.proxy(), "invocationMethod");
        
        oProperty.addValidator(oValidator, {failText:"fail"});
        oListenerMock.expects(once()).invocationMethod("fail", "sync");
        oProperty.setUserEnteredValue("fail");
        
        oProperty.removeListener(oPropertyListener);
    };

    EditablePropertyTest.prototype.test_specifyingANonExistentValidationErrorListenerMethodCausesAnException = function()
    {
        var oListenerMock = mock(this.getListenerClass());
        var oProperty = new EditableProperty();
        
        assertException("1a", function(){
            oProperty.addValidationErrorListener(oListenerMock.proxy(), "noSuchMethod");
        }, 'TypeError');
    };

    EditablePropertyTest.prototype.test_weCanRequestForTheListenerToInvokeCallbackImmediatelyForValidationErrorListener = function()
    {
        var oListenerMock = mock(this.getListenerClass());
        var oValidator = this._getTestValidator();
        var oProperty = new EditableProperty("fail").addValidator(oValidator, {failText:"fail"});
        
        oListenerMock.expects(once()).invocationMethod("fail", "sync");
        oProperty.addValidationErrorListener(oListenerMock.proxy(), "invocationMethod", true);
    };


    EditablePropertyTest.prototype.test_weCanAddAndRemoveAValidationSuccessListener = function()
    {
        var oListenerMock = mock(this.getListenerClass());
        var oProperty = new EditableProperty();
        var oValidator = this._getTestValidator();
        var oPropertyListener = oProperty.addValidationSuccessListener(oListenerMock.proxy(), "invocationMethod");

        oProperty.addValidator(oValidator, {failText:"fail"});
        oListenerMock.expects(once()).invocationMethod();
        oProperty.setUserEnteredValue("1");

        oProperty.removeListener(oPropertyListener);
    };

    EditablePropertyTest.prototype.test_specifyingANonExistentValidationSuccessListenerMethodCausesAnException = function()
    {
        var oListenerMock = mock(this.getListenerClass());
        var oProperty = new EditableProperty();

        assertException("1a", function(){
            oProperty.addValidationSuccessListener(oListenerMock.proxy(), "noSuchMethod");
        }, 'TypeError');
    };

    EditablePropertyTest.prototype.test_weCanRequestForTheListenerToInvokeCallbackImmediatelyForValidationSuccessListener = function()
    {
        var oListenerMock = mock(this.getListenerClass());
        var oValidator = this._getTestValidator();
        var oProperty = new EditableProperty("1").addValidator(oValidator, {failText:"fail"});

        oListenerMock.expects(once()).invocationMethod();
        oProperty.addValidationSuccessListener(oListenerMock.proxy(), "invocationMethod", true);

    };

    EditablePropertyTest.prototype.test_hasValidationErrorReturnsTrueIfAtLeastOneValidatorHasError = function()
    {
        var oValidator = this._getTestValidator(false);
        var oValidator2 = this._getTestValidator(false);

        var oEditableProperty = new EditableProperty();
        oEditableProperty.addValidator(oValidator, {failText:"fail"});
        oEditableProperty.addValidator(oValidator2, {failText:"fail2"});

        oEditableProperty.setUserEnteredValue("fail");
        
        assertTrue(oEditableProperty.hasValidationError());
        
    };

    EditablePropertyTest.prototype.test_hasValidationErrorReturnsFalseIfAllValidatorsAreCorrect = function()
    {
        var oValidator = this._getTestValidator(false);
        var oValidator2 = this._getTestValidator(false);

        var oEditableProperty = new EditableProperty();
        oEditableProperty.addValidator(oValidator, {failText:"fail"});
        oEditableProperty.addValidator(oValidator2, {failText:"fail2"});

        oEditableProperty.setUserEnteredValue("success");
        
        assertFalse(oEditableProperty.hasValidationError());
    };

    EditablePropertyTest.prototype.test_weDontContinuouslyReparseNaN = function()
    {
    	var fParser = function(){};
    	Core.implement(fParser, Parser);

    	fParser.prototype.parse = function(sText, mConfig){
    		return sText;
    	};
    	fParser.prototype.isSingleUseParser = function(){
    		return false;
    	};

    	var oEditableProperty = new EditableProperty().addParser(new fParser(), {});
    	oEditableProperty.setUserEnteredValue(NaN);
    	assertTrue(Number.isNaN(oEditableProperty.getValue()));
    };
    
    EditablePropertyTest.prototype.test_parsersCanBeRemovedAndReturnTrueOnlyIfTheyAreRemoved = function()
    {
    	var fParser = function(parseOperation) {
    		this.parseOperation = parseOperation;
    	};
    	Core.implement(fParser, Parser);
    
    	fParser.prototype.parse = function(sValue, mConfig){
    		return isNaN(sValue) ? null : this.parseOperation(sValue);
    	};
    	fParser.prototype.isSingleUseParser = function(sValue, mConfig){
    		return true;
    	};
    	
    	var oParser1 = new fParser(function(nValue){return nValue / 10});
    	var oParser2 = new fParser(function(nValue){return nValue + "K"});
    	
    	var oEditableProperty = new EditableProperty().addParser(oParser1, {}).addParser(oParser2, {});
    	
    	oEditableProperty.setUserEnteredValue("10");
    	assertEquals("1K", oEditableProperty.getValue());
    	
    	assertTrue(oEditableProperty.removeParser(oParser1));
    	oEditableProperty.setUserEnteredValue("10");
    	assertEquals("10K", oEditableProperty.getValue());
    	
    	assertFalse(oEditableProperty.removeParser(oParser1));
    };

})();