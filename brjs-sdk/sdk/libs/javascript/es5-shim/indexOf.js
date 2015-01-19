
// We've added this file as there is a bug in IE8 in the current version of this shim (2.0.2)
// "i in self" fails.

if((navigator.appName == 'Microsoft Internet Explorer') && (navigator.userAgent.match(/MSIE ([0-9]+)/)[1] <= 8))
	{
	var prepareString = "a"[0] != "a"; 
	Array.prototype.indexOf = function indexOf(sought /*, fromIndex */ ) {
	    var self = toObject(this),
	        length = self.length >>> 0;
	
	    if (!length) {
	        return -1;
	    }
	
	    var i = 0;
	    if (arguments.length > 1) {
	        i = toInteger(arguments[1]);
	    }
	
	    // handle negative indices
	    i = i >= 0 ? i : Math.max(0, length + i);
	    for (; i < length; i++) {
	        if (self[i] === sought) { // Patch was this line
	            return i;
	        }
	    }
	    return -1;
	};
	
	//ES5 9.4
	//http://es5.github.com/#x9.4
	//http://jsperf.com/to-integer
	
	function toInteger(n) {
	 n = +n;
	 if (n !== n) { // isNaN
	     n = 0;
	 } else if (n !== 0 && n !== (1/0) && n !== -(1/0)) {
	     n = (n > 0 || -1) * Math.floor(Math.abs(n));
	 }
	 return n;
	}
	
	//ES5 9.9
	//http://es5.github.com/#x9.9
	var toObject = function (o) {
	 if (o == null) { // this matches both null and undefined
	     throw new TypeError("can't convert "+o+" to object");
	 }
	 return Object(o);
	};
}