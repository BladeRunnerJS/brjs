"use strict";

exports.getBundlePath = function(prefix, bundlePath) {
	if (bundlePath != undefined) {
		/* make sure there are no leading or trailing /s that might mess up the generated path */
		prefix = prefix.replace(/^\/|\/$/g, '');
		if (bundlePath.substring(0, 1) == '/') { 
			bundlePath = bundlePath.substring(1);
		}
		return prefix + "/" + bundlePath
	}
	return prefix;
}