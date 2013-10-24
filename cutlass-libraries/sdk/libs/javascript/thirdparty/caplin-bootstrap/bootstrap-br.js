(function() {
	var tempBr = require('br');
	if(window.br)
	{
		Object.keys(window.br).forEach(function(key)
		{
			tempBr[key] = window.br[key];
		});
	}
	window.br = tempBr;
	br.Errors = require('br/Errors');
	br.AliasRegistry = require('br/AliasRegistry');
	br.ServiceRegistry = require('br/ServiceRegistry');
	br.i18n = {};
	br.i18n.Translator = require('br/i18n').getTranslator();
})();