function Events() {
}

// all events that can be fired in all supported browsers
// (a list can be  found at http://help.dottoro.com/larrqqck.php)
Events.ALL_EVENTS = new Array("blur","change","click","contextmenu","copy","cut","dblclick",
		"drag","dragend","dragenter","dragleave","dragover","dragstart","drop","error",
		"focus","focusin","focusout","hashchange","input","keydown","keypress","keyup",
		"load","message","mousedown","mousemove","mouseout","mouseenter","mouseleave",
		"mouseover","mouseup","mousewheel","paste","reset","resize","scroll","select",
		"submit","unload");

Events.ALL_EVENTS.withoutEvents = function(matching) {
	ret = Events.ALL_EVENTS;
	for (i = 0; i < matching.length; i++) {
		ret = jQuery.grep(ret, function(value) {
		  return value != matching[i];
		});
	}
	return ret;
}

Events.NO_MOUSE_EVENTS = Events.ALL_EVENTS.withoutEvents(["mousedown","mousemove",
	"mouseout","mouseenter","mouseleave","mouseover","mouseup","mousewheel"]);

Events.EVENT_TYPES = {};
Events.EVENT_TYPES['*'] = Events.ALL_EVENTS;

//removes the event if the callback returns true
function filterEvents(array,callback) {
	retArray = [];
	for (i = 0; i < array.length; i++) {
		if (!callback(array[i])) {
			retArray.push(array[i]);
		}
	}
	return retArray;
}

function eventContains(match) {
	return (eventName.indexOf(match) == -1);
}

module.exports = Events;
