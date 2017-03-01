var jQuery = window.jQuery || module.exports;

/*
 Fix for https://github.com/jquery/jquery/issues/2432
 this fix was originally part of jquery 1.12.4 but it was removed as it contained a backward
 breaking change, but this compatibility issue should not impact brjs libraries
 */
jQuery.ajaxPrefilter( function( s ) {
	if ( s.crossDomain ) {
		s.contents.script = false;
	}
} );