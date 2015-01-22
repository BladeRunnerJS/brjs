function showHide(eElement, shID) {
	var content = document.getElementById(shID);
	if (content) {
		if (content.style.display != 'none') {
			content.style.display = 'none';
			eElement.setAttribute("class", "previous_heading");
		}
		else {
			content.style.display = 'block';
			eElement.setAttribute("class", "previous_heading expanded");
		}
	}
}