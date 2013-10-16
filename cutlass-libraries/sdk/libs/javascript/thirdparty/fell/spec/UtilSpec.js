describe('The Log Utility ', function() {
	var global = (function() {return this;})();
	var Util =  global.fell ? global.fell.Utils : require("../lib/Utils");
/*

	module.exports = {
		format: format,
		padBefore: padBefore,
		padAfter: padAfter,
		interpolate: interpolate,
		templateFormatter: templateFormatter,
		ansiFormatter: ansiFormatter,
		allowAll: allowAll
	};
	*/

	it('provides an interpolation function.', function() {
		expect(Util.interpolate(""))
				.toBe("");

		expect(Util.interpolate("{0}", 23, 49))
				.toBe("23");

		expect(Util.interpolate('hello {1} fred is {0} again, {1}, {2}', "sleepy", "world"))
				.toBe("hello world fred is sleepy again, world, undefined");
	});

	it('provides a format function for dates.', function() {

		var date = new Date(2013, 5, 4, 3, 2, 1, 23);

		expect(Util.format("", date))
				.toBe("");

		expect(Util.format("EEE MMM dd yyyy HH:mm:ss", date))
				.toBe("Tue Jun 04 2013 03:02:01");

		expect(Util.format("EEEE MMMM d yy H:m:s.SSS", date))
				.toBe("Tuesday June 4 13 3:2:1.023");

		expect(Util.format("MM", date))
				.toBe("06");
	});

});