section = {}
section.app = {}
section.app.main1= function() {
	write("hello from section.app.main1 ");
	new section.fi.fi-blade1.app.blade2();
	new section.fi.fi-blade2.app.blade2();
	new section.fx.fx-blade1.app.blade1();
	new section.fx.fx-blade2.app.blade1();
}
new section.app.main1();