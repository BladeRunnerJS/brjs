// this class is here since it seems to be break KO binding tests in some browsers if it's a commonJS module

/* global br, require, presenter_knockout, jQuery */
br.Core.thirdparty('jquery');
br.Core.thirdparty('presenter-knockout');

/**
* @class
* Utility class containing static methods that can be useful for tests.
*/
var nsUtils = function() {};

nsUtils.pLoadedAndAttachedCSSElements = [];

/**
* Fires a DOM Event in a cross Browser compatible way.
*
* @static
* @param {DOMElement} element The DOM Element the Event is fired from
* @param {String} eventString The Event to be fired without 'on', e.g. 'click', 'keydown'
* @param {String} [character] A character associated with typing events
*/
nsUtils.fireDomEvent = function(element, eventString, character) {
	var evt;
	if (document.createEventObject) {
		evt = jQuery.Event(eventString);
		if (character) {
			evt.which = nsUtils.getKeyCodeForChar(character);
		}
		jQuery(element).trigger(evt);
	} else if (document.createEvent) {
		//FF, WEBKIT etc..
		evt = document.createEvent('HTMLEvents');
		if (typeof character !== 'undefined') {
			evt.which = nsUtils.getKeyCodeForChar(character);
		}
		evt.initEvent(eventString, true, true);
		return !element.dispatchEvent(evt);
	}
};

/**
* Returns the Keycode of a letter.
*
* @static
* @param {String} character a single Character to get the Keycode for
* @returns {Number} keyCode The key code for the specified char.
*/
nsUtils.getKeyCodeForChar = function(character) {
	if (character.toString().length !== 1){
		throw br.Errors.CustomError(
			br.Errors.INVALID_TEST,
			'getKeyCodeForChar Error! ' + character + ' should only be a single Character'
		);
	}

	return character.charCodeAt(0);
};

/**
* Fires a DOM KeyboardEvent in a cross Browser compatible way.
*
* @static
* @param {DOMElement} element The DOM Element the Event is fired from
* @param {String} eventString The Event to be fired without 'on', e.g. 'keydown'
* @param {String} character a character associated with typing events.
* @param {Map} options a map of values, passed in to <code>initKeyboardEvent</code>, associated with typing events.
*/
nsUtils.fireKeyEvent = function(element, eventString, character, options) {
	var keyCode = nsUtils.getKeyCodeForChar(character),
		args = nsUtils._mergeDefaultKeyEventArgumentsWithArgumentsMap(options || {}),
		evt;

	if (document.createEvent) {
		//FF, WEBKIT, IE9-IE9 mode etc..
		evt = document.createEvent('KeyboardEvent');

		if (evt.initKeyboardEvent) {
			if (navigator.userAgent.indexOf('WebKit') !== -1) {
				//https://bugs.webkit.org/show_bug.cgi?id=16735 Due to bug in Webkit and Chrome we must use keyIdentifier instead of keycode - which is the standard anyway.
				//Webkit.
				evt.initKeyboardEvent(eventString, args.canBubble, args.cancelable, args.view, args.keyIdentifier, 0, args.ctrlKey, args.altKey, args.shiftKey, args.metaKey);
			} else {
				//IE.
				evt.initKeyboardEvent(eventString, args.canBubble, args.cancelable, args.view, args.key, 0, args.modifiersListArg, 0, 'en-US');
			}
		} else if (evt.initKeyEvent) {
			//Gecko.
			evt.initKeyEvent(eventString, args.canBubble, args.cancelable, args.view, args.ctrlKey, args.altKey, args.shiftKey, args.metaKey, keyCode, 0);
		}

		return !element.dispatchEvent(evt);
	} else if (document.createEventObject) {
		//IE earlier then IE9-IE9 mode
		evt = document.createEventObject();

		evt.keyCode = keyCode;

		for (var arg in args) {
			if (typeof evt[arg] !== 'undefined') {
				evt[arg] = args[arg];
			}
		}

		return element.fireEvent('on' + eventString, evt);
	}
};

/**
* Fires a DOM MouseEvents in a cross Browser compatible way.
*
* @static
* @param {DOMElement} element The DOM Element the Event is fired from
* @param {String} eventString The Event to be fired without 'on', e.g. 'click'
* @param {Map} options a map of values, passed in to <code>initMouseEvent</code>, associated with mouse events.
*/
nsUtils.fireMouseEvent = function(element, eventString, options) {
	var args = nsUtils.fireMouseEvent._mergeDefaultMouseEventArgumentsWithArgumentsMap(options || {}),
		evt;

	if (document.createEvent) {
		evt = nsUtils.fireMouseEvent._getMouseEventDOMStandard(eventString, args.canBubble, args.cancelable, args.view, args.detail, args.screenX, args.screenY,
			args.clientX, args.clientY, args.ctrlKey, args.altKey, args.shiftKey, args.metaKey, args.button, args.relatedTarget);
		element.dispatchEvent(evt);

	} else if (element.fireEvent) {

		if (nsUtils._isClickEventWithNoEventOptionsAndKOIsAvailable(eventString, element, options)) {
			/*
			* The reason for this KO call is due to jQuery not behaving like a browser and only setting a checked value
			* after calling the event handlers. This code should only run in IE8 (element.fireEvent check above).
			*
			* There is a comment in the KO code which explains why this is required.
			*/
			presenter_knockout.utils.triggerEvent(element, eventString);
		} else if (nsUtils._isClickEventWithNoEventOptions(eventString, element, options)) {
			jQuery(element).click();
		} else {
			evt = nsUtils.fireMouseEvent._getMouseEventIENonStandard(args.canBubble, args.cancelable, args.view, args.detail, args.screenX, args.screenY,
					args.clientX, args.clientY, args.ctrlKey, args.altKey, args.shiftKey, args.metaKey, args.button, args.relatedTarget);
			element.fireEvent('on' + eventString, evt);
		}
	}
};

