// see <http://www.adequatelygood.com/2011/4/Replacing-setTimeout-Globally> for info
if((navigator.appName == 'Microsoft Internet Explorer') && (navigator.userAgent.match(/MSIE ([0-9]+)/)[1] <= 8))
{
	var pProperties = ["alert", "setTimeout", "clearTimeout", "setInterval", "clearInterval"];
	for(var i = 0, l = pProperties.length; i < l; ++i)
	{
		var sProperty = pProperties[i];
		var fNativePropertyAccessor = window[sProperty];
		eval("var " + sProperty + ";");
		window[sProperty] = fNativePropertyAccessor;
	}
}

if (!Function.prototype['bind']) {
	// 24/01/2011 - AmirH - changed for better performance when there are no arguments to be curried.
	// Function.prototype.bind is a standard part of ECMAScript 5th Edition (December 2009, http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-262.pdf)
	// In case the browser doesn't implement it natively, provide a JavaScript implementation. This implementation is based on the one in prototype.js
	Function.prototype['bind'] = function (object) {
		var originalFunction = this;
		
		if (arguments.length === 1) {
			return function () {
					return originalFunction.apply(object, arguments);
				};
		} else {
			var args = Array.prototype.slice.call(arguments, 1);
			return function () {
				return originalFunction.apply(object, args.concat(Array.prototype.slice.call(arguments)));
			};
		}
	};
}
