live.examples.presenter.EventEmitter = function(){
	/**
	 * @param {MAP} _callbacks
	 */
	this._callbacks = {};
	/**
	 * @param {STRING} eventName
	 * @param {FUNCTION} callback
	 * @param {OBJECT} me
	 * @return void
	 */
	this.bind = function(eventName,callback,me){
		if (!this._callbacks[eventName]){
			this._callbacks[eventName] = [];
		}
		this._callbacks[eventName].push({
			fn:callback,
			emitter:me
		});
	};
	/**
	 * @param {STRING} eventName
	 * @param {FUNCTION} fCallback
	 */
	this.unbind = function(eventName, fCallback){
		for( var i = 0; this._callbacks[eventName].length; i++ )
		{
			if( this._callbacks[eventName][i].fn === fCallback )
			{
				this._callbacks[eventName].splice(i, 1);
				return true;

			}
		}
		return false;
	};
	/**
	 * @param {STRING} eventName
	 * @param {V} args
	 */
	this.emit = function(eventName, args)
	{
		if(!this._callbacks[eventName]) return;

		for ( var i = 0; i < this._callbacks[eventName].length; i++ )
		{
			var callArguments = [];
			for(var a = 1;a < arguments.length;a++){
				callArguments.push(arguments[a]);
			}
			this._callbacks[eventName][i].fn.apply(this._callbacks[eventName][i].emitter, callArguments);
		}
	};
};
