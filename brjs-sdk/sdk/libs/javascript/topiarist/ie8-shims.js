'use strict';

// http://www.adequatelygood.com/Replacing-setTimeout-Globally.html
global.setTimeout = global.setTimeout;
global.setInterval = global.setInterval;
global.clearTimeout = global.clearTimeout;
global.clearInterval = global.clearInterval;

require('core-js/es5');
