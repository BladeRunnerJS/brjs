caplin.namespace("emptycorp.a.helloworld");

caplin.include("caplin.presenter.PresentationModel", true);

emptycorp.a.helloworld.HelloWorldPresentationModel = function()
{
	this.message = new caplin.presenter.property.Property( ct.i18n("emptycorp.a.helloworld.i18n.hello") + " World from Presenter Blade!" );
};
caplin.implement(emptycorp.a.helloworld.HelloWorldPresentationModel, caplin.presenter.PresentationModel);