/**
* Attaches CSS files to the test page. Cleaning them up is done via the <code>removeLoadedAndAttachedCSSFromPage</code>
* method.
*
* @static
* @param {Array} cssFiels list of css file URLs to be loaded into the test page.
*/
nsUtils.loadCSSAndAttachToPage = function(cssFiles) {
	var FileUtility = require('br/core/File'),
		elHead = document.getElementsByTagName('head')[0],
		cssCode, cssEl;
	for (var i = 0, len = cssFiles.length; i < len; ++i) {
		cssCode = FileUtility.readFileSync(cssFiles[i]);
		cssEl = document.createElement('style');

		cssEl.type = 'text/css';

		try {
			cssEl.appendChild(document.createTextNode(cssCode));
		} catch (e) {
			//IE workaround
			if (typeof cssEl.styleSheet.cssText !== 'undefined') {
				cssEl.styleSheet.cssText += cssCode;
			} else {
				cssEl.styleSheet.cssText = cssCode;
			}
		}

		nsUtils.pLoadedAndAttachedCSSElements.push(elHead.appendChild(cssEl));
	}
};

/**
* Attaches CSS files to the test page. Cleaning them up is done via the <code>removeLoadedAndAttachedCSSFromPage</code>
* method.
*
* @static
* @param {Array} cssFiles list of css file URLs to be loaded into the test page.
*/
nsUtils.removeLoadedAndAttachedCSSFromPage = function() {
	var cssElements = nsUtils.pLoadedAndAttachedCSSElements,
		cssEl;

	while (cssElements.length) {
		cssEl = cssElements.shift();
		cssEl.parentNode.removeChild(cssEl);
	}
};

/**
* Fires a DOM scroll event in a cross Browser compatible way.
*
* @static
* @param {DOMElement} element The DOM Element the Event is fired from
*/
nsUtils.fireScrollEvent = function(element) {
	if (document.createEvent) {
		// FF
		var evt = document.createEvent('HTMLEvents');
		evt.initEvent('scroll', true, true);
		element.dispatchEvent(evt);
	} else if (document.createEventObject) {
		// IE
		element.fireEvent('onscroll');
	}
};

/** @private */
nsUtils.fireMouseEvent._mergeDefaultMouseEventArgumentsWithArgumentsMap = function(config) {
	return {
		canBubble : config.canBubble !== false ? true : false,
		cancelable : config.cancelable !== false ? true : false,
		view : config.view ? config.view : window,
		detail : config.detail ? config.detail : 0,
		screenX : config.screenX ? config.screenX : 0,
		screenY : config.screenY ? config.screenY : 0,
		clientX : config.clientX ? config.clientX : 0,
		clientY : config.clientY ? config.clientY : 0,
		ctrlKey : config.ctrlKey ? config.ctrlKey : false,
		altKey : config.altKey ? config.altKey : false,
		shiftKey : config.shiftKey ? config.shiftKey : false,
		metaKey : config.metaKey ? config.metaKey : false,
		button : config.button ? config.button : 0,
		relatedTarget : config.relatedTarget ? config.relatedTarget : null
	};
};

/** @private */
nsUtils.fireMouseEvent._getMouseEventDOMStandard = function(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget) {
	var evt = document.createEvent('MouseEvents');
	evt.initMouseEvent(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget);
	return evt;
};

/** @private */
nsUtils.fireMouseEvent._getMouseEventIENonStandard = function(canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget) {
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
	switch(button) {
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
	* Have to use relatedTarget because IE won't allow assignment to toElement or fromElement on generic events. This
	*  keeps YAHOO.util.customEvent.getRelatedTarget() functional.
	*/
	customEvent.relatedTarget = relatedTarget;
	return customEvent;
};

/** @private */
nsUtils._mergeDefaultKeyEventArgumentsWithArgumentsMap = function(config) {
	return {
		canBubble : config.canBubble !== false ? true : false,
		cancelable : config.cancelable !== false ? true : false,
		view : config.view ? config.view : window,
		keyIdentifier : config.keyIdentifier ? config.keyIdentifier : 'undefined',
		modifiersListArg : config.modifiersListArg ? config.modifiersListArg : '',
		key : config.key ? config.key : 'undefined',
		ctrlKey : config.ctrlKey ? config.ctrlKey : false,
		altKey : config.altKey ? config.altKey : false,
		shiftKey : config.shiftKey ? config.shiftKey : false,
		metaKey : config.metaKey ? config.metaKey : false
	};
};

/** @private */
nsUtils._isClickEventWithNoEventOptions = function(eventString, element, options) {
	var Utility = require('br/core/Utility');
	return eventString === 'click' && element.click && Utility.isEmpty(options);
};

/** @private */
nsUtils._isClickEventWithNoEventOptionsAndKOIsAvailable = function(eventString, element, options) {
	return nsUtils._isClickEventWithNoEventOptions(eventString, element, options) && presenter_knockout && presenter_knockout.utils;
};

br.test.ns.Utils = nsUtils;
