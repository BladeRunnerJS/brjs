caplin.namespace("emptycorp.example.helloworld");

caplin.include("caplin.presenter.PresentationModel", true);

emptycorp.example.helloworld.HelloWorldWorkbenchPresentationModel = function()
{
	this.message = new caplin.presenter.property.Property( ct.i18n("emptycorp.example.helloworld.i18n.hello") + " World from Presenter Blade's workbench!" );
};
caplin.implement(emptycorp.example.helloworld.HelloWorldWorkbenchPresentationModel, caplin.presenter.PresentationModel);
