var hasDefinedProperty = false;

if (Object.defineProperty) {
	try {
		// IE8 throws an error here.
		Object.defineProperty({}, 'x', {});
		hasDefinedProperty = true;
	} catch (e) {
		hasDefinedProperty = false;
	}
}

if (!hasDefinedProperty) {
	Object.defineProperty = function(obj, prop, descriptor) {
		obj[prop] = descriptor.value;
	};
}

if (!Object.create) {
	Object.create = function(proto, descriptors) {
		var myConstructor = function() {};
		myConstructor.prototype = proto;

		var result = new myConstructor();

		var keys = Object.keys(descriptors);
		for (var i = 0; i < keys.length; ++i) {
			var key = keys[i];
			Object.defineProperty(result, key, descriptors[key]);
		}

		return result;
	};
}
