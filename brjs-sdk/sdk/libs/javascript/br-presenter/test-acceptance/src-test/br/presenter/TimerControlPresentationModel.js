var PresentationModel = require("br/presenter/PresentationModel");
var Core = require("br/Core");
var EditableProperty = require("br/presenter/property/EditableProperty");
TimerControlPresentationModel = function()
{
    this.counter = new EditableProperty(0);
    this.status = new EditableProperty("Up");
    this.name = new EditableProperty("Caplin");

};
Core.extend(TimerControlPresentationModel, PresentationModel);

/*
 * Set interval not fully supported
 */
TimerControlPresentationModel.prototype.addOneToCounterOnceASecond = function()
{
    var self = this;
    var addOne = function(){
        self.counter.setValue(self.counter.getValue() + 1);
    }
    window.setInterval(addOne, 1000);
};

TimerControlPresentationModel.prototype.addOneToCounterAfterOneSecond = function()
{
    var self = this;
    window.setTimeout(function(){
        self.counter.setValue(self.counter.getValue() + 1);
    }, 1000);
}

TimerControlPresentationModel.prototype.changeStatusToDownAfter5Seconds = function()
{
    var self = this;
    window.setTimeout(function(){
        self.status.setValue("Down");
    }, 5000);
};

TimerControlPresentationModel.prototype.addTheLetterAtoNameAfter1Minute = function()
{
    var self = this;
    window.setTimeout(function(){
        self.name.setValue(self.name.getValue() + "A");
    }, 60000);
};

TimerControlPresentationModel.prototype.addTheLetterBtoNameAfter5Minutes = function()
{
    var self = this;
    window.setTimeout(function(){
        self.name.setValue(self.name.getValue() + "B");
    }, 300000);
};

TimerControlPresentationModel.prototype.addTheLetterCtoNameAfter10Minutes = function()
{
    var self = this;
    window.setTimeout(function(){
        self.name.setValue(self.name.getValue() + "C");
    }, 600000);
};

TimerControlPresentationModel.prototype.addsTheLetterAafter1MinuteThenBafter2Minutes = function()
{
    var self = this;
    window.setTimeout(function(){
        self.name.setValue(self.name.getValue() + "B");
    }, 120000);
    window.setTimeout(function(){
        self.name.setValue(self.name.getValue() + "A");
    }, 60000);
};

module.exports = TimerControlPresentationModel;
