
/** Called automatically by JsDoc Toolkit. */
function publish(symbolSet) {
	publish.conf = {  // trailing slash expected for dirs
		ext:         ".html",
		outDir:      JSDOC.opt.d || SYS.pwd+"../out/jsdoc/",
		templatesDir: JSDOC.opt.t || SYS.pwd+"../templates/jsdoc/",
		symbolsDir:  ""
	};
	publish.conf.staticDir = publish.conf.templatesDir+"/static/";
	
	
	// create the folders and subfolders to hold the output
	IO.mkPath((publish.conf.outDir).split("/"));
		
	// used to allow Link to check the details of things being linked to
	Link.symbolSet = symbolSet;

	// create the required templates
	try {
		var classTemplate = new JSDOC.JsPlate(publish.conf.templatesDir+"class.tmpl");
		var classesTemplate = new JSDOC.JsPlate(publish.conf.templatesDir+"allclasses.tmpl");
	}
	catch(e) {
		print("Couldn't create the required templates: "+e);
		quit();
	}
	
	// some ustility filters
	function hasNoParent($) {return ($.memberOf == "")}
	function isaFile($) {return ($.is("FILE"))}
	function isaClass($) {return ($.is("CONSTRUCTOR") || $.isNamespace)}
	function isNotGlobal($) {return ($.alias != "_global_") }
	
	// get an array version of the symbolset, useful for filtering
	var symbols = symbolSet.toArray();
	 	
 	// get a list of all the classes in the symbolset
 	var classes = symbols.filter(isaClass).filter(isNotGlobal).sort(makeSortby("alias"));
	
	// create a filemap in which outfiles must be to be named uniquely, ignoring case
	if (JSDOC.opt.u) {
		var filemapCounts = {};
		Link.filemap = {};
		for (var i = 0, l = classes.length; i < l; i++) {
			var lcAlias = classes[i].alias.toLowerCase();
			
			if (!filemapCounts[lcAlias]) filemapCounts[lcAlias] = 1;
			else filemapCounts[lcAlias]++;
			
			Link.filemap[classes[i].alias] = 
				(filemapCounts[lcAlias] > 1)?
				lcAlias+"_"+filemapCounts[lcAlias] : lcAlias;
		}
	}
	
	// create a class index, displayed in the left-hand column of every class page
	Link.base = "";
	classes.html = generateClassesListNestedHtml(classes);
 	publish.classesIndex = classesTemplate.process(classes); // kept in memory
	
	// create each of the class pages
	for (var i = 0, l = classes.length; i < l; i++) {
		var symbol = classes[i];
		
		symbol.events = symbol.getEvents();   // 1 order matters
		symbol.methods = symbol.getMethods(); // 2
		
		Link.currentSymbol= symbol;
		var output = "";
		output = classTemplate.process(symbol);
		
		IO.saveFile(publish.conf.outDir, ((JSDOC.opt.u)? Link.filemap[symbol.alias] : symbol.alias) + publish.conf.ext, output);
	}
	
	
	Link.base = ""
	classes.html = generateClassesListNestedHtml(classes);
 	publish.classesIndex = classesTemplate.process(classes); // kept in memory
	
	// create the class index page
	try {
		var classesindexTemplate = new JSDOC.JsPlate(publish.conf.templatesDir+"classesindex.tmpl");
	}
	catch(e) { print(e.message); quit(); }
	
	var classesIndex = classesindexTemplate.process(classes);
	IO.saveFile(publish.conf.outDir, "classesindex"+publish.conf.ext, classesIndex);
	classesindexTemplate = classesIndex = null;	
	
	
	// create the index page
	try {
		var indexTemplate = new JSDOC.JsPlate(publish.conf.templatesDir+"index.tmpl");
	}
	catch(e) { print(e.message); quit(); }
	
	var index = indexTemplate.process(classes);
	IO.saveFile(publish.conf.outDir, "index"+publish.conf.ext, index);
	indexTemplate = index = classes = null;	
	
	/* copy some static css and js into /static */	
	IO.mkPath((publish.conf.outDir+"static").split("/"));

	IO.copyFile(publish.conf.staticDir+"/functions.js", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/jquery-1.8.2.min.js", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/jquery.jstree.js", publish.conf.outDir+"/static");

	IO.copyFile(publish.conf.staticDir+"/caplin-styles.css", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/style-overrides.css", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/tree.css", publish.conf.outDir+"/static");

	IO.copyFile(publish.conf.staticDir+"/d.gif", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/d.png", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/divider-icon.png", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/throbber.gif", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/caplin-logo.png", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/background.png", publish.conf.outDir+"/static");
	IO.copyFile(publish.conf.staticDir+"/top-right-branding.png", publish.conf.outDir+"/static");
	
	IO.mkPath((publish.conf.outDir+"static/icons").split("/"));
	IO.copyFile(publish.conf.staticDir+"/icons/beta.gif", publish.conf.outDir+"/static/icons");
	IO.copyFile(publish.conf.staticDir+"/icons/class.gif", publish.conf.outDir+"/static/icons");
	IO.copyFile(publish.conf.staticDir+"/icons/component.gif", publish.conf.outDir+"/static/icons");
	IO.copyFile(publish.conf.staticDir+"/icons/control.gif", publish.conf.outDir+"/static/icons");
	IO.copyFile(publish.conf.staticDir+"/icons/interface.gif", publish.conf.outDir+"/static/icons");
	IO.copyFile(publish.conf.staticDir+"/icons/package.gif", publish.conf.outDir+"/static/icons");
	IO.copyFile(publish.conf.staticDir+"/icons/singleton.gif", publish.conf.outDir+"/static/icons");
	
	IO.copyFile(publish.conf.staticDir+"/favicon.ico", publish.conf.outDir);
}


/** Just the first sentence (up to a full stop). Should not break on dotted variable names. */
function summarize(desc) {
	if (typeof desc != "undefined")
		return desc.match(/([\w\W]+?\.)[^a-z0-9_$]/i)? RegExp.$1 : desc;
}

/** Make a symbol sorter by some attribute. */
function makeSortby(attribute) {
	return function(a, b) {
		if (a[attribute] != undefined && b[attribute] != undefined) {
			a = a[attribute].toLowerCase();
			b = b[attribute].toLowerCase();
			if (a < b) return -1;
			if (a > b) return 1;
			return 0;
		}
	}
}

/** Pull in the contents of an external file at the given path. */
function include(path) {
	var path = publish.conf.templatesDir+path;
	return IO.readFile(path);
}


/** Build output for displaying function parameters. */
function makeSignature(params) {
	if (!params) return "()";
	var signature = "("
	+
	params.filter(
		function($) {
			return $.name.indexOf(".") == -1; // don't show config params in signature
		}
	).map(
		function($) {
			return new Link().toSymbol($.type) + " " + $.name;
		}
	).join(", ")
	+
	")";
	return signature;
}

/** Find symbol {@link ...} strings in text and turn into html links */
function resolveLinks(str, from) {
	str = str.replace(/\{@link ([^} ]+) ?\}/gi,
		function(match, symbolName) {
			return new Link().toSymbol(symbolName);
		}
	);
	
	return str;
}



