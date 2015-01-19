// TODO: this class needs to be separately unit tested, and lots of the repetitive tests in the model validators should be removed
brjs.dashboard.app.NameValidator = function()
{
};

/**
 * @static
 */
brjs.dashboard.app.NameValidator.RESERVED_JS_WORDS = ['break', 'case', 'catch', 'continue', 'deb'+'ugger', 'default', 'delete', 'do', 'else', 'finally', 'for', 'function', 'if', 'in', 'instanceof', 'new', 'return', 'switch', 'this', 'throw', 'try', 'typeof', 'var', 'void', 'while', 'with', 'class', 'const', 'enum', 'export', 'extends', 'import', 'super', 'implements', 'interface', 'let', 'package', 'private', 'protected', 'public', 'static', 'yield'];

/**
 * @static
 */
brjs.dashboard.app.NameValidator.DIRECTORY_CHARACTERS_MESSAGE = "Directory names can only contain alphanumeric and underscore characters.";

/**
 * @static
 */
brjs.dashboard.app.NameValidator.INVALID_PACKAGE_NAME_MESSAGE = "Package names can only contain lower-case alphanumeric characters, where the first character is non-numeric.";

/**
 * @static
 */
brjs.dashboard.app.NameValidator.RESERVED_JS_WORD_MESSAGE = "Package names can't be reserved javascript words.";

/**
 * @static
 */
brjs.dashboard.app.NameValidator.isValidDirectoryName = function(sName)
{
	return sName.match(/^[a-zA-Z0-9_-]*$/);
};

/**
 * @static
 */
brjs.dashboard.app.NameValidator.isValidPackageName = function(sName)
{
	return sName.match(/^([a-z][a-z0-9]*)?$/);
};

/**
 * @static
 */
brjs.dashboard.app.NameValidator.isReservedJsWord = function(sName)
{
	if(!this.mReservedWords)
	{
		this.mReservedWords = br.util.MapUtility.addArrayToMap({}, this.RESERVED_JS_WORDS);
	}
	
	return (this.mReservedWords[sName]) ? true : false;
};
