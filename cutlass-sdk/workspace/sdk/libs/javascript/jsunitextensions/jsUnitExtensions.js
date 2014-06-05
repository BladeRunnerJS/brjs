/**
 * This file extends new functionality to the core jsUnit code
 */

function assertFails(sMsg, fFunc, oExpectedFailure)
{
//	if (!_isMessageSupplied(arguments, 3)) return _reinvokeWithMessage(arguments);
	
    var oException = null;
    try
    {
        fFunc();
    }
    catch (e)
    {
        oException = e;
    }
    if (oExpectedFailure !== undefined)
    {
        if (oException === null)
        {
            fail("Expected an Exception but none was thrown (" + sMsg + ")");
        }
        else if (oExpectedFailure.equals !== undefined)
        {
            assertTrue("The failure was not the expected one (" + sMsg + ")\nEXPECTED:\n" + oExpectedFailure.toString() + "\nRECEIVED:\n" + oException.toString(), oExpectedFailure.equals( oException ));
        }
        else
        {
            assertEquals("The failure was not the right one (" + sMsg + ")", oExpectedFailure.toString(), oException.toString());
        }
    }
    else
    {
        assertNotNull(sMsg, oException);
    }
};

function assertAssertError(sMsg, fFunction)
{
	if (arguments.length == 1)
	{
		fFunction = sMsg;
		sMsg = "";
	}
	else
	{
		sMsg += " ";
	}

	try
	{
		fFunction();
	}
	catch (e)
	{
		if (e.name !== "AssertError")
		{
			fail(sMsg + "expected to throw AssertError but threw " + e.name);
		}
		return true;
	}
	fail(sMsg + "expected to throw AssertError");
}

function assertNoException(sMsg, fCode)
{   
	if (!_isMessageSupplied(arguments, 2)) return _reinvokeWithMessage(arguments);
	
    try
    {
        fCode();       
    }
    catch(e)
    {
        fail(sMsg + ": An exception was thrown");
    }
};

function assertInArray(sMsg, oObj, pArr)
{
	if (!_isMessageSupplied(arguments, 3)) return _reinvokeWithMessage(arguments);
	
    for (var i=0, l=pArr.length; i<l; ++i)
    {
        if (oObj === pArr[i])
        {
            return;
        }
    }
    fail(sMsg);
}

function assertNotInArray(sMsg, oObj, pArr)
{
	if (!_isMessageSupplied(arguments, 3)) return _reinvokeWithMessage(arguments);
	
    for (var i=0, l=pArr.length; i<l; ++i)
    {
        if (oObj === pArr[i])
        {
            fail(sMsg);
        }
    }
}

function assertMapEquals(sMsg, mExpectedMap, mActualMap, pSeenObjects)
{
	// NB: the normal way to invoke this function is with 3 arguments (i.e. without pSeenObjects)
	if (!_isMessageSupplied(arguments, 3)) return _reinvokeWithMessage(arguments);

	pSeenObjects = pSeenObjects || [];
	
    var vExpectedValue, vActualValue;

    for(var sKey in mExpectedMap)
    {
        vExpectedValue = mExpectedMap[sKey];
        vActualValue = mActualMap[sKey];

        assertVariantEquals(sMsg, vExpectedValue, vActualValue, pSeenObjects);
    }

    for(var sKey in mActualMap)
    {
        vActualValue = mActualMap[sKey];
        vExpectedValue = mExpectedMap[sKey];

        if((vActualValue !== undefined) && (vExpectedValue === undefined))
        {
            fail(sMsg + ": actual map contains a value " + sKey + "=" + vExpectedValue + " that does not exist within the expected map.");
        }
    }
}

