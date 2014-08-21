/**
 * @extends br.presenter.parser.Parser
 * @singleton
 */
br.presenter.testing.LetterShifterParser = function()
{
	//		this.m_pAlphabet = [
	//			"a","b","c","d","e","f","g","h",
	//			"i","j","k","l","m","n","o","p",
	//			"q","r","s","t","u","v","w","x",
	//			"y","z","a"]; // the last "a" is not a mistake
};

br.Core.extend(br.presenter.testing.LetterShifterParser, br.presenter.parser.Parser);

/**
 * @type String
 */
br.presenter.testing.LetterShifterParser.prototype.parse = function(vValue, mAttributes)
{
	// just replace a's with b's for now
	return vValue.toLowerCase().replace("b", "c");
};

/**
 * @private
 */
br.presenter.testing.LetterShifterParser.prototype.toString = function()
{
	return "br.presenter.testing.LetterShifterParser";
};

br.presenter.testing.LetterShifterParser = new br.presenter.testing.LetterShifterParser();
