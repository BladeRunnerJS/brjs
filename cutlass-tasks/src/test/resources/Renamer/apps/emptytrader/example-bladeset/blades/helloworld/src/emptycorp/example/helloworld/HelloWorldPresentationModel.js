caplin.namespace("emptycorp.example.helloworld");

caplin.include("caplin.presenter.PresentationModel", true);

emptycorp.example.helloworld.HelloWorldPresentationModel = function()
{
	this.message = new caplin.presenter.property.Property( ct.i18n("emptycorp.example.helloworld.i18n.hello") + " World from Presenter Blade!" );
};
caplin.implement(emptycorp.example.helloworld.HelloWorldPresentationModel, caplin.presenter.PresentationModel);
