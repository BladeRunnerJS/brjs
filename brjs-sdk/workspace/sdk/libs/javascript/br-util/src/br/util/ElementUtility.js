'use strict';

/**
* @module br/util/ElementUtility
*/

var Errors = require('br/Errors');

/**
 * @alias module:br/util/ElementUtility
 * @classdesc
 * This class provides static, browser agnostic, utility methods for DOM interactions such as adding / removing event
 *  listeners, adjusting CSS classes, finding element positions etc.
 */
function ElementUtility() {
}

/** @private */
ElementUtility.m_eDisposalElem = null;

/** @private */
ElementUtility.m_bIsFirefox = undefined;

/** @private */
ElementUtility.m_bIsIe = undefined;

/**
 * Returns TRUE if the specified class name exists on the element.
 * @param {Element} element The DOMElement to check.
 * @param {String} className The class name to check.
 *
 * @returns {boolean} TRUE if the specified class name exists on the element.
 */
ElementUtility.hasClassName = function(element, className) {
	var classMatcher = ElementUtility._getClassNameRegExFor(className);
	return classMatcher.test(' '  + element.className + ' ' );
};

/**
 * Adds the specified class name to the list of CSS classes on the given element, if the class does not already exist.
 *
 * @param {Element} element The HTML DOM element to add the CSS class to.
 * @param {String} className The class name that will be added to the list of existing classes.
 */
ElementUtility.addClassName = function(element, className) {
	var elementClassName = element.className;
	var space = (elementClassName === '') ? '' : ' ';
	var classMatcher = ElementUtility._getClassNameRegExFor(className);

	if (!elementClassName.match(classMatcher)) {
		element.className = elementClassName + space + className;
	}
};

/**
 * Removes the CSS class name from the specified element.
 *
 * @param {Element} element The HTML element that the class name should be removed from.
 * @param {String} className The CSS class to remove from the element.
 */
ElementUtility.removeClassName = function(element, className) {
	ElementUtility.replaceClassName(element, className, '');
};

/**
 * Replaces the specified CSS class name on the DOM element with another class.
 *
 * @param {Element} element The HTML DOM element to add the class to.
 * @param {String} currentClassName The class name to replace.
 * @param {String} newClassName The new name of the class.
 */
ElementUtility.replaceClassName = function(element, currentClassName, newClassName) {
	var trimmedNewClassName = newClassName.trim();
	var replacableClassName = trimmedNewClassName.length == 0 ? ' ' : ' ' + trimmedNewClassName + ' ';
	var classMatcher = ElementUtility._getClassNameRegExFor(currentClassName);
	element.className = (element.className.replace(classMatcher, replacableClassName)).trim();
};

/**
 * Adds and/or removes the specified class names from the specified element. This operation is performed in a single
 *  DOM action, making this more efficient than adding/removing the classes individually. If a class exists in both the
 *  add and remove lists, the class will be added to the element.
 *
 * @param {Element} element The HTML DOM element to make the class changes to.
 * @param {String[]} classesToAdd The list of class names that will be added to the list of existing classes.
 * @param {String[]} classesToRemove The list of class names that will be removed from the list of existing classes.
 */
ElementUtility.addAndRemoveClassNames = function(element, classesToAdd, classesToRemove) {
	var originalClassName = element.className;
	var newClassName = originalClassName;

	if (classesToRemove && classesToRemove.length) {
		for (var idx = 0, len = classesToRemove.length; idx < len; idx++) {
			newClassName = newClassName.replace(ElementUtility._getClassNameRegExFor(classesToRemove[idx]), ' ');
		}
		newClassName = newClassName.trim();
	}

	if (classesToAdd && classesToAdd.length) {
		for (var idx = 0, len = classesToAdd.length; idx < len; idx++) {
			var classToAdd = classesToAdd[idx];
			var space = (newClassName === '') ? '' : ' ';
			var classMatcher = ElementUtility._getClassNameRegExFor(classToAdd);

			if (!newClassName.match(classMatcher)) {
				newClassName = newClassName + space + classToAdd;
			}
		}
	}

	if (newClassName !== originalClassName) {
		element.className = newClassName;
	}
};

/**
 * Returns an array of DOM elements that match the specified tag name and class name.
 *
 * @param {DOMElement} domElement The DOM element that should be used as the root of the search.
 * @param {String} tagName The tag name (can be *) of elements to search for.
 * @param {String} className The CSS class name of the elements to search for.
 *
 * @returns {DOMElement[]} An array of elements that match the specified criteria.
 */
