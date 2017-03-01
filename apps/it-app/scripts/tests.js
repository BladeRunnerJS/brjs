const {
	lstatSync,
	readdirSync
} = require('fs');
const {
	join
} = require('path');

const configurePackageTestDatatype = require('@caplin/karma-jstd');
const {
	createPackagesATsKarmaConfigs,
	createPackagesKarmaConfigs,
	runPackagesTests
} = require('@caplin/karma-test-runner');
const {
	sync
} = require('glob');

const createWebpackConfig = require('../webpack.config');

const isWin = /^win/.test(process.platform);
const packagesDir = join(__dirname, '..', '..', '..', 'packages');
const srcDir = join(__dirname, '..', 'src');
const webpackConfig = createWebpackConfig();

webpackConfig.resolve.alias['$aliases-data$'] = join(__dirname, 'aliases-test.js');

function escapePath(packageDirectory) {
	return isWin ? packageDirectory.replace(/\\/g, '\\\\') : packageDirectory;
}

function createFilesList(packageName) {
	if (packageName === 'ct-dom') {
		return [{
			pattern: '_test-ut/js-test-driver/resources/style/styles.css',
			watched: false,
			included: false
		}];
	} else 	if (packageName === 'ct-grid') {
		return [{
			pattern: '_resources/**/*.*',
			watched: false,
			included: false
		}, {
			pattern: '_test-ut/js-test-driver/**/*.*',
			watched: false,
			included: false
		}, {
			pattern: '_resources-test-at/**/*.*',
			watched: false,
			included: false
		}];
	}

	return [];
}

function createTestDatatypes(directoryPath, installedPackagePath = directoryPath) {
	const installedPackages = readdirSync(installedPackagePath);

	return readdirSync(directoryPath)
		.filter((packageName) => installedPackages.includes(packageName))
		.filter((packageName) => packageName !== 'jstestdriver-test')
		.map((packageName) => ({
			files: createFilesList(packageName),
			packageName,
			packageDirectory: escapePath(join(installedPackagePath, packageName)),
			webpackConfig
		}))
		.filter(({packageDirectory}) => lstatSync(packageDirectory).isDirectory());
}

const srcTestDatatypes = createTestDatatypes(srcDir);
const packagesTestDatatypes = createTestDatatypes(packagesDir, join(__dirname, '..', 'node_modules'));

function filterOutPackagesWithoutTests(testTypePattern) {
	return packagesTestDatatypes
		.filter(({packageDirectory}) => {
			const pattern = packageDirectory + testTypePattern;

			return sync(pattern).length > 0;
		});
}

const testDatatypesWithATs = filterOutPackagesWithoutTests('/**/_test-at/**/*.js')
	.map(configurePackageTestDatatype);
const testDatatypesWithUTs = filterOutPackagesWithoutTests('/**/_test-ut/**/*.js')
	.map(configurePackageTestDatatype);

const karmaConfigs = createPackagesKarmaConfigs(testDatatypesWithUTs);
const karmaATsConfigs = createPackagesATsKarmaConfigs(testDatatypesWithATs);

runPackagesTests(karmaConfigs.concat(karmaATsConfigs));