/*/#### CLASSES MAP ####/*/
ClassesMap = function()
{
	this.m_oMap = {};
}
ClassesMap.prototype.addClass = function(oClassNode)
{
	this.m_oMap[oClassNode.getClassName()] = oClassNode;
}
ClassesMap.prototype.getClassNode = function(sClass)
{
	return this.m_oMap[sClass];
}
ClassesMap.prototype.hasClass = function(sClass)
{
	return this.getClassNode(sClass) != null;
}

/*/#### CLASSES NODE ####/*/
ClassNode = function(sClassName, oClass)
{
	this.m_sClassName = sClassName;
	this.m_oChildren = new Array();
	if (oClass && oClass.comment)
	{
		var isBladerunnerClass = (oClass.comment.getTag("BladeRunner").length);
		if (isBladerunnerClass)
		{
			this.distTypeClass = "bladerunner";
		}
		else
		{
			this.distTypeClass = "caplintrader";
		}
	}
}
ClassNode.prototype.addChild = function(oChild)
{
	this.m_oChildren.push(oChild);
}
ClassNode.prototype.getClassName = function()
{
	return this.m_sClassName;
}
ClassNode.prototype.getChildren = function()
{
	return this.m_oChildren;
}


function generateClassesListNestedHtml(classes, curIndex)
{
	return _generateClassesListNestedHtml(makeIntoClassesTree(classes), 0);
}

function _generateClassesListNestedHtml(classNode)
{
	if (classNode == null) { return ""; }
	
	var navPrepend = "nav_";
	
	var html = "";
	if (classNode.getClassName() == "" || classNode.getClassName() == "_global_") {
		for (var i = 0; i < classNode.getChildren().length; i++)
		{
			var childNode = classNode.getChildren()[i];
			html += _generateClassesListNestedHtml(childNode);
		}
	}
	else {
		html = "<ul><li>";
		
		var className = classNode.getClassName();
		
		var linkText = new Link().toClass(className)+"";
			
		var classDepth = getPackageDepthForClass(className); 
		var depthClassName = "classDepth"+classDepth
		
		var distTypeClass = (classNode.distTypeClass) ? " "+classNode.distTypeClass+"-library " : "";
		
		var classId = className.replace(/\./g,'_');
		
		var idAttr = "id='" + navPrepend + classId + "' ";
		var classAttr = "class='classname " + depthClassName + distTypeClass + "' ";
		
		if (linkText.indexOf("<a") == 0)
		{
			linkText = "<a " + idAttr + classAttr + linkText.substring(2);
		}
		else
		{
			linkText = "<span " + idAttr + classAttr + ">"+linkText+"</span>";
		}
		var onlyClassName = className.substring(className.lastIndexOf(".")+1);
		linkText = linkText.replace(">"+className+"</", ">"+onlyClassName+"</");
	
		html += linkText;
	
	
		for (var i = 0; i < classNode.getChildren().length; i++)
		{
			var childNode = classNode.getChildren()[i];
			html += _generateClassesListNestedHtml(childNode);
			
		}
	
		html += "</li></ul>";
	}
	return html;
}

/* classes tree stuff */

function makeIntoClassesTree(classes)
{
	var classesMap = new ClassesMap();
	var rootClass = new ClassNode("", null);
	classesMap.addClass(rootClass);
	
	for (var i = 0; i < classes.length; i++)
	{
		var thisClass = classes[i].alias;
		var thisClassNode = new ClassNode(thisClass, classes[i]);
		classesMap.addClass(thisClassNode);
		
		var parentNode = getParentNode(classesMap, thisClass);		
		parentNode.addChild(thisClassNode);
	}
	
	return rootClass;
}

function getParentNode(classesMap, thisClass)
{
	var thisClassParentClass = thisClass.substring(0, thisClass.lastIndexOf("."));
	var parentNode = classesMap.getClassNode(thisClassParentClass);
	if (parentNode == null)
	{
		parentNode = new ClassNode(thisClassParentClass);
		classesMap.addClass(parentNode);
		getParentNode(classesMap, thisClassParentClass).addChild(parentNode);
	}
	return parentNode;
}

function getPackageDepthForClass(theClass)
{
	if (!theClass)
	{
		return -1;
	}
	var matches = theClass.match(/\./g)
	return (matches != null) ? matches.length + 1 : 1;
}

