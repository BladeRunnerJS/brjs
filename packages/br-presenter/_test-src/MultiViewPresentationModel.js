var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var MultiSelectionField = require('br-presenter/node/MultiSelectionField');
var SelectionField = require('br-presenter/node/SelectionField');
MultiViewPresentationModel = function()
{
    this.selectionField = new SelectionField(['a','b', 'c'], "b");
    this.selectionField.controlName.setValue("aSelectionField");

    this.multiSelectField = new MultiSelectionField(["A","B","C"], ["A","C"]);
    this.multiSelectField.controlName.setValue("aMultiSelectionField");
};
Core.extend(MultiViewPresentationModel, PresentationModel);

module.exports = MultiViewPresentationModel;
