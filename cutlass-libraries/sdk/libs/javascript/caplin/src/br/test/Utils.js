br.thirdparty("jquery");
br.thirdparty("knockout");

/**
 * @class
 * Utility class containing static methods that can be useful for tests.
 */
br.test.Utils = function()
{
};

br.test.Utils.pLoadedAndAttachedCSSElements = [];

/**
 * Fires a DOM Event in a cross Browser compatible way.
 *
 * @static
 * @param {DOM Element} eElement The DOM Element the Event is fired from
 * @param {String} sEvent The Event to be fired without "on", e.g. "click", "keydown"
 * @param {String} [sChar] an character associated with typing events
 */
br.test.Utils.fireDomEvent = function(eElement, sEvent, sChar)
{
	if(document.createEventObject)
	{
		var oEvent = jQuery.Event(sEvent);
		if (sChar)
		{
			oEvent.which = br.test.Utils.getKeyCodeForChar(sChar);
		}
		jQuery(eElement).trigger(oEvent);
	}
	else if (document.createEvent)
	{
		//FF, WEBKIT etc..
		var oEvt = document.createEvent("HTMLEvents");
		if(sChar != undefined)
		{
			oEvt.which = br.test.Utils.getKeyCodeForChar(sChar);
		}
		oEvt.initEvent(sEvent, true, true );
		return !eElement.dispatchEvent(oEvt);
		
	}
};

/**
 * Returns the Keycode of a letter.
 *
 * @static
 * @param {String} sChar a single Character to get the Keycode for
 * @returns {Number} keyCode The key code for the specified char.
 */
br.test.Utils.getKeyCodeForChar = function(sChar)
{
	if(sChar.toString().length !== 1){
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "getKeyCodeForChar Error! "+sChar+" should only be a single Character")
	};

	return sChar.charCodeAt(0);
};

/**
 * Fires a DOM KeyboardEvent in a cross Browser compatible way.
 *
 * @static
 * @param {DOM Element} eElement The DOM Element the Event is fired from
 * @param {String} sEvent The Event to be fired without "on", e.g. "keydown"
 * @param {String} sChar a character associated with typing events.
 * @param {Map} mValues a map of values, passed in to <code>initKeyboardEvent</code>, associated with typing events.
 */
br.test.Utils.fireKeyEvent = function(eElement, sEvent, sChar, mValues)
{
	var nKeyCode = br.test.Utils.getKeyCodeForChar(sChar);
	var mArgs = br.test.Utils._mergeDefaultKeyEventArgumentsWithArgumentsMap(mValues || {});

	if (document.createEvent) //FF, WEBKIT, IE9-IE9 mode etc..
	{
		var oEvt = document.createEvent("KeyboardEvent");
		
		if(oEvt.initKeyboardEvent)
		{
			if( navigator.userAgent.indexOf('WebKit') !== -1) //https://bugs.webkit.org/show_bug.cgi?id=16735 Due to bug in Webkit and Chrome we must use keyIdentifier instead of keycode - which is the standard anyway.
			{
				oEvt.initKeyboardEvent(sEvent, mArgs.canBubble, mArgs.cancelable, mArgs.view, mArgs.keyIdentifier, 0, mArgs.ctrlKey, mArgs.altKey, mArgs.shiftKey, mArgs.metaKey); //Webkit.
			}
			else
			{
				oEvt.initKeyboardEvent(sEvent, mArgs.canBubble, mArgs.cancelable, mArgs.view, mArgs.key, 0, mArgs.modifiersListArg, 0, "en-US"); //IE.
			}
		}
		else if(oEvt.initKeyEvent)
		{
			oEvt.initKeyEvent(sEvent, mArgs.canBubble, mArgs.cancelable, mArgs.view, mArgs.ctrlKey, mArgs.altKey, mArgs.shiftKey, mArgs.metaKey, nKeyCode, 0); //Gecko.
		}
		
		return !eElement.dispatchEvent(oEvt);
	}
	else if(document.createEventObject) //IE earlier then IE9-IE9 mode
	{
		var oEvt = document.createEventObject();
		
		oEvt.keyCode = nKeyCode;
		
		for (var sArg in mArgs)
		{
			if (oEvt[sArg] !== undefined)
			{
				oEvt[sArg] = mArgs[sArg];
			}
		}
		
		return eElement.fireEvent('on' + sEvent, oEvt);
	}
};

/**
 * Fires a DOM MouseEvents in a cross Browser compatible way.
 *
 * @static
 * @param {DOM Element} eElement The DOM Element the Event is fired from
 * @param {String} sEvent The Event to be fired without "on", e.g. "click"
 * @param {Map} mValues a map of values, passed in to <code>initMouseEvent</code>, associated with mouse events.
 */
