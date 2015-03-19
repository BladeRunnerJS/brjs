<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">

		<title>IT Application</title>
        <% String theme = request.getParameter("theme") == null ? "common" : request.getParameter("theme"); %>
		<@css.bundle theme="<%=theme%>" alternateTheme="alternate" @/>
	</head>
	<body>
		<div class="app">
			<h1 id="hello-world"></h1>
			<p>BRJS Integration Tests will be run against this application</p>
			<div class="br-logo">
				<img src="<@unbundled-resources@/>/br-logo.png" />
			</div>
			<div id="Itblade"></div>
		</div>
        <table id="outputTable" class="centre" border="1">
            <th>Test name</th>
            <th>Test Output</th>
        </table>
		<@i18n.bundle @/>
    	<!-- dev-minifier can be set to "combined" for all JS content to be bundled with a single request -->
		<@js.bundle dev-minifier="none" prod-minifier="combined"@/>
		<script>
			( function() {
				window.jndiToken = "@TEST.JNDI.TOKEN@";
				var App = require( 'itapp/App' );
				var app = new App();
			} )();
		</script>
	</body>
</html>
