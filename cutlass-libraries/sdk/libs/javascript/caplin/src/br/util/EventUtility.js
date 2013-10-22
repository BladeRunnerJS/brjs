var Errors = require("br/Errors");

br.util.EventUtility = function()
{
};

/**
 * @private
 */
br.util.EventUtility.UNIQUE_ID = 1;

/**
 * @private
 */
br.util.EventUtility.m_mEvents = undefined;

/**
 * @private
 */
br.util.EventUtility.m_bUnloadListenerAdded = false;

/**
 * Registers the specified event function to a particular event using the provided DOM element.
 *
 * @param {Object} oTargetElem The document element the event will be registered against.
 * @param {String} sEvent The name of the event that will be registered (e.g. 'click', but not 'onclick').
 * @param {Function}  fEventListener The function that will be called when the event fires.
 * @param {boolean}  bDirectAttachedEvent Whether the simpler form of event registration (e.g. <code>elem.onclick</code>)
 *				   should be used (<code>false</code> by default). This is still occasionally useful in IE when you
 *				   want access to the target element and that is different to <code>window.event.srcElement</code>.
 *
 * @see #getEventHandlerFromMethod
 *
 * @returns An event listener id to be used in the method (@link #removeEventListenerById) to remove
 *		 the newly added event listener or NULL if the event listener could not be added for any reason.
 * @type int
 */
br.util.EventUtility.addEventListener = function(oTargetElem, sEvent, fEventListener, bDirectAttachedEvent, bUseCapture)
{
	br.util.EventUtility._initMap();
	
	if (sEvent.match(/^on/))
	{
		throw new Errors.IllegalStateError("events should be given without the on prefix");
	}
	var bSuccess = false;

	// set up the event
	if (bDirectAttachedEvent)
	{
		oTargetElem["on" + sEvent] = fEventListener;
		bSuccess = true;
	}
	else if (oTargetElem.addEventListener)
	{
		oTargetElem.addEventListener(sEvent, fEventListener, bUseCapture||false);
		bSuccess = true;
	}
	else
	{
		bSuccess = oTargetElem.attachEvent("on" + sEvent, fEventListener);
	}

	if (bSuccess)
	{
		// store a reference to the event for unload removal
		var nUniqueListenerId = br.util.EventUtility.UNIQUE_ID++;
		br.util.EventUtility.m_mEvents[nUniqueListenerId] = {"elem":oTargetElem, "event":sEvent, "listener":fEventListener, "isDirect":bDirectAttachedEvent};
		return nUniqueListenerId;
	}
	else
	{
		return null;
	}
};

/**
 * Removes the DOM event listener that has previously been added via the {@link #addEventListener} method.
 *
 * @param {int} nUniqueListenerId The event Listener Id that was returned by the method {@link #addEventListener}.
 */
br.util.EventUtility.removeEventListener = function(nUniqueListenerId)
{
	br.util.EventUtility._initMap();
	
	br.util.EventUtility._registerUnloadListener();
	var oEvent = br.util.EventUtility.m_mEvents[nUniqueListenerId];
	if (!oEvent)
	{
		return;
	}
	
	if (oEvent.isDirect)
	{
		oEvent.elem["on" + oEvent.event] = null;
	}
	else if (oEvent.elem.removeEventListener)
	{
		oEvent.elem.removeEventListener(oEvent.event, oEvent.listener, false);
	}
	else
	{
		oEvent.elem.detachEvent("on" + oEvent.event, oEvent.listener);
	}

	br.util.EventUtility.m_mEvents = br.util.MapFactory.removeItem(br.util.EventUtility.m_mEvents, nUniqueListenerId);
};

/**
 * Stops the propagation of an event. 
 * 
 * This method should be used within an event listener to prevent bubbling of an
 * event up to other event listeners.
 *
 * @param {Event} oEvent Event passed to your event handler. Note that event handlers are not wrapped
 *				so you need to do something like <code>oEvent = oEvent || window.event;</code>
 */
br.util.EventUtility.stopPropagation = function(oEvent)
{
	oEvent.cancelBubble = true;
	if (oEvent.stopPropagation)
	{
		oEvent.stopPropagation();
	}
};

/**
 * Allows the user to prevent the default event, with the use <code> return Utility.preventEventDefault(oEvent); </code>
 *
 * @param {Event} oEvent Event passed to your event handler. Note that event handlers are not wrapped
 *				so you need to do something like <code>oEvent = oEvent || window.event;</code>
 *				
 * @type boolean
 * @returns Always returns false
 */
br.util.EventUtility.preventDefault = function(oEvent)
{
	oEvent.returnValue = false;
	if (oEvent.preventDefault)
	{
		oEvent.preventDefault();
	}
	return false;
};

/**
 * @private
 */
br.util.EventUtility._initMap = function()
{
	if (br.util.EventUtility.m_mEvents === undefined)
	{
		br.util.EventUtility.m_mEvents = br.util.MapFactory.createMap();
	}
};

/**
 * @private
 */
br.util.EventUtility._registerUnloadListener = function()
{
	if (!br.util.EventUtility.m_bUnloadListenerAdded)
	{
		if (window.addEventListener)
		{
			window.addEventListener("unload", br.util.EventUtility._handlePageUnload, false);
		}
		else
		{
			window.attachEvent("onunload", br.util.EventUtility._handlePageUnload);
		}
		br.util.EventUtility.m_bUnloadListenerAdded = true;
	}
};

/**
 * Since event handlers are prone to cause memory leaks in IE, we unregister 
 * them all when the page is unloaded.
 *
 * @private
 */
br.util.EventUtility._handlePageUnload = function()
{
	br.util.EventUtility._initMap();
	
	// remove all the event handler registered with addEventListener()
	for (nUniqueListenerId in br.util.EventUtility.m_mEvents)
	{
		br.util.EventUtility.removeEventListenerById(nUniqueListenerId);
	}

	// remove the unload event listener that caused this callback
	if (window.removeEventListener)
	{
		window.removeEventListener("unload", br.util.EventUtility._handlePageUnload, false);
	}
	else
	{
		window.detachEvent("onunload", br.util.EventUtility._handlePageUnload);
	}

	br.util.EventUtility.m_mEvents = br.util.MapFactory.createMap();
};
