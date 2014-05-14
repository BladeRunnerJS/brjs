var Emitter = require('emitr');
var Errors = require('br/Errors');

/**
 * @name br.component.Frame
 * @beta
 * @class
 * Instances of <code>Frame</code> are used to wrap components before they are displayed on the
 * screen. They are created automatically by whatever code is controlling the view and passed
 * in to the component that they are displaying.
 *
 * Frame is an abstract class. The specific implementations will interface with some sort of
 * layout manager.
 *
 * @constructor
 */
function Frame() {
	/**
	 * The width in pixels of this frame.  Will be null initially.
	 * @var {number?} width
	 */
	this.width = null;

	/**
	 * The height in pixels of this frame.  Will be null initially.
	 * @var {number?} height
	 */
	this.height = null;

	/**
	 * True if this Frame has focus, false otherwise.
	 * @var {boolean} isFocused
	 */
	this.isFocused = false;

	/**
	 * True if the content of this frame is visible, false otherwise.
	 * @var {boolean} isContentVisible
	 */
	this.isContentVisible = false;

	/**
	 * The current state of this frame.
	 *
	 * One of Frame.NOT_ATTACHED (initially), Frame.MINIMIZED, Frame.MAXIMIZED,
	 * Frame.NORMAL, Frame.CLOSED.
	 *
	 * Every change of state should be accompanied by one of the specified events.
	 */
	this.state = Frame.NOT_ATTACHED;
}

Emitter.mixInto(Frame);

Frame.NOT_ATTACHED = "not attached";
Frame.MINIMIZED = "minimized";
Frame.MAXIMIZED = "maximized";
Frame.NORMAL = "normal";
Frame.CLOSED = "closed";

Frame.EVENTS = ["attach", "close", "resize", "minimize", "maximize", "restore", "show", "hide", "focus", "blur"];

/**
 * Allows the component to set an html element that contains the dom content of this component.
 *
 * May only be called once.
 *
 * @param contentElement
 */
Frame.prototype.setContent = function(contentElement) {
	throw new Errors.UnimplementedAbstractMethodError("Frame.setContent: Implementations of Frame need to provide a setContent method that the component can use to provide a dom elements that should be displayed.");
};

/**
 * Sets the title of this frame.
 *
 * <p>Implementations of Frame for layout managers that display the title should override this
 * method.
 */
Frame.prototype.setTitle = function(title) {
	this.title = title;
};

/**
 * Allows the component to request that it be given a particular amount of room.  This is only
 * a request, if the frame is resized in response to this method, then a resize event will be
 * fired.
 *
 * <p>Implementations of Frame for layout managers that can allow visual components to be
 * resized should override this method.
 */
Frame.prototype.setPreferredSize = function(width, height) {};

/**
 * Allows the component to indicate that state that it would serialize has changed.
 *
 * <p>Implementations of Frame for layout managers that can persist state should override this
 * method.
 */
Frame.prototype.setComponentModified = function() {};

/**
 * This is a method for the component to call if it wishes to be closed.
 *
 * <p>Implementations of Frame for layout managers that are prepared to remove panels should
 * override this method and ensure that they fire the appropriate events.
 *
 * <p>If a frame is closed in response to this method, then a close event will be fired.
 */
Frame.prototype.close = function() {};

/**
 * This event must be raised after the content element has been attached to the DOM.
 *
 * <p>The state must be updated to one of HIDDEN, MINIMIZED, MAXIMIZED or NORMAL before this
 * event is fired, and width and height should be set.
 *
 * <p>This event will only be fired once.
 *
 * <p>It allows the component to do any calculations that need to take sizes into account.
 *
 * @event Frame#attach
 */

/**
 * This event must be raised when the component is closed and will not be used again.
 *
 * <p>It should be raised after the frame has been removed from the DOM. If the content of the
 * frame is visible at the time of close, it should be preceded by a hide event.
 *
 * <p>The state must be updated to CLOSED after this event has fired.
 *
 * <p>This event will only be fired once.
 *
 * <p>It allows the component to do cleanup.
 *
 * @event Frame#close
 */

