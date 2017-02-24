import i18n from 'br/I18n';
import HtmlResourceService from '../HtmlResourceService';

const htmlFilesContents = [];
const htmlFilesContentsToAppend = [];

export default class extends HtmlResourceService {
	constructor() {
		super();

		this._templateElements = new Map();
	}

	static registerHTMLFileContents(htmlFileContents) {
		htmlFilesContents.push(htmlFileContents);
	}

	static registerAndAppendHTMLFileContents(htmlFileContents) {
		htmlFilesContentsToAppend.push(htmlFileContents);
	}

	getTemplateFragment(templateID) {
		processHTMLFilesContents(this._templateElements, templateID);

		const template = this._templateElements.get(templateID);

		return document.importNode(template, true);
	}

	getTemplateElement(templateID) {
		const templateFragment = this.getTemplateFragment(templateID);

		// If the template was inside a `template` tag.
		if (templateFragment instanceof DocumentFragment) {
			// We need to search for the first valid node as non empty text nodes are valid and even if they
			// weren't IE/Edge and Safari don't support `firstElementChild` and `children` on `DocumentFragment`s.
			for (const childNode of Array.from(templateFragment.childNodes)) {
				if (isValidTemplateElement(childNode)) {
					return childNode;
				}
			}
		}

		// else it was a plain HTML element e.g. `div`.
		return templateFragment;
	}

	getHTMLTemplate(templateID) {
		debugger;
	}
}

function createTranslatedTemplateContainer(htmlFileContents) {
	const templateElementContainer = document.createElement('div');
	const translatedHTMLFileContents = i18n.getTranslator().translate(htmlFileContents, 'html');

	templateElementContainer.innerHTML = translatedHTMLFileContents;

	return templateElementContainer;
}

function appendTemplateElement(templateElement) {
	// Karma clears down the DOM during tests so we need to clone the elements. If we don't adding and
	// removing elements from the DOM can cause issues when re-adding appended templates.
	const clonedTemplateElement = templateElement.cloneNode(true);
	const head = document.querySelector('head');

	head.appendChild(templateElement);

	return clonedTemplateElement;
}

function registerHTMLFile(htmlFilesContentsToRegister, templateElements, processChild) {
	htmlFilesContentsToRegister
		.map(createTranslatedTemplateContainer)
		.forEach((templateElementContainer) => {
			Array
				.from(templateElementContainer.children)
				.map(processChild)
				.forEach((templateElement) => {
					// Not all templates are wrapped in a `template` tag so we must check for `content`.
					templateElements.set(templateElement.id, templateElement.content || templateElement);
				});
		})
}

function processHTMLFilesContents(templateElements, templateID) {
	if (templateElements.get(templateID) === undefined) {
		registerHTMLFile(htmlFilesContents, templateElements, (templateElement) => templateElement);
		registerHTMLFile(htmlFilesContentsToAppend, templateElements, appendTemplateElement);
	}
}

// Is the provided node a DOM node or a text node with content
function isValidTemplateElement({nodeType, textContent}) {
	const isElement = nodeType === document.ELEMENT_NODE;
	const isNonEmptyTextNode = nodeType === document.TEXT_NODE && textContent.trim() !== '';

	return isElement || isNonEmptyTextNode;
}
