/**********************************************************************
/* Emprise JavaScript Charts 2.2 http://www.ejschart.com/
/*
/* Copyright (C) 2006-2011 Emprise Corporation. All Rights Reserved.
/*
/* WARNING: This software program is protected by copyright law
/* and international treaties. Unauthorized reproduction or
/* distribution of this program, or any portion of it, may result
/* in severe civil and criminal penalties, and will be prosecuted
/* to the maximum extent possible under the law.
/*
/* See http://www.ejschart.com/license.html for full license.
/**********************************************************************/
(function() {

	var m		= Math;
	var ma		= m.abs;
	var mr		= m.round;
	var ms		= m.sin;
	var mc		= m.cos;
	var pi		= m.PI;
	var dec2hex	= [];

	for (var i = 0; i < 16; i++) {
		for (var j = 0; j < 16; j++) {
			dec2hex[i * 16 + j] = i.toString(16) + j.toString(16);
		};
	};

	function processStyle(styleString) {
		var str, alpha = 1;

		styleString = String(styleString);

		if (styleString.substring(0, 3) == "rgb") {
			var start	= styleString.indexOf("(", 3);
			var end		= styleString.indexOf(")", start + 1);
			var guts	= styleString.substring(start + 1, end).split(",");

			str = "#";

			for (var i = 0; i < 3; i++) {
				str += dec2hex[Number(guts[i])];
			}

			if ((guts.length == 4) && (styleString.substr(3, 1) == "a")) {
				alpha = guts[3];
			}
		} else {
			str = styleString;
		}

		return [str, alpha];
	};

	function processLineCap(lineCap) {
		switch (lineCap) {
			case "butt":
				return "flat";
			case "round":
				return "round";
			case "square":
			default:
				return "square";
		}
	};

	function correctScatterDraw(ctx) {

		if( this.__drawing ) return;
		if( !this.visible ) return;

		if (!this.__getHasData()) {
			// Load the series data
			// JHM: 2007-12-22 - Added to fix issue with series added but not immediately drawn
			var dh = this.__dataHandler;
			window.setTimeout(function() { dh.__loadData(); }, 0);
			return;
		}

		this.__drawing = true;

		try {

			var plen 			= this.__points.length;
			var ps 				= this.pointSize;

			var x_axis 			= this.__getChart()["axis_" + this.x_axis];
			var y_axis 			= this.__getChart()["axis_" + this.y_axis];

			var ox_min 			= x_axis.__current_min;
			var ox_max 			= x_axis.__current_max;
			var oy_min 			= y_axis.__current_min;
			var oy_max 			= y_axis.__current_max;

			var da				= this.__getChart().__draw_area;
			var canvas_height	= da.height;
			var canvas_width	= da.width;
			var canvas_top		= da.top;
			var canvas_left		= da.left;
			var canvas_right	= da.right;
			var canvas_bottom	= da.bottom;

			var m_PI			= Math.PI;
			var m_PIx2			= Math.PI * 2;

			var j = 0;
			while( j < plen && ( x_axis.__pt2px(this.__points[j].__x()) + ps ) < canvas_left )
				j++;
			var j_start = j;
			var pointsDrawn = 0;

			// Draw Points
			if (j < plen) {

				switch (this.pointStyle) {
					case "box": ctx.lineCap = "square"; ps = ps * 2; break;
					case "circle": ctx.lineCap = "round"; ps = ps * 2; break;
					case "diamond": ctx.lineCap = "square"; break;
					case "triangle": ctx.lineCap = "butt"; break;
				}

				var r = ps / 2;

				ctx.lineWidth = r;
				ctx.strokeStyle = EJSC.utility.__getColor( this.color , (this.opacity / 100) ).rgba;

				r = r / 2;

				if (!EJSC.__isIE) {
					ctx.beginPath();
				}

				// JGD: 2007-05-29  - Fixed __scatter_series.__doDraw counter to go to the last point
				while (j < plen) {

					plotX = x_axis.__pt2px(this.__points[j].__x());
					plotY = y_axis.__pt2px(this.__points[j].__y());

					if ((plotX - ps) > canvas_right) { break; }

					// JHM: Filled scatter fix in IE
					// JGD: 2007-05-29  - Fixed __scatter_series.__doDraw to check current point for in-bounds
					if ((plotY + ps) >= canvas_top && (plotY - ps) <= canvas_bottom) {

						// JHM: Added to support larger data sets in IE
						if (EJSC.__isIE && ++pointsDrawn > 4000) {
							ctx.stroke();
							pointsDrawn = 0;
							ctx.beginPath();
						}

						switch(this.pointStyle) {
							case 'box':
								ctx.moveTo( plotX + r , plotY + r );
								ctx.lineTo( plotX - r , plotY + r );
								ctx.lineTo( plotX - r , plotY - r );
								ctx.lineTo( plotX + r , plotY - r );
								ctx.lineTo( plotX + r , plotY + r );
								break;
							case 'circle':
								// JGD: 2007-08-22
								//	Fixed to not draw line through center of circle
								ctx.moveTo(plotX + r ,plotY);
								ctx.arc(plotX, plotY, r, 0, m_PI, true);
								ctx.arc(plotX, plotY, r, m_PI, m_PIx2, true);
								// JHM: 2007-11-11 - Removed lineTo as its not necessary
								// ctx.lineTo(plotX+ps,plotY);
								break;
							case 'diamond':
								ctx.moveTo(plotX, plotY - r);
								ctx.lineTo(plotX - r, plotY);
								ctx.lineTo(plotX, plotY + r);
								ctx.lineTo(plotX + r, plotY);
								ctx.lineTo(plotX, plotY - r);
								break;
							case 'triangle':
								ctx.moveTo(plotX - r, plotY + r);
								ctx.lineTo(plotX + r, plotY + r);
								ctx.lineTo(plotX, plotY - r);
								ctx.lineTo(plotX - r, plotY + r);
								break;
						}

					}

					j++;

				}

				ctx.stroke();

			}


		} catch (e) {
		} finally {
			this.__drawing = false;
		}

	};

	function correctMajorTickMarker( i , hint_text , point_hint_x , point_hint_y , o_tick , ctx ) {

		var label		= this.__tickmarkers[i];

		ctx.lineWidth	= 1;
		ctx.fillStyle	= EJSC.utility.__getColor(__getCurrentStyle(label, "color")).rgba;
		ctx.fontSize	= __getCurrentStyle(label, "font-size");
		ctx.fontFamily	= __getCurrentStyle(label, "font-family");
		ctx.fontWeight	= __getCurrentStyle(label, "font-weight");
		ctx.fontStyle	= __getCurrentStyle(label, "font-style");

		ctx.beginPath();

		ctx.drawText(label.offsetLeft, label.offsetTop + (label.offsetHeight*3/4),
			label.offsetLeft + label.offsetWidth, label.offsetTop + (label.offsetHeight*3/4), label.innerHTML, __getCurrentStyle(label, "text-align"));

		ctx.stroke();

	};

	function correctLabel( canvas_width , x_center, y_center, x_min, y_min, x_max, y_max , dif , ctx ) {

		var label		= this.__label;

		ctx.lineWidth	= 1;
		ctx.fillStyle	= EJSC.utility.__getColor(__getCurrentStyle(label, "color")).rgba;
		ctx.fontSize	= __getCurrentStyle(label, "font-size");
		ctx.fontFamily	= __getCurrentStyle(label, "font-family");
		ctx.fontWeight	= __getCurrentStyle(label, "font-weight");
		ctx.fontStyle	= __getCurrentStyle(label, "font-style");

		ctx.beginPath();

		ctx.drawText(label.offsetLeft, label.offsetTop + (label.offsetHeight*3/4),
			label.offsetLeft + label.offsetWidth, label.offsetTop + (label.offsetHeight*3/4), label.firstChild.innerHTML, __getCurrentStyle(label, "text-align"));

		ctx.stroke();

	};

	__textSize = function(aText, aFontFamily, aFontSize, aFontWeight, aFontStyle) {

		// If given multiple strings to measure, return the widest of them all (for multiline text)
		if(typeof(aText) == 'object') {
			var ret = new Array();
			var lrg = {width: undefined, height: undefined};

			for(var i = 0;i < aText.length;i++) {
				ret.push(__textSize(aText[i], aFontFamily, aFontSize, aFontWeight, aFontStyle));
			}

			for(var j = 0;j < ret.length;j++) {
				if((ret[j].width > lrg.width) || (lrg.width == undefined))
					lrg = ret[j];
			}

			return lrg;
		}

		var doc	= document;
		var div	= doc.createElement('svgTextContainer');
		var spn	= doc.createElement('svgTextMeasure');
		var txt	= doc.createTextNode(aText);
		var ret	= {width: undefined, height: undefined};

		div.style.position		= 'absolute';
		div.style.top			= '-2000px';
		div.style.left			= '-2000px';
		div.style.margin		= '0';
		div.style.padding		= '0';
		div.style.overflow		= 'visible';
		div.style.border		= '0';
		div.style.cssFloat		= 'none';
		div.style.textAlign		= 'left';
		div.style.display		= 'block';

		spn.style.fontFamily	= aFontFamily;
		spn.style.fontSize		= aFontSize;
		spn.style.fontWeight	= aFontWeight;
		spn.style.fontStyle		= aFontStyle;
		spn.style.lineHeight	= '1';
		spn.style.border		= '0';
		spn.style.margin		= '0';
		spn.style.padding		= '0';
		spn.style.cssFloat		= 'none';
		spn.style.textAlign		= 'left';
		spn.style.display		= 'inline';

		spn.appendChild(txt);
		div.appendChild(spn);
		doc.body.appendChild(div);

		ret.width	= spn.offsetWidth;
		ret.height	= spn.offsetHeight;

		// HACK:	Browser detection is a bad idea, but Safari renders text differently then everybody else, and as a result,
		//			we need to accomodate for its differing text size. There really isn't any decent way around this.

		var isOpera = window.opera;
		var isIE	= EJSC.__isIE;
		var isGecko	= (window.crypto && window.navigator.buildID);

		if(!isOpera && !isIE && !isGecko) {
			// Should be Safari, in which case text size is 'relatively' normal
			// Otherwise it's some other minor browser, and text size will be somewhat larger then usual.

			// In either instance, text should be bigger then the others by a small margin no matter what you do
			// and as such, text will always be visible, and not clipped. This is the optimal use case.

			ret.width += mr(ret.width * 0.07);
		}

		doc.body.removeChild(div);

		return ret;
	};

	function __getCurrentStyle(el, style, defaultValue) {

		if (window.getComputedStyle) {
			__getCurrentStyle = function(el, style) {
				return document.defaultView.getComputedStyle(el, null).getPropertyValue(style);
			};
			return __getCurrentStyle(el, style);
		} else if (el.currentStyle) {
			__getCurrentStyle = function(el, style) {
				return el.currentStyle[style.replace(/(\-([a-zA-Z]))/g,function(m1,m2,m3){return m3.toUpperCase();})];
			};
			return __getCurrentStyle(el, style);
		} else {
			__getCurrentStyle = function(el, style, defaultValue) {
				return defaultValue;
			}
		}

	};

	// Check for the existance of EJSC namespace, alert if not defined and exit
	if (window.EJSC == undefined || window.EJSC == null) {
		alert("EJSC is not defined, please ensure EJSChart.js is loaded before EJSChart_SVGExport.js.");
		return;
	}

	EJSC.SVGCanvas = function(height, width, nameSpace) {

		this.currentPath_	= [];
		this.strokeStyle	= "#000";
		this.fillStyle		= "#000";
		this.lineWidth		= 1;
		this.lineJoin		= "miter";
		this.lineCap		= "butt";
		this.globalAlpha	= 1;

		this.textPrefix		= "";
		this.textIndex		= 0;
		this.pathDraw		= "";
		this.pathDefine		= [];
		this.text			= [];

		this.fontSize		= '10pt';
		this.fontFamily		= 'Verdana';
		this.fontWeight		= 'normal';
		this.textDecoration	= 'underline';
		this.fontStyle		= 'normal';

		this.height			= height;
		this.width			= width;
		this.offsetTop		= 0;
		this.offsetLeft		= 0;

		this.nameSpace		= nameSpace;

	};

	___svgcanvas = EJSC.SVGCanvas.prototype;

	___svgcanvas.beginUpdate = function() {};
	___svgcanvas.endUpdate = function() {};

	___svgcanvas.getContext = function(contextType) {
		return this;
	};

	___svgcanvas.clearRect = function() {
		this.textIndex		= 0;
		this.pathDraw		= "";
		this.pathDefine		= [];
		this.text			= [];
		this.currentPath_	= [];
	};

	___svgcanvas.beginPath = function() {
		this.currentPath_ = [];
	};

	___svgcanvas.moveTo = function(aX, aY) {

	//	aX += this.offsetLeft;
	//	aY += this.offsetTop;

		this.currentPath_.push({type: "moveTo", x: aX, y: aY});
	};

	___svgcanvas.lineTo = function(aX, aY) {

	//	aX += this.offsetLeft;
	//	aY += this.offsetTop;

		this.currentPath_.push({type: "lineTo", x: aX, y: aY});
	};

	___svgcanvas.arc = function(aX, aY, aRadius, aStartAngle, aEndAngle, aClockwise) {

	//	aX += this.offsetLeft;
	//	aY += this.offsetTop;

		var xStart		= aX + (aRadius * mc(aStartAngle));
		var yStart		= aY + (aRadius * ms(aStartAngle));
		var xEnd		= aX + (aRadius * mc(aEndAngle));
		var yEnd		= aY + (aRadius * ms(aEndAngle));

		// Arcs don't like doing full circles, divide this one in half, and make sure largeArc and sweepArc are set right.
		if(((xStart == xEnd) && (yStart == yEnd)) || ((aStartAngle == 0) && (aEndAngle == (pi*2)) && aClockwise))
		{

			var xStart	= aX + (aRadius * mc(0));
			var yStart	= aY + (aRadius * ms(0));
			var xEnd	= aX + (aRadius * mc(pi));
			var yEnd	= aY + (aRadius * ms(pi));

			this.currentPath_.push({type: "arcTo",
				radius: aRadius,
				start: {x: xStart, y: yStart},
				end: {x: xEnd, y: yEnd},
				largeArc: 1,
				clockwise: 1
			});

			xStart		= aX + (aRadius * mc(pi));
			yStart		= aY + (aRadius * ms(pi));
			xEnd		= aX + (aRadius * mc(pi*2+1));
			yEnd		= aY + (aRadius * ms(pi*2+1));

			this.currentPath_.push({type: "arcTo",
				radius: aRadius,
				start: {x: xStart, y: yStart},
				end: {x: xEnd, y: yEnd},
				largeArc: 1,
				clockwise: 1
			});
		}
		else
		{
			var startDeg	= (aStartAngle * (180/pi));
			var endDeg		= (aEndAngle * (180/pi));
			var largeArc	= 0;

			// If an arc is greater then 180 degrees, it's a large arc.
			if(ma(endDeg - startDeg) >= 180) {
				largeArc = 1;
				aRadius -= 1;
			}

			this.currentPath_.push({type: "arcTo",
				radius: aRadius, // Fix arcs??
				start: {x: xStart, y: yStart},
				end: {x: xEnd, y: yEnd},
				largeArc: largeArc,
				clockwise: ((aClockwise) ? 0 : 1)
			});
		}
	};

	___svgcanvas.drawText = function(aStartX, aStartY, aEndX, aEndY, aText, aAlign) {

		var align			= 'text-anchor="start" ';
		var tagNameText		= (this.nameSpace) ? this.nameSpace + ':text' : 'text';
		var tagNamePath		= (this.nameSpace) ? this.nameSpace + ':path' : 'path';
		var tagNameTextPath	= (this.nameSpace) ? this.nameSpace + ':textPath' : 'textPath';

	//	aStartX += this.offsetLeft;
	//	aStartY	+= this.offsetTop;
	//	aEndX	+= this.offsetLeft;
	//	aEndY	+= this.offsetTop;

		switch(aAlign) {
			case "left":
				align = 'text-anchor="start" ';
				break;
			case "center":
				align = 'text-anchor="middle" startOffset="50%" ';
				break;
			case "right":
				align = 'text-anchor="end" startOffset="100%" ';
				break;
		}

		var fontSize	= this.fontSize;

		this.pathDefine.push('<', tagNamePath, ' id="textPath_', this.textPrefix, '_', this.textIndex, '" d="M', aStartX, ',', aStartY, ' L', aEndX, ',', aEndY, '"/>\r\n');
		// JHM: 2008-01-25 - Added font-style support
		// JHM: 2008-02-25 - Updated font style to push 'normal' if fontstyle attribute is blank
		this.text.push('<', tagNameText, ' style="font-size:', this.fontSize , ';font-family:', this.fontFamily , ';font-weight:', this.fontWeight , ';font-style:', (this.fontStyle==""?"normal":this.fontStyle), ';fill:', EJSC.utility.__getColor(processStyle(this.fillStyle)[0]).hex , ';"><', tagNameTextPath, ' ', align, 'xlink:href="#textPath_', this.textPrefix, '_', this.textIndex++, '">', aText, '</', tagNameTextPath, '></', tagNameText, '>\r\n');

	};

	___svgcanvas.stroke = function(aFill) {

		// Don't stroke or fill if we're empty, just creates useless output.
		if(this.currentPath_.length <= 0)
			return;

		var lineStr		= [];
		var lineOpen	= false;
		var a			= processStyle(aFill ? this.fillStyle : this.strokeStyle);
		var color		= a[0];
		var opacity		= a[1] * this.globalAlpha;
		var canClose	= true;
		var tagName		= (this.nameSpace) ? this.nameSpace + ':path' : 'path';

		var lineWidth	= (this.lineWidth > 0) ? this.lineWidth : ((!aFill) ? 1 : 0); // If we're not filling, and lineWidth is 0, make it 1 (IE/VML draws it anyway if lineWidth is 0, so we have to too)

		// Don't draw if opacity is 0
		if(!opacity)
			return;

		lineStr.push(
			'<', tagName, ' style="',
			'fill:', (Boolean(aFill) ? color : 'none'), '; ',
			'opacity:', opacity, '; ',
			'stroke:', ((!aFill) ? color : 'none'), '; ',
			'stroke-width:', lineWidth, ';" ',
			'd="'
		);

		for (var i = 0, p = this.currentPath_[i], n = this.currentPath_[i+1]; i < this.currentPath_.length; i++, p = this.currentPath_[i]) {
			if((p.type == "moveTo") && ((n && n.type != "arcTo") || !n)) {
				lineStr.push(" M", mr(p.x), ",", mr(p.y));
				canClose = true;
			} else if (p.type == "lineTo") {
				lineStr.push(" L", mr(p.x), ",", mr(p.y));
				canClose = true;
			} else if ((p.type == "close") && canClose){
				lineStr.push(" Z");
			} else if (p.type == "arcTo") {
				lineStr.push(" M", mr(p.start.x), ",", mr(p.start.y), " A", mr(p.radius), ",", mr(p.radius), " 0 ", p.largeArc, ",", p.clockwise, " ", mr(p.end.x), ",", mr(p.end.y));
				canClose = false;
			}
		}

		lineStr.push(' "/>\r\n');

		this.pathDraw += lineStr.join("");
		this.currentPath_ = [];

	};

	___svgcanvas.fill = function() {
		this.stroke(true);
	};

	___svgcanvas.closePath = function() {
		this.currentPath_.push({type: "close"});
	};

	___svgcanvas.exportSVG = function(aIncludeHeader, aHeight, aWidth) {

		var height		= (aHeight != undefined) ? aHeight : this.height;
		var width		= (aWidth != undefined) ? aWidth : this.width;
		var nameSpace	= (this.nameSpace != undefined) ? (this.nameSpace + ':') : '';

		var tagSVG		= nameSpace + 'svg';
		var tagDefs		= nameSpace + 'defs';

		var header		= (aIncludeHeader || (aIncludeHeader == undefined)) ? '<?xml version="1.0"?>\r\n<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">\r\n' : '';
		var openTag		= '<' + tagSVG + ' width="' + width + '" height="' + height + '" viewBox="0 0 ' + this.width + ' ' + this.height + '" version="1.1" xmlns' + ((this.nameSpace) ? ':' + this.nameSpace : '') + '="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r\n';
		var closeTag	= '</' + tagSVG + '>';
		var pathDefine	= (this.pathDefine.length) ? '<' + tagDefs + '>\r\n' + this.pathDefine.join("") + '</' + tagDefs + '>\r\n' : '';
		var pathDraw	= this.pathDraw;
		var textDraw	= this.text.join("");

		return header + openTag + pathDefine + pathDraw + textDraw + closeTag;

	};

	// Define export method for the chart
	___chart = EJSC.Chart.prototype;

	___chart.__exportSVGAxis = function(axis,ctx) {

/* JGD - 2011-03-02 - Fixed */
		if( !axis.visible || axis.__el == undefined ) return;

		var el			= axis.__el;
		var text		= axis.__el_caption;

		ctx.lineWidth	= 0;
		ctx.fillStyle	= 'rgb(255,255,255,1)';

		ctx.beginPath();
		ctx.moveTo(el.offsetLeft, el.offsetTop);
		ctx.lineTo(el.offsetLeft + el.offsetWidth, el.offsetTop);
		ctx.lineTo(el.offsetLeft + el.offsetWidth, el.offsetTop + el.offsetHeight);
		ctx.lineTo(el.offsetLeft, el.offsetTop + el.offsetHeight);
		ctx.lineTo(el.offsetLeft, el.offsetTop);
		ctx.fill();

		ctx.fillStyle 	= EJSC.utility.__getColor(__getCurrentStyle(text, "color")).rgba;
		ctx.fontSize 	= __getCurrentStyle(text, "font-size");
		ctx.fontFamily 	= __getCurrentStyle(text, "font-family");
		ctx.fontWeight 	= __getCurrentStyle(text, "font-weight");
		ctx.fontStyle 	= __getCurrentStyle(text, "font-style");

		ctx.beginPath();

		var isIE = EJSC.__isIE;
		try {
			EJSC.__isIE = true;
			var caption = axis.__getCaption();
		} catch (e) {
		} finally {
			EJSC.__isIE = isIE;
		}

		switch( axis.__side ) {
			case 'left' :
				ctx.drawText(text.offsetWidth, el.offsetTop + ctx.offsetHeight, text.offsetWidth, el.offsetTop,
					caption, "center");
				break;
			case 'bottom' :
				ctx.drawText(el.offsetLeft, el.offsetTop + text.offsetTop + text.offsetHeight*3/4, el.offsetLeft + el.offsetWidth, el.offsetTop + text.offsetTop + text.offsetHeight*3/4,
					caption, "center");
				break;
			case 'right' :
				ctx.drawText(el.offsetLeft + el.offsetWidth - text.offsetWidth, el.offsetTop, el.offsetLeft + el.offsetWidth - text.offsetWidth, el.offsetTop + ctx.offsetHeight,
					caption, "center");
				break;
			case 'top' :
				ctx.drawText(el.offsetLeft, el.offsetTop + text.offsetHeight, el.offsetLeft + el.offsetWidth, el.offsetTop + text.offsetHeight,
					caption, "center");
				break;
			default:
				break;
		}

		ctx.stroke();

		var labels		= axis.__el_labels.childNodes;

		for( var i=0 ; i<labels.length ; i++ ) {
			if( labels[i].className.indexOf('ejsc-invisible') == -1 ) {

				text = labels[i];

				ctx.fillStyle 	= EJSC.utility.__getColor(__getCurrentStyle(text, "color")).rgba;
				ctx.fontSize 	= __getCurrentStyle(text, "font-size");
				ctx.fontFamily 	= __getCurrentStyle(text, "font-family");
				ctx.fontWeight 	= __getCurrentStyle(text, "font-weight");
				ctx.fontStyle 	= __getCurrentStyle(text, "font-style");

				ctx.beginPath();

				ctx.drawText(
					el.offsetLeft + text.offsetLeft,
					el.offsetTop + text.offsetTop + text.offsetHeight*3/4,
					el.offsetLeft + text.offsetLeft + text.offsetWidth,
					el.offsetTop + text.offsetTop + text.offsetHeight*3/4,
					labels[i].innerHTML, "center");

				ctx.stroke();

			}
		}

	};

	___chart.__exportSVGTitleBar = function(ctx) {

		if( !this.show_titlebar ) return;

		var titlebar	= this.__el_titlebar;
		var text		= this.__el_titlebar_text;

		ctx.lineWidth	= 0;
		ctx.fillStyle	= 'rgb(255,255,255,1)';

		ctx.beginPath();
		ctx.moveTo(0, 0);
		ctx.lineTo(ctx.width, 0);
		ctx.lineTo(ctx.width, titlebar.offsetHeight);
		ctx.lineTo(0, titlebar.offsetHeight);
		ctx.lineTo(0, 0);
		ctx.fill();

		ctx.lineWidth	= 1;
		ctx.fillStyle	= EJSC.utility.__getColor(__getCurrentStyle(text, "color")).rgba;
		ctx.fontSize	= __getCurrentStyle(text, "font-size");
		ctx.fontFamily	= __getCurrentStyle(text, "font-family");
		ctx.fontWeight	= __getCurrentStyle(text, "font-weight");
		ctx.fontStyle	= __getCurrentStyle(text, "font-style");

		ctx.beginPath();

		ctx.drawText(titlebar.offsetLeft + text.offsetLeft, titlebar.offsetTop + text.offsetHeight,
			titlebar.offsetWidth, titlebar.offsetTop + text.offsetHeight, text.innerHTML, __getCurrentStyle(titlebar, "text-align"));

		ctx.stroke();

	};

	___chart.exportSVG = function(options) {

		var exportOptions = {
			includeHeader: true,
			height: undefined,
			width: undefined,
			namespace: undefined
		};
		EJSC.Inheritable.__extendTo(exportOptions);
		exportOptions.__copyOptions(options);

		var isIE	= EJSC.__isIE;
		var charts	= EJSC.__Charts;

		try {

			EJSC.__Charts = [];
			EJSC.__isIE = false;

			var svgcanvas 				= new EJSC.SVGCanvas( this.__el.offsetHeight , this.__el.offsetWidth , exportOptions.namespace );
			svgcanvas.offsetTop			= this.__draw_area.top;
			svgcanvas.offsetHeight		= this.__draw_area.height;
			svgcanvas.offsetLeft		= this.__draw_area.left;
			svgcanvas.offsetWidth		= this.__draw_area.width;
			svgcanvas.parentNode		= this.__el_series_canvas.parentNode;
			svgcanvas.textPrefix		= this.__id + "_chart";

			if( this.__canDraw === false ) 										return;
			if( this.__axes_context == null || this.__series_context == null )	return;
			if( this.__draw_area == undefined ) 								return;

			for( var i=0 ; i<this.__series.length ; i++ ) {
				if( this.__series[i].__type == 'scatter' ) {
					this.__series[i].___doDraw 						= this.__series[i].__doDraw;
					this.__series[i].__doDraw 						= correctScatterDraw;
				} else if( this.__series[i].__type == 'analoggauge' ) {
					this.__series[i].___updateMajorTickMarker		= this.__series[i].__updateMajorTickMarker;
					this.__series[i].__updateMajorTickMarker		= correctMajorTickMarker;
					this.__series[i].___updateLabel					= this.__series[i].__updateLabel;
					this.__series[i].__updateLabel					= correctLabel;
				}
			}

			this.__draw_axes( svgcanvas );
			this.__draw_series( svgcanvas , false );
			this.__draw_zero_planes( svgcanvas );

			this.__exportSVGAxis( this.axis_left , svgcanvas );
			this.__exportSVGAxis( this.axis_bottom , svgcanvas );
			this.__exportSVGAxis( this.axis_right , svgcanvas );
			this.__exportSVGAxis( this.axis_top , svgcanvas );

			this.__exportSVGTitleBar( svgcanvas );

			for( var i=0 ; i<this.__series.length ; i++ ) {
				if( this.__series[i].__type == 'scatter' ) {
					this.__series[i].__doDraw = this.__series[i].___doDraw;
				} else if( this.__series[i].__type == 'analoggauge' ) {
					this.__series[i].__updateMajorTickMarker		= this.__series[i].___updateMajorTickMarker;
					this.__series[i].__updateLabel					= this.__series[i].___updateLabel;
				}
			}

			return svgcanvas.exportSVG( exportOptions.includeHeader , exportOptions.height , exportOptions.width );

		} catch (e) {
			alert(e.message);
		} finally {
			EJSC.__isIE		= isIE;
			EJSC.__Charts	= charts;
		}

	};

	__icons = {

		__draw_scatter: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = "rgba(0,0,0,1)";
			ctx.moveTo(rect.l + 11, rect.t + 3);
			ctx.lineTo(rect.l + 10, rect.t + 2);
			ctx.moveTo(rect.l + 14, rect.t + 6);
			ctx.lineTo(rect.l + 13, rect.t + 5);
			ctx.moveTo(rect.l + 3, rect.t + 8);
			ctx.lineTo(rect.l + 2, rect.t + 7);
			ctx.moveTo(rect.l + 7, rect.t + 8);
			ctx.lineTo(rect.l + 6, rect.t + 7);
			ctx.moveTo(rect.l + 5, rect.t + 9);
			ctx.lineTo(rect.l + 4, rect.t + 8);
			ctx.moveTo(rect.l + 10, rect.t + 10);
			ctx.lineTo(rect.l + 9, rect.t + 9);
			ctx.moveTo(rect.l + 4, rect.t + 12);
			ctx.lineTo(rect.l + 3, rect.t + 11);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#5088F0").rgba;
			ctx.moveTo(rect.l + 6, rect.t + 6);
			ctx.lineTo(rect.l + 5, rect.t + 5);
			ctx.moveTo(rect.l + 12, rect.t + 7);
			ctx.lineTo(rect.l + 11, rect.t + 6);
			ctx.moveTo(rect.l + 2, rect.t + 11);
			ctx.lineTo(rect.l + 1, rect.t + 10);
			ctx.moveTo(rect.l + 8, rect.t + 11);
			ctx.lineTo(rect.l + 7, rect.t + 10);
			ctx.moveTo(rect.l + 9, rect.t + 13);
			ctx.lineTo(rect.l + 8, rect.t + 12);
			ctx.moveTo(rect.l + 13, rect.t + 12);
			ctx.lineTo(rect.l + 12, rect.t + 11);
			ctx.stroke();

		},

		__draw_pie: function(ctx, rect) {

			var x = rect.l + 8;
			var y = rect.t + 8;
			var r = 6;

			var os = 0 - Math.PI / 2;
			var oe = Math.PI * 2 / 3 - Math.PI / 2;
			var as = r * Math.cos( os );
			var ae = r * Math.sin( os );

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#7ca2f1").rgba;
			ctx.moveTo(x, y);
			ctx.lineTo(x + as, y + ae);
			ctx.arc(x, y, r, os, oe, false);
			ctx.lineTo(x, y);
			ctx.fill();

			var os = Math.PI * 2 / 3 - Math.PI / 2;
			var oe = Math.PI * 4 / 3 - Math.PI / 2;
			var as = r * Math.cos( os );
			var ae = r * Math.sin( os );

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#a93b71").rgba;
			ctx.moveTo(x, y);
			ctx.lineTo(x + as, y + ae);
			ctx.arc(x, y, r, os, oe, false);
			ctx.lineTo(x, y);
			ctx.fill();

			var os = Math.PI * 4 / 3 - Math.PI / 2;
			var oe = Math.PI * 2 - Math.PI / 2;
			var as = r * Math.cos( os );
			var ae = r * Math.sin( os );

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#f1c661").rgba;
			ctx.moveTo(x, y);
			ctx.lineTo(x + as, y + ae);
			ctx.arc(x, y, r, os, oe, false);
			ctx.lineTo(x, y);
			ctx.fill();

			ctx.beginPath();
			ctx.strokeStyle = EJSC.utility.__getColor("#999999").rgba;

			var os = 0 - Math.PI / 2;
			var oe = Math.PI * 2 / 3 - Math.PI / 2;
			var as = r * Math.cos( os );
			var ae = r * Math.sin( os );

			ctx.moveTo(x, y);
			ctx.lineTo(x + as, y + ae);
			ctx.arc(x, y, r, os, oe, false);
			ctx.lineTo(x, y);

			var os = Math.PI * 2 / 3 - Math.PI / 2;
			var oe = Math.PI * 4 / 3 - Math.PI / 2;
			var as = r * Math.cos( os );
			var ae = r * Math.sin( os );

			ctx.moveTo(x, y);
			ctx.lineTo(x + as, y + ae);
			ctx.arc(x, y, r, os, oe, false);
			ctx.lineTo(x, y);

			var os = Math.PI * 4 / 3 - Math.PI / 2;
			var oe = Math.PI * 2 - Math.PI / 2;
			var as = r * Math.cos( os );
			var ae = r * Math.sin( os );

			ctx.moveTo(x, y);
			ctx.lineTo(x + as, y + ae);
			ctx.arc(x, y, r, os, oe, false);
			ctx.lineTo(x, y);

			ctx.stroke();

		},

		__draw_line: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#2050C0").rgba;
			ctx.moveTo(rect.l + 2, rect.t + 10);
			ctx.lineTo(rect.l + 6, rect.t + 10);
			ctx.lineTo(rect.l + 13, rect.t + 5);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#FF4890").rgba;
			ctx.moveTo(rect.l + 2, rect.t + 5);
			ctx.lineTo(rect.l + 6, rect.t + 5);
			ctx.lineTo(rect.l + 13, rect.t + 10);
			ctx.stroke();

		},

		__draw_bar: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#2050C0").rgba;
			ctx.moveTo(rect.l + 1, rect.t + 3);
			ctx.lineTo(rect.l + 12, rect.t + 3);
			ctx.lineTo(rect.l + 12, rect.t + 4);
			ctx.lineTo(rect.l + 1, rect.t + 4);
			ctx.moveTo(rect.l + 1, rect.t + 10);
			ctx.lineTo(rect.l + 8, rect.t + 10);
			ctx.lineTo(rect.l + 8, rect.t + 11);
			ctx.lineTo(rect.l + 1, rect.t + 11);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#FF4890").rgba;
			ctx.moveTo(rect.l + 1, rect.t + 5);
			ctx.lineTo(rect.l + 7, rect.t + 5);
			ctx.lineTo(rect.l + 7, rect.t + 6);
			ctx.lineTo(rect.l + 1, rect.t + 6);
			ctx.moveTo(rect.l + 1, rect.t + 12);
			ctx.lineTo(rect.l + 13, rect.t + 12);
			ctx.lineTo(rect.l + 13, rect.t + 13);
			ctx.lineTo(rect.l + 1, rect.t + 13);
			ctx.stroke();

		},

		__draw_column: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#2050C0").rgba;
			ctx.moveTo(rect.l + 3, rect.t + 14);
			ctx.lineTo(rect.l + 3, rect.t + 3);
			ctx.lineTo(rect.l + 4, rect.t + 3);
			ctx.lineTo(rect.l + 4, rect.t + 14);
			ctx.moveTo(rect.l + 10, rect.t + 14);
			ctx.lineTo(rect.l + 10, rect.t + 7);
			ctx.lineTo(rect.l + 11, rect.t + 7);
			ctx.lineTo(rect.l + 11, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#FF4890").rgba;
			ctx.moveTo(rect.l + 5, rect.t + 14);
			ctx.lineTo(rect.l + 5, rect.t + 8);
			ctx.lineTo(rect.l + 6, rect.t + 8);
			ctx.lineTo(rect.l + 6, rect.t + 14);
			ctx.moveTo(rect.l + 12, rect.t + 14);
			ctx.lineTo(rect.l + 12, rect.t + 2);
			ctx.lineTo(rect.l + 13, rect.t + 2);
			ctx.lineTo(rect.l + 13, rect.t + 14);
			ctx.stroke();

		},

		__draw_area: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 0;
			ctx.strokeStyle = EJSC.utility.__getColor("#FF4890").rgba;
			ctx.fillStyle = EJSC.utility.__getColor("#FF4890").rgba;
			ctx.moveTo(rect.l + 1, rect.t + 14);
			ctx.lineTo(rect.l + 1, rect.t + 3);
			ctx.lineTo(rect.l + 7, rect.t + 6);
			ctx.lineTo(rect.l + 11, rect.t + 3);
			ctx.lineTo(rect.l + 14, rect.t + 7);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.lineTo(rect.l + 1, rect.t + 14);
			ctx.fill();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#2050C0").rgba;
			ctx.fillStyle = EJSC.utility.__getColor("#2050C0").rgba;
			ctx.moveTo(rect.l + 1, rect.t + 14);
			ctx.lineTo(rect.l + 1, rect.t + 7);
			ctx.lineTo(rect.l + 7, rect.t + 10);
			ctx.lineTo(rect.l + 11, rect.t + 7);
			ctx.lineTo(rect.l + 14, rect.t + 11);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.lineTo(rect.l + 1, rect.t + 14);
			ctx.fill();

		},

		__draw_trend: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = "rgba(0,0,0,1)";
			ctx.moveTo(rect.l + 11, rect.t + 3);
			ctx.lineTo(rect.l + 10, rect.t + 2);
			ctx.moveTo(rect.l + 14, rect.t + 6);
			ctx.lineTo(rect.l + 13, rect.t + 5);
			ctx.moveTo(rect.l + 3, rect.t + 8);
			ctx.lineTo(rect.l + 2, rect.t + 7);
			ctx.moveTo(rect.l + 7, rect.t + 8);
			ctx.lineTo(rect.l + 6, rect.t + 7);
			ctx.moveTo(rect.l + 5, rect.t + 9);
			ctx.lineTo(rect.l + 4, rect.t + 8);
			ctx.moveTo(rect.l + 10, rect.t + 10);
			ctx.lineTo(rect.l + 9, rect.t + 9);
			ctx.moveTo(rect.l + 4, rect.t + 12);
			ctx.lineTo(rect.l + 3, rect.t + 11);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#5088F0").rgba;
			ctx.moveTo(rect.l + 6, rect.t + 6);
			ctx.lineTo(rect.l + 5, rect.t + 5);
			ctx.moveTo(rect.l + 12, rect.t + 7);
			ctx.lineTo(rect.l + 11, rect.t + 6);
			ctx.moveTo(rect.l + 2, rect.t + 11);
			ctx.lineTo(rect.l + 1, rect.t + 10);
			ctx.moveTo(rect.l + 8, rect.t + 11);
			ctx.lineTo(rect.l + 7, rect.t + 10);
			ctx.moveTo(rect.l + 9, rect.t + 13);
			ctx.lineTo(rect.l + 8, rect.t + 12);
			ctx.moveTo(rect.l + 13, rect.t + 12);
			ctx.lineTo(rect.l + 12, rect.t + 11);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#FF0000").rgba;
			ctx.moveTo(rect.l + 3, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 3);
			ctx.stroke();

		},

		__draw_function: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#000000").rgba;
			ctx.moveTo(rect.l + 2, rect.t + 10);
			ctx.lineTo(rect.l + 4, rect.t + 10);
			ctx.lineTo(rect.l + 6, rect.t + 4);
			ctx.lineTo(rect.l + 8, rect.t + 4);
			ctx.moveTo(rect.l + 4, rect.t + 7);
			ctx.lineTo(rect.l + 7, rect.t + 7);
			ctx.lineTo(rect.l + 9, rect.t + 7);
			ctx.lineTo(rect.l + 8, rect.t + 8);
			ctx.lineTo(rect.l + 8, rect.t + 11);
			ctx.lineTo(rect.l + 9, rect.t + 12);
			ctx.moveTo(rect.l + 10, rect.t + 8);
			ctx.lineTo(rect.l + 12, rect.t + 11);
			ctx.moveTo(rect.l + 10, rect.t + 11);
			ctx.lineTo(rect.l + 12, rect.t + 8);
			ctx.moveTo(rect.l + 13, rect.t + 7);
			ctx.lineTo(rect.l + 14, rect.t + 8);
			ctx.lineTo(rect.l + 14, rect.t + 11);
			ctx.lineTo(rect.l + 13, rect.t + 12);
			ctx.stroke();

		},

		__draw_analoggauge: function(ctx, rect) {

			var r1 = 9;
			var r2 = 6;
			var r = 1;

			var x = rect.l + 6;
			var y = rect.t + 11;

			var os = -Math.PI/4 - Math.PI/2;
			var oe = 0 - Math.PI/2;
			var as = r1 * Math.cos( os );
			var ae = r1 * Math.sin( os );
			var bs = r2 * Math.cos( oe );
			var be = r2 * Math.sin( oe );

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#366c33").rgba;
			ctx.moveTo(x + as, y + ae);
			ctx.arc(x, y, r1, os, oe, false);
			ctx.lineTo(x + bs, y + be);
			ctx.arc(x, y, r2, oe, os, true);
			ctx.lineTo(x + as, y + ae);
			ctx.fill();

			var os = 0 - Math.PI/2;
			var oe = Math.PI/4 - Math.PI/2;
			var as = r1 * Math.cos( os );
			var ae = r1 * Math.sin( os );
			var bs = r2 * Math.cos( oe );
			var be = r2 * Math.sin( oe );

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#c3c11b").rgba;
			ctx.moveTo(x + as, y + ae);
			ctx.arc(x, y, r1, os, oe, false);
			ctx.lineTo(x + bs, y + be);
			ctx.arc(x, y, r2, oe, os, true);
			ctx.lineTo(x + as, y + ae);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#000000").rgba;
			ctx.moveTo(x, y);
			ctx.arc(x, y, r, 0, Math.PI*2, false);
			ctx.fill();

			ctx.beginPath();
			ctx.strokeStyle = EJSC.utility.__getColor("#000000").rgba;
			ctx.moveTo(x, y);
			ctx.lineTo(x-3, y-9);
			ctx.stroke();



		},

		__draw_candle: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 4, rect.t + 11);
			ctx.lineTo(rect.l + 4, rect.t + 3);
			ctx.stroke();
			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 2, rect.t + 9);
			ctx.lineTo(rect.l + 6, rect.t + 9);
			ctx.lineTo(rect.l + 6, rect.t + 5);
			ctx.lineTo(rect.l + 2, rect.t + 5);
			ctx.lineTo(rect.l + 2, rect.t + 9);
			ctx.fill();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 7, rect.t + 12);
			ctx.lineTo(rect.l + 7, rect.t + 4);
			ctx.stroke();
			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 5, rect.t + 10);
			ctx.lineTo(rect.l + 9, rect.t + 10);
			ctx.lineTo(rect.l + 9, rect.t + 6);
			ctx.lineTo(rect.l + 5, rect.t + 6);
			ctx.lineTo(rect.l + 5, rect.t + 10);
			ctx.fill();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 10, rect.t + 12);
			ctx.lineTo(rect.l + 10, rect.t + 6);
			ctx.stroke();
			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 8, rect.t + 10);
			ctx.lineTo(rect.l + 12, rect.t + 10);
			ctx.lineTo(rect.l + 12, rect.t + 8);
			ctx.lineTo(rect.l + 8, rect.t + 8);
			ctx.lineTo(rect.l + 8, rect.t + 10);
			ctx.fill();

		},

		__draw_hloc: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 4, rect.t + 11);
			ctx.lineTo(rect.l + 4, rect.t + 9);
			ctx.lineTo(rect.l + 2, rect.t + 9);
			ctx.lineTo(rect.l + 4, rect.t + 9);
			ctx.lineTo(rect.l + 4, rect.t + 5);
			ctx.lineTo(rect.l + 6, rect.t + 5);
			ctx.lineTo(rect.l + 4, rect.t + 5);
			ctx.lineTo(rect.l + 4, rect.t + 3);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 7, rect.t + 4);
			ctx.lineTo(rect.l + 7, rect.t + 6);
			ctx.lineTo(rect.l + 5, rect.t + 6);
			ctx.lineTo(rect.l + 7, rect.t + 6);
			ctx.lineTo(rect.l + 7, rect.t + 10);
			ctx.lineTo(rect.l + 9, rect.t + 10);
			ctx.lineTo(rect.l + 7, rect.t + 10);
			ctx.lineTo(rect.l + 7, rect.t + 12);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 10, rect.t + 12);
			ctx.lineTo(rect.l + 10, rect.t + 10);
			ctx.lineTo(rect.l + 8, rect.t + 10);
			ctx.lineTo(rect.l + 10, rect.t + 10);
			ctx.lineTo(rect.l + 10, rect.t + 7);
			ctx.lineTo(rect.l + 12, rect.t + 7);
			ctx.lineTo(rect.l + 10, rect.t + 7);
			ctx.lineTo(rect.l + 10, rect.t + 8);
			ctx.stroke();

		},

		__draw_bar_floating: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 11, rect.t + 3);
			ctx.lineTo(rect.l + 11, rect.t + 5);
			ctx.lineTo(rect.l + 3, rect.t + 5);
			ctx.lineTo(rect.l + 3, rect.t + 3);
			ctx.lineTo(rect.l + 11, rect.t + 3);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 7, rect.t + 5);
			ctx.lineTo(rect.l + 7, rect.t + 7);
			ctx.lineTo(rect.l + 11, rect.t + 7);
			ctx.lineTo(rect.l + 11, rect.t + 5);
			ctx.lineTo(rect.l + 7, rect.t + 5);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 4, rect.t + 8);
			ctx.lineTo(rect.l + 4, rect.t + 10);
			ctx.lineTo(rect.l + 6, rect.t + 10);
			ctx.lineTo(rect.l + 6, rect.t + 8);
			ctx.lineTo(rect.l + 4, rect.t + 8);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 2, rect.t + 10);
			ctx.lineTo(rect.l + 2, rect.t + 12);
			ctx.lineTo(rect.l + 8, rect.t + 12);
			ctx.lineTo(rect.l + 8, rect.t + 10);
			ctx.lineTo(rect.l + 2, rect.t + 10);
			ctx.fill();

		},

		__draw_column_floating: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 3, rect.t + 11);
			ctx.lineTo(rect.l + 5, rect.t + 11);
			ctx.lineTo(rect.l + 5, rect.t + 3);
			ctx.lineTo(rect.l + 3, rect.t + 3);
			ctx.lineTo(rect.l + 3, rect.t + 11);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 5, rect.t + 7);
			ctx.lineTo(rect.l + 7, rect.t + 7);
			ctx.lineTo(rect.l + 7, rect.t + 11);
			ctx.lineTo(rect.l + 5, rect.t + 11);
			ctx.lineTo(rect.l + 5, rect.t + 7);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 8, rect.t + 4);
			ctx.lineTo(rect.l + 10, rect.t + 4);
			ctx.lineTo(rect.l + 10, rect.t + 6);
			ctx.lineTo(rect.l + 8, rect.t + 6);
			ctx.lineTo(rect.l + 8, rect.t + 4);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 10, rect.t + 2);
			ctx.lineTo(rect.l + 12, rect.t + 2);
			ctx.lineTo(rect.l + 12, rect.t + 8);
			ctx.lineTo(rect.l + 10, rect.t + 8);
			ctx.lineTo(rect.l + 10, rect.t + 2);
			ctx.fill();

		},

		__draw_bar_stacked: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 10, rect.t + 2);
			ctx.lineTo(rect.l + 6, rect.t + 2);
			ctx.lineTo(rect.l + 6, rect.t + 6);
			ctx.lineTo(rect.l + 10, rect.t + 6);
			ctx.lineTo(rect.l + 10, rect.t + 2);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 6, rect.t + 2);
			ctx.lineTo(rect.l + 1, rect.t + 2);
			ctx.lineTo(rect.l + 1, rect.t + 6);
			ctx.lineTo(rect.l + 6, rect.t + 6);
			ctx.lineTo(rect.l + 6, rect.t + 2);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 12, rect.t + 8);
			ctx.lineTo(rect.l + 9, rect.t + 8);
			ctx.lineTo(rect.l + 9, rect.t + 12);
			ctx.lineTo(rect.l + 12, rect.t + 12);
			ctx.lineTo(rect.l + 12, rect.t + 8);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 9, rect.t + 8);
			ctx.lineTo(rect.l + 1, rect.t + 8);
			ctx.lineTo(rect.l + 1, rect.t + 12);
			ctx.lineTo(rect.l + 9, rect.t + 12);
			ctx.lineTo(rect.l + 9, rect.t + 8);
			ctx.fill();

		},

		__draw_column_stacked: function(ctx, rect) {

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.strokeStyle = EJSC.utility.__getColor("#304860").rgba;
			ctx.moveTo(rect.l, rect.t + 2);
			ctx.lineTo(rect.l, rect.t + 14);
			ctx.lineTo(rect.l + 14, rect.t + 14);
			ctx.stroke();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 3, rect.t + 4);
			ctx.lineTo(rect.l + 7, rect.t + 4);
			ctx.lineTo(rect.l + 7, rect.t + 7);
			ctx.lineTo(rect.l + 3, rect.t + 7);
			ctx.lineTo(rect.l + 3, rect.t + 4);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 3, rect.t + 7);
			ctx.lineTo(rect.l + 7, rect.t + 7);
			ctx.lineTo(rect.l + 7, rect.t + 13);
			ctx.lineTo(rect.l + 3, rect.t + 13);
			ctx.lineTo(rect.l + 3, rect.t + 7);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#618cf1").rgba;
			ctx.moveTo(rect.l + 8, rect.t + 3);
			ctx.lineTo(rect.l + 12, rect.t + 3);
			ctx.lineTo(rect.l + 12, rect.t + 5);
			ctx.lineTo(rect.l + 8, rect.t + 5);
			ctx.lineTo(rect.l + 8, rect.t + 3);
			ctx.fill();

			ctx.beginPath();
			ctx.fillStyle = EJSC.utility.__getColor("#b82f7e").rgba;
			ctx.moveTo(rect.l + 8, rect.t + 5);
			ctx.lineTo(rect.l + 12, rect.t + 5);
			ctx.lineTo(rect.l + 12, rect.t + 13);
			ctx.lineTo(rect.l + 8, rect.t + 13);
			ctx.lineTo(rect.l + 8, rect.t + 5);
			ctx.fill();

		}

	};

	___chart.exportSVGLegend = function(options) {

		var exportOptions = {
			orientation: "horizontal",
			includeHeader: true,
			height: undefined,
			width: undefined,
			namespace: undefined,
			border: {
				show: true,
				color: "rgba(0,0,0,1)",
				size: 1
			},
			show_title: true
		};
		EJSC.Inheritable.__extendTo(exportOptions);
		exportOptions.__copyOptions(options);

		try {

			var i, j;

			var title = this.title;

			// Get the list of visible series which show their legend item
			var items = [];
			var pItem, itemCaption;

			for (i = 0; i < this.__series.length; i++) {

				if (this.__series[i].visible == true && this.__series[i].legendIsVisible == true) {

					var icon = this.__series[i].__type;
					if( icon == 'bar' ) {
						if( this.__series[i].orientation == 'vertical' )
							icon = 'column';
						if( this.__series[i].__subtype != undefined )
							icon += '_' + this.__series[i].__subtype;
					}

					pItem = {
						caption: this.__series[i].title,
						color: this.__series[i].color,
						coloredLabel: this.__series[i].coloredLegend,
						icon: icon
					};
					items.push(pItem);

					if (this.__series[i].treeLegend == true) {

						treeUsed = true;

						for (j = 0; j < this.__series[i].__points.length; j++) {

							if (this.__x_axis_text_values) { itemCaption = this.__series[i].__points[j].__x_label.__label; }
							else if (this.__y_axis_text_values) { itemCaption = this.__series[i].__points[j].__y_label.__label; }
							else if (this.__series[i].__points[j].label != "" && this.__series[i].__points[j].label != undefined) { itemCaption = this.__series[i].__points[j].label; }
							else { itemCaption = this.__series[i].__points[j].__x(); }

							items.push({
								caption: itemCaption,
								color: (this.__series[i].__points[j].__color!=undefined)?EJSC.utility.__getColor(this.__series[i].__points[j].__color).hex:this.__series[i].color,
								coloredLabel: true,
								icon: null
							});

						}

					} else if (this.__series[i].__subtype == 'stacked') {

						treeUsed = true;

						for (j = 0; j < this.__series[i].__series.length; j++) {

							var icon = this.__series[i].__series[j].__type;
							if( icon == 'bar' ) {
								if( this.__series[i].orientation == 'vertical' )
									icon = 'column';
							}

							items.push({
								caption: this.__series[i].__series[j].title,
								color: (this.__series[i].__series[j].__color!=undefined)?EJSC.utility.__getColor(this.__series[i].__series[j].__color).hex:this.__series[i].color,
								coloredLabel: true,
								icon: icon,
								child: true
							});

						}

					}

				}

			}

			// Determine the widest and tallest series title.
			var cur_label_size = { width: 0, height: 0 };
			var max_label_size = { width: 0, height: 0 };

			for (i = 0; i < items.length; i++) {

				cur_label_size = __textSize(items[i].caption, "Verdana", "10px", "normal", "");

				if (cur_label_size.width > max_label_size.width) { max_label_size.width = cur_label_size.width; }
				if (cur_label_size.height > max_label_size.height) { max_label_size.height = cur_label_size.height; }

			}

			var legend_header_size = __textSize(title, "Verdana", "9px", "normal", "");
			legend_header_size.height += 8; // 8 is 4 px padding top and bottom
			legend_header_size.width += 8;

			// Adjust max height/width to account for colored box and margins
			max_label_size.width += 28; // | 4px [20px] 4px Caption 4px |
			max_label_size.height += 4; // 4px top and bottom

			var legend_width, legend_height;
			var num_cols, num_rows;

			// Default orientation to horizontal
			if (exportOptions.orientation == "horizontal") {

				// Horizontal output, determine the height, width is static
				legend_width = this.__el.offsetWidth;

				// Based on the widest caption, determine the number of columns available
				num_cols = Math.floor((legend_width - 10) / max_label_size.width); // - 10 is 2px border + 8 px padding

				if (num_cols > items.length) {
					num_cols = items.length;
					if (num_cols == 0) { num_cols = 1; }
				}
				max_label_size.width = Math.floor((legend_width - 10) / num_cols);

				num_rows = Math.ceil(items.length / num_cols);

				// Calculate legend height
				legend_height = num_rows * max_label_size.height;

				// Adjust for border and padding
				legend_height += 9;

				if (exportOptions.show_title && title != "") {
					legend_height += legend_header_size.height;
				}

			} else {

				// Vertical output, determine the width, height is static
				legend_height = this.__el.offsetHeight;

				// Based on the tallest caption, determine the number of rows available
				num_rows = Math.floor((legend_height - ((exportOptions.show_title && title != "")?legend_header_size.height:0) - 10) / max_label_size.height); // - 10 is 2px border + 8 px padding
				if (num_rows > items.length) {
					num_rows = items.length;
					if (num_rows == 0) { num_rows = 1; }
				}

				num_cols = Math.ceil(items.length / num_rows);

				// Calculate legend width
				legend_width = num_cols * max_label_size.width;

				if (legend_header_size.width > legend_width) {
					legend_width = legend_header_size.width + 10;
				}

				// Adjust for border and padding
				legend_width += 9;

			}

			var canvas = new EJSC.SVGCanvas(legend_height, legend_width, exportOptions.namespace);
			canvas.textPrefix	= this.__id + "_legend";
			var ctx = canvas.getContext("2d");

			if (exportOptions.show_title && title != "") {
				ctx.lineWidth = 0;
				ctx.fillStyle = "rgba(0,0,0,.1)";
				ctx.moveTo(0, 0);
				ctx.lineTo(0, legend_header_size.height);
				ctx.lineTo(legend_width, legend_header_size.height);
				ctx.lineTo(legend_width, 0);
				ctx.lineTo(0, 0);
				ctx.fill();

				// Draw caption
				ctx.lineWidth = 1;
				ctx.fillStyle = "rbga(0,0,0,1)";
				ctx.beginPath();
				ctx.fontSize = "10px";
				ctx.fontFamily = "Verdana";
				ctx.fontWeight = "normal";
				ctx.fontStyle = "";
				ctx.drawText(4, legend_header_size.height - 5, legend_width - 4, legend_header_size.height - 5, title, "left");
				ctx.stroke();
			}

			var col = 0;
			var row = 0;

			var top, left;
			var iconType;

			for (i = 0; i < items.length; i++) {

				// Calculate top left of current item
				top = (row * max_label_size.height) + ((exportOptions.show_title && title != "")?legend_header_size.height:0) + 4;
				left = (col * max_label_size.width);

				// Draw icon
				ctx.lineWidth = 1;
				ctx.strokeStyle = "rgba(0,0,0,1)";
				ctx.fillStyle = EJSC.utility.__getColor(items[i].color).rgba;

				iconType = items[i].icon;//.match(/.*\/(.*)\.(gif|jpg|png)$/)[1];
				try {
					if( iconType != null )
						__icons["__draw_" + iconType](ctx, { l: left + 4 + ((items[i].child==true)?(20):(0)), t: top, r: left + 16, b: top + max_label_size.height - 2 });
				} catch (e) {
					// Unknown/unimplemented icon, draw a colored box
					ctx.beginPath();
					ctx.moveTo(left + 4, top + 3);
					ctx.lineTo(left + 4, top + max_label_size.height);
					ctx.lineTo(left + 16, top + max_label_size.height);
					ctx.lineTo(left + 16, top + 3);
					ctx.lineTo(left + 4, top + 3);
					ctx.fill();
				}

				// Draw label
				ctx.lineWidth = 1;
				ctx.fillStyle = items[i].coloredLabel?EJSC.utility.__getColor(items[i].color).rgba:"rgba(0,0,0,1)";
				ctx.fontSize = "10px";
				ctx.fontFamily = "Verdana";
				ctx.fontWeight = "Normal";
				ctx.fontStyle = "";
				ctx.drawText(left + 20 + ((iconType==null||items[i].child==true)?(20):(0)), top + max_label_size.height - 4, left + max_label_size.width, top + max_label_size.height - 4, items[i].caption, "left");
				ctx.stroke();

				// Adjust col/row
				if (exportOptions.orientation == "horizontal") {
					if (col == (num_cols - 1)) {
						col = 0;
						row++;
					} else {
						col++;
					}
				} else {
					if (row == (num_rows - 1)) {
						row = 0;
						col++;
					} else {
						row++;
					}
				}

			}

			// Draw border
			if (exportOptions.border.show) {
				ctx.lineWidth = exportOptions.border.size;
				ctx.strokeStyle = EJSC.utility.__getColor(exportOptions.border.color).rgba;

				ctx.beginPath();

				ctx.moveTo(0,0);
				ctx.lineTo(0,legend_height);
				ctx.lineTo(legend_width, legend_height);
				ctx.lineTo(legend_width, 0);
				ctx.lineTo(0,0);
				ctx.stroke();

				if (exportOptions.show_title && title != "") {
					ctx.lineWidth = 0.5;

					ctx.beginPath();
					ctx.moveTo(0, legend_header_size.height);
					ctx.lineTo(legend_width, legend_header_size.height);
					ctx.stroke();
				}
			}

			var result = canvas.exportSVG(exportOptions.includeHeader, exportOptions.height, exportOptions.width);
			return result;

		} catch (e) {
		} finally {
		}

	};

})();