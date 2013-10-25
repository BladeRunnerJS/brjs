/**********************************************************************/
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
	var m_ABS = Math.abs;

	var ___chart = EJSC.Chart.prototype;
	___chart.__applyDeprecatedProperties = function(options) {

		var axis_left = (options.axis_left != undefined?options.axis_left:{});
		var axis_bottom = (options.axis_bottom != undefined?options.axis_bottom:{});

		var o;
		for (o in options) {

			switch (o) {

				case "show_x_axis":
					console.log("EJSChart: show_x_axis property has been deprecated, use axis_bottom.visible");
					if (options[o] == false) {
						if (axis_bottom.visible == undefined) { axis_bottom.visible = options[o]; }
					}
					break;
				case "show_y_axis":
					console.log("EJSChart: show_y_axis property has been deprecated, use axis_left.visible");
					if (options[o] == false) {
						if (axis_left.visible == undefined) { axis_left.visible = options[o]; }
					}
					break;
				case "x_zero_plane":
					console.log("EJSChart: x_zero_plane property has been deprecated, use axis_bottom.zero_plane");
					if (axis_bottom.zero_plane == undefined) { axis_bottom.zero_plane = options[o]; }
					break;
				case "y_zero_plane":
					console.log("EJSChart: y_zero_plane property has been deprecated, use axis_left.zero_plane");
					if (axis_left.zero_plane == undefined) { axis_left.zero_plane = options[o]; }
					break;
				case "x_value_hint_caption":
					console.log("EJSChart: x_value_hint_caption property has been deprecated, use axis_bottom.hint_caption");
					if (axis_bottom.hint_caption == undefined) { axis_bottom.hint_caption = options[o]; }
					break;
				case "y_value_hint_caption":
					console.log("EJSChart: y_value_hint_caption property has been deprecated, use axis_left.hint_caption");
					if (axis_left.hint_caption == undefined) { axis_left.hint_caption = options[o]; }
					break;
				case "x_axis_caption":
					console.log("EJSChart: x_axis_caption property has been deprecated, use axis_bottom.caption");
					if (axis_bottom.caption == undefined) { axis_bottom.caption = options[o]; }
					break;
				case "y_axis_caption":
					console.log("EJSChart: y_axis_caption property has been deprecated, use axis_left.caption");
					if (axis_left.caption == undefined) { axis_left.caption = options[o]; }
					break;
				case "x_axis_formatter":
					console.log("EJSChart: x_axis_formatter property has been deprecated, use axis_bottom.formatter");
					if (axis_bottom.formatter == undefined) { axis_bottom.formatter = options[o]; }
					break;
				case "y_axis_formatter":
					console.log("EJSChart: y_axis_formatter property has been deprecated, use axis_left.formatter");
					if (axis_left.formatter == undefined) { axis_left.formatter = options[o]; }
					break;
				case "x_cursor_position_caption":
					console.log("EJSChart: x_cursor_position_caption property has been deprecated, use axis_bottom.cursor_position.caption");
					if (axis_bottom.cursor_position == undefined) { axis_bottom.cursor_position = { caption: options[o], show: true }; }
					else {
						if (axis_bottom.cursor_position.caption == undefined) { axis_bottom.cursor_position.caption = options[o]; }
						if (axis_bottom.cursor_position.show == undefined) { axis_bottom.cursor_position.show = true; }
					}
					break;
				case "y_cursor_position_caption":
					console.log("EJSChart: y_cursor_position_caption property has been deprecated, use axis_left.cursor_position.caption");
					if (axis_left.cursor_position == undefined) { axis_left.cursor_position = { caption: options[o] }; }
					else if (axis_left.cursor_position.caption == undefined) { axis_left.cursor_position.caption = options[o]; }
					break;
				case "x_axis_extremes_ticks":
					console.log("EJSChart: x_axis_extremes_ticks property has been deprecated, use axis_bottom.extremes_ticks");
					if (axis_bottom.extremes_ticks == undefined) { axis_bottom.extremes_ticks = options[o]; }
					break;
				case "y_axis_extremes_ticks":
					console.log("EJSChart: y_axis_extremes_ticks property has been deprecated, use axis_left.extremes_ticks");
					if (axis_left.extremes_ticks == undefined) { axis_left.extremes_ticks = options[o]; }
					break;
				case "show_crosshairs":
					console.log("EJSChart: show_crosshairs property has been deprecated, use axis_[bottom|left].crosshair.show");
					if (axis_bottom.crosshair == undefined) { axis_bottom.crosshair = { show: options[o].x }; }
					else if (axis_bottom.crosshair.show == undefined) { axis_bottom.crosshair.show = options[o].x; }
					if (axis_left.crosshair == undefined) { axis_left.crosshair = { show: options[o].y }; }
					else if (axis_left.crosshair.show == undefined) { axis_left.crosshair.show = options[o].y; }
					break;
				case "show_mouse_position":
					console.log("EJSChart: show_mouse_position property has been deprecated, use axis_[bottom|left].cursor_position.show");
					if (axis_bottom.cursor_position == undefined) { axis_bottom.cursor_position = { show: options[o] }; }
					else if (axis_bottom.cursor_position.show == undefined) { axis_bottom.cursor_position.show = options[o]; }
					if (axis_left.cursor_position == undefined) { axis_left.cursor_position = { show: options[o] }; }
					else if (axis_left.cursor_position.show == undefined) { axis_left.cursor_position.show = options[o]; }
					break;
				case "force_static_points_x":
					console.log("EJSChart: force_static_points_x property has been deprecated, use axis_bottom.force_static_points");
					if (axis_bottom.force_static_points == undefined) { axis_bottom.force_static_points = options[o]; }
					break;
				case "force_static_points_y":
					console.log("EJSChart: force_static_points_y property has been deprecated, use axis_left.force_static_points");
					if (axis_left.force_static_points == undefined) { axis_left.force_static_points = options[o]; }
					break;
				case "force_static_points":
					console.log("EJSChart: force_static_points property has been deprecated, use axis_bottom.force_static_points");
					if (axis_bottom.force_static_points == undefined) { axis_bottom.force_static_points = options[o]; }
					break;
				case "x_axis_size":
					console.log("EJSChart: x_axis_size property has been deprecated, use axis_bottom.size");
					if (axis_bottom.size == undefined) { axis_bottom.size = options[o]; }
					break;
				case "y_axis_size":
					console.log("EJSChart: y_axis_size property has been deprecated, use axis_left.size");
					if (axis_left.size == undefined) { axis_left.size = options[o]; }
					break;
				case "x_axis_className":
					console.log("EJSChart: x_axis_className property has been deprecated, use axis_bottom.caption_class");
					if (axis_bottom.caption_class == undefined) { axis_bottom.caption_class = options[o]; }
					break;
				case "y_axis_className":
					console.log("EJSChart: y_axis_className property has been deprecated, use axis_left.caption_class");
					if (axis_left.caption_class == undefined) { axis_left.caption_class = options[o]; }
					break;
				case "x_axis_tick_className":
					console.log("EJSChart: x_axis_tick_className property has been deprecated, use axis_bottom.label_class");
					if (axis_bottom.label_class == undefined) { axis_bottom.label_class = options[o]; }
					break;
				case "y_axis_tick_className":
					console.log("EJSChart: y_axis_tick_className property has been deprecated, use axis_left.label_class");
					if (axis_left.label_class == undefined) { axis_left.label_class = options[o]; }
					break;
				case "x_axis_tick_count":
					console.log("EJSChart: x_axis_tick_count property has been deprecated, use axis_bottom.major_ticks.count");
					if (axis_bottom.major_ticks == undefined) { axis_bottom.major_ticks = { count: options[o] }; }
					else if (axis_bottom.major_ticks.count == undefined) { axis_bottom.major_ticks.count = options[o]; }
					break;
				case "y_axis_tick_count":
					console.log("EJSChart: y_axis_tick_count property has been deprecated, use axis_left.major_ticks.count");
					if (axis_left.major_ticks == undefined) { axis_left.major_ticks = { count: options[o] }; }
					else if (axis_left.major_ticks.count == undefined) { axis_left.major_ticks.count = options[o]; }
					break;
				case "onXAxisNeedsTicks":
					console.log("EJSChart: onXAxisNeedsTicks event has been deprecated, use axis_bottom.onNeedsTicks");
					if (axis_bottom.onNeedsTicks == undefined) { axis_bottom.onNeedsTicks = options[o]; }
					break;
				case "onYAxisNeedsTicks":
					console.log("EJSChart: onYAxisNeedsTicks event has been deprecated, use axis_left.onNeedsTicks");
					if (axis_left.onNeedsTicks == undefined) { axis_left.onNeedsTicks = options[o]; }
					break;
				case "x_axis_minor_ticks":
					console.log("EJSChart: x_axis_minor_ticks property has been deprecated, use axis_bottom.minor_ticks");
					if (axis_bottom.minor_ticks == undefined) { axis_bottom.minor_ticks = options[o]; }
					break;
				case "y_axis_minor_ticks":
					console.log("EJSChart: y_axis_minor_ticks property has been deprecated, use axis_left.minor_ticks");
					if (axis_left.minor_ticks == undefined) { axis_left.minor_ticks = options[o]; }
					break;
				case "x_min":
					console.log("EJSChart: x_min property has been deprecated, use axis_bottom.min_extreme");
					if (axis_bottom.min_extreme == undefined) { axis_bottom.min_extreme = options[o]; }
					break;
				case "x_max":
					console.log("EJSChart: x_max property has been deprecated, use axis_bottom.max_extreme");
					if (axis_bottom.max_extreme == undefined) { axis_bottom.max_extreme = options[o]; }
					break;
				case "y_min":
					console.log("EJSChart: y_min property has been deprecated, use axis_left.min_extreme");
					if (axis_left.min_extreme == undefined) { axis_left.min_extreme = options[o]; }
					break;
				case "y_max":
					console.log("EJSChart: y_max property has been deprecated, use axis_left.max_extreme");
					if (axis_left.max_extreme == undefined) { axis_left.max_extreme = options[o]; }
					break;
				case "show_grid":
					console.log("EJSChart: show_grid property has been deprecated, use axis_[left|bottom].grid.show");
					if (axis_bottom.grid == undefined) { axis_bottom.grid = { show: options[o] }; }
					else if (axis_bottom.grid.show == undefined) { axis_bottom.grid.show = options[o]; }
					if (axis_left.grid == undefined) { axis_left.grid = { show: options[o] }; }
					else if (axis_left.grid.show == undefined) { axis_left.grid.show = options[o]; }
					break;
				case "legendTitle":
					console.log("EJSChart: legendTitle property has been deprecated, use legend_title");
					if (options.legend_title == undefined) { options.legend_title = options[o]; }
					break;
				case "y_min_xtreme":
					console.log("EJSChart: y_min_xtreme property has been deprecated, use axis_left.min_extreme");
					if (axis_left.min_extreme == undefined) { axis_left.min_extreme = options[o]; }
					break;
				case "y_max_xtreme":
					console.log("EJSChart: y_max_xtreme property has been deprecated, use axis_left.max_extreme");
					if (axis_left.max_extreme == undefined) { axis_left.max_extreme = options[o]; }
					break;
				case "x_min_xtreme":
					console.log("EJSChart: x_min_xtreme property has been deprecated, use axis_bottom.min_extreme");
					if (axis_bottom.min_extreme == undefined) { axis_bottom.min_extreme = options[o]; }
					break;
				case "x_max_xtreme":
					console.log("EJSChart: x_max_xtreme property has been deprecated, use axis_bottom.max_extreme");
					if (axis_bottom.max_extreme == undefined) { axis_bottom.max_extreme = options[o]; }
					break;
				case "onBeforeBeginZoom":
					console.log("EJSChart: onBeforeBeginZoom event has been deprecated, use chart.onUserBeginZoom and axis_[left|right|top|bottom].getZoomBoxCoordinates() to obtain zoom begin point");
					if (options.onUserBeginZoom == undefined) { options.onUserBeginZoom = options[o]; }
					break;
				case "onBeforeEndZoom":
					console.log("EJSChart: onBeforeEndZoom event has been deprecated, use chart.onUserEndZoom and axis_[left|right|top|bottom].getZoomBoxCoordinates() to obtain zoom endpoint");
					if (options.onUserEndZoom == undefined) { options.onUserEndZoom = options[o]; }
					break;
				case "x_axis_stagger_ticks":
					console.log("EJSChart: x_axis_stagger_ticks property has been deprecated, use axis_bottom.stagger_ticks");
					if (axis_bottom.stagger_ticks == undefined) { axis_bottom.stagger_ticks = options[o]; }
					break;
				case "legendTitle":
					console.log("EJSChart: legendTitle property has been deprecated, use legend_title");
					if (options.legend_title == undefined) { options.legend_title = options[o]; }
					break;
			}

		}

		options.axis_left = axis_left;
		options.axis_bottom = axis_bottom;

		return options;

	};

	// Initializes the chart object
	___chart.__init = function(options) {

		function applyAxisOptions(axis, chart) {
			if (options["axis_" + axis] == undefined) { return options; }
			if (options["axis_" + axis].__type != undefined) {
				chart["axis_" + axis] = options["axis_" + axis];
				if (chart["axis_" + axis].__options.__chart_options == undefined) {
					chart["axis_" + axis].__options.__chart_options = {};
				}
			} else {
				for (var i in options["axis_" + axis]) {
					chart["axis_" + axis].__options[i] = options["axis_" + axis][i];
				}
			}
			if (options["axis_" + axis].visible == true) { chart["axis_" + axis].__options.__force_visible = true; }
			delete options["axis_" + axis];
		};

		if (options != undefined) {

			// Translate deprecated properties
			options = this.__applyDeprecatedProperties(options);

			applyAxisOptions("left", this);
			applyAxisOptions("bottom", this);
			applyAxisOptions("right", this);
			applyAxisOptions("top", this);

			this.__copyOptions(options);

		}

		this.__initializeAxes();

		if (options != undefined) {

			if (options.axis_left && options.axis_left.visible == true) { this.axis_left.__force_visible = true; }
			if (options.axis_bottom && options.axis_bottom.visible == true) { this.axis_bottom.__force_visible = true; }
			if (options.axis_right && options.axis_right.visible == true) { this.axis_right.__force_visible = true; }
			if (options.axis_top && options.axis_top.visible == true) { this.axis_top.__force_visible = true; }

		}

		// Added event for building
		if (this.onBeforeBuild != undefined) {
			this.onBeforeBuild(this);
		}

		// Create and append the chart objects
		this.__write();

		// Resize the chart objects
		this.__resize(false, true);

		// Show the chart
		this.__el_container.className = this.__el_container.className.replace(/ ejsc-invisible/,"");

		// Added event for building
		if (this.onAfterBuild != undefined) {
			this.onAfterBuild(this);
		}

		var self = this;
		window.setTimeout(function() { self.__draw(false); },1);

		// Added check for auto_resize
		if (this.auto_resize) {
			EJSC.__addChartResize(this);
		}

	};

	// CHART PUBLIC DEPRECATED METHODS
	___chart.selectClosestPoint = function(xPt, yPt) {
		console.log("EJSChart: selectClosestPoint has been deprecated, use findClosestPoint()");
		return this.findClosestPoint(this.axis_bottom.pointToPixel(xPt), this.axis_left.pointToPixel(yPt));
	};
	___chart.setYAxisCaption = function(caption) {
		console.log("EJSChart: setYAxisCaption() has been deprecated, use axis_left.setCaption()");
		this.axis_left.setCaption(caption);
	};
	___chart.setXAxisCaption = function(caption) {
		console.log("EJSChart: setXAxisCaption() has been deprecated, use axis_bottom.setCaption()");
		this.axis_bottom.setCaption(caption);
	};
	___chart.getXExtremes = function() {
		console.log("EJSChart: getXExtremes() has been deprecated, use axis_bottom.getExtremes()");
		var result = this.axis_bottom.getExtremes();
		return { x_min_extreme: result.min, x_max_extreme: result.max };
	};
	___chart.setXExtremes = function( x_min , x_max ) {
		console.log("EJSChart: setXExtremes() has been deprecated, use axis_bottom.setExtremes()");
		this.axis_bottom.setExtremes(x_min, x_max);
	};
	___chart.getYExtremes = function() {
		console.log("EJSChart: getYExtremes() has been deprecated, use axis_left.getExtremes()");
		var result = this.axis_left.getExtremes();
		return { y_min_extreme: result.min, y_max_extreme: result.max };
	};
	___chart.setYExtremes = function( y_min, y_max ) {
		console.log("EJSChart: setYExtremes() has been deprecated, use axis_left.setExtremes()");
		this.axis_left.setExtremes(y_min, y_max);
	};
	___chart.showGrid = function( redraw ) {
		console.log("EJSChart: showGrid() has been deprecated, use axis_[left|bottom].showGrid()");
		this.axis_bottom.showGrid(false);
		this.axis_left.showGrid(redraw);
	};
	___chart.hideGrid = function( redraw ) {
		console.log("EJSChart: hideGrid() has been deprecated, use axis_[left|bottom].hideGrid()");
		this.axis_bottom.hideGrid(false);
		this.axis_left.hideGrid(redraw);
	};
	___chart.showXAxis = function(redraw) {
		console.log("EJSChart: showXAxis() has been deprecated, use axis_bottom.show()");
		this.axis_bottom.show(redraw);
	};
	___chart.hideXAxis = function(redraw) {
		console.log("EJSChart: hideXAxis() has been deprecated, use axis_bottom.hide()");
		this.axis_bottom.hide(redraw);
	};
	___chart.showYAxis = function(redraw) {
		console.log("EJSChart: showYAxis() has been deprecated, use axis_left.show()");
		this.axis_left.show(redraw);
	};
	___chart.hideYAxis = function(redraw) {
		console.log("EJSChart: hideYAxis() has been deprecated, use axis_left.hide()");
		this.axis_left.hide(redraw);
	};
	___chart.convertPointToPixel = function(x, y) {
		console.log("EJSChart: convertPointToPixel() is deprecated, use axis_[left|bottom].pointToPixel()");
		return { left: this.axis_bottom.pointToPixel(x), top: this.axis_left.pointToPixel(y) }
	};
	___chart.convertPixelToPoint = function(left, top) {
		console.log("EJSChart: convertPixelToPoint() is deprecated, use axis_[left|bottom].pixelToPoint()");
		return { left: this.axis_bottom.pixelToPoint(x), top: this.axis_left.pixelToPoint(y) }
	};
	___chart.displayZoomBox = function( x_min , x_max , y_min , y_max ) {
		console.log("EJSChart: displayZoomBox() is deprecated, use chart.showZoomBox() or chart.hideZoomBox()");
		if (x_min != undefined && x_max != undefined && y_min != undefined && y_max != undefined) {
			this.showZoomBox(x_min, x_max, "bottom", y_min, y_max, "left");
		} else {
			this.hideZoomBox();
		}
	};
	___chart.findClosestPointInSeries = function( x , y , series ) {
		console.log("EJSChart: findClosestPointInSeries() is deprecated, use series.findClosestByPoint()");
		var xValue = x;
		var yValue = y;
		var result = series.findClosestByPoint({ x: xValue, y: yValue });
		if (result != null) { result = result.point; }
		return result;
	};
	___chart.addYAxisBin = function(label, redraw) {
		console.log("EJSChart: addYAxisBin() has been deprecated, use axis_left.addBin()");
		this.axis_left.addBin(label, redraw);
	};
	___chart.addXAxisBin = function(label, redraw) {
		console.log("EJSChart: addXAxisBin() has been deprecated, use axis_bottom.addBin()");
		this.axis_bottom.addBin(label, redraw);
	};
	___chart.removeYAxisBin = function(label, redraw) {
		console.log("EJSChart: removeYAxisBin() has been deprecated, use axis_left.removeBin()");
		this.axis_left.removeBin(label, redraw);
	};
	___chart.removeXAxisBin = function(label, redraw) {
		console.log("EJSChart: removeXAxisBin() has been deprecated, use axis_bottom.removeBin()");
		this.axis_bottom.removeBin(label, redraw);
	};
	___chart.getZoomBoxCoordinates = function() {
		console.log("EJSChart: getZoomBoxCoordinates() has been deprecated, use axis.getZomBoxCoordinates()");
		var xmm = this.axis_bottom.getZoomBoxCoordinates();
		var ymm = this.axis_left.getZoomBoxCoordinates();
		return { x_min: xmm.min, x_max: xmm.max, y_min: ymm.min, y_max: ymm.max };
	};
	___chart.setCrosshairs = function( visible, x , y ) {
		console.log("EJSChart: setCrosshairs() is deprecated, use axis_[left|bottom].setCrosshair()");
		this.axis_bottom.setCrosshair(visible, y);
		this.axis_left.setCrosshair(visible, y);
	};
	___chart.getZoom = function() {
		console.log("EJSChart: getZoom() is deprecated, use axis_[left|top|right|bottom].getZoom()");
		var left = this.axis_left;
		var bottom = this.axis_bottom;
		return {
			x_min: bottom.__current_min,
			x_max: bottom.__current_max,
			y_min: left.__current_min,
			y_max: left.__current_max
		};
	};
	___chart.setZoom = function(x_min, x_max, y_min, y_max, redraw, reselectPoint) {
		console.log("EJSChart: setZoom() has been deprecated, use axis_[left|top|right|bottom].setZoom()");
		this.axis_left.setZoom(y_min, y_max, false);
		this.axis_bottom.setZoom(x_min, x_max, redraw, reselectPoint);
	};

	EJSC.__Axis.__calculateExtremes = function(reset) {
		if (this.__owner == undefined) { return false; }

		if (this.__doCalculateExtremes != undefined) {
			if (this.__doCalculateExtremes(reset) == false) { return false; }
		}

		if (reset) {
			this.__min_extreme = undefined;
			this.__max_extreme = undefined;
			this.__current_min = undefined;
			this.__current_max = undefined;
			// DEPRECATED
			if (this.__side=="left") {
				this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
				this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
			}
			if (this.__side=="bottom") {
				this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
				this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
			}
		}

		var min = isNaN(this.__min_extreme)?undefined:this.__min_extreme;
		var max = isNaN(this.__max_extreme)?undefined:this.__max_extreme;
		var i = 0;
		var series = this.__series;
		var padding = {
			min: 0,
			max: 0
		};
		for(; i < series.length; i++ ) {
			if (!series[i].__getHasData() || !series[i].visible) continue;
			if (this.__orientation == "h") {
				if (min == undefined || series[i].__minX < min) min = series[i].__minX;
				if (max == undefined || series[i].__maxX > max) max = series[i].__maxX;
				if (series[i].__padding.x_min > padding.min) padding.min = series[i].__padding.x_min;
				if (series[i].__padding.x_max > padding.max) padding.max = series[i].__padding.x_max;
			} else {
				if (min == undefined || series[i].__minY < min) min = series[i].__minY;
				if (max == undefined || series[i].__maxY > max) max = series[i].__maxY;
				if (series[i].__padding.y_min > padding.min) padding.min = series[i].__padding.y_min;
				if (series[i].__padding.y_max > padding.max) padding.max = series[i].__padding.y_max;
			}
		}

		if (min == undefined || max == undefined) {
			if (this.__hasManualExtremes()) {
				min = this.__forced_min_extreme;
				max = this.__forced_max_extreme;
			} else {
				return;
			}
		}

		if (this.__text_values.__count() > 0) {
			if (min > 1) { min = 1; }
			if (max < (this.__text_values.__count())) {
				max = this.__text_values.__count();
			}
		}

		if (min == max) {
			if (min == 0) {
				min = -0.01;
				max = 0.01;
			} else {
				min -= (m_ABS(min) * 0.01);
				max += (m_ABS(max) * 0.01);
			}
		}

		this.__data_extremes = { min_extreme: min, max_extreme: max };

		this.__padding = { min: (this.padding == undefined || this.padding.min == undefined?padding.min:this.padding.min), max: (this.padding == undefined || this.padding.max == undefined?padding.max:this.padding.max) };

		if (this.__forced_min_extreme != undefined) min = this.__forced_min_extreme;
		if (this.__forced_max_extreme != undefined) max = this.__forced_max_extreme;

		// If extremes have changed, record new extremes, recalculate scale
		// and apply padding
		if (this.__min_extreme != min || this.__max_extreme != max) {

			// Save new extremes
			this.__min_extreme = min;
			this.__max_extreme = max;

			if (this.__current_min == undefined || isNaN(this.__current_min)) { this.__current_min = min; }
			if (this.__current_max == undefined || isNaN(this.__current_max)) { this.__current_max = max; }

			// DEPRECATED
			if (this.__side=="left") {
				this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
				this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
			}
			if (this.__side=="bottom") {
				this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
				this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
			}

			this.__calculateScale(true, true);
		}

	};

	var ___linearaxis = EJSC.LinearAxis.prototype;
	___linearaxis.__doMove = function(start, end) {

		if (this.__scale == undefined) { return false; }

		var start = (this.__orientation == "h"?start.x:start.y);
		var end = (this.__orientation == "h"?end.x:end.y);

		if (start == end) { return false; }

		var change = m_ABS(end - start);
		var amount = change * this.__scale;

		switch ((this.__orientation == "h" && start > end) || (this.__orientation == "v" && end > start)) {
			case true:
				// add to min and max
				this.__current_max += amount;
				if (this.__current_max > this.__max_extreme) {
					this.__current_max = this.__max_extreme;
				}
				this.__current_min = this.__current_max - (this.__owner.__draw_area[this.__orientation=="h"?"width":"height"] * this.__scale);
				// DEPRECATED
				if (this.__side=="left") {
					this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
					this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
				}
				if (this.__side=="bottom") {
					this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
					this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
				}
				break;
			case false:
				// subtract from min and max
				this.__current_min -= amount;
				if (this.__current_min < this.__min_extreme) {
					this.__current_min = this.__min_extreme;
				}
				this.__current_max = this.__current_min + (this.__owner.__draw_area[this.__orientation=="h"?"width":"height"] * this.__scale);
				// DEPRECATED
				if (this.__side=="left") {
					this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
					this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
				}
				if (this.__side=="bottom") {
					this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
					this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
				}
				break;
		}

		this.__calculateScale(false);

	};
	___linearaxis.__doZoom = function(start, end) {

		var result = true;
		if (this.__scale == undefined) { result = false; }

		var start = (this.__orientation == "h"?start.x:start.y);
		var end = (this.__orientation == "h"?end.x:end.y);
		var da = this.__owner.__draw_area;
		// JHM: 2008-05-21 - Added additional checks for draw area undefined
		if (da == undefined) { result = false; }

		if (start == end) { result = false; }

		if (result) {
			switch (start > end) {
				case true:
					// zoom out
					this.__resetZoom();
					break;
				case false:
					// add to min and max
					var old_ticks = this.__ticks.slice();
					var old_min = this.__current_min;
					var old_max = this.__current_max;

					var min_adj = m_ABS((this.__orientation=="h"?start:da.height - end + da.top) * this.__scale);
					var max_adj = m_ABS((this.__orientation=="h"?da.width - end + da.left:start) * this.__scale);
					if (this.__orientation == "h") {
						if (da.right < end) {
							this.__current_max += max_adj;
						} else {
							this.__current_max -= max_adj;
						}
						if (da.left > start) {
							this.__current_min -= min_adj;
						} else {
							this.__current_min += min_adj;
						}
					} else {
						if (da.bottom > end) {
							this.__current_max -= max_adj;
						} else {
							this.__current_max += max_adj;
						}
						if (da.top < start) {
							this.__current_min += min_adj;
						} else {
							this.__current_min -= min_adj;
						}
					}

					if (this.__current_min < this.__min_extreme) { this.__current_min = this.__min_extreme; }
					if (this.__current_max > this.__max_extreme) { this.__current_max = this.__max_extreme; }
					if (this.__current_min == this.__min_extreme && this.__current_max == this.__max_extreme &&
					this.__current_min == old_min && this.__current_max == old_max) {
						result = false;
					}

					this.__generateTicks();
					for (var i = 0; i < this.__ticks.length; i++) {
						if (isNaN(this.__ticks[i].p)) {
							this.__ticks = old_ticks.slice();
							this.__current_min = old_min;
							this.__current_max = old_max;
							result = false;
							break;
						}
					}

					this.__calculateScale(false);
					break;
			}
		}
		// DEPRECATED
		if (this.__side=="left") {
			this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
			this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
		}
		if (this.__side=="bottom") {
			this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
			this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
		}

		return result;

	};

	___linearaxis.__doCalculateScale = function(calculatePadding, generateTicks) {

		var area = this.__owner.__draw_area;
		if (area == undefined) { return false; }

		var dimension = (this.__orientation=="h"?area.width:area.height);
		var scale;

		if (dimension > 0) {
			scale = ((this.__current_max - this.__current_min) / dimension);
		} else {
			scale = 0;
		}

		if (calculatePadding == true) {
			// Adjust extremes for padding
			this.__min_extreme -= this.__padding.min * scale;
			this.__max_extreme += this.__padding.max * scale;
			this.__current_min = this.__min_extreme;
			this.__current_max = this.__max_extreme;

			if (dimension > 0) {
				scale = ((this.__current_max - this.__current_min) / dimension);
			} else {
				scale = 0;
			}
		}

		this.__scale = scale;

		// DEPRECATED
		if (this.__side=="left") {
			this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
			this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
			this.__owner.y_scale = this.__scale;
		}
		if (this.__side=="bottom") {
			this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
			this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
			this.__owner.x_scale = this.__scale;
		}

		return true;

	};

	___linearaxis.__doGenerateTicks = function() {

		var area = this.__owner.__draw_area;
		var dimension = (this.__orientation=="h"?area.width:area.height);
		var label_count = this.__el_labels.childNodes.length;
		var scale = this.__scale;
		var ticks = undefined;
		var tickOffsetIndex = 0;
		var round = undefined;
		var min_tick = undefined;
		var max_tick = undefined;
		var dif = undefined;
		var label = undefined;
		var current_tick = undefined;
		var i = undefined;

		// Generate array of tick positions and captions
		// Check for onNeedsTicks event, we're done if the user is providing ticks
		if (this.onNeedsTicks != undefined && this.onNeedsTicks != null) {

			// ticks should be an array in the format of
			// [[coordinate, label (or null)]]

			if (this.__text_values.__count() > 0) {
				ticks = this.onNeedsTicks(null, null, this.__owner, this);
			} else {
				ticks = this.onNeedsTicks(this.__current_min, this.__current_max, this.__owner, this);
			}

			if (ticks == null || ticks == undefined) { ticks = undefined; }
			else {

				// Convert user returned ticks into the correct format
				// Update ticks array to fill in unspecified labels with
				// formatted values
				for (i = ticks.length - 1; i >= 0; i--) {

					if (this.__text_values.__count() > 0) {
						label = this.__text_values.__find(ticks[i][0]);
						if (label != null) {
							ticks[i] = { p: label.__index, l: label.__label };
						} else {
							// Remove if tick is not recognized
							ticks.splice(i,1);
							continue;
						}
					} else {
						ticks[i] = { p: ticks[i][0], l: ticks[i][1] };
					}

					// Update null labels to use the inherited formatter
					if (ticks[i].l == null) {
						ticks[i].l = this.__getLabel(ticks[i].p, 0);
					}

				}
			}

		}

		// Calculate ticks
		if (ticks == undefined) {

			if (this.major_ticks.count != undefined && this.major_ticks.count > 1) {

				min_tick = this.__current_min;
				max_tick = this.__current_max;
				this.__increment = ((this.__current_max - this.__current_min) / (this.major_ticks.count - 1));
				round = 0;

			} else {

				// Make sure at least 3 ticks will be shown
				dif = ((this.__current_max - this.__current_min) / 3);

				tickOffsetIndex = 0;
				if (this.__text_values.__count() > 0) { tickOffsetIndex = 27; }
				else {
				 	// Find relevant tick difference
					while (EJSC.__ticks[tickOffsetIndex] > dif) { tickOffsetIndex++; }

					if (this.major_ticks.max_interval != undefined && EJSC.__ticks[tickOffsetIndex] > this.major_ticks.max_interval) {
						while (EJSC.__ticks[tickOffsetIndex] >= this.major_ticks.max_interval) {
							tickOffsetIndex++;
						}
						if (EJSC.__ticks[tickOffsetIndex] != this.major_ticks.max_interval) {
							EJSC.__ticks.splice(tickOffsetIndex, 0, this.major_ticks.max_interval);
						}
					} else if (this.major_ticks.min_interval != undefined && dif < this.major_ticks.min_interval) {
						while (EJSC.__ticks[tickOffsetIndex] <= this.major_ticks.min_interval) {
							tickOffsetIndex--;
						}
						if (EJSC.__ticks[tickOffsetIndex] != this.major_ticks.min_interval) {
							EJSC.__ticks.splice(tickOffsetIndex, 0, this.major_ticks.min_interval);
						}
					}
				}

				// Used to save the round index for use later (cursor position formatting)
				this.__tick_round = tickOffsetIndex;

				// What to round the tick values off to (to prevent JavaScript rounding errors)
				round = EJSC.__tickRound[tickOffsetIndex];

				// JHM: 2008-05-16 - Added to suppport small date intervals better
				// If the assigned formatter is a date formatter and the round is > 0 then set round
				// and the tick array index to represent whole numbers (fractional numbers mean nothing
				// for dates).  This will take effect when looking at a very small range where only
				// a millisecond or two can be displayed and the ticks generated will land exactly on
				// those values
				if (round > 0 && this.formatter != undefined && this.formatter.__type == "date") {
					tickOffsetIndex = 27;
					this.__tick_round = tickOffsetIndex;
					round = EJSC.__tickRound[tickOffsetIndex];
				}

				// Tick increment
				this.__increment = EJSC.__ticks[tickOffsetIndex];

				// Find lowest multiple of tick visible on graph
				min_tick = m_CEIL(this.__current_min / this.__increment) * this.__increment;

				// In case chosen tick landed outside graph
				while (min_tick < this.__current_min) { min_tick += this.__increment; }

				max_tick = this.__current_max;

				// When extremes_ticks is true and no zoom is applied,
				// adjust extremes so that extremes_ticks can be displayed
				if ((this.extremes_ticks == true) && (this.__text_values.__count() == 0) &&
				(this.__forced_min_extreme == undefined) && (this.__forced_max_extreme == undefined) &&
				(this.__current_min == this.__min_extreme) && (this.__current_max == this.__max_extreme)) {

					// Figure out new min_extreme
					if (this.__data_extremes.min_extreme < min_tick) {
						min_tick -= this.__increment;
					}

					// Figure out new x_max_xtreme
					max_tick = min_tick;
					while (max_tick < this.__data_extremes.max_extreme) {
						max_tick += this.__increment;
					}
					if (max_tick - this.__increment >= this.__data_extremes.max_extreme) {
						max_tick -= this.__increment;
					}

					// Adjust extremes for padding
					this.__current_min = min_tick;
					this.__min_extreme = min_tick;
					this.__current_max = max_tick;
					this.__max_extreme = max_tick;

					// DEPRECATED
					if (this.__side=="left") {
						this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
						this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
					}
					if (this.__side=="bottom") {
						this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
						this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
					}

					this.__calculateScale(false, false);

				}

			}

			if (isNaN(this.__current_min) || isNaN(this.__current_max)) { return; }

			// Configure first tick
			current_tick = min_tick;
			ticks = [{ p: current_tick, l: this.__getLabel(current_tick, round) }];

			if (this.extremes_ticks == true && this.__text_values.__count() == 0 &&
			this.__forced_min_extreme != undefined && this.__forced_max_extremes != undefined &&
			this.__current_min == this.__min_extreme && this.__current_max == this.__max_extreme) {
				ticks[0] = { p: this.__current_min, l: this.__getLabel(this.__current_min, round) };
			}

			while ((current_tick + this.__increment) <= max_tick) {

				current_tick += this.__increment;
				ticks.push({ p: current_tick, l: this.__getLabel(current_tick, round) });

			}

			if (current_tick < this.__current_max && this.__text_values.__count() == 0 &&
			((this.__extremes_ticks == true && this.__forced_min_extremes != undefined &&
			this.__forced_max_extremes != undefined) || (this.major_ticks.count != undefined &&
			this.major_ticks.count > 1))) {

				current_tick = this.__current_max;
				ticks.push({ p: current_tick, l: this.__getLabel(current_tick, round) });

			}

		}

		for (i = ticks.length - 1; i >= 0; i--) {
			// Remove if tick is not in view
			if (ticks[i].p > this.__current_max || ticks[i].p < this.__current_min) {
				ticks.splice(i, 1);
				continue;
			}
		}

		// Save ticks
		this.__ticks = ticks.slice();

		return true;

	};

	var ___logarithmicaxis = EJSC.LogarithmicAxis.prototype;
	___logarithmicaxis.__doMove = function(start, end) {

		if (this.__scale == undefined) { return false; }

		var start = (this.__orientation == "h"?start.x:start.y);
		var end = (this.__orientation == "h"?end.x:end.y);
		var area = this.__owner.__draw_area;
		var new_max, new_min;

		if (start == end) { return false; }

		var change = m_ABS(end - start);

		switch ((this.__orientation == "h" && start > end) || (this.__orientation == "v" && end > start)) {
			case true:
				new_max = this.__px2pt(this.__orientation=="h"?area.width + area.left + change:area.top - change);
				if (new_max > this.__max_extreme) {
					new_max = this.__max_extreme;
					new_min = m_POW(this.base, this.__logX(new_max) - (area[this.__orientation=="h"?"width":"height"] * this.__scale));
				} else {
					new_min = this.__px2pt(this.__orientation=="h"?area.left + change:area.height + area.top - change);
				}
				break;
			case false:
				new_min = this.__px2pt(this.__orientation=="h"?area.left - change:area.height + area.top + change);
				if (new_min < this.__min_extreme) {
					new_min = this.__min_extreme;
					new_max = m_POW(this.base, this.__logX(new_min) + (area[this.__orientation=="h"?"width":"height"] * this.__scale));
				} else {
					new_max = this.__px2pt(this.__orientation=="h"?area.width + area.left - change:area.top + change);
				}
				break;
		}

		this.__current_max = new_max;
		this.__current_min = new_min;

		// DEPRECATED
		if (this.__side=="left") {
			this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
			this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
		}
		if (this.__side=="bottom") {
			this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
			this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
		}

		this.__calculateScale(false);

	};

	___logarithmicaxis.__doZoom = function(start, end) {

		var result = true;
		if (this.__scale == undefined) { result = false; }

		var start = (this.__orientation == "h"?start.x:start.y);
		var end = (this.__orientation == "h"?end.x:end.y);
		var da = this.__owner.__draw_area;
		// JHM: 2008-05-21 - Added additional checks for draw area undefined
		if (da == undefined) { result = false; }

		if (start == end) { result = false; }

		if (result) {
			switch (start > end) {
				case true:
					// zoom out
					this.__resetZoom();
					break;
				case false:
					// add to min and max
					var old_ticks = this.__ticks.slice();
					var old_min = this.__current_min;
					var old_max = this.__current_max;

					var min_adj = m_ABS((this.__orientation=="h"?start + area.left:end) * this.__scale);
					var max_adj = m_ABS((this.__orientation=="h"?end:start + area.top) * this.__scale);
					if (this.__orientation == "h") {
						if (da.right < end) {
							this.__current_max += max_adj;
						} else {
							this.__current_max -= max_adj;
						}
						if (da.left > start) {
							this.__current_min -= min_adj;
						} else {
							this.__current_min += min_adj;
						}
					} else {
						if (da.bottom > end) {
							this.__current_max -= max_adj;
						} else {
							this.__current_max += max_adj;
						}
						if (da.top < start) {
							this.__current_min += min_adj;
						} else {
							this.__current_min -= min_adj;
						}
					}

					if (this.__current_min < this.__min_extreme) { this.__current_min = this.__min_extreme; }
					if (this.__current_max > this.__max_extreme) { this.__current_max = this.__max_extreme; }
					if (this.__current_min == this.__min_extreme && this.__current_max == this.__max_extreme &&
					this.__current_min == old_min && this.__current_max == old_max) {
						result = false;
					}

					this.__generateTicks();
					for (var i = 0; i < this.__ticks.length; i++) {
						if (isNaN(this.__ticks[i].p)) {
							this.__ticks = old_ticks.slice();
							this.__current_min = old_min;
							this.__current_max = old_max;
							result = false;
							break;
						}
					}

					this.__calculateScale(false);
					break;
			}
		}
		// DEPRECATED
		if (this.__side=="left") {
			this.__owner.y_min = this.__current_min; this.__owner.y_max = this.__current_max;
			this.__owner.y_min_xtreme = this.__min_extreme; this.__owner.y_max_xtreme = this.__max_extreme;
		}
		if (this.__side=="bottom") {
			this.__owner.x_min = this.__current_min; this.__owner.x_max = this.__current_max;
			this.__owner.x_min_xtreme = this.__min_extreme; this.__owner.x_max_xtreme = this.__max_extreme;
		}

		return result;

	};

	___logarithmicaxis.__doCalculateScale = function(calculatePadding, generateTicks) {

		var area = this.__owner.__draw_area;

		this.__log_min = this.__logX(this.__current_min);
		this.__log_max = this.__logX(this.__current_max);

		var dimension = (this.__orientation=="h"?area.width:area.height);

		this.__scale = (this.__log_max - this.__log_min) / dimension;

		// DEPRECATED
		if (this.__side=="left") {
			this.__owner.y_scale = this.__scale;
		} else {
			this.__owner.x_scale = this.__scale;
		}

	};

})();