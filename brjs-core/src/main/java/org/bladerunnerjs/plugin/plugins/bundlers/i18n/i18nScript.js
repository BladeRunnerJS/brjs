<script>	
	function getCookieLocale() {
		var localeCookieName = "%s=";
		var ca = document.cookie.split(';');
		for(var i=0; i<ca.length; i++) {
			var c = ca[i].trim();
			if (c.indexOf(localeCookieName)==0) return c.substring(localeCookieName.length,c.length);
		}
		return "";
	}
	function getLocale() {
		var supportedLocales = [%s]; var defaultLocale = "%s";  var browserLocale = browserLocale;
		if (navigator.userLanguage) {
			browserLocale = navigator.userLanguage;
		} else if (navigator.language) {
			browserLocale = navigator.language;
		}
	
		var cookieLocale = getCookieLocale();
		browserLocale = (cookieLocale !== "") ? cookieLocale : browserLocale;
		
		if (supportedLocales.indexOf(browserLocale) == -1 ) {
			browserLocale = defaultLocale;
		}
	
		return browserLocale;
	}
	
	document.write('<scr'+'ipt src="i18n/' + getLocale() + '.js"></script>');
</script>