function assertArrayEquals(sMsg, pExpectedArray, pActualArray)
{
	if (!_isMessageSupplied(arguments, 3)) return _reinvokeWithMessage(arguments);
	
    if(pExpectedArray.length != pActualArray.length)
    {
        fail(sMsg + ": expected array has " + pExpectedArray.length + " items but actual array has " + pActualArray.length +
            " items (actual array was [" + pActualArray.join(", ") + "]).");
    }

    for(var i = 0, l = pExpectedArray.length; i < l; ++i)
    {
        var vExpectedValue = pExpectedArray[i];
        var vActualValue = pActualArray[i];

        assertVariantEquals(sMsg, vExpectedValue, vActualValue);
    }
}

function assertVariantEquals(sMsg, vExpectedValue, vActualValue, pSeenObjects)
{
	// NB: the normal way to invoke this function is with 3 arguments (i.e. without pSeenObjects)
	if (!_isMessageSupplied(arguments, 3)) return _reinvokeWithMessage(arguments);

	pSeenObjects = pSeenObjects || [];
	
    if(vExpectedValue === undefined)
    {
        // undefined
        assertUndefined(sMsg, vActualValue);
    }
    else if(vExpectedValue === null)
    {
        assertNull(sMsg, vActualValue);
    }
    else if(vExpectedValue instanceof Array)
    {
        // array
        assertArrayEquals(sMsg, vExpectedValue, vActualValue);
    }
    else if(vExpectedValue.constructor == Object.prototype.constructor)
    {
        // map
        storeSeenObject(sMsg, pSeenObjects, vActualValue);
        assertMapEquals(sMsg, vExpectedValue, vActualValue, pSeenObjects);
    }
    else if((vExpectedValue instanceof RegExp) || (vExpectedValue instanceof Date))
    {
        // regexp or date
        assertEquals(sMsg, vExpectedValue.toString(), vActualValue.toString());
    }
    else if(vExpectedValue instanceof Object)
    {
        if(vExpectedValue === vActualValue)
        {
            // these objects are one and the same -- there is nothing to check
        }
        else
        {
            // these objects are not the same actual object, but may have the same contents -- keep digging
            storeSeenObject(sMsg, pSeenObjects, vActualValue);
            assertMapEquals(sMsg, vExpectedValue, vActualValue, pSeenObjects);
        }
    }
    else
    {
        // value
        assertEquals(sMsg, vExpectedValue, vActualValue);
    }
}

function storeSeenObject(sMsg, pSeenObjects, oNewObject)
{
    assertNotInArray(sMsg + ": Circular reference", oNewObject, pSeenObjects);
    pSeenObjects.push(oNewObject);
}

function getResourceUrl(sRelativeUrl)
{
    return window.location.href.replace(/jsunit\/HTMLWrapperPage\.html.*$/, "") + sRelativeUrl;
}

function mergeDefaultMouseEventArgumentsWithArgumentsMap(mOptions)
{
	return {
		canBubble : mOptions.canBubble !== false ? true : false,
		cancelable : mOptions.cancelable !== false ? true : false,
		view : mOptions.view ? mOptions.view : window,
		detail : mOptions.detail ? mOptions.detail : 0,
		screenX : mOptions.screenX ? mOptions.screenX : 0,
		screenY : mOptions.screenY ? mOptions.screenY : 0,
		clientX : mOptions.clientX ? mOptions.clientX : 0,
		clientY : mOptions.clientY ? mOptions.clientY : 0,
		ctrlKey : mOptions.ctrlKey ? mOptions.ctrlKey : false,
		altKey : mOptions.altKey ? mOptions.altKey : false,
		shiftKey : mOptions.shiftKey ? mOptions.shiftKey : false,
		metaKey : mOptions.metaKey ? mOptions.metaKey : false,
		button : mOptions.button ? mOptions.button : 0,
		relatedTarget : mOptions.relatedTarget ? mOptions.relatedTarget : null
	};
};

  function triggerKeyEvent(element, eventName, props) {
    var options = {
      keyCode:0, charCode:0,
      ctrlKey: false, altKey: false, shiftKey: false, metaKey:false
    };
    if (props)
      for(var n in props) options[n] = props[n];
 
 
    if (document.createEvent && element.dispatchEvent) {
		// DOM support
      if (window.KeyEvent) {
        var e = document.createEvent("KeyEvents");
        e.initKeyEvent(eventName,true,true,document.defaultView,
          options.ctrlKey, options.altKey, options.shiftKey, options.metaKey, options.keyCode, options.charCode );
      } else {
	  	try {
			var e = document.createEvent("Events");
		}
		catch(ex) {
			// Older DOM browsers
			var e = document.createEvent("UIEvents");
		}
		e.initEvent(eventName, true, true);
		e.detail = 1;
		e.view = document.defaultView;
        e.ctrlKey = options.ctrlKey;
        e.altKey = options.altKey;
        e.shiftKey = options.shiftKey;
        e.metaKey = options.metaKey;
        e.keyCode = options.keyCode;
        e.which = options.keyCode;
      }
      element.dispatchEvent(e);
    }
    else if (document.createEventObject) {
		// IE
      var e = document.createEventObject();
      //e.type = eventName;
      //for(var n in options) e[n] = options[n];
      e.ctrlKey = options.ctrlKey;
      e.altKey = options.altKey;
      e.shiftKey = options.shiftKey;
      e.metaKey = options.metaKey;
      e.keyCode = options.keyCode;
      element.fireEvent("on"+eventName,e);
    }
    else throw new ExampleException("No support for synthetic events");
  }
  
