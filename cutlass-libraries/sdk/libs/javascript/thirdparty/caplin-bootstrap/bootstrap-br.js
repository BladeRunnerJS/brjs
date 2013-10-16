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
})();