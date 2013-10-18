---
layout: main
permalink: /index.html
title: Emitter
---

<script type="text/javascript" src="Emitter.js"></script>

EventEmitter
============

* This document is available nicely formatted [here](http://BladeRunnerJS.github.io/emitter).
* Tests are [here](http://BladeRunnerJS.github.io/emitter/spec).
* Source code is [here](https://github.com/BladeRunnerJS/emitter).
* JSDoc for the Emitter mixin is [here](http://BladeRunnerJS.github.io/emitter/doc/Emitter.html).

The rendered form of this document includes the Emitter script so you can open
a console and try it immediately.

My two main inspirations in making this implementation were the events in [backbone](http://backbonejs.org/#Events),
and the EventEmitter in [node](http://nodejs.org/docs/latest/api/all.html#all_class_events_eventemitter).

If we hadn't required the ability to pass 'context' into `on` and `off`, we probably would have gone with
[LucidJS](http://robertwhurst.github.io/LucidJS/).  Not leaking memory is difficult enough when
building large applications; forcing people to keep extra objects around just so they can clean up after
themselves is ugly enough that it discourages people from doing something important.

While I haven't come across this exact combination of focussed microlibrary combined with context,
the only unusual feature of this Emitter (and deliberately so) is that it allows you to listen to and
dispatch objects rather than just strings.  Related to this is type based events, which I describe
later.

Making an Emitter
-----------------

While you can directly create a new Emitter, or use standard prototypical
inheritance to inherit from it, usually you will want to mix the Emitter
methods in to your own classes or objects.

```javascript

	function MyEmitter() {};
	Emitter.mixInto(MyEmitter);

	var emitter = new MyEmitter();
```

Standard Emitter Features
-------------------------

The big three methods are provided:

on:

```javascript

	// Basic example:

	emitter.on('some-event', function() {
		// By default, 'this' is set to emitter inside here.
		// you can change that by providing a context argument.
	});

	// Example using context:

	function MyObject() {}
	MyObject.prototype.onBoom = function() {
		// in this example, 'this' is set to 'obj'.
	};

	var obj = new MyObject();
	emitter.on('end-of-the-world', obj.onBoom, obj);
```

The poorly (but commonly) named off:

```javascript

	// clears all listeners registered on emitter.
	emitter.off();

	// clears all listeners for 'some-event'.
	emitter.off('some-event');

	// removes the listener added with
	//    emitter.on('some-event', callback);
	emitter.off('some-event', callback);

	// removes the listener added with
	//    emitter.on('some-event', callback, context);
	emitter.off('some-event', callback, context);

	// removes all listeners registered with a context of context.
	emitter.off(null, null, context);
	// or
	emitter.clearListeners(context);
```

trigger (sometimes called emit or fire or notify):

```javascript

	// All listeners registered for the 'end-of-the-world' event
	// will get called with alienSpacecraft as their first argument.
	emitters.trigger('end-of-the-world', alienSpacecraft);
```

once is another function that is commonly provided by Emitters:

```javascript

	// Once behaves similarly to .on, but the listener is only
	// ever called once.
	emitter.once('some-event', function() {
		// this function will only be called once.
	});

	emitter.trigger('some-event');
	emitter.trigger('some-event');
```

Extra Features
-----------------

This Emitter provides two extra features.

### MetaEvents

The emitter will also trigger special events that you can listen to in certain
circumstances. The event emitter in node does a similar thing, firing `newListener`
and `removeListener` events at the appropriate time.

There are three meta events which are:

* `Emitter.meta.AddListenerEvent`, triggered when a listener is added.
* `Emitter.meta.RemoveListenerEvent`, triggered when a listener is removed.
* `Emitter.meta.DeadEvent`, triggered when an event is fired but no listeners receive it.

```javascript

	// In this example, I use an AddListenerEvent metaevent to
	// create 'sticky' events behaviour for the ready event.

	function Document() {
		this.isReady = false;

		this.on(Emitter.meta.AddListenerEvent, function(addEvent) {
			if (this.isReady) {
				addEvent.listener.call(addEvent.context);
			}
		}, this);
	}
	Emitter.mixInto(Document);
	Document.prototype.makeReady = function() {
		this.isReady = true;
		this.trigger('ready');
	};

	var doc = new Document();

	doc.makeReady();

	// Even though makeReady was called before this 'on',
	// the listener will still be called.

	doc.on('ready', function() {
		console.log('ready now');
	});
```

### Type Based Events

The Events themselves in normal usage are usually string identifiers and then a list of
arguments, almost like an algebraic data type - a tag and then a tuple of data items.  In an
object language like javascript, it seems more natural to dispatch event objects instead and
listen for them based on their type.

```javascript

	function MouseEvent(x, y) {
		this.x = x;
		this.y = y;
	}

	emitter.on(MouseEvent, function(event) {
		// in here, event is the instance of MouseEvent that
		// we trigger the emitter with.
	});

	emitter.trigger(new MouseEvent(100, 99));
```

It obeys the Liskov Substitution Principle, so a listener will also get notified of
events that are subclasses of the event type it is registered for.

```javascript

	function ClickEvent(button, x, y) {
		MouseEvent.call(this, x, y);
		this.button = button;
	}

	ClickEvent.prototype = Object.create(MouseEvent.prototype);

	emitter.on(MouseEvent, function(event) {
		// in here, event is the instance of ClickEvent that
		// we trigger the emitter with.
	});

	emitter.trigger(new ClickEvent("right", 101, 100));
```