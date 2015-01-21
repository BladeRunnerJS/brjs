/**
 * @module br/presenter/node/TemplateAware
 */

/**
 * @class
 * @interface
 * @alias module:br/presenter/node/TemplateAware
 * 
 * @classdesc
 * Interface implemented by presentation nodes that need to dynamically specify the template that should
 * be used to render them.
 *
 * <p>By default, presentation nodes held within a list (e.g. using the {@link module:br/presenter/node/NodeList}
 * class) are all rendered using the same template &mdash; specified within the view. Sometimes, however,
 * it can be useful to render some or all of the list using different templates. Presentation nodes can
 * signal the need to specify the template dynamically by implementing the <code>TemplateAware</code>
 * interface, and by returning the name of the template that should be used to render the node when
 * {@link #getTemplateName} is invoked.</p>
 */
function TemplateAware() {
}

/**
 * Returns the name of the HTML template that will be used to render this presentation node
 * (must not be <code>null</code> or <code>undefined</code>).
 *
 * @type String
 */
TemplateAware.prototype.getTemplateName = function() {
	throw new br.Errors.UnimplementedInterfaceError("This method should be overridden.");
};

br.presenter.node.TemplateAware = TemplateAware;
