var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var SelectionField = require('br-presenter/node/SelectionField');
MultiViewClickPresentationModel = function()
{
    var pHobbies = ["Cooking", "Extreme Ironing"];
    this.hobbies = new SelectionField(pHobbies);
};
Core.extend(MultiViewClickPresentationModel, PresentationModel);

module.exports = MultiViewClickPresentationModel;
