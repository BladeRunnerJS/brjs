(function() {
  'use strict';
  var NativeKeyboardEvent = window.KeyboardEvent || function() {};

  // IE8KeyboardEvent
  var IE8KeyboardEvent = function(eventType, args) {
    args = normalizeArgs(args);
    var evt = document.createEventObject();

    evt.type = eventType;
    evt.key = args.key;
    evt.keyCode = args.key;
    evt.ctrlKey = args.ctrlKey;
    evt.shiftKey = args.shiftKey;
    evt.altKey = args.altKey;
    evt.repeat = args.repeat;

    return evt;
  };
  IE8KeyboardEvent.type = 'IE8';

  // DL3KeyboardEvent
  var DL3KeyboardEvent = function(eventType, args) {
    // chrome polyfill
    if(args && args.key) {
      args.keyIdentifier = args.key;
    }

    args = normalizeArgs(args);
    var evt = document.createEvent('KeyboardEvent');
    var modifiers = modifiersList(args);

    evt.initKeyboardEvent(eventType, args.bubbles, args.cancelable, null,
      args.key, args.location, modifiers, args.repeat, args.locale);

    // chrome polyfill
    if(evt.keyIdentifier) {
      evt.key = evt.keyIdentifier;
    }

    return evt;
  };
  DL3KeyboardEvent.prototype = Object.create(NativeKeyboardEvent.prototype);
  DL3KeyboardEvent.prototype.constructor = DL3KeyboardEvent;

  // DL4KeyboardEvent
  var DL4KeyboardEvent = function(eventType, args) {
    // chrome polyfill
    if(args && args.key) {
      args.keyIdentifier = args.key;
    }

    var evt = new NativeKeyboardEvent(eventType, args);

    // chrome polyfill
    if(evt.keyIdentifier) {
      evt.key = evt.keyIdentifier;
    }

    return evt;
  };
  DL4KeyboardEvent.prototype = Object.create(NativeKeyboardEvent.prototype);
  DL4KeyboardEvent.prototype.constructor = DL4KeyboardEvent;

  // Polyfilling
  if(document.implementation.hasFeature('KeyboardEvent', '4.0')) {
    KeyboardEvent = DL4KeyboardEvent;
  }
  else if(document.implementation.hasFeature('KeyboardEvent', '3.0')) {
    KeyboardEvent = DL3KeyboardEvent;
  }
  else if(document.createEventObject) {
    KeyboardEvent = IE8KeyboardEvent;
  }
  else if(navigator.userAgent == 'PhantomJS') {
	// seems to support DL3 keyboard events even though it doesn't claim to
    KeyboardEvent = DL3KeyboardEvent;
  }
  else {
    throw new Error('keyboard-event polyfill unable to shim browser.');
  }

  // Private Functions
  function normalizeArgs(args) {
    args = args || {};
    args.bubbles = defaultsTo(false, args.bubbles);
    args.cancelable = defaultsTo(false, args.cancelable);
    args.location = defaultsTo(0, args.location);
    args.repeat = defaultsTo(false, args.repeat);
    args.locale = defaultsTo('', args.locale);
    args.ctrlKey = defaultsTo(false, args.ctrlKey);
    args.shiftKey = defaultsTo(false, args.shiftKey);
    args.altKey = defaultsTo(false, args.altKey);
    args.metaKey = defaultsTo(false, args.metaKey);

    return args;
  }

  function defaultsTo(defaultValue, actualValue) {
    return (actualValue === undefined) ? defaultValue : actualValue;
  }

  function modifiersList(args) {
    var modifiers = [];

    if(args.ctrlKey) {
      modifiers.push('Control');
    }
    if(args.shiftKey) {
      modifiers.push('Shift');
    }
    if(args.altKey) {
      modifiers.push('Alt');
    }
    if(args.metaKey) {
      modifiers.push('Meta');
    }
    if(args.ctrlKey && args.altKey) {
      modifiers.push('AltGraph');
    }

    return modifiers.join(' ');
  }
})();