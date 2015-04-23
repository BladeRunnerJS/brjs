if(!window.Element) {
	throw new Error(
		"The importNode shim requires IE8 to be in strict mode which can be done by adding a DOCTYPE to the page" +
		" -- full details at <http://blogs.msdn.com/b/ie/archive/2010/03/02/how-ie8-determines-document-mode.aspx>");
}

if(!window.DocumentFragment && window.HTMLDocument) {
	window.DocumentFragment = HTMLDocument;
}

if(!document.ELEMENT_NODE) {
	document.ELEMENT_NODE = 1;
	document.ATTRIBUTE_NODE = 2;
	document.TEXT_NODE = 3;
	document.CDATA_SECTION_NODE = 4;
	document.ENTITY_REFERENCE_NODE = 5;
	document.ENTITY_NODE = 6;
	document.PROCESSING_INSTRUCTION_NODE = 7;
	document.COMMENT_NODE = 8;
	document.DOCUMENT_NODE = 9;
	document.DOCUMENT_TYPE_NODE = 10;
	document.DOCUMENT_FRAGMENT_NODE = 11;
	document.NOTATION_NODE = 12;
}

if(!document.createElementNS) {
	document.createElementNS = function(namespaceURI, qualifiedName) {
		return document.createElement(qualifiedName);
	};
}

if(!Element.prototype.setAttributeNS) {
	Element.prototype.setAttributeNS = function(namespace, name, value) {
		this.setAttribute(name, value);
	}
}

if(!document.importNode) {
	document.importNode = function(node, deep) {
		var a, i, il;

		switch (node.nodeType) {
			case document.ELEMENT_NODE:
				var newNode = document.createElementNS(node.namespaceURI, node.nodeName);
				if (node.attributes && node.attributes.length > 0) {
					for (i = 0, il = node.attributes.length; i < il; i++) {
						a = node.attributes[i];
						try {
							newNode.setAttributeNS(a.namespaceURI, a.nodeName, node.getAttribute(a.nodeName));
						}
						catch (err) {
							// ignore this error... doesn't seem to make a difference
						}
					}
				}
				if (deep && node.childNodes && node.childNodes.length > 0) {
					for (i = 0, il = node.childNodes.length; i < il; i++) {
						newNode.appendChild(document.importNode(node.childNodes[i], deep));
					}
				}
				return newNode;

			case document.TEXT_NODE:
			case document.CDATA_SECTION_NODE:
				return document.createTextNode(node.nodeValue);

			case document.COMMENT_NODE:
				return document.createComment(node.nodeValue);

			case document.DOCUMENT_FRAGMENT_NODE:
				docFragment = document.createDocumentFragment();

				for (i = 0, il = node.childNodes.length; i < il; ++i) {
					docFragment.appendChild(document.importNode(node.childNodes[i], deep));
				}
				return docFragment;
		}
	};
}
