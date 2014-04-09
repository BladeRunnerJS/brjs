
/**
 * @class
 * Allows instances of {@link br.presenter.component.PresenterComponent} to be constructed via XML snippets.
 * 
 * @constructor
 */
br.presenter.PresenterComponentFactory = function() {};

br.presenter.PresenterComponentFactory.prototype.createFromXml = br.presenter.component.PresenterComponent.deserialize;
