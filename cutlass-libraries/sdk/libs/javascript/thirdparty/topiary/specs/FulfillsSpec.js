/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.fulfills", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var Class, Interface, instance;

	beforeEach(function() {
		Class = function() {};
		Interface = function() {};
		instance = new Class();
	});

	it("throws an error if the object is null.", function() {
		expect( function() {
			topiary.fulfills(null, Interface);
		}).toThrow(err.NULL('Object', 'fulfills'));
	});

	it("throws an error if the protocol is null.", function() {
		expect( function() {
			topiary.fulfills(instance, null);
		}).toThrow(err.NULL('Protocol', 'fulfills'));
	});

	it("returns true if all functions on an Interface prototype are also on the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		instance.randomThing = function() {};
		instance.otherThing = function() {};
		instance.nonRelevantThing = function() {};

		expect( topiary.fulfills(instance, Interface)).toBe(true);
	});

	it("returns false if not all functions on an Interface prototype are on the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		instance.randomThing = function() {};

		expect( topiary.fulfills(instance, Interface)).toBe(false);
	});

	it("returns true if all functions on an Interface prototype are inherited by the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		function Parent() {}
		Parent.prototype.randomThing = function() {};
		Parent.prototype.otherThing = function() {};
		Parent.prototype.nonRelevantThing = function() {};

		topiary.extend(Class, Parent);

		instance = new Class();

		expect( topiary.fulfills(instance, Interface)).toBe(true);
	});

	it("returns false if the protocol has properties of a different type to the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = 23;

		instance.randomThing = function() {};
		instance.otherThing = "string";

		expect( topiary.fulfills(instance, Interface)).toBe(false);
	});

	it("returns true if the protocol has properties of the same nonfunction type to the instance.", function() {
		Interface.prototype.randomThing = "string";
		Interface.prototype.otherThing = 23;

		instance.randomThing = "different string";
		instance.otherThing = 40;
		instance.nonRelevantThing = 90;

		expect( topiary.fulfills(instance, Interface)).toBe(true);
	});

	it("returns true if an adhoc protocol has properties indicating the same type as the properties on the instance.", function() {
		instance.randomThing = "different string";
		instance.otherThing = 40;
		instance.nonRelevantThing = 90;

		expect( topiary.fulfills(instance, {randomThing: String, otherThing: Number})).toBe(true);
	});

});