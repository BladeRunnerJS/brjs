import i18n from 'br/I18n';
import XMLResourceService from '../XmlResourceService';

const xmlFilesContents = [];

export default class extends XMLResourceService {
	constructor() {
		super();

		const parser = new DOMParser();

		this._mergedXMLDocuments = parser.parseFromString('<div></div>', 'text/xml').documentElement;
		this._xmlFilesProcessed = false;
	}

	static registerXMLFileContents(xmlFileContents) {
		xmlFilesContents.push(xmlFileContents);
	}

	getXmlDocument(elementName) {
		if (this._xmlFilesProcessed === false) {
			xmlFilesContents
				.map((xmlFileContents) => i18n.getTranslator().translate(xmlFileContents))
				.map(parseXMLFileContents)
				.forEach((xmlDocument) => mergeInXMLDocument(this._mergedXMLDocuments, xmlDocument));

			this._xmlFilesProcessed = true;
		}

		return this._mergedXMLDocuments.getElementsByTagName(elementName);
	}
}

function parseXMLFileContents(xmlFileContents) {
	const domParser = new DOMParser();
	const dom = domParser.parseFromString(xmlFileContents, 'text/xml');

	return dom.documentElement;
}

function findNodeToMergeDocumentInto(mergedXMLDocuments, xmlDocumentToMergeIn) {
	const nodeWithSameTagName = mergedXMLDocuments.querySelector(xmlDocumentToMergeIn.tagName);
	const nodeToMergeInto = nodeWithSameTagName || mergedXMLDocuments;
	// `childNodes` is used instead of `children` as `children` isn't supported in IE11 for XML documents.
	// http://stackoverflow.com/questions/8210236/ie9-not-getting-children-of-xml-node
	// This will mean text nodes are also appended to the combined XML document but that doesn't seem to
	// cause issues, if it does they can be filtered out.
	const nodesToMergeIn = nodeWithSameTagName ? Array.from(xmlDocumentToMergeIn.childNodes) : [xmlDocumentToMergeIn];

	return {nodeToMergeInto, nodesToMergeIn};
}

function mergeInXMLDocument(mergedXMLDocuments, xmlDocument) {
	const {nodeToMergeInto, nodesToMergeIn} = findNodeToMergeDocumentInto(mergedXMLDocuments, xmlDocument);

	for (const nodeToMergeIn of nodesToMergeIn) {
		nodeToMergeInto.appendChild(nodeToMergeIn);
	}
}