/* Fire a mouse event in a browser-compatible manner */
function triggerMouseEvent(element, type, mOptions) 
{
	var mArguments = mergeDefaultMouseEventArgumentsWithArgumentsMap(mOptions || {});
	if(document.createEvent)
	{
		var evt = getMouseEventDOMStandard(type, mArguments.canBubble, mArguments.cancelable, mArguments.view, mArguments.detail, mArguments.screenX, mArguments.screenY, 
			mArguments.clientX, mArguments.clientY, mArguments.ctrlKey, mArguments.altKey, mArguments.shiftKey, mArguments.metaKey, mArguments.button, mArguments.relatedTarget);
		element.dispatchEvent(evt);
	}
	else if (element.fireEvent) 
    {
		evt = getMouseEventIENonStandard(mArguments.canBubble, mArguments.cancelable, mArguments.view, mArguments.detail, mArguments.screenX, mArguments.screenY, 
			mArguments.clientX, mArguments.clientY, mArguments.ctrlKey, mArguments.altKey, mArguments.shiftKey, mArguments.metaKey, mArguments.button, mArguments.relatedTarget);
		element.fireEvent("on" + type, evt); 
    }
};

function getMouseEventDOMStandard(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget) 
{
	var evt = document.createEvent('MouseEvents');
	evt.initMouseEvent(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget);
	return evt;
};

function getMouseEventIENonStandard(canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget) 
{
	//create an IE event object
	var customEvent = document.createEventObject();
	
	//assign available properties
	customEvent.bubbles = canBubble;
	customEvent.cancelable = cancelable;
	customEvent.view = view;
	customEvent.detail = detail;
	customEvent.screenX = screenX;
	customEvent.screenY = screenY;
	customEvent.clientX = clientX;
	customEvent.clientY = clientY;
	customEvent.ctrlKey = ctrlKey;
	customEvent.altKey = altKey;
	customEvent.metaKey = metaKey;
	customEvent.shiftKey = shiftKey;
	
	//fix button property for IE's wacky implementation
	switch(button){
	case 0:
		customEvent.button = 1;
		break;
	case 1:
		customEvent.button = 4;
		break;
	case 2:
		customEvent.button = 2;
		break;
	default:
		customEvent.button = 0;                    
	}    
	
	/*
	 * Have to use relatedTarget because IE won't allow assignment
	 * to toElement or fromElement on generic events. This keeps
	 * YAHOO.util.customEvent.getRelatedTarget() functional.
	 */
	customEvent.relatedTarget = relatedTarget;
	return customEvent;
};

//if (!_isMessageSupplied(arguments, 2)) return _reinvokeWithMessage(arguments);

