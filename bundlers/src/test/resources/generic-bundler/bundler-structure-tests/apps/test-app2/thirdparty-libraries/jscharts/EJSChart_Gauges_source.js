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

	// Cache math functions and variables
	var m_SQRT = Math.sqrt;
	var m_POW = Math.pow;
	var m_SIN = Math.sin;
	var m_COS = Math.cos;
	var m_TAN = Math.tan;
	var m_ATAN = Math.atan;
	var m_ROUND = Math.round;
	var m_FLOOR = Math.floor;
	var m_CEIL = Math.ceil;
	var m_PI = Math.PI;
	var m_PIx2 = m_PI * 2;
	var m_PId2 = m_PI / 2;

	//===================//
	//    Gauge Point    //
	//===================//

	EJSC.GaugePoint = function( x, label, userdata, owner ) {

		EJSC.Point.__extendTo(this);

		this.__owner 		= owner;
		this.x 				= x;
		this.label          = label;
		this.userdata		= userdata;

	};

	//====================//
	//    Gauge Series    //
	//====================//

	EJSC.GaugeSeries = function( dh , options ) {

		EJSC.Series.__extendTo(this);

		this.__doSetDataHandler(dh, false);

		var self 	= this;
		// JHM: 2008-06-05 - Added type property
		this.__type = "gauge";

		this.__needsXAxis = false;
		this.__needsYAxis = false;

		this.__copyOptions(options);

	};

	EJSC.Series.__extendTo( EJSC.GaugeSeries );

	//==============================//
	//    Gauge Series Prototype    //
	//==============================//

	var ___gaugeseries = EJSC.GaugeSeries.prototype;

		___gaugeseries.__doReload = function() {

/* JGD - 2011-02-28 - Fixed to reload data correctly */
			this.__points = [];
			this.__hasData = false;
			this.__dataHandler.__loading = false;
			this.__dataHandler.__loaded = false;
			this.__dataHandler.loadData();

		};

		___gaugeseries.__doSetDataHandler = function( handler , reload ) {

			var self = this;

			this.__points = [];

			this.__dataHandler = handler;
			this.__dataHandler.__init(this,
				{
					x: null,
					label: null,
					userdata: null
				}
			);

			if (reload) {
				this.__dataHandler.__loadData();
			}

		};

		___gaugeseries.__doOnDataAvailable = function( data ) {

			if (data.length == 0) {
				this.__hasData = false;
				return;
			}

			data[0].x = parseFloat(data[0].x);

			this.__points.push(new EJSC.GaugePoint(data[0].x, data[0].label, data[0].userdata, this));

			this.__hasData = true;

		};

		___gaugeseries.__free = function() {

			this.__dataHandler.onDataAvailable = null;
			this.__dataHandler.__data = [];
			this.__dataHandler = null;
			this.__points = [];

		};

	//===========================//
	//    Analog Gauge Series    //
	//===========================//

	EJSC.AnalogGaugeSeries = function( dh , options ) {

		var self 				= this;

		this.__doSetDataHandler(dh, false);

		this.__type 			= 'analoggauge';

		// JHM: 2007-09-27 - Added __previousLabel
		this.__previousLabel	= "";

		this.__points 			= [];
		this.__tickmarkers		= [];
		this.__inverse			= false;

		this.ranges				= [];

		this.position			= "center";							// topLeft, topCenter, topRight,
																	// leftCenter, center, rightCenter
																	// bottomLeft, bottomCenter, bottomRight

		this.marker_position	= "outer";							// inner, outer

		this.height				= "100%";
		this.width				= "100%";

		this.min				= 0;
		this.max				= 100;

		this.tickCount			= 11;								// Defines the # of tick marks to be displayed

		this.fillColor			= undefined;
		this.fillOpacity		= 100;

		this.anchor = {
			color					: 'rgb(0,0,0)' ,				//
			opacity					: 100 ,							//
			size					: 10							//
		};
		this.axis = {
			color					: 'rgb(255,255,255)' ,			//
			innerBorderColor		: 'rgb(0,0,0)' ,				//
			innerBorderOpacity		: 100 ,							//
			innerBorderWidth		: 1 ,							//
			innerBorderVisible		: true ,
			opacity					: 0 ,							//
			outerBorderColor		: 'rgb(0,0,0)' ,				//
			outerBorderOpacity		: 100 ,							//
			outerBorderWidth		: 1 ,							//
			outerBorderVisible		: true ,
			thickness				: 15							//
		};
		this.needle = {
			color					: 'rgb(255,0,0)' ,				//
			opacity					: 100 ,							//
			borderColor				: 'rgb(255,0,0)' ,				//
			borderOpacity			: 100 ,							//
			borderWidth				: 1 ,							//
			size					: 4								//
		};
		this.range = {
			borderColor				: undefined,					//
			borderOpacity 			: 100,
			borderWidth				: 1 ,							//
			offset					: 0 ,							//
			opacity					: 100 ,							//
			style					: 'doughnut' ,					//
			thickness				: 15							//
		};
		this.tick = {
			className				: '' ,
			color					: 'rgb(0,0,0)' ,				// The color of the major tick marks
			offset					: 0 ,							// How far out (- for in) from the inner axis border the major ticks should be displayed
			opacity					: 100 ,							// The opacity of the major ticks
			size					: 1 ,							// The width of the major ticks
			thickness				: undefined						// The length of the major ticks (takes the axis thickness if undefined)
		};
		this.minorTick = {
			color					: 'rgb(150,150,150)' ,			// The color of the minor tick marks
			count					: 4 ,							// How many minor ticks should be displayed between each major tick
			offset					: 0 ,							// How far out (- for in) from the inner axis border the minor ticks should be displayed
			opacity					: 100 ,							// The opacity of the minor ticks
			size					: 1 ,							// The width of the minor ticks
			thickness				: undefined						// The length of the minor ticks (takes the axis thickness if undefined)
		};
		this.lock = {
			color					: 'rgb(0,0,0)' ,				//
			offset					: 5 ,							//
			opacity					: 100 ,							//
			size					: 6	,							//
			visible					: false
		};

		this.start_degree		= 270;				// Start from the left side
		this.range_degrees		= 180;			// Draw to the right side

		this.label = {
			className				: '' ,
			textAlign				: 'center' ,
			position				: 'centerBottom' ,
			lines					: 1
		};

		this.__labelcontainer	= undefined;
		this.__label			= undefined;

		this.__needsXAxis = false;
		this.__needsYAxis = false;

		this.__copyOptions(options);

	};

	EJSC.GaugeSeries.prototype.__extendTo( EJSC.AnalogGaugeSeries );

	//==============================//
	//    Analog Gauge Prototype    //
	//==============================//

	var ___analoggaugeseries = EJSC.AnalogGaugeSeries.prototype;

		___analoggaugeseries.addRange = function(min, max, color, redraw) {

			// See if the exact range is already defined
			var found = false;
			for (var i = 0; i < this.ranges.length; i++) {
				if (this.ranges[i][0] == min && this.ranges[i][1] == max) {
					// Found a matching range, update its color
					this.ranges[i][2] = color;
					found = true;
					break;
				}
			}

			if (!found) {
				// No matching range found, add one
				this.ranges.push([min, max, color]);
			}

			if (this.__owner && redraw) {
				// Redraw the chart if the series has been added
				this.__owner.__draw(true);
			}

		};

		___analoggaugeseries.deleteRange = function(min, max, redraw) {

			// Find the range
			var found = false;
			for (var i = 0; i < this.ranges.length; i++) {
				if (this.ranges[i][0] == min && this.ranges[i][1] == max) {
					// Found a matching range, remove it
					this.ranges.splice(i, 1);
					found = true;
					break;
				}
			}

			if (this.__owner && redraw && found) {
				// Redraw the chart if the series has been added
				this.__owner.__draw(true);
			}

		};

		___analoggaugeseries.__doDraw = function( ctx ) {

			if( this.__labelcontainer == undefined ) {

			//	this.__labelcontainer = document.getElementById('ejsc_container_' + this.__owner.__index).appendChild(
				this.__labelcontainer = this.__getChart().__el_labels.appendChild(
					EJSC.utility.__createDOMArray(
						[ 'DIV' , {} ]
					)
				);

				this.__label = this.__labelcontainer.appendChild(
					EJSC.utility.__createDOMArray(
						[ 'DIV' , {} ,
							[ 'SPAN' , { className: this.label.className } ]
						]
					)
				);
				this.__label.style.position = 'absolute';
				this.__label.style.textAlign = this.label.textAlign;
			//	this.__label.style.overflow = 'hidden';
				this.__label.style.lineHeight = '14px';

				for( var i=0 ; i<this.tickCount ; i++ ) {
					this.__tickmarkers[i] =
						this.__labelcontainer.appendChild(
							EJSC.utility.__createDOMArray(
								[ 'SPAN' , { className: this.tick.className } ]
							)
						);
					this.__tickmarkers[i].style.position = 'absolute';
				}

			}

			if( this.__drawing ) return;
			if( !this.visible ) return;

			if (!this.__getHasData()) {

				this.__dataHandler.loadData();
				return;

			} else {

				this.__drawing = true;

				try {

					//--- Set up the Canvas ---//

					//	var canvas 				= this.__owner.__canvas;
					//	var ctx 				= canvas.getContext('2d');

					//--- Find Available Area ---//

					//	var canvas_width 		= canvas.offsetWidth;
					//	var canvas_height 		= canvas.offsetHeight;
						var canvas_height 		= this.__getChart().__getDrawArea().height;
						var canvas_width 		= this.__getChart().__getDrawArea().width;

					//--- Set Up Gauge Properties ---//

						var diameter			= this.__getPhysicalDiameter();
						var center				= this.__getPhysicalCenter();
						var radius				= m_FLOOR(diameter/2);
						var x_center			= center.x;
						var y_center			= center.y;

					//--- Compute AABB Bounding Box ---//
						var x_min				= 0;
						var x_max				= 0;
						var y_min				= ((this.label.position.match(/[bB]ottom/) != null)?(-14 * this.label.lines):(0));
						var y_max				= ((this.label.position.match(/[tT]op/) != null)?(14 * this.label.lines):(0));

						var g, h, i, j, r, o, x, y;
						var comp_h, comp_w;
						var comp_ratio, grid_ratio, diff;
						var o_s, o_e;
						var o_start, o_end;
						var point_1_y, point_1_x;
						var point_2_y, point_2_x;
						var point_3_y, point_3_x;
						var nlength, perc, rang;
						var o_needle, o_needle_r, o_needle_l;
						var o_tick;
						var point_hint_y, point_hint_x, hint_text;
						var div;

					//--- Compute 4 Sides ---//

						for( i = this.start_degree ; i <= ( this.start_degree + this.range_degrees ) ; i++ ) {
							j = i;
							if( j >= 360 )	j = j - 360;
							if( j == 0 )	y_max = radius;
							if( j == 90 )	x_max = radius;
							if( j == 180 )	y_min = 0 - radius;
							if( j == 270 )	x_min = 0 - radius;
						}

					//--- Compute Start Point ---//


						// JHM: 2007-11-11 - Modified to use cached math functions and variables
						o = ( ( ( this.start_degree - 90 ) / 180 ) * m_PI );

						x = radius * m_COS(o);
						y = -(radius * m_SIN(o));

						if( x > 0 )	{ if( x > x_max ) x_max = m_ROUND(x); }
						else		{ if( x < x_min ) x_min = m_ROUND(x); }
						if( y > 0 )	{ if( y > y_max ) y_max = m_ROUND(y); }
						else		{ if( y < y_min ) y_min = m_ROUND(y); }

					//--- Compute End Point ---//


						// JHM: 2007-11-11 - Modified to use cached math functions and variables
						o = ( ( ( this.start_degree + this.range_degrees - 90 ) / 180 ) * m_PI );

						x = radius * m_COS(o);
						y = -(radius * m_SIN(o));

						if( x > 0 )	{ if( x > x_max ) x_max = m_ROUND(x); }
						else		{ if( x < x_min ) x_min = m_ROUND(x); }
						if( y > 0 )	{ if( y > y_max ) y_max = m_ROUND(y); }
						else		{ if( y < y_min ) y_min = m_ROUND(y); }

					//--- Compute Ratios ---//

						comp_h		= y_max - y_min;
						comp_w		= x_max - x_min;
						comp_ratio	= comp_w / comp_h;
						grid_ratio	= canvas_width / canvas_height;

					//--- Compare Ratios ---//

						// JGD: 2007-08-22
						//	Fixed to exclude non-used variable "g"
						// JGD: 2007-09-12
						//	Fixed to base ratio off of radius instead of diameter
						if( comp_ratio < grid_ratio ) {
							dif 		= diameter / comp_h;// * grid_ratio;
						} else {
							dif 		= diameter / comp_w;// * grid_ratio;
						}
						radius 		= radius * dif;
						x_center 	= x_center - ( ( x_max + x_min ) / 2 * dif );
						y_center 	= y_center + ( ( y_max + y_min ) / 2 * dif );

					//--- Draw Background Fill ---//

						if( this.fillColor != undefined ) {

							// JHM: 2007-11-11 - Modified to use cached math functions and variables
							o_start 			= ( ( ( this.start_degree - 90 ) / 180 ) * m_PI );
							o_end 				= ( ( ( this.start_degree + this.range_degrees - 90 ) / 180 ) * m_PI );
							point_1_y			= y_center + ( ( radius ) * m_SIN(o_start) );
							point_1_x			= ( x_center + ( radius ) * m_COS(o_start) );
							point_2_y			= y_center + ( ( radius - this.axis.thickness ) * m_SIN(o_start) );
							point_2_x			= ( x_center + ( radius - this.axis.thickness ) * m_COS(o_start) );

							var c = EJSC.utility.__getColor(this.fillColor);
							ctx.fillStyle 		= "rgba(" + c.red + "," + c.green + "," + c.blue + "," + (this.fillOpacity/100) + ")";

							ctx.beginPath();
							ctx.moveTo( x_center , y_center );
							ctx.lineTo( point_1_x , point_1_y );
							ctx.arc( x_center , y_center , radius , o_start , o_end , false );
							ctx.lineTo( x_center , y_center );
							ctx.fill();
							ctx.closePath();

						}

					//--- Draw Ranges ---//

						r = this.ranges;
						o_s = this.start_degree;
						o_e = this.start_degree;

						for( var i=0 ; i<r.length ; i++ ) {

							o_s = this.start_degree + ( ( ( r[i][0] - this.min ) / ( this.max - this.min ) ) * this.range_degrees );
							o_e = this.start_degree + ( ( ( r[i][1] - this.min ) / ( this.max - this.min ) ) * this.range_degrees );

							// JHM: 2007-11-11 - Modified to use cached math functions and variables
							o_start 			= ( ( ( o_s - 90 ) / 180 ) * m_PI );
							o_end 				= ( ( ( o_e - 90 ) / 180 ) * m_PI );
							// JHM: 2008-01-10 - Added coordinates for start and end points on the arcs in order to connect
							point_1_y			= y_center + ( ( radius - this.axis.thickness - this.range.offset - this.range.thickness ) * m_SIN(o_start) );
							point_1_x			= ( x_center + ( radius - this.axis.thickness - this.range.offset - this.range.thickness ) * m_COS(o_start) );
							point_2_y			= y_center + ( ( radius - this.axis.thickness - this.range.offset - this.range.thickness ) * m_SIN(o_end) );
							point_2_x			= ( x_center + ( radius - this.axis.thickness - this.range.offset - this.range.thickness ) * m_COS(o_end) );
							point_3_y			= y_center + ( ( radius - this.axis.thickness - this.range.offset ) * m_SIN(o_start) );
							point_3_x			= ( x_center + ( radius - this.axis.thickness - this.range.offset ) * m_COS(o_start) );


							if( this.range.borderColor != undefined ) {
								var tColor = EJSC.utility.__getColor(this.range.borderColor);
								var rc = "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.range.borderOpacity/100) + ")";
							} else {
								var rc = r[i][2];
							}
							ctx.strokeStyle 	= rc;
							var tColor = EJSC.utility.__getColor(r[i][2]);
							ctx.fillStyle 		= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.range.opacity/100) + ")";
							ctx.lineWidth		= this.range.borderWidth;

							ctx.beginPath();
							ctx.moveTo( point_3_x , point_3_y );
							ctx.arc( x_center , y_center , radius - this.axis.thickness - this.range.offset , o_start , o_end , false );
							if( this.range.style == 'doughnut' ) {
								// JHM: 2008-01-10 - Added connecting lines between the arcs to properly fill
								ctx.lineTo( point_2_x, point_2_y );
								ctx.arc( x_center , y_center , radius - this.axis.thickness - this.range.offset - this.range.thickness , o_end , o_start , true );
								ctx.lineTo( point_3_x, point_3_y );
							} else if (this.range.style == "pie") {
								ctx.lineTo( x_center , y_center );
								ctx.lineTo( point_1_x , point_1_y );
							}
							ctx.closePath();
							ctx.fill();

							ctx.beginPath();
							ctx.moveTo( point_1_x , point_1_y );
							ctx.arc( x_center , y_center , radius - this.axis.thickness - this.range.offset , o_start , o_end , false );
							if( this.range.style == 'doughnut' ) {
								// JHM: 2008-01-10 - Added connecting lines between the arcs to properly fill
								ctx.lineTo( point_2_x, point_2_y );
								ctx.arc( x_center , y_center , radius - this.axis.thickness - this.range.offset - this.range.thickness , o_end , o_start , true );
								ctx.lineTo( point_3_x, point_3_y );
							} else if (this.range.style == "pie") {
								ctx.lineTo( x_center , y_center );
								ctx.lineTo( point_1_x , point_1_y );
							}
							ctx.closePath();
							ctx.stroke();

						}

					//--- Compute Axis ---//

						// JHM: 2007-11-11 - Modified to use cached math functions and variables
						o_start 			= ( ( ( this.start_degree - 90 ) / 180 ) * m_PI );
						o_end 				= ( ( ( this.start_degree + this.range_degrees - 90 ) / 180 ) * m_PI );
						point_1_y			= y_center + ( ( radius ) * m_SIN(o_start) );
						point_1_x			= ( x_center + ( radius ) * m_COS(o_start) );
						point_2_y			= y_center + ( ( radius - this.axis.thickness ) * m_SIN(o_start) );
						point_2_x			= ( x_center + ( radius - this.axis.thickness ) * m_COS(o_start) );

					//--- Draw Axis ---//

						var tColor = EJSC.utility.__getColor(this.axis.color);
						ctx.fillStyle 		= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.axis.opacity/100) + ")";

						ctx.beginPath();
						ctx.moveTo( point_1_x , point_1_y );
						if( this.range_degrees == 360 ) {
							// JHM: 2007-11-11 - Modified to use cached math functions and variables
							o_end 				= ( ( ( this.start_degree + 180 - 90 ) / 180 ) * m_PI );
							ctx.arc( x_center , y_center , radius , o_start , o_end , false );
							ctx.arc( x_center , y_center , radius , o_end , o_start , false );
						} else {
							ctx.arc( x_center , y_center , radius , o_start , o_end , false );
						}
						ctx.arc( x_center , y_center , radius - this.axis.thickness , o_end , o_start , true );
						ctx.fill();
						ctx.closePath();

						if( this.axis.outerBorderVisible == true ) {

							var tColor = EJSC.utility.__getColor(this.axis.outerBorderColor);
							ctx.strokeStyle 	= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.axis.outerBorderOpacity/100) + ")";
							ctx.lineWidth		= this.axis.outerBorderWidth;

							ctx.beginPath();
							ctx.moveTo( point_1_x , point_1_y );
							ctx.arc( x_center , y_center , radius , o_start , o_end , false );
							ctx.arc( x_center , y_center , radius , o_end , o_start , ((this.range_degrees==360)?(false):(true)) );
							ctx.closePath();
							ctx.stroke();

						}

						if( this.axis.innerBorderVisible == true ) {

							var tColor = EJSC.utility.__getColor(this.axis.innerBorderColor);
							ctx.strokeStyle 	= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.axis.innerBorderOpacity/100) + ")";
							ctx.lineWidth		= this.axis.innerBorderWidth;

							ctx.beginPath();
							ctx.moveTo( point_2_x , point_2_y );
							if( this.range_degrees == 360 ) {
								// JHM: 2007-11-11 - Modified to use cached math functions and variables
								o_end 				= ( ( ( this.start_degree + 180 - 90 ) / 180 ) * m_PI );
							}
							ctx.arc( x_center , y_center , radius - this.axis.thickness  , o_start , o_end , false );
							ctx.arc( x_center , y_center , radius - this.axis.thickness  , o_end , o_start , ((this.range_degrees==360)?(false):(true)) );
							ctx.closePath();
							ctx.stroke();

						}

					//--- Compute Locks ---//

						var o 			= this.lock.offset / radius;

						// JHM: 2007-11-11 - Modified to use cached math functions and variables
						var o_needle_min	= ( ( ( this.start_degree - 90 ) / 180 ) * m_PI ) - o;
						var o_needle_max	= ( ( ( this.start_degree + this.range_degrees - 90 ) / 180 ) * m_PI ) + o;

							o 			= ( this.lock.offset + ( this.lock.size / 2 ) ) / radius;

						var o_first 	= ( ( ( this.start_degree - 90 ) / 180 ) * m_PI ) - o;
						var point_l1_y	= y_center + ( ( radius ) * m_SIN(o_first) );
						var point_l1_x	= ( x_center + ( radius ) * m_COS(o_first) );

						var o_last 		= ( ( ( this.start_degree + this.range_degrees - 90 ) / 180 ) * m_PI ) + o;
						var point_l2_y	= y_center + ( ( radius ) * m_SIN(o_last) );
						var point_l2_x	= ( x_center + ( radius ) * m_COS(o_last) );

					//--- Compute Needle ---//

						nlength				= radius * 1.05;

						perc				= ( ( this.__points[0].x - this.min ) / ( this.max - this.min ) );
						rang				= this.range_degrees * perc;

						// JHM: 2007-11-11 - Modified to use cached math functions and variables
						o_needle			= ( ( ( this.start_degree + rang - 90 ) / 180 ) * m_PI );
						o_needle_r			= ( ( ( this.start_degree + rang ) / 180 ) * m_PI );
						o_needle_l			= ( ( ( this.start_degree + rang - 180 ) / 180 ) * m_PI );

						if( o_needle < o_needle_min && this.range_degrees != 360 ) o_needle = o_needle_min;
						if( o_needle > o_needle_max && this.range_degrees != 360 ) o_needle = o_needle_max;

						point_1_y			= y_center + ( ( nlength ) * m_SIN(o_needle) );
						point_1_x			= ( x_center + ( nlength ) * m_COS(o_needle) );
						point_2_y			= y_center + ( ( this.needle.size / 2 ) * m_SIN(o_needle_r) );
						point_2_x			= ( x_center + ( this.needle.size / 2 ) * m_COS(o_needle_r) );
						point_3_y			= y_center + ( ( this.needle.size / 2 ) * m_SIN(o_needle_l) );
						point_3_x			= ( x_center + ( this.needle.size / 2 ) * m_COS(o_needle_l) );

					//--- Methods ---//

						this.__drawLocks( ctx , point_l1_x , point_l1_y , point_l2_x , point_l2_y );
						this.__addMinorTickLines( ctx , y_center , x_center , radius , point_1_y , point_1_x , point_2_y , point_2_x );
						this.__addMajorTickLines( ctx , y_center , x_center , radius );
						this.__drawNeedle( ctx , point_1_x , point_1_y , point_2_x , point_2_y , point_3_x , point_3_y );
						this.__drawAnchor( ctx , x_center , y_center );
						this.__updateLabel( canvas_width , x_center , y_center , x_min , y_min , x_max , y_max , dif , ctx );

				} catch (e) {
				} finally {
					this.__drawing = false;
				}

			}

		};

		___analoggaugeseries.__drawNeedle = function( ctx , point_1_x , point_1_y , point_2_x , point_2_y , point_3_x , point_3_y ) {

			var tColor = EJSC.utility.__getColor(this.needle.borderColor);
			ctx.strokeStyle		= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.needle.borderOpacity/100) + ")";
				tColor = EJSC.utility.__getColor(this.needle.color);
			ctx.fillStyle		= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.needle.opacity/100) + ")";
			ctx.lineWidth		= this.needle.borderWidth;

			ctx.beginPath();
			ctx.moveTo( point_1_x , point_1_y );
			ctx.lineTo( point_2_x , point_2_y );
			ctx.lineTo( point_3_x , point_3_y );
			ctx.lineTo( point_1_x , point_1_y );
			ctx.fill();
			ctx.closePath();

			ctx.beginPath();
			ctx.moveTo( point_1_x , point_1_y );
			ctx.lineTo( point_2_x , point_2_y );
			ctx.lineTo( point_3_x , point_3_y );
			ctx.lineTo( point_1_x , point_1_y );
			ctx.stroke();
			ctx.closePath();

		};

		___analoggaugeseries.__drawLocks = function( ctx , point_l1_x , point_l1_y , point_l2_x , point_l2_y ) {

			if( this.lock.visible == true && this.lock.offset != undefined && this.range_degrees != 360 ) {

				var tColor = EJSC.utility.__getColor(this.lock.color);
				ctx.fillStyle	= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.lock.opacity/100) + ")";
				ctx.beginPath();
				ctx.moveTo( point_l1_x , point_l1_y );
				// JHM: 2007-11-11 - Modified to use cached math functions and variables
				ctx.arc( point_l1_x , point_l1_y , this.lock.size/2 , 0 , m_PI , false );
				ctx.arc( point_l1_x , point_l1_y , this.lock.size/2 , m_PI , m_PIx2 , false );
				ctx.fill();
				ctx.closePath();

				ctx.fillStyle	= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.lock.opacity/100) + ")";
				ctx.beginPath();
				ctx.moveTo( point_l2_x , point_l2_y );
				// JHM: 2007-11-11 - Modified to use cached math functions and variables
				ctx.arc( point_l2_x , point_l2_y , this.lock.size/2 , 0 , m_PI , false );
				ctx.arc( point_l2_x , point_l2_y , this.lock.size/2 , m_PI , m_PIx2 , false );
				ctx.fill();
				ctx.closePath();

			}

		};

		___analoggaugeseries.__drawAnchor = function( ctx , x_center , y_center ) {

			var tColor = EJSC.utility.__getColor(this.anchor.color);
			ctx.fillStyle			= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.anchor.opacity/100) + ")";
			ctx.beginPath();
			ctx.moveTo( x_center , y_center );
			// JHM: 2007-11-11 - Modified to use cached math functions and variables
			ctx.arc( x_center , y_center , this.anchor.size/2 , 0 , m_PI , false );
			ctx.arc( x_center , y_center , this.anchor.size/2 , m_PI , m_PIx2 , false );
			ctx.fill();
			ctx.closePath();

		};

		___analoggaugeseries.__addMinorTickLines = function( ctx , y_center, x_center, radius, point_1_y, point_1_x, point_2_y, point_2_x ) {

			var tColor = EJSC.utility.__getColor(this.minorTick.color);
			ctx.strokeStyle		= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.minorTick.opacity/100) + ")";
			ctx.lineWidth		= this.minorTick.size;

			var t_size = this.minorTick.thickness;
			if( t_size == undefined || t_size == null ) t_size = this.axis.thickness;
			var a_thick = this.axis.thickness;

			var i, o_tick, e_tick, m_tick, dif;

			for( i=0 ; i<this.tickCount-1 ; i++ ) {

				// JHM: 2007-11-11 - Modified to use cached math functions and variables
				o_tick				= ( ( ( this.start_degree - 90 + ( this.range_degrees / ( this.tickCount - 1 ) ) * i ) / 180 ) * m_PI );
				e_tick				= ( ( ( this.start_degree - 90 + ( this.range_degrees / ( this.tickCount - 1 ) ) * (i+1) ) / 180 ) * m_PI );

				dif = ( ( e_tick - o_tick ) / ( this.minorTick.count + 1 ) );

				this.__drawMinorTickLines( ctx , o_tick , dif , y_center , x_center , radius , a_thick , t_size , e_tick );

			}

		};

		___analoggaugeseries.__drawMinorTickLines = function( ctx , o_tick , dif , y_center , x_center , radius , a_thick , t_size , e_tick ) {

			var m_tick, point_1_y, point_1_x, point_2_y, point_2_x;

			var r1 = radius - a_thick + this.minorTick.offset;
			var r2 = radius - a_thick + this.minorTick.offset + t_size;

			if( EJSC.__isIE ) ctx.beginPath();


			for( var j=1 ; j<this.minorTick.count+1 ; j++ ) {

				if( !EJSC.__isIE ) ctx.beginPath();

				m_tick 				= o_tick + ( dif * j );
				// JHM: 2007-11-11 - Modified to use cached math functions
				point_1_y			= y_center + ( r1 * m_SIN(m_tick) );
				point_1_x			= x_center + ( r1 * m_COS(m_tick) );
				point_2_y			= y_center + ( r2 * m_SIN(m_tick) );
				point_2_x			= x_center + ( r2 * m_COS(m_tick) );
				ctx.moveTo( point_1_x , point_1_y );
				ctx.lineTo( point_2_x , point_2_y );

				if( !EJSC.__isIE ) {
					ctx.stroke();
					ctx.closePath();
				}

			}

			if( EJSC.__isIE ) {
				ctx.stroke();
				ctx.closePath();
			}


		};

		___analoggaugeseries.__addMajorTickLines = function( ctx , y_center, x_center, radius ) {

			var o_tick, point_1_y, point_1_x, point_2_y, point_2_x;
			var point_hint_y, point_hint_x, hint_text, h;

			var tColor = EJSC.utility.__getColor(this.tick.color);
			ctx.strokeStyle		= "rgba(" + tColor.red + "," + tColor.green + "," + tColor.blue + "," + (this.tick.opacity/100) + ")";
			ctx.lineWidth		= this.tick.size;

			var t_size = this.tick.thickness;
			if( t_size == undefined || t_size == null ) t_size = this.axis.thickness;
			var a_thick = this.axis.thickness;

			var r1 = radius - a_thick + this.tick.offset;
			var r2 = radius - a_thick + this.tick.offset + t_size;

			if( EJSC.__isIE ) ctx.beginPath();

			for( var i=0 ; i<this.tickCount ; i++ ) {

				if( !EJSC.__isIE ) ctx.beginPath();

				// JHM: 2007-11-11 - Modified to use cached math functions and variables
				o_tick				= ( ( ( this.start_degree - 90 + ( this.range_degrees / ( this.tickCount - 1 ) ) * i ) / 180 ) * m_PI );

				point_1_y			= y_center + ( ( r1 ) * m_SIN(o_tick) );
				point_1_x			= ( x_center + ( r1 ) * m_COS(o_tick) );
				point_2_y			= y_center + ( ( r2 ) * m_SIN(o_tick) );
				point_2_x			= ( x_center + ( r2 ) * m_COS(o_tick) );

				ctx.moveTo( point_1_x , point_1_y );
				ctx.lineTo( point_2_x , point_2_y );

				point_hint_y		= y_center + ( ( radius ) * m_SIN(o_tick) );
				point_hint_x		= ( x_center + ( radius ) * m_COS(o_tick) );

				if( !EJSC.__isIE ) {
					ctx.stroke();
					ctx.closePath();
				}

				hint_text 			= this.min + ( ( ( this.max - this.min ) / ( this.tickCount - 1 ) ) * i );

				if( hint_text != this.min || this.range_degrees < 360 )
					this.__updateMajorTickMarker( i , hint_text , point_hint_x , point_hint_y , o_tick , ctx );

			}

			if( EJSC.__isIE ) {
				ctx.stroke();
				ctx.closePath();
			}

		};

		___analoggaugeseries.__updateMajorTickMarker = function( i , hint_text , point_hint_x , point_hint_y , o_tick ) {

			var h = this.__tickmarkers[i];

/* JGD - 2011-02-28 - Fixed to use x_axis_formatter if defined */
			if( this.x_axis_formatter !== undefined )
				h.innerHTML = this.x_axis_formatter.format( hint_text );
			else
				h.innerHTML = this.__getChart().axis_bottom.formatter.format( hint_text );

			var l = point_hint_x - ( h.offsetWidth / 2 );
			var t = point_hint_y - ( h.offsetHeight / 2 );

			var te = ((this.tick.extension>0)?(this.tick.extension/1.8):(0));

			var c = m_COS(o_tick);
			var s = m_SIN(o_tick);

			this.__moveMajorTickMarker( h , l , t , te , o_tick , c , s );

		};

		___analoggaugeseries.__moveMajorTickMarker = function(marker, left, top, tick_extension, o_tick, o_tick_cos, o_tick_sin) {

			if (this.marker_position == 'outer') {
				marker.style.left = ( m_ROUND(left + ((marker.offsetWidth / 2 + tick_extension) * o_tick_cos * 1.5) ) + 'px' );
				marker.style.top = ( m_ROUND(top + ((marker.offsetHeight / 2 + tick_extension) * o_tick_sin * 1.5) ) + 'px' );
			} else if( this.marker_position == 'inner' ) {
				marker.style.left = ( m_ROUND(left - ((marker.offsetWidth / 2 + this.axis.thickness) * o_tick_cos * 1.2) ) + 'px' );
				marker.style.top = ( m_ROUND(top - ((marker.offsetHeight / 2 + this.axis.thickness) * o_tick_sin * 1.2) ) + 'px' );
			}
		};

		___analoggaugeseries.__updateLabel = function( canvas_width , x_center, y_center, x_min, y_min, x_max, y_max , dif ) {

		//	x_center += this.__owner.__canvas.parentNode.offsetLeft;
		//	y_center += this.__owner.__canvas.parentNode.offsetTop;

			var div = this.__label;
				// JHM: 2007-09-27 - Added check to avoid resetting innerHTML unless the label has actually changed.
				if (this.__previousLabel != this.__points[0].label) {
					div.firstChild.innerHTML = this.__points[0].label;
					this.__previousLabel = this.__points[0].label;
				}
				div.style.height = ( ( 14 * this.label.lines ) + 'px' );
				div.style.width = ( ( canvas_width ) + 'px' );
				div.style.width = ( ( div.firstChild.offsetWidth ) + 'px' );

			switch (this.label.position) {
				case "top":
					div.style.top = ( ( y_center - ( y_max * dif ) ) + 'px' );
					div.style.left = ( ( x_center - div.offsetWidth/2 ) + 'px' );
					break;
				case "left":
					div.style.top = ( ( y_center - div.offsetHeight / 2 ) + 'px' );
					div.style.left = ( ( x_center + ( x_min * dif ) ) + 'px' );
					break;
				case "centerTop":
					div.style.top = ( ( y_center - 10 - div.offsetHeight ) + 'px' );
					div.style.left = ( ( x_center - div.offsetWidth/2 ) + 'px' );
					break;
				case "centerLeft":
					div.style.top = ( ( y_center - div.offsetHeight / 2 ) + 'px' );
					div.style.left = ( ( x_center - 10 - div.offsetWidth ) + 'px' );
					break;
				case "centerRight":
					div.style.top = ( ( y_center - div.offsetHeight / 2 ) + 'px' );
					div.style.left = ( ( x_center + 10 ) + 'px' );
					break;
				case "centerBottom":
					div.style.top = ( ( y_center + 10 ) + 'px' );
					div.style.left = ( ( x_center - div.offsetWidth/2 ) + 'px' );
					break;
				case "right":
					div.style.top = ( ( y_center - div.offsetHeight / 2 ) + 'px' );
					div.style.left = ( ( x_center + ( x_max * dif ) - div.offsetWidth ) + 'px' );
					break;
				case "bottom":
					div.style.top = ( ( y_center - ( y_min ) - div.offsetHeight ) + 'px' );
					div.style.left = ( ( x_center - div.offsetWidth/2 ) + 'px' );
					break;
				default:
					alert("'" + this.label.position + "' is an invalid label position, using 'centerBottom'.");
					div.style.top = ( ( y_center + 10 ) + 'px' );
					div.style.left = ( ( x_center - div.offsetWidth/2 ) + 'px' );

			}

		};

		___analoggaugeseries.__getPhysicalDiameter = function() {

		//	var canvas 	= this.__owner.__canvas;
		//	var w		= canvas.offsetWidth;
		//	var h 		= canvas.offsetHeight;
			var h 		= this.__getChart().__getDrawArea().height;
			var w 		= this.__getChart().__getDrawArea().width;


			var perc	= .8;

			var n0 = this.width;
			var n1 = this.height;

			if( typeof( n0 ) == 'number' ) { n0 = this.width.toString(); }
			if( typeof( n1 ) == 'number' ) { n1 = this.height.toString(); }

			if( n0.indexOf('%') == -1 ) { c = parseFloat( n0 ); }
			else { c = w * ( parseFloat( n0.replace('%','') ) / 100 ); }
			if( n1.indexOf('%') == -1 ) { d = parseFloat( n1 ); }
			else { d = h * ( parseFloat( n1.replace('%','') ) / 100 ); }

			return m_FLOOR( ( c < d ) ? (  c * perc ) : ( d * perc ) );

		};

		___analoggaugeseries.__getPhysicalCenter = function() {

		//	var canvas 	= this.__owner.__canvas;
		//	var w		= canvas.offsetWidth;
		//	var h 		= canvas.offsetHeight;
			var h 		= this.__getChart().__getDrawArea().height;
			var w 		= this.__getChart().__getDrawArea().width;
			var l		= this.__getChart().__getDrawArea().left;
			var t		= this.__getChart().__getDrawArea().top;

			var n0 = this.width;
			var n1 = this.height;
			if( typeof( n0 ) == 'number' ) { n0 = this.width.toString(); }
			if( typeof( n1 ) == 'number' ) { n1 = this.height.toString(); }

			if( n0.indexOf('%') == -1 ) { c = parseFloat( n0 ); }
			else { c = w * ( parseFloat( n0.replace('%','') ) / 100 ); }
			if( n1.indexOf('%') == -1 ) { d = parseFloat( n1 ); }
			else { d = h * ( parseFloat( n1.replace('%','') ) / 100 ); }

			var ret = { x: w/2 , y: w/2 };

			switch (this.position) {
				case "topLeft": 		ret = { x: c*.5 , y: d*.5 }; break;
				case "topCenter":		ret = { x: w/2 , y: d*.5 }; break;
				case "topRight":		ret = { x: w-c*.5 , y: d*.5 }; break;
				case "centerLeft":		ret = { x: c*.5 , y: h/2 }; break;
				case "centerRight":		ret = { x: w-c*.5 , y: h/2 }; break;
				case "bottomLeft":		ret = { x: c*.5 , y: h-d*.5 }; break;
				case "bottomCenter":	ret = { x: w/2 , y: h-d*.5 }; break;
				case "bottomRight":		ret = { x: w-c*.5 , y: h-d*.5 }; break;
				case "center":			ret = { x: w/2 , y: h/2 }; break;
				// Added gauge series position = left or right
				case "left":			ret = { x: (c<d)?(c/2):(d/2), y: h/2 }; break;
				case "right":			ret = { x: (c<d)?w-(c/2):w-(d/2), y: h/2 }; break;
				default:
					alert("'" + this.position + "' is an invalid gauge position, using 'center'.");
					ret = { x: w/2 , y: h/2 };
			}

			return { x: ret.x + l , y: ret.y + t };

		};

		// Hide/show labels
		___analoggaugeseries.doAfterVisibilityChange = function() {

			if (this.visible) {
				this.__labelcontainer.style.display = "block";
			} else {
				this.__labelcontainer.style.display = "none";
			}

		};

	___analoggaugeseries.__doGetLegendIcon = function() {

		// JHM: 2008-08-16 - Updated to correct non-secure warning in https session
		return "analog-gauge";

	};


})();