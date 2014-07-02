var Errors = require("br/Errors");

/**
 * @class 
 * This class provides static, browser agnostic, utility methods for DOM interactions such as adding / removing event listeners,
 * adjusting CSS classes, finding element positions etc.
 */
br.util.ElementUtility = function()
{
};

/**
 * @private
 */
br.util.ElementUtility.m_eDisposalElem = null;

/**
 * @private
 */
br.util.ElementUtility.m_bIsFirefox = undefined;

/**
 * @private
 */
br.util.ElementUtility.m_bIsIe = undefined;

/**
 * Returns TRUE if the specified class name exists on the element.
 * @param {Element} eElement The DOMElement to check.
 * @param {String} sClass The class name to check.
 * 
 * @type boolean
 * @returns TRUE if the specified class name exists on the element.
 */
br.util.ElementUtility.hasClassName = function(eElement, sClassName)
{
	var oClassMatcher = br.util.ElementUtility._getClassNameRegExFor(sClassName);
	return oClassMatcher.test(" " + eElement.className + " ");
};

/**
 * Adds the specified class name to the list of CSS classes on the given element,
 *  if the class does not already exist.
 *
 * @param {Element} eElement The HTML DOM element to add the CSS class to.
 * @param {String} sClass The class name that will be added to the list of existing classes.
 */
br.util.ElementUtility.addClassName = function(eElement, sClass)
{
	var sClassName = eElement.className;
	var sSpace = (sClassName=="") ? "" : " ";
	var oClassMatcher = br.util.ElementUtility._getClassNameRegExFor(sClass);
	if (!sClassName.match(oClassMatcher))
	{
		eElement.className = sClassName + sSpace + sClass;
	}
};

/**
 * Removes the CSS class name from the specified element.
 *
 * @param {Element} eElement The HTML element that the class name should be removed from.
 * @param {String} sClass The CSS class to remove from the element.
 */
br.util.ElementUtility.removeClassName = function(eElement, sClass)
{
	br.util.ElementUtility.replaceClassName(eElement, sClass, "");	
};

/**
 * Replaces the specified CSS class name on the DOM element with another class.
 *
 * @param {Element} eElement The HTML DOM element to add the class to.
 * @param {String} sCurrentClass The class name to replace.
 * @param {String} sNewClass The new name of the class.
 */
br.util.ElementUtility.replaceClassName = function(eElement, sCurrentClass, sNewClass)
{
	var sTrimmedNewClass = (sNewClass.trim());
	var sReplacableClassName = sTrimmedNewClass.length == 0 ? ' ' : ' ' + sTrimmedNewClass + ' ';
	var oClassMatcher = br.util.ElementUtility._getClassNameRegExFor(sCurrentClass); 
	eElement.className = (eElement.className.replace(oClassMatcher, sReplacableClassName)).trim();	
};

/**
 * Adds and/or removes the specified class names from the specified element. This operation is performed 
 * in a single DOM action, making this more efficient than adding/removing the classes individually. If a
 * class exists in both the add and remove lists, the class will be added to the element.
 *
 * @param {Element} eElement The HTML DOM element to make the class changes to.
 * @param {String[]} pClassesToAdd The list of class names that will be added to the list of existing classes.
 * @param {String[]} pClassesToRemove The list of class names that will be removed from the list of existing classes.
 */
br.util.ElementUtility.addAndRemoveClassNames = function(eElement, pClassesToAdd, pClassesToRemove)
{
	var sOriginalClassName = eElement.className;
	var sNewClassName = sOriginalClassName;
	if (pClassesToRemove && pClassesToRemove.length)
	{
		for(var i = 0, l = pClassesToRemove.length; i < l; i++)
		{
			sNewClassName = sNewClassName.replace(br.util.ElementUtility._getClassNameRegExFor(pClassesToRemove[i])," ");
		}
		sNewClassName = (sNewClassName.trim());
	}
	if (pClassesToAdd && pClassesToAdd.length)
	{
		for (i = 0, l = pClassesToAdd.length; i < l; i++) 
		{
			var sClassToAdd = pClassesToAdd[i];
			var sSpace = (sNewClassName == "") ? "" : " ";
			var oClassMatcher = br.util.ElementUtility._getClassNameRegExFor(sClassToAdd);

			if (!sNewClassName.match(oClassMatcher))
			{
				sNewClassName = sNewClassName + sSpace + sClassToAdd;
			}
		}
	}
	if (sNewClassName !== sOriginalClassName)
	{
		eElement.className = sNewClassName;
	}
};

/**
 * Returns an array of DOM elements that match the specified tag name and class name.
 *
 * @param {DOMElement} eDomElement The DOM element that should be used as the root of the search.
 * @param {String} sTagName The tag name (can be *) of elements to search for.
 * @param {String} sClassName The CSS class name of the elements to search for.
 * 
 * @type DOMElement[]
 * @returns An array of elements that match the specified criteria.
 */