_reinvokeWithMessage = function(oArguments) {
	var pArgumentsWithMessage = _prependBlankMessageToArgument(oArguments);
	return oArguments.callee.apply(this, pArgumentsWithMessage);
};

_isMessageSupplied = function(oArguments, nExpectedArgumentCount) {
	if (oArguments.length >= nExpectedArgumentCount) {
		var sMsg = oArguments[0];   // first argument is always the message
		if (typeof sMsg != "string") {
			caplin.core.Logger.log(caplin.core.LogLevel.ERROR, "assert incorrectly invoked: first argument needs to be a string message");
		}
		return true;
	}
	return false;
};

_prependBlankMessageToArgument = function(oArguments) {	
	var pArgumentsWithBlankMessage = [""];
	for (var i = 0; i < oArguments.length; ++i) {
		pArgumentsWithBlankMessage.push(oArguments[i]);
	}
	return pArgumentsWithBlankMessage;
};

var Clock = {
	    timeoutsMade: 0,
	    scheduledFunctions: {},
	    nowMillis: 0,
	    reset: function() {
	        this.scheduledFunctions = {};
	        this.nowMillis = 0;
	        this.timeoutsMade = 0;
	    },
	    tick: function(millis) {
	        var oldMillis = this.nowMillis;
	        var newMillis = oldMillis + millis;
	        this.runFunctionsWithinRange(oldMillis, newMillis);
	        this.nowMillis = newMillis;
	    },
	    runFunctionsWithinRange: function(oldMillis, nowMillis) {
	        var scheduledFunc;
	        var funcsToRun = [];
	        for (var timeoutKey in this.scheduledFunctions) {
	            scheduledFunc = this.scheduledFunctions[timeoutKey];
	            if (scheduledFunc != undefined &&
	                scheduledFunc.runAtMillis >= oldMillis &&
	                scheduledFunc.runAtMillis <= nowMillis) {
	                funcsToRun.push(scheduledFunc);
	                this.scheduledFunctions[timeoutKey] = undefined;
	            }
	        }

	        if (funcsToRun.length > 0) {
	            funcsToRun.sort(function(a, b) {
	                return a.runAtMillis - b.runAtMillis;
	            });
	            for (var i = 0; i < funcsToRun.length; ++i) {
	                try {
	                    this.nowMillis = funcsToRun[i].runAtMillis;
	                    funcsToRun[i].funcToCall();
	                    if (funcsToRun[i].recurring) {
	                        Clock.scheduleFunction(funcsToRun[i].timeoutKey,
	                                funcsToRun[i].funcToCall,
	                                funcsToRun[i].millis,
	                                true);
	                    }
	                } catch(e) {
	                }
	            }
	            this.runFunctionsWithinRange(oldMillis, nowMillis);
	        }
	    },
	    scheduleFunction: function(timeoutKey, funcToCall, millis, recurring) {
	        Clock.scheduledFunctions[timeoutKey] = {
	            runAtMillis: Clock.nowMillis + millis,
	            funcToCall: funcToCall,
	            recurring: recurring,
	            timeoutKey: timeoutKey,
	            millis: millis
	        };
	    }
};

window.setTimeout = function(funcToCall, millis) {
    Clock.timeoutsMade = Clock.timeoutsMade + 1;
    Clock.scheduleFunction(Clock.timeoutsMade, funcToCall, millis, false);
    return Clock.timeoutsMade;
};

window.setInterval = function(funcToCall, millis) {
    Clock.timeoutsMade = Clock.timeoutsMade + 1;
    Clock.scheduleFunction(Clock.timeoutsMade, funcToCall, millis, true);
    return Clock.timeoutsMade;
};

window.clearTimeout = function(timeoutKey) {
    Clock.scheduledFunctions[timeoutKey] = undefined;
};

window.clearInterval = function(timeoutKey) {
    Clock.scheduledFunctions[timeoutKey] = undefined;
};
