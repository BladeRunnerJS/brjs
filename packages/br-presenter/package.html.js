'use strict';

/**
 * @module br/presenter
 * 
 */
 
 /*
 * @class
 * @alias module:br/presenter/PresentationModel
 *
 * @classdesc
 *  The Presenter library enables developers to create screen views using standard HTML
 *  templates, and then bind these to business logic that is defined within object-oriented
 *  JavaScript classes. This allows a <em>separation of concerns</em>, where UX/UI designers
 *  create HTML appropriately styled with CSS, which JavaScript developers are then
 *  responsible for enhancing with dynamic behaviour.
 * </p>
 * <p>
 *  To achieve this separation of concerns, the Presenter library implements the 
 *  <a href="http://martinfowler.com/eaaDev/PresentationModel.html">Presentation Model
 *  pattern</a> [Martin Fowler 2007] &mdash; sometimes also known
 *  as <a href="http://en.wikipedia.org/wiki/Model_View_ViewModel">MVVM</a> (Model View View
 *  Model).
 * </p>
 * 
 * <h3>The View </h3> 
 * <p>
 *  Presenter embeds the <a href="http://knockoutjs.com/">Knockout</a> library for HTML
 *  to JavaScript binding. A simple example of a binding is as follows:
 * </p>
 * <pre>
 *  &lt;input type="text" name="firstName" data-bind="value:firstName"/&gt;
 * </pre>
 * <p>
 *  This creates a two-way binding between the input element and a JavaScript property
 *  (<code>firstName</code>) contained within the presentation model. All view bindings
 *  are defined using similar <code>data-bind</code> attributes. The <code>data-</code>
 *  prefix is defined in the HTML5 specification to indicate a custom attribute that is
 *  valid HTML, but which is ignored by the browser.
 * </p>
 * <p>
 *  Presenter enhances <em>Knockout</em>, allowing it to be embedded within web
 *  applications. It makes it easy to create standard components built using
 *  a presentation model and an HTML view (via
 *  {@link module:br/presenter/component/PresenterComponent}), and it makes it easy for
 *  these components to further embed any other components within themselves.
 * </p>
 * 
 * <p>
 *  Presenter also supports nested HTML templates which allows parts of views to be 
 *  reused (avoiding repetition) and enhancing modularity.
 * </p>
 * 
 * <h3>The Presentation Model </h3> 
 * <p>
 *  The presentation model is a logical representation of a component's view on screen
 *  (the view). Everything on the screen is represented within the presentation model
 *  using <em>properties</em>. The presenter library binds these <em>properties</em>
 *  (instances of {@link module:br/presenter/property/Property}) to the view. Assuming
 *  the binding is correct then the GUI is tested by unit testing the presentation
 *  model. 
 * </p>
 * <p>
 *  This is extremely important since it allows fast, reliable unit testing,
 *  enabling TDD. The alternative is to test via the GUI (using a tool like Selenium)
 *  which is much slower and results in fragile tests.
 * </p>
 * @package
 * @BladeRunner
 */