br.util.ElementUtility.getElementsByClassName = function(eDomElement, sTagName, sClassName)
{
	var pElements = (sTagName == "*" && eDomElement.all)? eDomElement.all : eDomElement.getElementsByTagName(sTagName);
	var pReturnElements = [];
	sClassName = sClassName.replace(/\-/g, "\\-");
	var oRegExp = new RegExp("(^|\\s)" + sClassName + "(\\s|$)");

	for (var i = 0, l = pElements.length; i < l; i++)
	{
		var oElement = pElements[i];
		if (oRegExp.test(oElement.className))
		{
			pReturnElements.push(oElement);
		}
	}

	return pReturnElements;
};

/**
 * Returns the first element that contains the given class as part of its <code>className</code> string.
 * 
 * @param {Element} eElement the element to start the search at.
 * @param {String} sClassName the class name to look for.
 * 
 * @type DOMElement
 * @returns The ancestor element with the specified class, or null. 
 */
br.util.ElementUtility.getAncestorElementWithClass = function(eElement, sClassName)
{
	var rClassMatcher = new RegExp("(^| )" + sClassName + "($| )");
	
	while (!eElement.className || !eElement.className.match(rClassMatcher))
	{
		if (!eElement.parentNode)
		{
			return null;
		}
		
		eElement = eElement.parentNode;
	}
	
	return eElement;
};

/**
 * Returns the node index of the element as it exists in the parent.
 *
 * @param {Element} eElement The element to get the index for.
 * 
 * @type int
 * @returns the node index
 * @throws Error If the specified element does not have a parent.
 */
br.util.ElementUtility.getNodeIndex = function(eElement)
{
	if (!eElement)
	{
		throw new Errors.IllegalStateError("eElement should be passed as a paramter");
	}
	var pSiblings = eElement.parentNode.childNodes;

	var nRowIndex = 0;
	for (var i = 0, l = pSiblings.length; i < l; ++i)
	{
		var eSibling = pSiblings[i];

		if (eSibling == eElement)
		{
			return nRowIndex;
		}

		if (eSibling.nodeType == 1)
		{
			nRowIndex++;
		}
	}

	// this should never happen
	throw new Errors.IllegalStateError("Element could not be found within its parent node");
};

/**
 * Inserts the specified node immediately after the reference element.
 * 
 * <p>This convenience method saves the programmer from having to determine whether to call <code>insertBefore()</code> or
 * <code>appendChild()</code>, depending on whether the reference element is the last child node.</p>
 * 
 * @param {Element} eElement The element to insert
 * @param {Element} eReferenceElement The reference element to insert the element after.
 */
br.util.ElementUtility.insertAfter = function(eElement, eReferenceElement)
{
	var eParentElement = eReferenceElement.parentNode;
	
	if (eReferenceElement == eParentElement.lastChild)
	{
		eParentElement.appendChild(eElement);
	}
	else
	{
		eParentElement.insertBefore(eElement, eReferenceElement.nextSibling);
	}
};

/**
 * Checks to see if the specified ancestor element contains the specified child element.
 * 
 *  @param {Element} ePossibleAncestor the element that is presumed to be a parent node.
 *  @param {Element} ePossibleChildElement the element to start the search at.
 *  
 *  @type boolean
 *  @returns TRUE if the specified ancestor element contains the child element.
 */
br.util.ElementUtility.isAncestorOfElement = function(ePossibleAncestor, ePossibleChildElement)
{
	while (ePossibleChildElement && ePossibleAncestor)
	{
		if (ePossibleChildElement === ePossibleAncestor)
		{
			return true;
		}
		ePossibleChildElement = ePossibleChildElement.parentNode;
	}
	
	return false;
};

/**
 * Sets the innerHTML of the specified element in an efficient way.
 *
 * @param {Element} eElement the element on which <code>innerHTML</code> needs to be set
 * @param {String} sHtml The HTML that will be set in the element.
 */
br.util.ElementUtility.setInnerHtml = function(eElement, sHtml)
{
	// See http://blog.stevenlevithan.com/archives/faster-than-innerhtml
	if (br.util.ElementUtility._isFirefox() && eElement.firstChild && eElement.parentNode)
	{
		var eNewElement = eElement.cloneNode(false);
		eNewElement.innerHTML = sHtml;
		eElement.parentNode.replaceChild(eNewElement, eElement);
		return eNewElement;
	}
	else
	{
		// this is not the firefox browser
		// or this is the first time we've set the html
		// or the element has no parent (happends in TreeView)
		eElement.innerHTML = sHtml;
		return eElement;
	}
};

/**
 * Removes the specified child from its parent.
 * 
 * @param {Element} eChildElement The child to remove.
 */
