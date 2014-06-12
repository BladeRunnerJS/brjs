/**
 * @private
 */
br.test.GwtFailureMessage = function() {
	_errorMsg = "";
	_statck = "";
}
br.test.GwtFailureMessage.prototype.setMessage = function(sMsg) {
	this._errorMsg = sMsg;
}
br.test.GwtFailureMessage.prototype.getMessage = function() {
	return this._errorMsg;
}
br.test.GwtFailureMessage.prototype.setStack = function(sStack) {
	this._statck = sStack;
}
br.test.GwtFailureMessage.prototype.getStack = function() {
	return this._statck;
}

