(function() {

	var sTestCaseName = "PropertyHelperTest";
	var oTestCase = {

		testAttachesChangeListeners: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesStringMethodName('handler'), thatWillNotBeCalledImmediately()).toNewProperty();
			thenThe(firstHandler()).was(notCalled);

			whenThe(firstProperty()).hasValue("new value");
			thenThe(firstHandler()).was(calledOnce);
			thenThe(firstHandler()).was(calledOn(firstHandler()));
		},

		testAttachesChangeListenersThatCanBeCalledImmediately: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesStringMethodName('handler'), thatWillBeCalledImmediately() ).toNewProperty();
			thenThe(firstHandler()).was(calledOnce);

			whenThe(firstProperty()).hasValue("new value");
			thenThe(firstHandler()).was(calledTwice);
		},

		testAttachesListenersAsFunctionReferences: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();
			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesStringMethodName('handler'), thatWillBeCalledImmediately() ).toNewProperty();

			thenThe(firstHandler()).was(notCalled);
			thenThe(secondHandler()).was(calledOnce);

			whenThe(firstProperty()).hasValue("new value");

			thenThe(firstHandler()).was(calledOnce);
			thenThe(secondHandler()).was(calledOnce);

			whenThe(secondProperty()).hasValue("another value");

			thenThe(firstHandler()).was(calledOnce);
			thenThe(secondHandler()).was(calledTwice);
		},

		testMultipleListenersAttachedToSameProperty: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();
			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).to(firstProperty());

			thenThe(firstHandler()).was(notCalled);
			thenThe(secondHandler()).was(notCalled);

			whenThe(firstProperty()).hasValue("next value");

			thenThe(firstHandler()).was(calledOnce);
			thenThe(secondHandler()).was(calledOnce);
		},

		testClearingListenersOfOneProperty: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();
			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();
			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).to(firstProperty());

			whenThe(firstProperty()).hasValue("next value");
			thenThe(firstHandler()).was(calledOnce);
				andThe(thirdHandler()).was(calledOnce);

			whenThePropertyHelperRemoves('all').eventsFromThe(firstProperty());
				andThe(firstProperty()).hasValue("another value");

			thenThe(firstHandler()).was(calledOnce);
				andThe(thirdHandler()).was(calledOnce);

			whenThe(secondProperty()).hasValue("next value");
			thenThe(secondHandler()).was(calledOnce);
		},

		testClearingNamedListenersOfOneProperty: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), 			thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();
			whenThePropertyHelperAttaches( aValidationSuccessHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).to(firstProperty());
			whenThePropertyHelperAttaches( aValidationSuccessHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).to(firstProperty());

			whenThe(firstProperty()).hasValue("next value");
			thenThe(propertyEventHandlers).wereAll(calledOnce);

			whenThePropertyHelperRemoves('validation').eventsFromThe(firstProperty());
				andThe(firstProperty()).hasValue("another value");

			thenThe(firstHandler()).was(calledTwice);
				andThe(secondHandler).was(calledOnce);
				andThe(thirdHandler).was(calledOnce);
		},

		testClearingAllListeners: function() {
			givenPropertyHelper();

			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();
			whenThePropertyHelperAttaches( aChangeHandler(), thatUsesAnAnonymousFunction(), thatWillNotBeCalledImmediately() ).toNewProperty();

			whenThePropertyHelperRemoves('all').events();
				andThe(firstProperty()).hasValue("another value");
				andThe(secondProperty()).hasValue("next value");

			thenThe(firstHandler()).was(notCalled);
				andThe(secondHandler()).was(notCalled);
		},

		setUp: function() {
			propertyEventHandlers = [];
			properties = [];
		},

		tearDown: function() {
			propertyHelper = properties = null;
		}
	};

	var propertyHelper, propertyEventHandlers, properties;

	function givenPropertyHelper() {
		return propertyHelper = new br.presenter.property.PropertyHelper;
	}

	function whenThePropertyHelperAttaches(attachFunction, handler, callImmediately) {
		var attach = function(property) {
			attachFunction.call(propertyHelper,
				properties[properties.length] = property,
				handler[0],
				handler[1],
				callImmediately
			);

			propertyEventHandlers.push(handler[0]);
		};

		return {
			toNewProperty: function() { attach(new br.presenter.property.EditableProperty("")); },
			to: function(property) { attach(property); }
		}
	}

	function whenThePropertyHelperRemoves(eventType) {
		var mapping = {all: propertyHelper.clearProperty, 'validation': propertyHelper.removeValidationSuccessListeners, change: propertyHelper.removeChangeListeners};
		return {
			eventsFromThe: function(property) {
				mapping[eventType].call(propertyHelper, property);
			},
			events: function() {
				propertyHelper.removeAllListeners();
			}
		}
	}

	function aChangeHandler() {
		return propertyHelper.addChangeListener;
	}

	function aValidationSuccessHandler() {
		return propertyHelper.addValidationSuccessListener;
	}

	function thatUsesStringMethodName(methodName) {
		var scope = {callCount: 0};
		scope[methodName] = function() {
			scope.callCount++;
			scope.calledOn = this;
		};
		return [scope, methodName];
	}

	function thatUsesAnAnonymousFunction() {
		var scope = {callCount: 0};
		return [scope, function() {
			scope.callCount++;
			scope.calledOn = this;
		}];
	}

	function thatWillNotBeCalledImmediately() { return false; }
	function thatWillBeCalledImmediately() { return true; }

	function calledTimes(expected) {
		return function(actual) {
			return actual.callCount === expected;
		}
	}
	var notCalled = calledTimes(0), calledOnce = calledTimes(1), calledTwice = calledTimes(2);

	function propertyGetter(index) {
		return function() {
			return properties[index];
		}
	}
	var firstProperty = propertyGetter(0), secondProperty = propertyGetter(1), thirdProperty = propertyGetter(2);

	function handlerGetter(index) {
		return function() {
			return propertyEventHandlers[index];
		}
	}
	var firstHandler = handlerGetter(0), secondHandler = handlerGetter(1), thirdHandler = handlerGetter(2);

	function calledOn(expected) {
		return function(actual) {
			return expected === actual;
		}
	}

	function whenThe(getter) {
		if(typeof getter === "function") {
			getter = getter();
		}

		var bdd =  {
			and: function() { return bdd; },
			hasValue: function(value) {getter.setValue(value); return bdd; },
			wereAll: function(test) {
				for(var tests = [].concat(getter), i = 0, l = tests.length; i < l; i++) {
					thenThe(tests[i]).was(test);
				}
				return bdd;
			},
			was: function(test) {
				if(typeof test === "function")
					assert(test(getter));
				else
					assertEquals(getter, test);
				return bdd; },
			wasCalled: function(expected) { if(typeof expected === "function") expected = expected(); assertEquals("Call count must be verified", expected, getter.callCount); return bdd; }
		};
		return bdd;
	}
	var andThe = whenThe, thenThe = whenThe;

	TestCase(sTestCaseName, oTestCase);

}());