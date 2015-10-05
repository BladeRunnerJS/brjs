
(function() {
    var AliasRegistry = require("br/AliasRegistry");
    var ControlAdaptorFactory = require("br/presenter/control/ControlAdaptorFactory");
    ControlAdaptorFactoryTest = TestCase("ControlAdaptorFactoryTest");

    ControlAdaptorFactoryTest.prototype.setUp = function(){
        var oSelf = this;
        this.TestControlAdaptorClass=function(){
            this.setOptions=function(mOptions)
            {
                oSelf.optionsRecieved = mOptions;
            };
        };
        this.originalControlAdaptorFactory = ControlAdaptorFactory;
        
        this.fAliasRegistryGetClass = AliasRegistry.getClass;
        this.fAliasRegistryisAliasAssigned = AliasRegistry.isAliasAssigned;
        AliasRegistry.isAliasAssigned = function(sAlias)
        {
            return sAlias === "control_testControl";
        }
        
        AliasRegistry.getClass = function(sAlias)
        {
            return (sAlias == "control_testControl") ? oSelf.TestControlAdaptorClass : null;
        }
        
    };

    ControlAdaptorFactoryTest.prototype.tearDown = function(){
        ControlAdaptorFactory = this.originalControlAdaptorFactory;
        
        AliasRegistry.getClass = this.fAliasRegistryGetClass;
        AliasRegistry.isAliasAssigned = this.fAliasRegistryisAliasAssigned;
    };


    ControlAdaptorFactoryTest.prototype.test_createControlAdaptorReturnsControlAdaptorFromAlias = function()
    {
        var myControlAdaptor = ControlAdaptorFactory.createControlAdaptor('control_testControl');
        assertInstanceOf(this.TestControlAdaptorClass,myControlAdaptor);
    };

    ControlAdaptorFactoryTest.prototype.test_createControlAdaptorReturnsRegisteredControlAdaptorIfNoAliasIsFound = function()
    {
        ControlAdaptorFactory.registerConfiguredControlAdaptor('TestControlAdaptor',this.TestControlAdaptorClass);
        var myControlAdaptor = ControlAdaptorFactory.createControlAdaptor('TestControlAdaptor');
        assertInstanceOf(this.TestControlAdaptorClass,myControlAdaptor);
    };
})();
