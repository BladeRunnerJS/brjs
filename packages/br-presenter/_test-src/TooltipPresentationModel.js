var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var Field = require('br-presenter/node/Field');
TooltipPresentationModel = function()
{
    this.theField = new Field(1234);
};
Core.extend(TooltipPresentationModel, PresentationModel);

module.exports = TooltipPresentationModel;
