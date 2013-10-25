$(function () {
	
	makeLocalLinksHashLinks("#flex-con");

	$(window).bind('hashchange', function() {
		loadUrlFromHash(window.location.hash);
	});

	$("#api_tree").jstree({
		"plugins"	: ["html_data","themes"],
		"themes" : {
			"theme" : "default",
			"url"	: "static/tree.css",
			"dots"	: true,
			"icons" : false
		}
	});


	if (window.location.hash != "")
	{
		loadUrlFromHash(window.location.hash);
	}

});	

function getParentNavClassIdFromUrl(href)
{
	var classId = getNavClassIdFromUrl(href);
	var parentClassId = classId.substring(0, classId.lastIndexOf("_"));
	return parentClassId;
}

function getNavClassIdFromUrl(href)
{
	var classPage = href.substring(href.lastIndexOf("/")+1);
	var className = classPage.substring(0, classPage.lastIndexOf(".html"));
	classId = "nav_"+className.replace(/\./g,'_');
	return classId;
}


function loadUrlFromHash(href)
{
	if (href.indexOf("#") == 0)
	{
		href = href.substring(1);
	}
	$("#api_content_wrapper").load(href + " #api_content", function() {
		changeTitleForCurrentPage();
		markCurrentClassInNav();
		openIndexNodeForCurrentClass();
		makeLocalLinksHashLinks("#api_content");
		scollToAnchor();
	});	
}

function changeTitleForCurrentPage()
{
	var title = getNavClassIdFromUrl(window.location.hash);
	title = title.substring(title.indexOf("_")+1);
	if (title.indexOf("#") == 0)
	{
		title = title.substring(1);
	}
	document.title = "JsDoc Reference - " + title;
}

function markCurrentClassInNav()
{
	$(".classname.active-class").removeClass("active-class");
	var classId = getNavClassIdFromUrl(window.location.href);
	$("#"+classId).addClass("active-class");
}

function openIndexNodeForCurrentClass()
{
	var classId = getParentNavClassIdFromUrl(window.location.href);
	$("#api_tree").jstree("open_node", $("#"+classId) )
}

function makeLocalLinksHashLinks(rootQuerySelector)
{
	$(rootQuerySelector+' a').each(function() {
	
		var href = $(this).attr("href");
		if (href)
		{
			var hash = href.substring(href.indexOf("#"));
			if ( this.hostname === window.location.hostname || $(this).attr('href').indexOf('/') === 0) {
				$(this).attr('href',"#" + $(this).attr("href"));
			}
		}
	});
}

function scollToAnchor()
{
	var hash = window.location.hash.substring(1);
	if (hash.indexOf("#") > -1)
	{
		var anchorIdToScrollTo = hash.substring(hash.indexOf("#")+1);
		var scrollToElement = document.getElementById(anchorIdToScrollTo);
		if (scrollToElement)
		{
			var position = $(scrollToElement).position();
			if (position)
			{
				$(window).scrollTop(position.top);
			}
		}
	}
}
