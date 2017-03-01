'use strict';

function App() {
	var element = document.getElementById('hello-world');
	element.innerHTML = 'Successfully loaded the application';
}

App.getHello = function() {
	return 'hello world!';
};

App.logHello = function() {
	console.log(App.getHello());
};

module.exports = App;