br.util.ElementUtility.removeChild = function(eChildElement)
{
	// see http://outofhanwell.com/ieleak/index.php?title=Fixing_Leaks
	if (!br.util.ElementUtility.isIE())
		// if this is not ie then do it the normal way
	{
		eChildElement.parentNode.removeChild(eChildElement);
	}
	else
	{
		// lazy initialize the disposal element where the work will be done
		if (!br.util.ElementUtility.m_eDisposalElem)
		{
			br.util.ElementUtility.m_eDisposalElem = document.createElement("div");
			br.util.ElementUtility.m_eDisposalElem.style.display = "none";

			document.body.appendChild(br.util.ElementUtility.m_eDisposalElem);
		}

		// TODO consider replacing with a blank TextNode instead
		// TODO do this on an interval, so high volume is handled
		br.util.ElementUtility.m_eDisposalElem.appendChild(eChildElement);
		br.util.ElementUtility.m_eDisposalElem.innerHTML = "";
	}
};

/**
 * Discards one or more elements (all arguments passed will be discarded) by removing children, and removing
 * it from any parentNode.
 * 
 * @param {Element} eFirstElement First Element DOM Element
 */
br.util.ElementUtility.discardChild = function(eFirstElement)
{
	for (var i = 0; elem = arguments[i]; ++i) 
	{
		if (br.util.ElementUtility._isFirefox()) 
		{
			while(elem.lastChild) elem.removeChild(elem.lastChild);
		} 
		else 
		{
			elem.innerHTML = "";
		}
		if (elem.parentNode) 
		{
			br.util.ElementUtility.removeChild(elem);
		}
	}
};

/**
 * Returns the absolute position of the element relative to the window in pixels. 
 * The position also takes into account any scrolling of parents.
 * 
 * @param {DOMElement} elem The DOM element to calculate the position of
 * @type Object
 * @return {left:x,top:y}
 */
br.util.ElementUtility.getPosition = function(elem)
{
	var offsetTrail = elem;
	var offsetLeft = 0;
	var offsetTop = 0;
	while (offsetTrail)
	{
		offsetLeft += offsetTrail.offsetLeft;
		offsetTop += offsetTrail.offsetTop;
		offsetTrail = offsetTrail.offsetParent;
	}
	var scroll = br.util.ElementUtility.getScrollOffset(elem);
	return {left:(offsetLeft - scroll.left), top:(offsetTop - scroll.top)};
};

/**
 * Returns the bounding rectangle of the specified element.
 *
 * @param {Object} elem The element to get the bounding recangle for.
 * @type Object
 * @returns {left:x,right:y}
 */
br.util.ElementUtility.getSize = function(elem)
{
	// Firefox 2 does not support getBoundingClientRect so this method abstracts out the box size calculations for figuring out if a column
	// is visible or not. If getBoundingClientRect is not supported we calculate the values using offsetParent instead.
	if (elem.getBoundingClientRect)
	{
		return elem.getBoundingClientRect();
	}
	else 
	{
		var nLeft = 0;
		var nRight = 0;
		var nOffsetWidth = null;

		if (elem.offsetParent)
		{
			do
			{
				nLeft += elem.offsetLeft;
				nOffsetWidth = nOffsetWidth ? nOffsetWidth : elem.offsetWidth;
			}
			while (elem = elem.offsetParent);

			nRight = nOffsetWidth + nLeft;
		}

		return {'left': nLeft, 'right': nRight};
	}
};

/**
 * Returns the scrolled offset of the element (if any)
 * in an object containing a <tt>left</tt> and <tt>top</tt> properties.
 *
 * @param {Element} elem The DOM element to calculate the scrolled offset of.
 * @type Object
 * @returns {left:x,top:y}
 */
br.util.ElementUtility.getScrollOffset = function(elem)
{
	var offsetTrail = elem;
	var offsetLeft = 0;
	var offsetTop = 0;
	while (offsetTrail &&  !isNaN(offsetTrail.scrollTop))
	{
		offsetLeft += offsetTrail.scrollLeft;
		offsetTop += offsetTrail.scrollTop;
		offsetTrail = offsetTrail.parentNode;
	}
	return {left:offsetLeft, top:offsetTop};
};

/** @private */
br.util.ElementUtility._getClassNameRegExFor = function(sClassName)
{
	var sPatternForTheOnlyWord = "^" + sClassName + "$";
	var sPatternForFirstWord = "^" + sClassName + "\\s";
	var sPatternForInBetweenWords = "\\s" + sClassName + "\\s";
	var sPatternForLastWord = "\\s" + sClassName + "$";
	return new RegExp(sPatternForTheOnlyWord + '|' + sPatternForFirstWord + '|' + sPatternForInBetweenWords + '|' + sPatternForLastWord, "g");	
};

/**
 * @private
 */
br.util.ElementUtility._isFirefox = function()
{
	if (br.util.ElementUtility.m_bIsFirefox === undefined)
	{
		br.util.ElementUtility.m_bIsFirefox = navigator.userAgent.match("Firefox");
	}
	return br.util.ElementUtility.m_bIsFirefox;
};

/**
 * @private
 */
br.util.ElementUtility.isIE = function()
{
	if (br.util.ElementUtility.m_bIsIe === undefined)
	{
		br.util.ElementUtility.m_bIsIe = navigator.userAgent.match("MSIE");
	}
	return br.util.ElementUtility.m_bIsIe;
};
