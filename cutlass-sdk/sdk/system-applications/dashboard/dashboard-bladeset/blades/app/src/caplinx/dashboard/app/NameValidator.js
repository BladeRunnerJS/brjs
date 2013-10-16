// TODO: this class needs to be separately unit tested, and lots of the repetitive tests in the model validators should be removed
caplinx.dashboard.app.NameValidator = function()
{
};

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.RESERVED_JS_WORDS = ['break', 'case', 'catch', 'continue', 'debugger', 'default', 'delete', 'do', 'else', 'finally', 'for', 'function', 'if', 'in', 'instanceof', 'new', 'return', 'switch', 'this', 'throw', 'try', 'typeof', 'var', 'void', 'while', 'with', 'class', 'const', 'enum', 'export', 'extends', 'import', 'super', 'implements', 'interface', 'let', 'package', 'private', 'protected', 'public', 'static', 'yield'];

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.DIRECTORY_CHARACTERS_MESSAGE = "Directory names can only contain alphanumeric and underscore characters.";

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.INVALID_PACKAGE_NAME_MESSAGE = "Package names can only contain lower-case alphanumeric characters, where the first character is non-numeric.";

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.RESERVED_JS_WORD_MESSAGE = "Package names can't be reserved javascript words.";

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.isValidDirectoryName = function(sName)
{
	return sName.match(/^[a-zA-Z0-9_-]*$/);
};

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.isValidPackageName = function(sName)
{
	return sName.match(/^([a-z][a-z0-9]*)?$/);
};

/**
 * @static
 */
caplinx.dashboard.app.NameValidator.isReservedJsWord = function(sName)
{
	if(!this.mReservedWords)
	{
		this.mReservedWords = caplin.core.MapUtility.addArrayToMap({}, this.RESERVED_JS_WORDS);
	}
	
	return (this.mReservedWords[sName]) ? true : false;
};