ElementUtility.getElementsByClassName = function(domElement, tagName, className) {
	var elements = (tagName === '*' && domElement.all)? domElement.all : domElement.getElementsByTagName(tagName);
	var returnElements = [];

	className = className.replace(/\-/g, "\\-");
	var regExpObj = new RegExp("(^|\\s)" + className + "(\\s|$)");

	for (var idx = 0, len = elements.length; idx < len; idx++) {
		var oElement = elements[idx];
		if (regExpObj.test(oElement.className)) {
			returnElements.push(oElement);

		}
	}

	return returnElements;

};

/**
 * Returns the first element that contains the given class as part of its <code>className</code> string.
 *
 * @param {Element} element the element to start the search at.
 * @param {String} className the class name to look for.
 *
 * @returns {DOMElement} The ancestor element with the specified class, or null.
 */
ElementUtility.getAncestorElementWithClass = function(element, className) {
	var classMatcher = new RegExp("(^| )" + className + "($| )");

	while (!element.className || !element.className.match(classMatcher)) {
		if (!element.parentNode) {
			return null;
		}

		element = element.parentNode;
	}

	return element;
};

/**
 * Returns the node index of the element as it exists in the parent.
 *
 * @param {Element} element The element to get the index for.
 *
 * @type int
 * @returns the node index
 * @throws Error If the specified element does not have a parent.
 */
ElementUtility.getNodeIndex = function(element) {
	if (!element) {
		throw new Errors.IllegalStateError('element should be passed as a paramter');
	}

	var siblings = element.parentNode.childNodes;

	var rowIndex = 0;
	for (var idx = 0, len = siblings.length; idx < len; idx++) {
		var sibling = siblings[idx];

		if (sibling == element) {
			return rowIndex;
		}

		if (sibling.nodeType == 1) {
			rowIndex++;
		}
	}

	// this should never happen
	throw new Errors.IllegalStateError('Element could not be found within its parent node');
};

/**
 * Inserts the specified node immediately after the reference element.
 *
 * <p>This convenience method saves the programmer from having to determine whether to call <code>insertBefore()</code>
 *  or <code>appendChild()</code>, depending on whether the reference element is the last child node.</p>
 *
 * @param {Element} element The element to insert.
 * @param {Element} referenceElement The reference element to insert the element after.
 */
ElementUtility.insertAfter = function(element, referenceElement) {
	var parentElement = referenceElement.parentNode;

	if (referenceElement == parentElement.lastChild) {
		parentElement.appendChild(element);
	} else {
		parentElement.insertBefore(element, referenceElement.nextSibling);
	}
};

/**
 * Checks to see if the specified ancestor element contains the specified child element.
 *
 * @param {Element} possibleAncestor the element that is presumed to be a parent node.
 * @param {Element} possibleChildElement the element to start the search at.
 *
 * @returns {boolean} TRUE if the specified ancestor element contains the child element.
 */
ElementUtility.isAncestorOfElement = function(possibleAncestor, possibleChildElement) {
	while (possibleChildElement && possibleAncestor) {
		if (possibleChildElement === possibleAncestor) {
			return true;
		}

		possibleChildElement = possibleChildElement.parentNode;
	}

	return false;
};

/**
 * Sets the innerHTML of the specified element in an efficient way.
 *
 * @param {Element} element the element on which <code>innerHTML</code> needs to be set.
 * @param {String} htmlToSet The HTML that will be set in the element.
 */
ElementUtility.setInnerHtml = function(element, htmlToSet) {
	// See http://blog.stevenlevithan.com/archives/faster-than-innerhtml
	if (ElementUtility._isFirefox() && element.firstChild && element.parentNode) {
		var eNewElement = element.cloneNode(false);
		eNewElement.innerHTML = htmlToSet;
		element.parentNode.replaceChild(eNewElement, element);
		return eNewElement;
	} else {
		// this is not the firefox browser
		// or this is the first time we've set the html
		// or the element has no parent (happends in TreeView)
		element.innerHTML = htmlToSet;
		return element;
	}
};

/**
 * Removes the specified child from its parent.
 *
 * @param {Element} childElement The child to remove.
 */
