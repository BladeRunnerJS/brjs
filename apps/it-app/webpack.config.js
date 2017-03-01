const {
	resolve
} = require('path');

const {
	configureAliases
} = require('@caplin/alias-loader/alias-configuration');
const {
	webpackConfigGenerator
} = require('@caplin/webpack-config-app');

const aliases = require('./src/config/aliases');
const testAliases = require('./scripts/aliases-test');

// Some thirdparty library modules check if `define` exists so they can export themself using it as
// opposed to attaching to the global. To simulate a similar environment to BRJS's we need to remove `module`.
function moduleCannotBelieveItsAMD(absolutePath) {
	return absolutePath.match(/jquery/);
}

// Delete
function moduleUsesGlobal(absolutePath) {
	return absolutePath.match(/interact/);
}

function moduleCannotHaveCJSExports(absolutePath) {
	return absolutePath.match(/jshamcrest/);
}

// Delete
// Some thirdparty libraries use a check for `require` to set themself to CJS module mode.
function moduleCannotHaveRequire(absolutePath) {
	return absolutePath.match(/sinon/);
}

const webpackAppAliases = {
	'some.alias1': 'br/_test-src/a/AliasClass1',
	'some.alias2': 'br/_test-src/b/AliasClass2'
};

module.exports = function createWebpackConfig(version) {
	const webpackConfig = webpackConfigGenerator({
		basePath: resolve(__dirname),
		version
	});

	webpackConfig.module.loaders.push({
		test: moduleUsesGlobal,
		loader: 'imports-loader?this=>window'
	}, {
		test: moduleCannotHaveRequire,
		loader: 'imports-loader?require=>undefined'
	}, {
		test: moduleCannotHaveCJSExports,
		loader: 'imports-loader?exports=>undefined'
	}, {
		test: moduleCannotBelieveItsAMD,
		loader: 'imports-loader?define=>false'
	}, {
		test: /jasmine/,
		loaders: ['exports?jasmine', 'script']
	});

	configureAliases(aliases, webpackConfig, testAliases, webpackAppAliases);

	return webpackConfig;
}
