/* global describe, beforeEach, it, expect, topiarist, err */
describe("topiarist.fulfills", function() {
	if (typeof topiarist === 'undefined') topiarist = require('../lib/topiarist.js');
	var err = topiarist._err;

	var Class, Interface, instance;

	beforeEach(function() {
		Class = function() {};
		Interface = function() {};
		instance = new Class();
	});

	it("throws an error if the object is null.", function() {
		expect( function() {
			topiarist.fulfills(null, Interface);
		}).toThrow(err.NULL('Object', 'fulfills'));
	});

	it("throws an error if the protocol is null.", function() {
		expect( function() {
			topiarist.fulfills(instance, null);
		}).toThrow(err.NULL('Protocol', 'fulfills'));
	});

	it("returns true if all functions on an Interface prototype are also on the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		instance.randomThing = function() {};
		instance.otherThing = function() {};
		instance.nonRelevantThing = function() {};

		expect( topiarist.fulfills(instance, Interface)).toBe(true);
	});

	it("returns false if not all functions on an Interface prototype are on the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		instance.randomThing = function() {};

		expect( topiarist.fulfills(instance, Interface)).toBe(false);
	});

	it("returns true if all functions on an Interface prototype are inherited by the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		function Parent() {}
		Parent.prototype.randomThing = function() {};
		Parent.prototype.otherThing = function() {};
		Parent.prototype.nonRelevantThing = function() {};

		topiarist.extend(Class, Parent);

		instance = new Class();

		expect( topiarist.fulfills(instance, Interface)).toBe(true);
	});

	it("returns false if the protocol has properties of a different type to the instance.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = 23;

		instance.randomThing = function() {};
		instance.otherThing = "string";

		expect( topiarist.fulfills(instance, Interface)).toBe(false);
	});

	it("returns true if the protocol has properties of the same nonfunction type to the instance.", function() {
		Interface.prototype.randomThing = "string";
		Interface.prototype.otherThing = 23;

		instance.randomThing = "different string";
		instance.otherThing = 40;
		instance.nonRelevantThing = 90;

		expect( topiarist.fulfills(instance, Interface)).toBe(true);
	});

	it("returns true if an adhoc protocol has properties indicating the same type as the properties on the instance.", function() {
		instance.randomThing = "different string";
		instance.otherThing = 40;
		instance.nonRelevantThing = 90;

		expect( topiarist.fulfills(instance, {randomThing: String, otherThing: Number})).toBe(true);
	});

	it("works with type indicators even if the thing being checked is a function.", function() {
		instance.randomThing = function() {};
		expect( topiarist.fulfills(instance, {randomThing: Number})).toBe(false);
	});

	it("needs all types to be true, not just one when used with type indicators.", function() {
		instance.randomThing = 30;
		instance.otherThing = 20;
		expect( topiarist.fulfills(instance, {randomThing: Number, otherThing: String})).toBe(false);
	});

});