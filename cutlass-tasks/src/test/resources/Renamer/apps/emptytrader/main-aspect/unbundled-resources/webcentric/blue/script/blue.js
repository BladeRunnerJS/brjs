

function initialiseScreen(event)
{
//	var caplinLogo = document.getElementById("caplin-logo");
//	caplinLogo.style.display = "block";
}
caplin.webcentric.core.EventManager.registerAsEventListener("ViewReady", initialiseScreen);



webcentric.HTMLHandle_dialog = function(presentation,domHost){
	if (presentation){
		webcentric.HTMLHandle.call(this,presentation,domHost);
	}
};
webcentric.HTMLHandle_dialog.prototype = new webcentric.HTMLHandle();

webcentric.HTMLHandle_dialog.prototype.build = function(){
	webcentric.HTMLHandle.prototype.build.call(this);

	var $info = this.captionContainer.appendChild(document.createElement("span"));
	$info.className = "info";
	var $model = this.presentation.Component().model;
	if ($model){// eliminate Dialog Stretcher
		$model.registerAsEventListener("HeaderItemTextChanged",this.onHeaderItemTextChanged($info)) 
	}	
};

webcentric.HTMLHandle_dialog.prototype.onHeaderItemTextChanged = function(info){
	return function(evt){ 
       if (typeof evt.infoText != "undefined"){ 
            info.innerHTML= evt.infoText; 
       } 
    }; 
    
};

/**
 * extend the base Handle functionality...
 */
webcentric.HTMLHandle.assignCaptionSetter = function(title){
	return function(caption){
		title.innerHTML = "";
		title.appendChild(ViewFactory.getTextNode(caption));
	};
};

webcentric.HTMLHandle_drag = function(presentation,domHost){
	if (presentation){
		webcentric.HTMLHandle.call(this,presentation,domHost);
	}
};
webcentric.HTMLHandle_drag.prototype = new webcentric.HTMLHandle();

webcentric.HTMLHandle_drag.prototype.build = function(){
	webcentric.HTMLHandle.prototype.build.call(this);

	var $caption = this.HTMLElement.getElementsByTagName("SPAN")[0];

	var $extraTabBody = this.HTMLElement.insertBefore(document.createElement("DIV"),this.HTMLElement.childNodes[2]);
	$extraTabBody.className = "extra_tab_body";
	$extraTabBody.style[webcentric.FLOAT_STYLE] = "left";
    this.extraTabMain = $extraTabBody.appendChild(document.createElement("DIV"));
	this.extraTabMain.style.height = "18px";
    var $extraTabSpan = this.extraTabMain.appendChild(document.createElement("SPAN"));
	$extraTabSpan.innerHTML = "...";
	$extraTabSpan.style.color = "#000";
	$extraTabSpan.style.backgroundColor = "#E3D9DF";
	$extraTabSpan.style.marginLeft = "5px";
	$extraTabSpan.style.paddingLeft = "10px";
	$extraTabSpan.style.paddingRight = "10px";

	$extraTabBody.style.display = "none";


	this.setCaption = webcentric.HTMLHandle_drag.assignCaptionSetter($caption,$extraTabBody);

};

webcentric.HTMLHandle_drag.assignCaptionSetter = function(title,extraTabBody){
	return function(caption,draggingStack){
		title.innerHTML = "";
		title.appendChild(ViewFactory.getTextNode(caption));
		
		if (extraTabBody){		
			extraTabBody.style.display = draggingStack ? "" : "none";
		}
	};
};

//---------------------------------------------------------------------------------

/**
 *	The default fitrader tabstrip - extend the behaviour of the standard tabstrip view 
 */
caplin.webcentric.view.dom.decorator.HTMLTabstrip.prototype.buildMore = function(){
  	var $component = this.presentation.Component();
  	if ($component && $component.properties.colour){
  		this.HTMLElement.className = $component.properties.colour +  " " + this.HTMLElement.className;
  	}
};
 
caplin.webcentric.core.EventManager.raiseEvent({Name:"ScriptsLoaded"});