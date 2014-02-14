
/**
 * @class
 * Allows instances of {@link br.presenter.component.PresenterComponent} to be constructed via XML snippets.
 * 
 * @constructor
 */
br.presenter.PresenterComponentFactory = function() {};

br.presenter.PresenterComponentFactory.prototype.createFromXml = br.presenter.component.PresenterComponent.deserialize;

function onlyUsedToEnsureBundlerOrder_untilWeHaveTheNewBundler_doesNotActuallyRun() {
	br.Core.extend(br.presenter.PresenterComponentFactory, br.presenter.component.PresenterComponent);
}
