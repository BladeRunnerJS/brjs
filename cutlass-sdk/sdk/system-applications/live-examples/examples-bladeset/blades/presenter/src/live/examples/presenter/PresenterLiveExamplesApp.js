caplin.thirdparty("jquery");
caplin.thirdparty("jscrollpane");
caplin.thirdparty("codemirror");
caplin.thirdparty("jsbeautify");

live.examples.presenter.PresenterLiveExamplesApp = function()
{
	/**
	 * Start the live examples
	 */
	live.examples.presenter.INSTANCE = new live.examples.presenter.LiveExamples();
};

//--- Patches for IE ---

/*if(!Function.prototype.bind) {
	Function.prototype.bind = function (oThis) {
		if (typeof this !== "function") {
			// closest thing possible to the ECMAScript 5 internal IsCallable function  
			throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");  
		}
		
		var aArgs = Array.prototype.slice.call(arguments, 1),
			fToBind = this,
			fNOP = function () {},
			fBound = function () {
				return fToBind.apply(this instanceof fNOP
					? this
					: oThis || window,
					aArgs.concat(Array.prototype.slice.call(arguments)));
			};
		
		fNOP.prototype = this.prototype;
		fBound.prototype = new fNOP();
		
		return fBound;
	};
};*/

if(!window.Node) {
	Node = {ELEMENT_NODE:1};
};

if(!window.DOMParser)
{
	DOMParser = function()
	{
	};
	
	DOMParser.prototype.parseFromString = function(sXml)
	{
		oDom = new ActiveXObject("Microsoft.XMLDOM");
		oDom.async = "false";
		oDom.loadXML(sXml);
		
		return oDom;
	};
};
