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

	//====================//
	//     StockPoint     //
	//====================//

	EJSC.StockPoint = function( x , high , low , open , close , label , userdata , owner ) {

		EJSC.Point.__extendTo(this);

		this.__owner 		= owner;

		this.x				= (x.__label == undefined?x:x.__label);
		this.__x_label		= x;

		// JGD: 2008-09-09 - Fixed to parseFloat for Y-axis values
		this.high			= (high.__label==undefined?parseFloat(high):high.__label);
		this.low			= (low.__label==undefined?parseFloat(low):low.__label);
        this.open           = (open===null?null:(open.__label==undefined?parseFloat(open):open.__label));
        this.close          = (close===null?null:(close.__label==undefined?parseFloat(close):close.__label));

		this.y = ( this.high + this.low ) / 2;

		this.label          = label;
		this.userdata		= userdata;

		this.__x 			= function() {
			if (this.__x_label.__label == undefined) { this.__x = function() { return this.x; }; }
			else { this.__x = function() { return this.__x_label.__index; }; }
			return this.__x();
		};
		this.__y_label		= this.y;

		this.__y 			= function() {
			return this.y;
		};

	};

	//=====================//
	//  CandlestickSeries  //
	//=====================//

	EJSC.CandlestickSeries = function( dh , options ) {

		EJSC.Series.__extendTo(this);

		this.__doSetDataHandler(dh, false);

		var self = this;

		this.__type		= 'candle';

		this.__drawing	= false;
		this.__hasData	= false;

		this.color 		= 'rgb(0,0,0)';
		this.opacity	= 100;
		this.__padding = { x_min: (this.y_axis=="left"?5:0), x_max: (this.y_axis=="left"?0:5), y_min: 5, y_max: 5 };
		this.padding = { x_axis_min: undefined, x_axis_max: undefined, y_axis_min: undefined, y_axis_max: undefined };

		this.gain = {
			lineColor		: 'rgb(151,183,247)' ,
			lineOpacity		: 100 ,
			color			: 'rgb(151,183,247)' ,
			opacity			: 50
		};

		this.loss = {
			lineColor		: 'rgb(249,95,95)' ,
			lineOpacity		: 100 ,
			color			: 'rgb(249,95,95)' ,
			opacity			: 50
		};

		this.lineWidth	= 1;

		this.intervalOffset	= 0.5;

		this.__interval 		= undefined;

		this.__copyOptions(options);

	};

	EJSC.Series.__extendTo( EJSC.CandlestickSeries );

	var ___candlestickseries = EJSC.CandlestickSeries.prototype;

		___candlestickseries.__doReload = function() {

/* JGD - 2011-02-28 - Fixed to reload data correctly */
			this.__points = [];
			this.__hasData = false;
			this.__dataHandler.__loading = false;
			this.__dataHandler.__loaded = false;
			this.__dataHandler.loadData();

		};

		___candlestickseries.__doSetDataHandler = function( handler , reload ) {

			var self 			= this;
			this.__points 		= [];
			this.__dataHandler 	= handler;

			this.__dataHandler.__init(this,
				{
					x: null,
					high: null,
					low: null,
					open: null,
					close: null,
					label: null,
					userdata: null
				}
			);

			if (reload) {
				this.__dataHandler.__loadData();
			}

		};

		___candlestickseries.__doOnDataAvailable = function( data ) {

			if (data.length == 0) {
				this.__hasData = false;
				return;
			}

			var x_axis 			= this.__getChart()["axis_" + this.x_axis];
			var y_axis 			= this.__getChart()["axis_" + this.y_axis];
			var x_string_values = x_axis.__text_values.__count() > 0 || x_axis.force_static_points || !EJSC.utility.__stringIsNumber(data[0].x);
			var y_string_values = y_axis.__text_values.__count() > 0 || y_axis.force_static_points || !EJSC.utility.__stringIsNumber(data[0].y);

			for( var i=0 ; i<data.length ; i++ ) {

				if (x_string_values) {
					data[i].x = x_axis.__text_values.__add(data[i].x);
				} else {
					data[i].x = parseFloat(data[i].x);
				}

				data[i].low 	= parseFloat( data[i].low );
				data[i].high	= parseFloat( data[i].high );

				if( data[i].open == 'null' || data[i].open === '' ) 	data[i].open 	= null;
				if( data[i].close == 'null' || data[i].close === '' ) 	data[i].close 	= null;

				this.__points.push(new EJSC.StockPoint(data[i].x, data[i].high , data[i].low , data[i].open , data[i].close , data[i].label, data[i].userdata, this));

			}

			this.__hasData = true;

		};

		___candlestickseries.__doResetExtremes = function() {

			this.__minX = undefined;
			this.__maxX = undefined;
			this.__minY = undefined;
			this.__maxY = undefined;

		};

		___candlestickseries.__doCalculateExtremes = function() {

			if (this.__drawing || !this.__getHasData()) { return; }

			var points = this.__points;

			for (var i = 0; i < points.length; i++) {
				if ( this.__minX == undefined || points[i].__x() < this.__minX)	this.__minX = points[i].__x();
				if ( this.__maxX == undefined || points[i].__x() > this.__maxX)	this.__maxX = points[i].__x();
				if ( this.__minY == undefined || points[i].low < this.__minY)	this.__minY = points[i].low;
				if ( this.__maxY == undefined || points[i].high > this.__maxY)	this.__maxY = points[i].high;
			}

			this.__interval = this.__getInterval();

			this.__minX = this.__minX - (this.__interval / 2);
			this.__maxX = this.__maxX + (this.__interval / 2);

		};

		___candlestickseries.__doGetYRange = function( screenMinX , screenMaxX ) {

			var chart = this.__getChart();
			var x_axis = chart["axis_" + this.x_axis];
			var y_axis = chart["axis_" + this.y_axis];

			var minX = x_axis.__px2pt(screenMinX + chart.__draw_area.left);
			var maxX = x_axis.__px2pt(screenMaxX + chart.__draw_area.left);

			var i, minY, maxY;

			for ( i=0 ; i<this.__points.length ; i++ ) {
				if (this.__points[i].__x() >= minX && this.__points[i].__x() <= maxX) {

					if ( minY == undefined || minY > this.__points[i].low ) 	minY = this.__points[i].low;
					if ( maxY == undefined || maxY < this.__points[i].high ) 	maxY = this.__points[i].high;

				}
			}

			if (minY == undefined || maxY == undefined) { return null; }
			else {
				return {
					min: y_axis.__pt2px(maxY) - chart.__draw_area.top,
					max: y_axis.__pt2px(minY) - chart.__draw_area.top
				};
			}

		};

		___candlestickseries.__doGetXRange = function( screenMinY , screenMaxY ) {

			var chart = this.__getChart();
			var x_axis = chart["axis_" + this.x_axis];
			var y_axis = chart["axis_" + this.y_axis];

			var minY = y_axis.__px2pt(screenMinY + chart.__draw_area.top);
			var maxY = y_axis.__px2pt(screenMaxY + chart.__draw_area.top);

			var i, minX, maxX;

			var interval = this.__getInterval();

			for ( i=0 ; i<this.__points.length ; i++ ) {
				if (this.__points[i].low <= maxY && this.__points[i].high >= minY) {

					if ( minX == undefined || minX > this.__points[i].__x() - interval ) 	minX = this.__points[i].__x() - interval;
					if ( maxX == undefined || maxX < this.__points[i].__x() + interval ) 	maxX = this.__points[i].__x() + interval;

				}
			}

			if (minX == undefined || maxX == undefined) { return null; }
			else {
				return {
					min: y_axis.__pt2px(maxX) - chart.__draw_area.left,
					max: y_axis.__pt2px(minX) - chart.__draw_area.left
				};
			}

		};

		___candlestickseries.__doFindClosestPoint = function( mouse , use_proximity ) {

			var chart = this.__getChart();
			var x_axis = chart["axis_" + this.x_axis];
			var y_axis = chart["axis_" + this.y_axis];

			var x = x_axis.__px2pt(mouse.x + chart.__draw_area.left); // Convert screen pixels to scaled units applicable to this series
			var y = y_axis.__px2pt(mouse.y + chart.__draw_area.top);
			var line_x = (x_axis.__scale * this.lineWidth );
			var line_y = (x_axis.__scale * this.lineWidth );
			var snap_x = (x_axis.__scale * chart.proximity_snap); // Determine proximity_snap dimensions in scaled units
			var snap_y = (y_axis.__scale * chart.proximity_snap);

			var x1, x2, x3, y1, y2, y3 , y4;

			var distance = EJSC.math.__distance;

			var plotWidth = ( this.__interval * this.intervalOffset );

			for( var i=0 ; i<this.__points.length ; i++ ) {
				x2 = this.__points[i].__x();
				x1 = x2 - ( plotWidth / 2 );
				x3 = x2 + ( plotWidth / 2 );
				y1 = this.__points[i].low;
				if( this.__points[i].open == null && this.__points[i].close == null ) {
					y2 = null;
					y3 = null;
				} else if( this.__points[i].open == null || this.__points[i].close == null ) {
					y2 = ( this.__points[i].open == null ) ? this.__points[i].close : this.__points[i].open;
					y3 = ( this.__points[i].open == null ) ? this.__points[i].close : this.__points[i].open;
				} else {
					y2 = ( this.__points[i].open > this.__points[i].close ) ? this.__points[i].close : this.__points[i].open;
					y3 = ( this.__points[i].open <= this.__points[i].close ) ? this.__points[i].close : this.__points[i].open;
				}
				y4 = this.__points[i].high;
				if(
					x >= ( x2 - snap_x - line_x ) && x <= ( x2 + snap_x + line_x ) &&
					y >= ( y1 - snap_y - line_y ) && y <= ( y4 + snap_y + line_y )
				) {
					return {
						distance: distance( mouse.x , mouse.y , x_axis.__pt2px(x2) - chart.__draw_area.left , y_axis.__pt2px( ( y4 + y1 ) / 2 ) - chart.__draw_area.top ) ,
						point: this.__points[i]
					};
				} else if( y2 != null && y3 != null ) {
					if(
						x >= ( x1 - snap_x - line_x ) && x <= ( x3 + snap_x + line_x ) &&
						y >= ( y2 - snap_y - line_y ) && y <= ( y3 + snap_y + line_y )
					) {

						return {
							distance: distance( mouse.x , mouse.y , x_axis.__pt2px(x2) - chart.__draw_area.left , y_axis.__pt2px( ( y4 + y1 ) / 2 ) - chart.__draw_area.top ) ,
							point: this.__points[i]
						};
					}
				}
			}

			return null;

		};

		___candlestickseries.__doSelectPoint = function(point, sticky) {

			var x_axis = this.__getChart()["axis_" + this.x_axis];
			var y_axis = this.__getChart()["axis_" + this.y_axis];

			var x = point.__x();
			var y = ( point.high + point.low ) / 2;

			var plotWidth = ( this.__interval * this.intervalOffset );

			//	If point is outside range, unselect point
			if (x - plotWidth > x_axis.__current_max) {
				if (!sticky) { return null; }
				else { x = x_axis.__current_max; }
			} else if (x + plotWidth < x_axis.__current_min) {
				if (!sticky) { return null; }
				else { x = x_axis.__current_min; }
			}

			// Extract values for hint replacement
			var result = {
				series_title: "<label>" + this.title + "</label>",
				xaxis: x_axis.__getHintCaption(),
				yaxis: y_axis.__getHintCaption(),
				x: x_axis.__getLabel(point.__x()),
				low: point.low,
				high: point.high,
				open: point.open,
				close: point.close,
				label: point.label,
				__defaultHintString: "[series_title]<br/>[xaxis] [x]<br/>Low: [low]<br/>High: [high]<br/>Open: [open]<br/>Close: [close]",
				__center: false

			};

			// Determine where to show the hint
			if (y > y_axis.__current_max) {
				y = y_axis.__current_max;
			} else if (y < y_axis.__current_min) {
				y = y_axis.__current_min
			}
			result.__position = {
				x: x_axis.__pt2px(x),
				y: y_axis.__pt2px(y)
			};

			return result;

		};

		___candlestickseries.__doSelectPrevious = function( point ) {

			var i;
			var points 	= this.__points;
			var x_axis = this.__getChart()["axis_" + this.x_axis];

			for (i = 0; i < points.length; i++) {
				if (points[i] == point)
					break;
			}

			i--;

			if (i < 0 || points[i].__x() < x_axis.__current_min) {
				i = points.length - 1;
				while (points[i].__x() > x_axis.__current_max) i--;
			}

			this.__owner.__selectPoint(points[i]);

		};

		___candlestickseries.__doSelectNext = function( point ) {

			var i;
			var points 	= this.__points;
			var x_axis = this.__getChart()["axis_" + this.x_axis];

			for (i = 0; i < points.length; i++) {
				if (points[i] == point)
					break;
			}

			i++;

			if (i == points.length || points[i].__x() > x_axis.__current_max) {
				i = 0;
				while (points[i].__x() < x_axis.__current_min) i++;
			}

			this.__owner.__selectPoint(points[i]);

		};

		___candlestickseries.__free = function() {

			this.__dataHandler.onDataAvailable = null;
			this.__dataHandler.__data = [];
			this.__dataHandler = null;
			this.__points = [];

		};

		___candlestickseries.__doDraw = function( ctx ) {

			if( this.__drawing ) return;
			if( !this.visible ) return;

			if (!this.__getHasData()) {

			var dh = this.__dataHandler;
			window.setTimeout(function() { dh.__loadData(); }, 1);
			return;

			} else {

				this.__drawing = true;

				try {

					var points = this.__points;

					var x_axis = this.__getChart()["axis_" + this.x_axis];
					var y_axis = this.__getChart()["axis_" + this.y_axis];

					var x_min			= x_axis.__current_min;
					var x_max			= x_axis.__current_max;
					var y_min			= y_axis.__current_min;
					var y_max			= y_axis.__current_max;

					var canvas_width	= this.__getDrawArea().width;
					var canvas_height	= this.__getDrawArea().height;
					var canvas_top		= this.__getDrawArea().top;
					var canvas_left		= this.__getDrawArea().left;

					var plotWidth = ( this.__interval * this.intervalOffset ) / x_axis.__scale;

					ctx.lineWidth 	= this.lineWidth;
					ctx.strokeStyle = ( EJSC.utility.__getColor( this.gain.lineColor , this.gain.lineOpacity / 100 ) ).rgba;
					ctx.fillStyle 	= ( EJSC.utility.__getColor( this.gain.color , this.gain.opacity / 100 ) ).rgba;
					ctx.beginPath();

					// Loop through for positive days
					for( var i=0 ; i<points.length ; i++ ) {

						if( points[i].open == null || points[i].close == null ) continue;

						if( points[i].open > points[i].close ) continue;

						var x1 = x_axis.__pt2px( points[i].__x() ) - ( plotWidth / 2 );
						var x2 = x1 + ( plotWidth / 2 );
						var x3 = x1 + plotWidth;
						var y1 = y_axis.__pt2px( points[i].low );
						var y2 = y_axis.__pt2px( ( ( points[i].open < points[i].close ) ? ( points[i].open ) : ( points[i].close ) ) );
						var y3 = y_axis.__pt2px( ( ( points[i].open >= points[i].close ) ? ( points[i].open ) : ( points[i].close ) ) );
						var y4 = y_axis.__pt2px( points[i].high );

						if( x1 > canvas_left + canvas_width ) 	continue;
						if( x3 < canvas_left ) 					continue;
						if( y1 < canvas_top ) 					continue;
						if( y4 > canvas_top + canvas_height ) 	continue;

						if( x1 < canvas_left ) 					x1 = canvas_left - this.lineWidth;
						if( x3 > canvas_left + canvas_width ) 	x3 = canvas_left + canvas_width + this.lineWidth;
						if( y1 > canvas_top + canvas_height ) 	y1 = canvas_top + canvas_height + this.lineWidth;
						if( y4 < canvas_top ) 					y4 = canvas_top - this.lineWidth;

						if( x2 >= canvas_left && x2 < canvas_left + canvas_width ) {
							ctx.moveTo( x2 , y1 );
							ctx.lineTo( x2 , y2 );
							ctx.moveTo( x2 , y3 );
							ctx.lineTo( x2 , y4 );
						}

						ctx.moveTo( x1 , y2 );
						ctx.lineTo( x3 , y2 );
						ctx.lineTo( x3 , y3 );
						ctx.lineTo( x1 , y3 );
						ctx.lineTo( x1 , y2 );

					}

					ctx.closePath();
					if (EJSC.__isIE) {
						var save_path = ctx.currentPath_;
						ctx.fill();
						ctx.currentPath_ = save_path;
						ctx.stroke();
					} else {
						ctx.fill();
						ctx.stroke();
					}

					ctx.lineWidth 	= this.lineWidth;
					ctx.strokeStyle = ( EJSC.utility.__getColor( this.loss.lineColor , this.loss.lineOpacity / 100 ) ).rgba;
					ctx.fillStyle 	= ( EJSC.utility.__getColor( this.loss.color , this.loss.opacity / 100 ) ).rgba;
					ctx.beginPath();

					// Loop through for negative days
					for( var i=0 ; i<points.length ; i++ ) {

						if( points[i].open == null || points[i].close == null ) continue;

						if( points[i].open <= points[i].close ) continue;

						var x1 = x_axis.__pt2px( points[i].__x() ) - ( plotWidth / 2 );
						var x2 = x1 + ( plotWidth / 2 );
						var x3 = x1 + plotWidth;
						var y1 = y_axis.__pt2px( points[i].low );
						var y2 = y_axis.__pt2px( ( ( points[i].open < points[i].close ) ? ( points[i].open ) : ( points[i].close ) ) );
						var y3 = y_axis.__pt2px( ( ( points[i].open >= points[i].close ) ? ( points[i].open ) : ( points[i].close ) ) );
						var y4 = y_axis.__pt2px( points[i].high );

						if( x1 > canvas_left + canvas_width ) 	continue;
						if( x3 < canvas_left ) 					continue;
						if( y1 < canvas_top ) 					continue;
						if( y4 > canvas_top + canvas_height ) 	continue;

						if( x1 < canvas_left ) 					x1 = canvas_left - this.lineWidth;
						if( x3 > canvas_left + canvas_width ) 	x3 = canvas_left + canvas_width + this.lineWidth;
						if( y1 > canvas_top + canvas_height ) 	y1 = canvas_top + canvas_height + this.lineWidth;
						if( y4 < canvas_top ) 					y4 = canvas_top - this.lineWidth;

						if( x2 >= canvas_left && x2 < canvas_left + canvas_width ) {
							ctx.moveTo( x2 , y1 );
							ctx.lineTo( x2 , y2 );
							ctx.moveTo( x2 , y3 );
							ctx.lineTo( x2 , y4 );
						}

						ctx.moveTo( x1 , y2 );
						ctx.lineTo( x3 , y2 );
						ctx.lineTo( x3 , y3 );
						ctx.lineTo( x1 , y3 );
						ctx.lineTo( x1 , y2 );

					}

					ctx.closePath();
					if (EJSC.__isIE) {
						var save_path = ctx.currentPath_;
						ctx.fill();
						ctx.currentPath_ = save_path;
						ctx.stroke();
					} else {
						ctx.fill();
						ctx.stroke();
					}

					ctx.lineWidth 	= this.lineWidth;
					ctx.strokeStyle = ( EJSC.utility.__getColor( this.color , this.opacity / 100 ) ).rgba;
					ctx.beginPath();

					// Loop through for indifferent days
					for( var i=0 ; i<points.length ; i++ ) {

						if( points[i].open != null && points[i].close != null ) continue;

						var x1 = x_axis.__pt2px( points[i].__x() ) - ( plotWidth / 2 );
						var x2 = x1 + ( plotWidth / 2 );
						var x3 = x1 + plotWidth;
						var y1 = y_axis.__pt2px( points[i].low );
						var y2 = ( ( points[i].open == null ) ? ( ( ( points[i].close == null ) ? ( null ) : ( y_axis.__pt2px( points[i].close ) ) ) ) : ( y_axis.__pt2px( points[i].open ) ) );
						var y4 = y_axis.__pt2px( points[i].high );

						if( x1 > canvas_left + canvas_width ) 	continue;
						if( x3 < canvas_left ) 					continue;
						if( y1 < canvas_top ) 					continue;
						if( y4 > canvas_top + canvas_height ) 	continue;

						if( x1 < canvas_left ) 					x1 = canvas_left - this.lineWidth;
						if( x3 > canvas_left + canvas_width ) 	x3 = canvas_left + canvas_width + this.lineWidth;
						if( y1 > canvas_top + canvas_height ) 	y1 = canvas_top + canvas_height + this.lineWidth;
						if( y4 < canvas_top ) 					y4 = canvas_top - this.lineWidth;

						if( x2 >= canvas_left && x2 < canvas_left + canvas_width ) {
							ctx.moveTo( x2 , y1 );
							ctx.lineTo( x2 , y4 );
						}

						if( y2 != null ) {
							ctx.moveTo( x1 , y2 );
							ctx.lineTo( x3 , y2 );
						}

					}

					ctx.closePath();
					ctx.stroke();

				} catch (e) {
				} finally {
					this.__drawing = false;
				}

			}

		};

		___candlestickseries.__getInterval = function() {

			var result 	= undefined;
			var points 	= this.__points;
			var len 	= this.__points.length;
			var diff;
			var i = 0;

			for (; i < len - 1; i++) {
				diff = (points[i+1].__x() - points[i].__x());
				if (result == undefined || (result > diff)) {
					if (diff != 0) {
						result = diff;
					}
				}
			}

			if (result == undefined) { result = 1; }
			return result;

		};

	___candlestickseries.__doGetLegendIcon = function() {

		// JHM: 2008-08-16 - Updated to correct non-secure warning in https session
		return "candlestick";

	};


	//=====================//
	//  CandlestickSeries  //
	//=====================//

	EJSC.OpenHighLowCloseSeries = function( dh , options ) {

		EJSC.Series.__extendTo(this);

		this.__doSetDataHandler(dh, false);

		var self = this;

		this.__type		= 'hloc';

		this.__drawing	= false;
		this.__hasData	= false;

		this.color 		= 'rgb(0,0,0)';
		this.opacity	= 100;
		this.__padding = { x_min: (this.y_axis=="left"?5:0), x_max: (this.y_axis=="left"?0:5), y_min: 5, y_max: 5 };
		this.padding = { x_axis_min: undefined, x_axis_max: undefined, y_axis_min: undefined, y_axis_max: undefined };

		this.gain = {
			lineColor		: 'rgb(151,183,247)' ,
			lineOpacity		: 100 ,
			color			: 'rgb(151,183,247)' ,
			opacity			: 50
		};

		this.loss = {
			lineColor		: 'rgb(249,95,95)' ,
			lineOpacity		: 100 ,
			color			: 'rgb(249,95,95)' ,
			opacity			: 50
		};

		this.lineWidth	= 1;

		this.intervalOffset	= 0.5;

		this.__interval 		= undefined;

		this.__copyOptions(options);

	};

	EJSC.Series.__extendTo( EJSC.OpenHighLowCloseSeries );

	var ___ohlcseries = EJSC.OpenHighLowCloseSeries.prototype;

		___ohlcseries.__doReload = function() {

/* JGD - 2011-02-28 - Fixed to reload data correctly */
			this.__points = [];
			this.__hasData = false;
			this.__dataHandler.__loading = false;
			this.__dataHandler.__loaded = false;
			this.__dataHandler.loadData();

		};

		___ohlcseries.__doSetDataHandler = function( handler , reload ) {

			var self 			= this;
			this.__points 		= [];
			this.__dataHandler 	= handler;

			this.__dataHandler.__init(this,
				{
					x: null,
					high: null,
					low: null,
					open: null,
					close: null,
					label: null,
					userdata: null
				}
			);

			if (reload) {
				this.__dataHandler.__loadData();
			}

		};

		___ohlcseries.__doOnDataAvailable = function( data ) {

			if (data.length == 0) {
				this.__hasData = false;
				return;
			}

			var x_axis 			= this.__getChart()["axis_" + this.x_axis];
			var y_axis 			= this.__getChart()["axis_" + this.y_axis];
			var x_string_values = x_axis.__text_values.__count() > 0 || x_axis.force_static_points || !EJSC.utility.__stringIsNumber(data[0].x);
			var y_string_values = y_axis.__text_values.__count() > 0 || y_axis.force_static_points || !EJSC.utility.__stringIsNumber(data[0].y);

			for( var i=0 ; i<data.length ; i++ ) {

				if (x_string_values) {
					data[i].x = x_axis.__text_values.__add(data[i].x);
				} else {
					data[i].x = parseFloat(data[i].x);
				}

				data[i].low 	= parseFloat( data[i].low );
				data[i].high	= parseFloat( data[i].high );

				if( data[i].open == 'null' || data[i].open === '' ) 	data[i].open 	= null;
				if( data[i].close == 'null' || data[i].close === '' ) 	data[i].close 	= null;

				this.__points.push(new EJSC.StockPoint(data[i].x, data[i].high , data[i].low , data[i].open , data[i].close , data[i].label, data[i].userdata, this));

			}

			this.__hasData = true;

		};

		___ohlcseries.__doResetExtremes = function() {

			this.__minX = undefined;
			this.__maxX = undefined;
			this.__minY = undefined;
			this.__maxY = undefined;

		};

		___ohlcseries.__doCalculateExtremes = function() {

			if (this.__drawing || !this.__getHasData()) { return; }

			var points = this.__points;

			for (var i = 0; i < points.length; i++) {
				if ( this.__minX == undefined || points[i].__x() < this.__minX)	this.__minX = points[i].__x();
				if ( this.__maxX == undefined || points[i].__x() > this.__maxX)	this.__maxX = points[i].__x();
				if ( this.__minY == undefined || points[i].low < this.__minY)	this.__minY = points[i].low;
				if ( this.__maxY == undefined || points[i].high > this.__maxY)	this.__maxY = points[i].high;
			}

			this.__interval = this.__getInterval();

			this.__minX = this.__minX - (this.__interval / 2);
			this.__maxX = this.__maxX + (this.__interval / 2);

		};

		___ohlcseries.__doGetYRange = function( screenMinX , screenMaxX ) {

			var chart = this.__getChart();
			var x_axis = chart["axis_" + this.x_axis];
			var y_axis = chart["axis_" + this.y_axis];

			var minX = x_axis.__px2pt(screenMinX + chart.__draw_area.left);
			var maxX = x_axis.__px2pt(screenMaxX + chart.__draw_area.left);

			var i, minY, maxY;

			for ( i=0 ; i<this.__points.length ; i++ ) {
				if (this.__points[i].__x() >= minX && this.__points[i].__x() <= maxX) {

					if ( minY == undefined || minY > this.__points[i].low ) 	minY = this.__points[i].low;
					if ( maxY == undefined || maxY < this.__points[i].high ) 	maxY = this.__points[i].high;

				}
			}

			if (minY == undefined || maxY == undefined) { return null; }
			else {
				return {
					min: y_axis.__pt2px(maxY) - chart.__draw_area.top,
					max: y_axis.__pt2px(minY) - chart.__draw_area.top
				};
			}

		};

		___ohlcseries.__doGetXRange = function( minY , maxY ) {

			var chart = this.__getChart();
			var x_axis = chart["axis_" + this.x_axis];
			var y_axis = chart["axis_" + this.y_axis];

			var minY = y_axis.__px2pt(screenMinY + chart.__draw_area.top);
			var maxY = y_axis.__px2pt(screenMaxY + chart.__draw_area.top);

			var i, minX, maxX;

			var interval = this.__getInterval();

			for ( i=0 ; i<this.__points.length ; i++ ) {
				if (this.__points[i].low <= maxY && this.__points[i].high >= minY) {

					if ( minX == undefined || minX > this.__points[i].__x() - interval ) 	minX = this.__points[i].__x() - interval;
					if ( maxX == undefined || maxX < this.__points[i].__x() + interval ) 	maxX = this.__points[i].__x() + interval;

				}
			}

			if (minX == undefined || maxX == undefined) { return null; }
			else {
				return {
					min: y_axis.__pt2px(maxX) - chart.__draw_area.left,
					max: y_axis.__pt2px(minX) - chart.__draw_area.left
				};
			}

		};

		___ohlcseries.__doFindClosestPoint = function( mouse , use_proximity ) {

			var chart = this.__getChart();
			var x_axis = chart["axis_" + this.x_axis];
			var y_axis = chart["axis_" + this.y_axis];

			var x = x_axis.__px2pt(mouse.x + chart.__draw_area.left); // Convert screen pixels to scaled units applicable to this series
			var y = y_axis.__px2pt(mouse.y + chart.__draw_area.top);
			var line_x = (x_axis.__scale * this.lineWidth );
			var line_y = (x_axis.__scale * this.lineWidth );
			var snap_x = (x_axis.__scale * chart.proximity_snap); // Determine proximity_snap dimensions in scaled units
			var snap_y = (y_axis.__scale * chart.proximity_snap);

			var x1, x2, x3, y1, y2, y3 , y4;

			var distance = EJSC.math.__distance;

			var plotWidth = ( this.__interval * this.intervalOffset );

			for( var i=0 ; i<this.__points.length ; i++ ) {
				x2 = this.__points[i].__x();
				x1 = x2 - ( plotWidth / 2 );
				x3 = x2 + ( plotWidth / 2 );
				y1 = this.__points[i].low;
				y2 = this.__points[i].open;
				y3 = this.__points[i].close;
				y4 = this.__points[i].high;
				if(
					x >= ( x2 - snap_x - line_x ) && x <= ( x2 + snap_x + line_x ) &&
					y >= ( y1 - snap_y - line_y ) && y <= ( y4 + snap_y + line_y )
				) {
					return {
						distance: distance( mouse.x , mouse.y , x_axis.__pt2px(x2) - chart.__draw_area.left , y_axis.__pt2px( ( y4 + y1 ) / 2 ) - chart.__draw_area.top ) ,
						point: this.__points[i]
					};
				}
				if( y2 != null ) {
					if(
						x >= ( x1 - snap_x - line_x ) && x <= ( x2 + snap_x + line_x ) &&
						y >= ( y2 - snap_y - line_y ) && y <= ( y2 + snap_y + line_y )
					) {

						return {
							distance: distance( mouse.x , mouse.y , x_axis.__pt2px(x2) - chart.__draw_area.left , y_axis.__pt2px( ( y4 + y1 ) / 2 ) - chart.__draw_area.top ) ,
							point: this.__points[i]
						};
					}
				}
				if( y3 != null ) {
					if(
						x >= ( x2 - snap_x - line_x ) && x <= ( x3 + snap_x + line_x ) &&
						y >= ( y3 - snap_y - line_y ) && y <= ( y3 + snap_y + line_y )
					) {

						return {
							distance: distance( mouse.x , mouse.y , x_axis.__pt2px(x2) , y_axis.__pt2px( ( y4 + y1 ) / 2 ) ) ,
							point: this.__points[i]
						};
					}
				}
			}

			return null;

		};

		___ohlcseries.__doSelectPoint = function(point, sticky) {

			var x_axis = this.__getChart()["axis_" + this.x_axis];
			var y_axis = this.__getChart()["axis_" + this.y_axis];

			var x = point.__x();
			var y = ( point.high + point.low ) / 2;

			var plotWidth = ( this.__interval * this.intervalOffset );

			//	If point is outside range, unselect point
			if (x - plotWidth > x_axis.__current_max) {
				if (!sticky) { return null; }
				else { x = x_axis.__current_max; }
			} else if (x + plotWidth < x_axis.__current_min) {
				if (!sticky) { return null; }
				else { x = x_axis.__current_min; }
			}

			// Extract values for hint replacement
			var result = {
				series_title: "<label>" + this.title + "</label>",
				xaxis: x_axis.__getHintCaption(),
				yaxis: y_axis.__getHintCaption(),
				x: x_axis.__getLabel(point.__x()),
				low: point.low,
				high: point.high,
				open: point.open,
				close: point.close,
				label: point.label,
				__defaultHintString: "[series_title]<br/>[xaxis] [x]<br/>Low: [low]<br/>High: [high]<br/>Open: [open]<br/>Close: [close]",
				__center: false

			};

			// Determine where to show the hint
			if (y > y_axis.__current_max) {
				y = y_axis.__current_max;
			} else if (y < y_axis.__current_min) {
				y = y_axis.__current_min
			}
			result.__position = {
				x: x_axis.__pt2px(x),
				y: y_axis.__pt2px(y)
			};

			return result;

		};

		___ohlcseries.__doSelectPrevious = function( point ) {

			var i;
			var points 	= this.__points;
			var x_axis = this.__getChart()["axis_" + this.x_axis];

			for (i = 0; i < points.length; i++) {
				if (points[i] == point)
					break;
			}

			i--;

			if (i < 0 || points[i].__x() < x_axis.__current_min) {
				i = points.length - 1;
				while (points[i].__x() > x_axis.__current_max) i--;
			}

			this.__owner.__selectPoint(points[i]);

		};

		___ohlcseries.__doSelectNext = function( point ) {

			var i;
			var points 	= this.__points;
			var x_axis = this.__getChart()["axis_" + this.x_axis];

			for (i = 0; i < points.length; i++) {
				if (points[i] == point)
					break;
			}

			i++;

			if (i == points.length || points[i].__x() > x_axis.__current_max) {
				i = 0;
				while (points[i].__x() < x_axis.__current_min) i++;
			}

			this.__owner.__selectPoint(points[i]);

		};

		___ohlcseries.__free = function() {

			this.__dataHandler.onDataAvailable = null;
			this.__dataHandler.__data = [];
			this.__dataHandler = null;
			this.__points = [];

		};

		___ohlcseries.__doDraw = function( ctx ) {

			if( this.__drawing ) return;
			if( !this.visible ) return;

			if (!this.__getHasData()) {

			var dh = this.__dataHandler;
			window.setTimeout(function() { dh.__loadData(); }, 1);
			return;

			} else {

				this.__drawing = true;

				try {

					var points = this.__points;

					var x_axis = this.__getChart()["axis_" + this.x_axis];
					var y_axis = this.__getChart()["axis_" + this.y_axis];

					var x_min			= x_axis.__current_min;
					var x_max			= x_axis.__current_max;
					var y_min			= y_axis.__current_min;
					var y_max			= y_axis.__current_max;

					var canvas_width	= this.__getDrawArea().width;
					var canvas_height	= this.__getDrawArea().height;
					var canvas_top		= this.__getDrawArea().top;
					var canvas_left		= this.__getDrawArea().left;

					var plotWidth = ( this.__interval * this.intervalOffset ) / x_axis.__scale;

					ctx.lineWidth 	= this.lineWidth;
					ctx.strokeStyle = ( EJSC.utility.__getColor( this.gain.lineColor , this.gain.lineOpacity / 100 ) ).rgba;
					ctx.beginPath();

					// Loop through for positive days
					for( var i=0 ; i<points.length ; i++ ) {

						if( points[i].open == null || points[i].close == null ) continue;

						if( points[i].open > points[i].close ) continue;

						var x1 = x_axis.__pt2px( points[i].__x() ) - ( plotWidth / 2 );
						var x2 = x1 + ( plotWidth / 2 );
						var x3 = x1 + plotWidth;
						var y1 = y_axis.__pt2px( points[i].low );
						var y2 = y_axis.__pt2px( points[i].open );
						var y3 = y_axis.__pt2px( points[i].close );
						var y4 = y_axis.__pt2px( points[i].high );

						if( x1 > canvas_left + canvas_width ) 	continue;
						if( x3 < canvas_left ) 					continue;
						if( y1 < canvas_top ) 					continue;
						if( y4 > canvas_top + canvas_height ) 	continue;

						if( x1 < canvas_left ) 					x1 = canvas_left - this.lineWidth;
						if( x3 > canvas_left + canvas_width ) 	x3 = canvas_left + canvas_width + this.lineWidth;
						if( y1 > canvas_top + canvas_height ) 	y1 = canvas_top + canvas_height + this.lineWidth;
						if( y4 < canvas_top ) 					y4 = canvas_top - this.lineWidth;

						if( x2 >= canvas_left && x2 < canvas_left + canvas_width ) {
							ctx.moveTo( x2 , y1 );
							ctx.lineTo( x2 , y4 );
						}

						ctx.moveTo( x1 , y2 );
						ctx.lineTo( x2 , y2 );
						ctx.moveTo( x3 , y3 );
						ctx.lineTo( x2 , y3 );

					}

					ctx.closePath();
					ctx.stroke();

					ctx.lineWidth 	= this.lineWidth;
					ctx.strokeStyle = ( EJSC.utility.__getColor( this.loss.lineColor , this.loss.lineOpacity / 100 ) ).rgba;
					ctx.beginPath();

					// Loop through for negative days
					for( var i=0 ; i<points.length ; i++ ) {

						if( points[i].open == null || points[i].close == null ) continue;

						if( points[i].open <= points[i].close ) continue;

						var x1 = x_axis.__pt2px( points[i].__x() ) - ( plotWidth / 2 );
						var x2 = x1 + ( plotWidth / 2 );
						var x3 = x1 + plotWidth;
						var y1 = y_axis.__pt2px( points[i].low );
						var y2 = y_axis.__pt2px( points[i].open );
						var y3 = y_axis.__pt2px( points[i].close );
						var y4 = y_axis.__pt2px( points[i].high );

						if( x1 > canvas_left + canvas_width ) 	continue;
						if( x3 < canvas_left ) 					continue;
						if( y1 < canvas_top ) 					continue;
						if( y4 > canvas_top + canvas_height ) 	continue;

						if( x1 < canvas_left ) 					x1 = canvas_left - this.lineWidth;
						if( x3 > canvas_left + canvas_width ) 	x3 = canvas_left + canvas_width + this.lineWidth;
						if( y1 > canvas_top + canvas_height ) 	y1 = canvas_top + canvas_height + this.lineWidth;
						if( y4 < canvas_top ) 					y4 = canvas_top - this.lineWidth;

						if( x2 >= canvas_left && x2 < canvas_left + canvas_width ) {
							ctx.moveTo( x2 , y1 );
							ctx.lineTo( x2 , y4 );
						}

						ctx.moveTo( x1 , y2 );
						ctx.lineTo( x2 , y2 );
						ctx.moveTo( x3 , y3 );
						ctx.lineTo( x2 , y3 );

					}

					ctx.closePath();
					ctx.stroke();

					ctx.lineWidth 	= this.lineWidth;
					ctx.strokeStyle = ( EJSC.utility.__getColor( this.color , this.opacity / 100 ) ).rgba;
					ctx.beginPath();

					// Loop through for indifferent days
					for( var i=0 ; i<points.length ; i++ ) {

						if( points[i].open != null && points[i].close != null ) continue;

						var x1 = x_axis.__pt2px( points[i].__x() ) - ( plotWidth / 2 );
						var x2 = x1 + ( plotWidth / 2 );
						var x3 = x1 + plotWidth;
						var y1 = y_axis.__pt2px( points[i].low );
						var y2 = ( ( points[i].open == null ) ? ( null ) : ( y_axis.__pt2px( points[i].open ) ) );
						var y3 = ( ( points[i].close == null ) ? ( null ) : ( y_axis.__pt2px( points[i].close ) ) );
						var y4 = y_axis.__pt2px( points[i].high );

						if( x1 > canvas_left + canvas_width ) 	continue;
						if( x3 < canvas_left ) 					continue;
						if( y1 < canvas_top ) 					continue;
						if( y4 > canvas_top + canvas_height ) 	continue;

						if( x1 < canvas_left ) 					x1 = canvas_left - this.lineWidth;
						if( x3 > canvas_left + canvas_width ) 	x3 = canvas_left + canvas_width + this.lineWidth;
						if( y1 > canvas_top + canvas_height ) 	y1 = canvas_top + canvas_height + this.lineWidth;
						if( y4 < canvas_top ) 					y4 = canvas_top - this.lineWidth;

						if( x2 >= canvas_left && x2 < canvas_left + canvas_width ) {
							ctx.moveTo( x2 , y1 );
							ctx.lineTo( x2 , y4 );
						}

						if( y2 != null ) {
							ctx.moveTo( x1 , y2 );
							ctx.lineTo( x2 , y2 );
						} else if( y3 != null ) {
							ctx.moveTo( x3 , y3 );
							ctx.lineTo( x2 , y3 );
						}

					}

					ctx.closePath();
					ctx.stroke();

				} catch (e) {
				} finally {
					this.__drawing = false;
				}

			}

		};

		___ohlcseries.__getInterval = function() {

			var result 	= undefined;
			var points 	= this.__points;
			var len 	= this.__points.length;
			var diff;
			var i = 0;

			for (; i < len - 1; i++) {
				diff = (points[i+1].__x() - points[i].__x());
				if (result == undefined || (result > diff)) {
					if (diff != 0) {
						result = diff;
					}
				}
			}

			if (result == undefined) { result = 1; }
			return result;

		};

	___ohlcseries.__doGetLegendIcon = function() {

		// JHM: 2008-08-16 - Updated to correct non-secure warning in https session
		return "ohlc";

	};

})();