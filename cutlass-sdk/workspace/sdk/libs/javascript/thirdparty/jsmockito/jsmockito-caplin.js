JsMockito.Verifiers['NoMoreInteractions'].prototype.verifyInteractions = function(funcName, allInteractions, matchers, describeContext)
{
	var interactions = JsMockito.grep(allInteractions, function(interaction) {
		return interaction.verified != true;
	});

	if (interactions.length == 0) {
		return;
	}

	var description = this.buildDescription("No interactions wanted, but " + interactions.length + " remains", funcName, interactions, describeContext);

	throw description.get();
};

JsMockito.Verifiers['NoMoreInteractions'].prototype.buildDescription = function(message, funcName, interactions, describeContext)
{
	var description = new JsHamcrest.Description();
	description.append(message + ': ' + funcName + '(');

	JsMockito.each(interactions, function(interaction) {
		JsMockito.each(interaction.args.splice(1), function(arg, i) {
			if (i > 0)
				description.append(', ');
			description.append('<');
			description.append(arg);
			description.append('>');
		});
	});
	description.append(")");

	if (describeContext) {
		description.append(", 'this' being ");
		matchers[0].describeTo(description);
	}
	return description;
};


JsMockito.ArgumentCaptor = function() {
	var self = this;
	
	var matcher = new JsHamcrest.SimpleMatcher({
        matches: function(actual) {
        	self.value = actual;
            return true;
        },

        describeTo: function(description) {
            description.append('(captured)');
        }
    });
    
    this.capture = function() 
    {
    	return matcher;
    };
};

JsMockito.clear = function(fMockFunction)
{
	var func = fMockFunction._jsMockitoVerifier(function(funcName, interactions, matchers, isNotContextMatcher) {
		interactions.splice(0, interactions.length);
	});
	func.call();
};

var originalJsTestDriverIntegrationFunction = JsMockito.Integration.JsTestDriver
JsMockito.Integration.JsTestDriver = function()
{
	JsMockito._allMocks = [];
	originalJsTestDriverIntegrationFunction();
};

var originalMockFunction = JsMockito.mock;
JsMockito.mock = function(oObjectToMock)
{
	var mock = originalMockFunction(oObjectToMock);
	JsMockito._allMocks.push(mock);
	return mock;
};

JsMockito.clearAllMocks = function()
{
	JsMockito._allMocks.forEach(function(mock) {
		var mockFunctions = mock._jsMockitoMockFunctions();
		mockFunctions.forEach(function(mockFunction) {
			JsMockito.clear(mockFunction);
		});
	});
};

JsMockito._export.push("ArgumentCaptor","clear", "clearAllMocks");