ElementUtility.removeChild = function(childElement) {
	// see http://outofhanwell.com/ieleak/index.php?title=Fixing_Leaks
	if (!ElementUtility.isIE()) { // if this is not ie then do it the normal way
		childElement.parentNode.removeChild(childElement);
	} else { // lazy initialize the disposal element where the work will be done
		if (!ElementUtility.m_eDisposalElem) {
			ElementUtility.m_eDisposalElem = document.createElement('div');
			ElementUtility.m_eDisposalElem.style.display = 'none';

			document.body.appendChild(ElementUtility.m_eDisposalElem);
		}

		// TODO consider replacing with a blank TextNode instead
		// TODO do this on an interval, so high volume is handled
		ElementUtility.m_eDisposalElem.appendChild(childElement);
		ElementUtility.m_eDisposalElem.innerHTML = '';
	}
};

/**
 * Discards one or more elements (all arguments passed will be discarded) by removing children, and removing it from
 *  any parentNode.
 *
 * @param {Element} firstElement First Element DOM Element
 */
ElementUtility.discardChild = function(firstElement) {
	var idx, elem;

	for (idx = 0; elem = arguments[idx]; idx++) {
		if (ElementUtility._isFirefox()) {
			while(elem.lastChild) elem.removeChild(elem.lastChild);
		} else {
			elem.innerHTML = "";
		}

		if (elem.parentNode) {
			ElementUtility.removeChild(elem);
		}
	}
};

/**
 * Returns the absolute position of the element relative to the window in pixels.
 * The position also takes into account any scrolling of parents.
 *
 * @param {DOMElement} elem The DOM element to calculate the position of
 * @type Object
 * @return \{left:x,top:y\}
 */
ElementUtility.getPosition = function(elem) {
	var offsetTrail = elem;
	var offsetLeft = 0;
	var offsetTop = 0;
	while (offsetTrail) {
		offsetLeft += offsetTrail.offsetLeft;
		offsetTop += offsetTrail.offsetTop;
		offsetTrail = offsetTrail.offsetParent;
	}
	var scroll = ElementUtility.getScrollOffset(elem);
	return {left:(offsetLeft - scroll.left), top:(offsetTop - scroll.top)};
};

/**
 * Returns the bounding rectangle of the specified element.
 *
 * @param {Object} elem The element to get the bounding rectangle for.
 * @type Object
 * @returns \{left:x,right:y\}
 */
ElementUtility.getSize = function(elem) {
	// Firefox 2 does not support getBoundingClientRect so this method abstracts out the box size calculations for figuring out if a column
	// is visible or not. If getBoundingClientRect is not supported we calculate the values using offsetParent instead.
	if (elem.getBoundingClientRect) {
		return elem.getBoundingClientRect();
	} else {
		var left = 0;
		var right = 0;
		var offsetWidth = null;

		if (elem.offsetParent) {
			do {
				left += elem.offsetLeft;
				offsetWidth = offsetWidth ? offsetWidth : elem.offsetWidth;
			} while (elem = elem.offsetParent);

			right = offsetWidth + left;
		}

		return {left: left, right: right};
	}
};

/**
 * Returns the scrolled offset of the element (if any) in an object containing a <tt>left</tt> and <tt>top</tt>
 *  properties.
 *
 * @param {Element} elem The DOM element to calculate the scrolled offset of.
 * @returns {Object} \{left:x,top:y\}
 */
ElementUtility.getScrollOffset = function(elem) {
	var offsetTrail = elem;
	var offsetLeft = 0;
	var offsetTop = 0;
	while (offsetTrail &&  !isNaN(offsetTrail.scrollTop)) {
		offsetLeft += offsetTrail.scrollLeft;
		offsetTop += offsetTrail.scrollTop;
		offsetTrail = offsetTrail.parentNode;
	}
	return {left: offsetLeft, top: offsetTop};
};

/** @private */
ElementUtility._getClassNameRegExFor = function(className) {
	var sPatternForTheOnlyWord = "^" + className + "$";
	var sPatternForFirstWord = "^" + className + "\\s";
	var sPatternForInBetweenWords = "\\s" + className + "\\s";
	var sPatternForLastWord = "\\s" + className + "$";
	return new RegExp(sPatternForTheOnlyWord + '|' + sPatternForFirstWord + '|' + sPatternForInBetweenWords + '|' + sPatternForLastWord, "g");
};

/** @private */
ElementUtility._isFirefox = function() {
	if (ElementUtility.m_bIsFirefox === undefined) {
		ElementUtility.m_bIsFirefox = navigator.userAgent.match('Firefox');
	}
	return ElementUtility.m_bIsFirefox;
};

/** @private */
ElementUtility.isIE = function() {
	if (ElementUtility.m_bIsIe === undefined) {
		ElementUtility.m_bIsIe = navigator.userAgent.match('MSIE');
	}
	return ElementUtility.m_bIsIe;
};

module.exports = ElementUtility;
