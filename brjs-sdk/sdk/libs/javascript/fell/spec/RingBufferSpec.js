describe('A RingBuffer ', function(){
	var global = (function() {return this;})();
	var RingBuffer = global.fell ? global.fell.RingBuffer : require("..").RingBuffer;

	it('should throw exceptions when constructed with invalid arguments.', function() {
		expect(function() {
			new RingBuffer();
		}).toThrow(new Error(RingBuffer.errorMessage("size not integer", "undefined")));

		expect(function() {
			new RingBuffer("fred");
		}).toThrow(new Error(RingBuffer.errorMessage("size not integer", "fred")));

		expect(function() {
			new RingBuffer(0);
		}).toThrow(new Error(RingBuffer.errorMessage("size less than 1", 0)));

		expect(function() {
			new RingBuffer(3.2);
		}).toThrow(new Error(RingBuffer.errorMessage("size not integer", 3.2)));

		// This should not throw an Exception.
		new RingBuffer(4);
	});

	describe('with a maxsize of 5', function() {
		var buffer;

		function verifyNewestOldest(newest, oldest) {
			expect(buffer.newest()).toBe(newest);
			expect(buffer.oldest()).toBe(oldest);
		}

		function verifyIteration(expectedValues) {
			var currentItem = 0;
			buffer.forEach(function(item) {
				expect(item).toBe(expectedValues[currentItem++]);
			});
			expect(currentItem).toBe(expectedValues.length);
		}

		function fillWindow(data) {
			for (var i = 0; i < data.length; ++i) {
				buffer.push(data[i]);
			}
		}

		beforeEach(function() {
			buffer = new RingBuffer(5);
		});

		it('should always return the correct size from getSize.', function() {
			expect(buffer.getSize()).toBe(0);

			for (var i = 1; i < 10; ++i) {
				buffer.push("hi"+i);
				expect(buffer.getSize()).toBe(Math.min(5, i));
			}
		});

		it('should iterate over the items correctly.', function() {
			expect(function() {
				buffer.forEach(6);
			}).toThrow(new TypeError(RingBuffer.errorMessage("parameter not function", "number")));

			buffer.forEach(function(item) {
				throw new Error("There are no items currently in the buffer.");
			});

			buffer.push(0);
			verifyIteration([0]);

			buffer.push(1);
			verifyIteration([0, 1]);

			buffer.push(2);
			verifyIteration([0, 1, 2]);

			buffer.push(3);
			verifyIteration([0, 1, 2, 3]);

			buffer.push(4);
			verifyIteration([0, 1, 2, 3, 4]);

			buffer.push(5);
			verifyIteration([1, 2, 3, 4, 5]);

			buffer.push(6);
			verifyIteration([2, 3, 4, 5, 6]);
		});

		it('should return oldest and newest correctly.', function() {
			verifyNewestOldest(null, null);
			buffer.push(0);
			verifyNewestOldest(0, 0);
			buffer.push(1);
			verifyNewestOldest(1, 0);
			buffer.push(2);
			verifyNewestOldest(2, 0);
			buffer.push(3);
			verifyNewestOldest(3, 0);
			buffer.push(4);
			verifyNewestOldest(4, 0);
			buffer.push(5);
			verifyNewestOldest(5, 1);
			buffer.push(6);
			verifyNewestOldest(6, 2);
		});

		it('should clear correctly.', function() {
			verifyNewestOldest(null, null);
			expect(buffer.getSize()).toBe(0);
			verifyIteration([]);

			buffer.clear();
			verifyNewestOldest(null, null);
			expect(buffer.getSize()).toBe(0);
			verifyIteration([]);

			fillWindow([0, 1, 2, 3, 4, 5, 6]);
			verifyNewestOldest(6, 2);
			expect(buffer.getSize()).toBe(5);
			verifyIteration([2, 3, 4, 5, 6]);

			buffer.clear();
			verifyNewestOldest(null, null);
			expect(buffer.getSize()).toBe(0);
			verifyIteration([]);
		});

		it('should allow the size to be set.', function() {
			fillWindow([0, 1, 2, 3, 4, 5, 6]);
			verifyIteration([2, 3, 4, 5, 6]);

			buffer.setSize(3);
			verifyIteration([4, 5, 6]);

			buffer.setSize(7);
			verifyIteration([4, 5, 6]);

			fillWindow([7, 8, 9]);
			verifyIteration([4, 5, 6, 7, 8, 9]);

			fillWindow([10, 11, 12, 13, 14, 15]);
			verifyIteration([9, 10, 11, 12, 13, 14, 15]);
		});

		it('should allow individual items to be got.', function() {
			fillWindow([0, 1]);

			expect(buffer.get(0)).toBe(0);
			expect(buffer.get(1)).toBe(1);
			expect(buffer.get(2)).toBe(undefined);

			fillWindow([2, 3, 4, 5, 6]);

			expect(buffer.get(0)).toBe(2);
			expect(buffer.get(1)).toBe(3);
			expect(buffer.get(2)).toBe(4);
			expect(buffer.get(3)).toBe(5);
			expect(buffer.get(4)).toBe(6);
			expect(buffer.get(5)).toBe(undefined);
		});
	});
});