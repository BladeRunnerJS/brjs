/*! shivie8 | @syranide | MIT license */

(function shivIEDocumentElements(doc) {
	if (doc.documentMode < 9) {
		for (
			var tagNames = (
				'abbr,article,aside,audio,bdi,canvas,data,datalist,details,dialog,' +
				'figcaption,figure,footer,header,hgroup,main,mark,meter,nav,output,' +
				'picture,progress,section,summary,template,time,video'
			).split(',');
			tagNames[0];
			doc.createElement(tagNames.pop())
		);
	}
})(document);