br.test.Utils.fireMouseEvent = function(eElement, sEvent, mOptions)
{
	var mArguments = br.test.Utils.fireMouseEvent._mergeDefaultMouseEventArgumentsWithArgumentsMap(mOptions || {});
	
	if(document.createEvent)
	{
		var evt = br.test.Utils.fireMouseEvent._getMouseEventDOMStandard(sEvent, mArguments.canBubble, mArguments.cancelable, mArguments.view, mArguments.detail, mArguments.screenX, mArguments.screenY,
			mArguments.clientX, mArguments.clientY, mArguments.ctrlKey, mArguments.altKey, mArguments.shiftKey, mArguments.metaKey, mArguments.button, mArguments.relatedTarget);
		eElement.dispatchEvent(evt);
	}
	else if (eElement.fireEvent) 
	{
		if(br.test.Utils._isClickEventWithNoEventOptionsAndKOIsAvailable(sEvent, eElement, mOptions))
		{
			/*
			 * The reason for this KO call is due to jQuery not behaving like a browser and only setting a checked value
			 * after calling the event handlers. This code should only run in IE8 (eElement.fireEvent check above).
			 * 
			 * There is a comment in the KO code which explains why this is required.
			 */
			ko.utils.triggerEvent(eElement, sEvent);
		}
		else if(br.test.Utils._isClickEventWithNoEventOptions(sEvent, eElement, mOptions)) {
			jQuery(eElement).click();
		} else {
			evt = br.test.Utils.fireMouseEvent._getMouseEventIENonStandard(mArguments.canBubble, mArguments.cancelable, mArguments.view, mArguments.detail, mArguments.screenX, mArguments.screenY,
					mArguments.clientX, mArguments.clientY, mArguments.ctrlKey, mArguments.altKey, mArguments.shiftKey, mArguments.metaKey, mArguments.button, mArguments.relatedTarget);
			eElement.fireEvent("on" + sEvent, evt);
		}
	}
};

/**
 * Attaches CSS files to the test page. Cleaning them up is done via the <code>removeLoadedAndAttachedCSSFromPage</code>
 * method.
 *
 * @static
 * @param {Array} pCSSFiles list of css file URLs to be loaded into the test page.
 */
br.test.Utils.loadCSSAndAttachToPage = function(pCSSFiles) {
	var FileUtility = require('br/core/File');
	
	for(var nCSSFile = 0; nCSSFile < pCSSFiles.length; nCSSFile++) {
		var sCSSFile = pCSSFiles[nCSSFile];
		var cssCode = FileUtility.readFileSync(sCSSFile);
		
		var styleElement = document.createElement("style");
		styleElement.type = "text/css";
		
		try
		{
			styleElement.appendChild(document.createTextNode(cssCode));
		}
		catch (e)
		{
			//IE workaround
			if (styleElement.styleSheet.cssText)
			{
				styleElement.styleSheet.cssText += cssCode;
			}
			else
			{
				styleElement.styleSheet.cssText = cssCode;
			}
		}
		document.getElementsByTagName("head")[0].appendChild(styleElement);
		
		
		br.test.Utils.pLoadedAndAttachedCSSElements.push(styleElement);
	}
};

/**
 * Attaches CSS files to the test page. Cleaning them up is done via the <code>removeLoadedAndAttachedCSSFromPage</code>
 * method.
 *
 * @static
 * @param {Array} pCSSFiles list of css file URLs to be loaded into the test page.
 */
br.test.Utils.removeLoadedAndAttachedCSSFromPage = function()
{
	for(var nCSSElement = 0, nCSSElements = br.test.Utils.pLoadedAndAttachedCSSElements.length; nCSSElement < nCSSElements; nCSSElement++)
	{
		var eCSSElement = br.test.Utils.pLoadedAndAttachedCSSElements[nCSSElement];
		eCSSElement.parentNode.removeChild(eCSSElement);
	}
	br.test.Utils.pLoadedAndAttachedCSSElements = [];
};

/**
 * Fires a DOM scroll event in a cross Browser compatible way.
 *
 * @static
 * @param {DOM Element} eElement The DOM Element the Event is fired from
 */
br.test.Utils.fireScrollEvent = function(eElement)
{
	if (document.createEvent)
	{
		// FF
		var e = document.createEvent("HTMLEvents");
		e.initEvent("scroll", true, true);
		eElement.dispatchEvent(e);
	}
	else if (document.createEventObject)
	{
		// IE
		eElement.fireEvent("onscroll");
	}
};

/**
 * @private
 */
br.test.Utils.fireMouseEvent._mergeDefaultMouseEventArgumentsWithArgumentsMap = function(mOptions)
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

/**
 * @private
 */
br.test.Utils.fireMouseEvent._getMouseEventDOMStandard = function(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget)
{
	var evt = document.createEvent('MouseEvents');
	evt.initMouseEvent(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget);
	return evt;
};

/**
 * @private
 */
br.test.Utils.fireMouseEvent._getMouseEventIENonStandard = function(canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget)
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

/**
 * @private
 */
br.test.Utils._mergeDefaultKeyEventArgumentsWithArgumentsMap = function(mOptions)
{
	return {
		canBubble : mOptions.canBubble !== false ? true : false,
		cancelable : mOptions.cancelable !== false ? true : false,
		view : mOptions.view ? mOptions.view : window,
		keyIdentifier : mOptions.keyIdentifier ? mOptions.keyIdentifier : "Undefined",
		modifiersListArg : mOptions.modifiersListArg ? mOptions.modifiersListArg : "",
		key : mOptions.key ? mOptions.key : "Undefined",
		ctrlKey : mOptions.ctrlKey ? mOptions.ctrlKey : false,
		altKey : mOptions.altKey ? mOptions.altKey : false,
		shiftKey : mOptions.shiftKey ? mOptions.shiftKey : false,
		metaKey : mOptions.metaKey ? mOptions.metaKey : false
	};
};

/**
 * @private
 */
br.test.Utils._isClickEventWithNoEventOptions = function(sEvent, eElement, mOptions) {
	var Utility = require('br/core/Utility');

	return sEvent == "click" && eElement.click && Utility.isEmpty(mOptions);
};

/**
 * @private
 */
br.test.Utils._isClickEventWithNoEventOptionsAndKOIsAvailable = function(sEvent, eElement, mOptions)
{
	return br.test.Utils._isClickEventWithNoEventOptions(sEvent, eElement, mOptions) && ko && ko.utils;
};
