var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var EditableProperty = require('br-presenter/property/EditableProperty');
var Field = require('br-presenter/node/Field');
FocusPresentationModel = function()
{
    this.inputA = new Field("Input A");
    this.inputB = new Field("Input B");
    
    this.inputA.hasFocus = new EditableProperty(false);
    this.inputB.hasFocus = new EditableProperty(false);
    
    this.inputA.hasFocus.setValue(false);
    this.inputB.hasFocus.setValue(false);
};
Core.extend(FocusPresentationModel, PresentationModel);

module.exports = FocusPresentationModel;
