
// Extracting the dynamic require as it causes problems in webpack.
// When the dynamic require is in a separate module we can configure webpack
// to load another module instead of this module.
module.exports = function(ref) {
	return require(ref);
}
