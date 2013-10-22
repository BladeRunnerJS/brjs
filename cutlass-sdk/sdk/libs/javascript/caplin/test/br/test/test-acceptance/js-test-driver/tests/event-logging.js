br.thirdparty("jquery");

eventsLog = [];
logEvent = function(e) {
	ignoringEvent = (e.target.className.indexOf('ignoreEvents') != -1);
	targetTagName = e.target.tagName;
	targetId = (e.target.id) ? "#" + e.target.id : "";
	rawTargetId = (e.target.id) ? e.target.id : "";
	targetType = (e.target.type) ? e.target.type : ""; 
	targetLocator = targetTagName + targetId;
	eventType = e.type;
	
	tagNameIndex = targetTagName;
	tagNameAndTypeIndex = tagNameIndex + "-" + targetType;
	tagNameAndIDIndex = tagNameIndex + "#" + rawTargetId;
	eventIndex = "*";
	
	if (EVENT_TYPES[tagNameAndIDIndex]) {
		eventIndex = tagNameAndIDIndex;
	} else if (EVENT_TYPES[tagNameAndTypeIndex]) {
		eventIndex = tagNameAndTypeIndex;
	} else if (EVENT_TYPES[tagNameIndex]) {
		eventIndex = tagNameIndex;
	}
	 
	supportedEvents = EVENT_TYPES[eventIndex];
	eventSupported = jQuery.inArray(eventType, supportedEvents) != -1
	if (!ignoringEvent && eventSupported) {
		if (typeof console != "undefined" && typeof console.log != "undefined")  {
			console.log(targetLocator + "(" + eventType + ")"); 
		}
		eventsLog.push(evt(targetLocator, eventType));
	}
}
evt = function(locator, eventType) {
	elementsFound = jQuery(locator);
	assertEquals(1,elementsFound.length);
	theElement = elementsFound[0];
	return([theElement,eventType])
} 
eventsRecieved = function(matchingEvents) {
	if (matchingEvents == null) {
		return eventsLog;
	}
	retEvents = [];
	for (i = 0; i < eventsLog.length ; i++) {
		if (jQuery.inArray(eventsLog[i][1], matchingEvents) != -1) {
			retEvents.push(eventsLog[i]);
		}	
	}
	return retEvents;
}
/** check the expected events have been fired in the order specified
 * ignores extra events that might have been fired
 */
verifyEvents = function(expectedEvents) {
	actualEvents = eventsRecieved();
		
	if (expectedEvents.length > actualEvents.length) {
		failEventsDontMatch(expectedEvents,actualEvents);
	}
	willFail = false;
	
	expectedEventsIndex = 0;
	actualEventsIndex = 0;
	
	expectedEvent = expectedEvents[expectedEventsIndex];
	actualEvent = actualEvents[actualEventsIndex];
	
	while (actualEventsIndex < actualEvents.length && 
			expectedEventsIndex < expectedEvents.length) {
		actualEvent = actualEvents[actualEventsIndex]
		if ( (expectedEvent[0] == actualEvent[0]) && (expectedEvent[1] == actualEvent[1]) ) {
			// found event
			expectedEventsIndex++;
			expectedEvent = expectedEvents[expectedEventsIndex];
		}
		actualEventsIndex++;
	}
	if (expectedEventsIndex != expectedEvents.length) {
		failEventsDontMatch(expectedEvents,actualEvents);
	}
}
verifyEventsUnordered = function(expectedEvents, minimumNumberOfActualEvents) {
	actualEvents = eventsRecieved();

	if (expectedEvents.length > actualEvents.length) {
		failEventsDontMatch(expectedEvents,actualEvents);
	}
	
	eventsNotFired = []
	for (i = 0; i < expectedEvents.length; i++) {
		foundEvent = false;
		foundIndex = null;
		expected = expectedEvents[i];
		for (j = 0; j < actualEvents.length && !foundEvent; j++) {
			actual = actualEvents[j];
			foundEvent = (expected[0] == actual[0]) && (expected[1] == actual[1]);
			foundIndex = j;
		}
		if (!foundEvent) {
			eventsNotFired.push(expected);
		} else {
			actualEvents.splice(foundIndex, 1);
		}
	}
	if (eventsNotFired.length > 0) {
		failEventsNotFired(eventsNotFired,eventsRecieved())
	}
}
clearEvents = function() {
	eventsLog.length = 0;
	eventsLog = [];
}

failEventsDontMatch = function(expectedEvents,actualEvents) {
	failMsg = "not enough (or too many) events fired, expected (" + expectedEvents.length + "):\n";
	for (i = 0; i < expectedEvents.length; i++) {
		theExpectedElement = expectedEvents[i][0];
		theExpectedEvent = expectedEvents[i][1];
		tagName = theExpectedElement.tagName;
		tagId = (theExpectedElement.id) ? "#" + theExpectedElement.id : "";
		failMsg += "  " + tagName + tagId + "(" + theExpectedEvent + ")\n";
	}
	failMsg += "but got (" + actualEvents.length + "):\n";
	for (i = 0; i < actualEvents.length; i++) {
		theActualElement = actualEvents[i][0];
		theActualEvent = actualEvents[i][1];
		tagName = theActualElement.tagName;
		tagId = (theActualElement.id) ? "#" + theActualElement.id : "";
		failMsg += "  = " + tagName + tagId + "(" + theActualEvent + ")\n";
	}
	fail( failMsg );
}
failEventsNotFired = function(missedEvents,actualEvents) {
	failMsg = "events were meant to be fired, but didnt:\n";
	for (i = 0; i < missedEvents.length; i++) {
		theMissedElement = missedEvents[i][0];
		theMissedEvent = missedEvents[i][1];
		tagName = theMissedElement.tagName;
		tagId = (theMissedElement.id) ? "#" + theMissedElement.id : "";
		failMsg += "  " + tagName + tagId + "(" + theMissedEvent + ")\n";
	}
	failMsg += "got:\n";
	for (i = 0; i < actualEvents.length; i++) {
		theActualElement = actualEvents[i][0];
		theActualEvent = actualEvents[i][1];
		tagName = theActualElement.tagName;
		tagId = (theActualElement.id) ? "#" + theActualElement.id : "";
		failMsg += "  = " + tagName + tagId + "(" + theActualEvent + ")\n";
	}
	fail( failMsg );
}
