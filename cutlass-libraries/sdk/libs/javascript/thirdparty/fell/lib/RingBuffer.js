"use strict";

var Utils = require('./Utils');

/**
 * Creates a RingBuffer, which allows a maximum number of items to be stored.
 *
 * @param size {Number} The maximum size of this buffer. This must be an integer larger than 0.
 * @constructor
 */
function RingBuffer(size) {
	this._checkSize(size);
	this.maxSize = size;
	this.clear();
}

var ERRORS = {
	"parameter not function": "Parameter must be a function, was a {0}.",
	"size less than 1": "RingBuffer cannot be created with a size less than 1 (was {0}).",
	"size not integer": "RingBuffer cannot be created with a non integer size (was {0})."
};

/**
 * Clears all items from this RingBuffer and resets it.
 */
RingBuffer.prototype.clear = function () {
	this.buffer = new Array(this.maxSize);
	this.next = 0;
	this.isFull = false;
};

/**
 * @return the item most recently added into this RingBuffer, or null if no items have been added.
 */
RingBuffer.prototype.newest = function () {
	var newest = null;
	var index = (this.next + this.maxSize - 1) % this.maxSize;
	if (this.isFull || index < this.next) {
		newest = this.buffer[index];
	}
	return newest;
};

/**
 * @return the oldest item that is still in this RingBuffer or null if no items have been added.
 */
RingBuffer.prototype.oldest = function () {
	var oldest = null;
	if (this.isFull) {
		oldest = this.buffer[this.next];
	}
	else if (this.next > 0) {
		oldest = this.buffer[0];
	}
	return oldest;
};

/**
 * @param n {Number} the index of the item to be returned.
 * @return the nth oldest item that is stored or undefined if the index is larger than the number of
 *          items stored.
 */
RingBuffer.prototype.get = function (n) {
	if (n >= this.maxSize) return undefined;
	if (this.isFull) {
		return this.buffer[(this.next + n) % this.maxSize];
	}
	return this.buffer[n];
};

/**
 * Adds an item into the end of this RingBuffer, possibly pushing an item out of the buffer in the
 * process.
 *
 * @param {Object} object an item to add into this buffer.
 * @return the item that was pushed out of the buffer or null if the buffer is not full. Note, this
 *          is different to what an array.push returns.
 *
 */
RingBuffer.prototype.push = function(object) {
	var ousted = null;
	if (this.isFull) {
		ousted = this.oldest();
	}

	this._changeWindow(object);

	return ousted;
};

/**
 * Changes the size of a RingBuffer.
 *
 * This operation should not be expected to be performant; do not do it often.
 *
 * If the new size is smaller than the number of items in this window, this operation may cause some
 * objects to be pushed out of the window.
 *
 * @param {Number} newSize the new size the window should take up. Must be a positive integer.
 */
RingBuffer.prototype.setSize = function(newSize) {
	this._checkSize(newSize);

	if (this.maxSize == newSize) {
		return;
	}
	var tmpBuffer = new RingBuffer(newSize);
	this.forEach(tmpBuffer.push.bind(tmpBuffer));

	this.maxSize = tmpBuffer.maxSize;
	this.buffer = tmpBuffer.buffer;
	this.next = tmpBuffer.next;
	this.isFull = tmpBuffer.isFull;
};

/**
 * Iterates over each of the items in this buffer from oldest to newest.
 *
 * @param {Function} func a function that will be called with each item.
 */
RingBuffer.prototype.forEach = function (func) {
	if (typeof func != 'function') {
		throw new TypeError(errorMessage("parameter not function", typeof func));
	}

	for (var i = 0, end = this.getSize(); i < end; ++i) {
		var bufferIndex = this.isFull ? (this.next + i) % this.maxSize : i;
		func(this.buffer[bufferIndex]);
	}
};

/**
 * @returns the number of items in this buffer.
 */
RingBuffer.prototype.getSize = function () {
	return this.isFull ? this.maxSize : this.next;
};

/**
 * @return {String} Returns a string representation of this RingBuffer.  This is intended to be
 *          human readable for debugging but may change.
 */
RingBuffer.prototype.toString = function () {
	var result = [ "{sidingwindow start=" ];
	result.push(this.next);
	result.push(" values=[");
	result.push(this.buffer.join(","));
	result.push("] }");

	return result.join("");
};

RingBuffer.prototype._checkSize = function(size) {
	if (size !== (size|0)) {
		throw new Error(errorMessage("size not integer", size));
	}
	if (size < 1) {
		throw new Error(errorMessage("size less than 1", size));
	}
};

RingBuffer.prototype._changeWindow = function(incoming) {
	this.buffer[this.next] = incoming;
	this.next = (this.next + 1) % this.maxSize;
	if (this.next == 0) {
		this.isFull = true;
	}
};

function errorMessage() {
	var args = Array.prototype.slice.call(arguments);
	args[0] = ERRORS[args[0]];
	return Utils.interpolate.apply(Utils, args);
}
RingBuffer.errorMessage = errorMessage;

module.exports = RingBuffer;