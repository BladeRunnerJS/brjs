var Validator = require('br-presenter/validator/Validator');
var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var ErrorMonitor = require('br-presenter/util/ErrorMonitor');
var ToolTipNode = require('br-presenter/node/ToolTipNode');
var ToolTipField = require('br-presenter/node/ToolTipField');
TooltipControlPresentationModel = function()
{
    this.tooltipField1 = new ToolTipField("");
    this.tooltipField2 = new ToolTipField("");
    
    this.tooltipField1.value.addValidator(new TooltipControlPresentationModel.aValidator());
    this.tooltipField2.value.addValidator(new TooltipControlPresentationModel.anotherValidator());
    
    this.tooltipNode = new ToolTipNode();
    this.errorMonitor = new ErrorMonitor(this.tooltipNode);
    this.errorMonitor.monitorField(this.tooltipField1);
    this.errorMonitor.monitorField(this.tooltipField2);
};

Core.extend(TooltipControlPresentationModel, PresentationModel);

TooltipControlPresentationModel.aValidator = function()
{
};

Core.implement(TooltipControlPresentationModel.aValidator, Validator);

TooltipControlPresentationModel.aValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
    oValidationResult.setResult (vValue == 1, "ERR" );
    return;
};

TooltipControlPresentationModel.anotherValidator = function()
{
};

Core.implement(TooltipControlPresentationModel.anotherValidator, Validator);

TooltipControlPresentationModel.anotherValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
    oValidationResult.setResult (vValue == 0, "ANOTHERERR" );
    return;
};

module.exports = TooltipControlPresentationModel;
