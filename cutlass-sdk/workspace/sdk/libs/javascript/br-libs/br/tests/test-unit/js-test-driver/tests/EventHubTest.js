(function() {

	var ServiceRegistry = require('br/ServiceRegistry');
	var Emitter = require( 'emitr' );

	var EventHubTest = TestCase("EventHubTest").prototype;
	var hasFired = null;

	EventHubTest.setUp = function() {
		hasFired = {};

		this.eventHub = ServiceRegistry.getService('br.event-hub');
	};

	EventHubTest.callback = function(channel, event) {
		if (channel in hasFired === false) {
			hasFired[channel] = [];
		};
		return function() {
			hasFired[channel].push({event: event, args: arguments});
		};
	};
	
	// T E S T S
	
	//TODO: Don't really like this. why is the channel adding a "name" property onto the Emitter object from the outside?
	// encapsulation anyone
	EventHubTest["test canCreateChannels"] = function() {
		this.eventHub = ServiceRegistry.getService( 'br.event-hub');

		this.eventHub.channel( 'apple');
		this.eventHub.channel( 'grape');

		var appleEmitter = new Emitter()
		appleEmitter.name = "apple";
		var grapeEmitter = new Emitter()
		grapeEmitter.name = "grape";
		
		assertEquals(this.eventHub.channels, {
			'apple' :  appleEmitter,
			'grape' :  grapeEmitter 
		});
	};


	EventHubTest["test canEmitOnCallback"] = function() {
		this.eventHub = ServiceRegistry.getService( 'br.event-hub');

		this.eventHub.channel( 'apple' ).on('apple-juice', this.callback('apple', 'apple-juice'));
		this.eventHub.channel( 'grape' ).on('grape-juice', this.callback('grape', 'grape-juice'));
		this.eventHub.channel( 'orange' ).on('orange-juice', this.callback('orange', 'orange-juice'));

		// 1: Single triggered event
		this.eventHub.channel( 'apple').trigger('apple-juice');

		assertEquals(1, hasFired['apple'].length);
		assertEquals(0, hasFired['grape'].length);
		assertEquals(0, hasFired['orange'].length);

		// 2: Multiple triggered events
		this.eventHub.channel( 'apple').trigger('apple-juice');
		this.eventHub.channel( 'grape').trigger('grape-juice');
		this.eventHub.channel( 'orange').trigger('orange-juice');

		assertEquals(2, hasFired['apple'].length);
		assertEquals(1, hasFired['grape'].length);
		assertEquals(1, hasFired['orange'].length);
	};

	EventHubTest["test unknownEventBeingTriggeredDoesNotGetFired"] = function() {
		this.eventHub = ServiceRegistry.getService( 'br.event-hub');

		this.eventHub.channel( 'apple' ).on('apple-juice', this.callback('apple', 'apple-juice'));
		this.eventHub.channel( 'apple').trigger('unknown-event');

		assertEquals(0, hasFired['apple'].length);
	};

	EventHubTest["test itOnlyTriggersOnceWhenUsingOnce"] = function() {
		this.eventHub = ServiceRegistry.getService( 'br.event-hub');
		this.eventHub.channel( 'apple' ).once('apple-juice', this.callback('apple', 'apple-juice'));

		// First trigger
        this.eventHub.channel( 'apple').trigger('apple-juice');
		assertEquals(1, hasFired['apple'].length);

		// Second trigger
		this.eventHub.channel( 'apple').trigger('apple-juice');
		assertEquals(1, hasFired['apple'].length);
	};

	EventHubTest["test usingOffRemovesTheListenerAndStopsEventsFromBeingFiring"] = function() {
		this.eventHub = ServiceRegistry.getService( 'br.event-hub');
		this.eventHub.channel( 'apple' ).on('apple-juice', this.callback('apple', 'apple-juice'));
		this.eventHub.channel( 'apple' ).off('apple-juice');

		this.eventHub.channel( 'apple').trigger('apple-juice');

		assertEquals(0, hasFired['apple'].length);
	};

})();