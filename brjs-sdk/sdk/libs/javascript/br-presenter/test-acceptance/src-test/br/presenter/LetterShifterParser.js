 var Parser = require("br/presenter/parser/Parser");
 var Core = require("br/Core");
 /**
  * @class
  * @extends module:br/presenter/parser/Parser
  */
 LetterShifterParser = function()
 {
     //		this.m_pAlphabet = [
     //			"a","b","c","d","e","f","g","h",
     //			"i","j","k","l","m","n","o","p",
     //			"q","r","s","t","u","v","w","x",
     //			"y","z","a"]; // the last "a" is not a mistake
 };

 Core.extend(LetterShifterParser, Parser);

 /**
  * @type String
  */
 LetterShifterParser.prototype.parse = function(vValue, mAttributes)
 {
     // just replace a's with b's for now
     return vValue.toLowerCase().replace("b", "c");
 };

 module.exports = new LetterShifterParser();
