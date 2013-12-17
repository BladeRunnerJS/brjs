br.component.SimpleFrame = function(component, width, height) {
	this.width = width;
	this.height = height;
	this.frameElement = document.createElement("div");
	this.frameElement.className = "component-frame simple";
	component.setDisplayFrame(this);
};
br.Core.extend(br.component.SimpleFrame, br.component.Frame);

br.component.SimpleFrame.prototype.setContent = function(contentElement) {
	this.frameElement.appendChild(contentElement);
};

br.component.SimpleFrame.prototype.getElement = function() {
	return this.frameElement;
};
