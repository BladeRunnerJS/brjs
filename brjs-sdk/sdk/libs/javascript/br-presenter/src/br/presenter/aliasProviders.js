
module.exports = {
	'br.presenter.tooltip-helper': function() {
		return require('br-presenter/control/tooltip/DefaultTooltipHelper');
	},
	'br.date-picker': function() {
		return require('br-presenter/control/datefield/JQueryDatePickerControl');
	},
	'br.toggle-switch': function() {
		return require('br-presenter/control/selectionfield/ToggleSwitchControl');
	},
	'br.autocomplete-box': function() {
		return require('br-presenter/control/selectionfield/JQueryAutoCompleteControl');
	},
	'br.tooltip': function() {
		return require('br-presenter/control/tooltip/TooltipControl');
	}
};