/**
 * This event should be raised whenever the component is resized.  A component becoming invisible
 * is not a resize.
 *
 * <p>If the resize is due to a minimize (if the minimized form has visible content), maximise
 * or restore, it should be fired after that event.
 *
 * <p>Frame.width and Frame.height should be set to the new values before the event is raised.
 *
 * @event Frame#resize
 */

/**
 * This event should be raised whenever the contents of the frame are becoming visible after
 * previously not being visible.
 *
 * <p>It should be raised immediately after an attach event if the attach occurs somewhere that
 * should be visible.  If in its minimized form the contents of the frame are not visible, then
 * when the frame is restored or maximised, then a show event should be raised after the restore
 * or maximise event.
 *
 * <p>If a component is resized prior to becoming visible, the show event should occur after the
 * resize.
 *
 * <p>For example - a component is normal size, then minimized so that the content becomes
 * invisible.  The sequence of events is:
 *
 * 	<ol>	<li>minimize</li>	<li>hide</li>	</ol>
 *
 * <p>Then, the component is maximised, making the content visible, but also changing its size:
 * 	<ol>	<li>maximize</li>	<li>resize</li>	<li>show</li>	</ol>
 *
 * <p>The state must be updated to one of MINIMIZED, MAXIMIZED or NORMAL before this
 * event is fired, and width and height should be set.  isContentVisible must be set to true
 * before this event is fired.
 *
 * <p>It allows the component to resume or start long running resources.
 *
 * @event Frame#show
 */

/**
 * This event should be raised if the contents of this frame are no longer visible after
 * previously being visible.
 *
 * <p>It should be raised immediately before a close event if the contents were visible before
 * closing.  If in its minimized form the content is not visible, it should be raised immediately
 * after a minimize event.
 *
 * <p>Width and height should be set, and isContentVisible must be set to false before this event
 * is fired.
 *
 * <p>It allows the component to pause long running resources.
 *
 * @event Frame#hide
 */

/**
 * This event should be raised if the layout manager supports minimize/maximize/restore and the
 * this frame has been minimized.
 *
 * <p>If minimizing the frame causes the content to no longer be visible, a hide must be fired
 * afterwards.  If minimizing causes the content to change size, a resize event should be fired
 * afterwards.
 *
 * <p> The state must be updated to MINIMIZED before this event is fired.  If the content is no
 * longer visible, isContentVisible should be set before this event is fired.
 *
 * @event Frame#minimize
 */

/**
 * This event should be raised if the layout manager supports minimize/maximize/restore and
 * this frame has been maximized.
 *
 * <p>If maximizing the frame causes the content to become newly visible, a show must be fired
 * afterwards.  If maximizing causes the content to change size, a resize event should be fired
 * afterwards.
 *
 * <p>The state must be updated to MAXIMIZED before this event is fired.
 *
 * @event Frame#maximize
 */

/**
 * This event should be raised if the layout manager supports minimize/maximize/restore and the
 * this frame has been restored.
 *
 * <p>If restoring the frame causes the content to become newly visible, a show must be fired
 * afterwards.  If restoring causes the content to change size, a resize event should be fired
 * afterwards.
 *
 * <p>The state must be updated to NORMAL before this event is fired.  If the content is newly
 * visible, isContentVisible should be set before this event is fired.
 *
 * @event Frame#restore
 */

/**
 * This event should be raised if the layout manager supports focusing/activating a component
 * and this frame has been activated.
 *
 * <p>isFocused should be updated before this event is fired.
 *
 * @event Frame#focus
 */

/**
 * This event should be raised if the layout manager supports focusing/activating a component
 * and this frame has been inactivated.
 *
 * <p>isFocused should be updated before this event is fired.
 *
 * @event Frame#blur
 */

module.exports = Frame;
