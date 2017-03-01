var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var WritableProperty = require('br-presenter/property/WritableProperty');
var EditableProperty = require('br-presenter/property/EditableProperty');
OrderOfClickAndBlurPresentationModel = function()
{
    this.textInput = new EditableProperty("");
    this.storedText = new WritableProperty("");
};
Core.extend(OrderOfClickAndBlurPresentationModel, PresentationModel);

OrderOfClickAndBlurPresentationModel.prototype.storeCurrentText = function()
{
    this.storedText.setValue(this.textInput.getValue());
};

module.exports = OrderOfClickAndBlurPresentationModel;
