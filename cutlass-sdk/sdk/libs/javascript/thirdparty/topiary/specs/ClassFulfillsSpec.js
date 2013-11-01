/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.classFulfills", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var Class, Interface;

	beforeEach(function() {
		Class = function() {};
		Interface = function() {};
	});

	it("throws an error if the class is null.", function() {
		expect( function() {
			topiary.classFulfills(null, Interface);
		}).toThrow(err.NULL('Class', 'classFulfills'));
	});

	it("throws an error if the protocol is null.", function() {
		expect( function() {
			topiary.classFulfills(Class, null);
		}).toThrow(err.NULL('Protocol', 'classFulfills'));
	});

	it("returns true if all functions on an interface prototype are also provided by the class.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		Class.prototype.randomThing = function() {};
		Class.prototype.otherThing = function() {};
		Class.prototype.nonRelevantThing = function() {};

		expect( topiary.classFulfills(Class, Interface)).toBe(true);
	});

	it("returns false if not all functions on an interface prototype are provided by the class.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		Class.prototype.randomThing = function() {};

		expect( topiary.classFulfills(Class, Interface)).toBe(false);
	});

	it("returns true if all functions on an interface prototype are inherited by the class.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = function() {};

		function Parent() {}
		Parent.prototype.randomThing = function() {};
		Parent.prototype.otherThing = function() {};
		Parent.prototype.nonRelevantThing = function() {};

		topiary.extend(Class, Parent);

		expect( topiary.classFulfills(Class, Interface)).toBe(true);
	});

	it("returns false if the protocol has properties of a different type to the class.", function() {
		Interface.prototype.randomThing = function() {};
		Interface.prototype.otherThing = 23;

		Class.prototype.randomThing = function() {};
		Class.prototype.otherThing = "string";

		expect( topiary.classFulfills(Class, Interface)).toBe(false);
	});

	it("returns true if the protocol has properties of the same nonfunction type to the class.", function() {
		Interface.prototype.randomThing = "string";
		Interface.prototype.otherThing = 23;

		Class.prototype.randomThing = "different string";
		Class.prototype.otherThing = 40;
		Class.prototype.nonRelevantThing = 90;

		expect( topiary.classFulfills(Class, Interface)).toBe(true);
	});

	it("returns true if an adhoc protocol has properties indicating the same type as the properties on the class.", function() {
		Class.prototype.randomThing = "different string";
		Class.prototype.otherThing = 40;
		Class.prototype.nonRelevantThing = 90;

		expect( topiary.classFulfills(Class, {randomThing: String, otherThing: Number})).toBe(true);
	});


});