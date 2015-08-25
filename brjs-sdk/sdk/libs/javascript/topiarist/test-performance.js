var topiarist = require('./src/topiarist');

function topiaristFunc(date, num) {
  topiarist.isA(date, Date);
  topiarist.isA(num, Number);
}

function manualFunc(date, num) {
  if(!(date instanceof Date)) throw new Error('date must be of type Date');
  if(typeof(num) != 'number') throw new Error('num must be a number');
}

function speedTest(funcName, func) {
  var currentDate = new Date();
  var startTime = Date.now();

  for(var i = 0; i < 1000000; ++i) {
    func(currentDate, i);
  }

  var endTime = Date.now();
  console.log(funcName + ': ' + (endTime - startTime));
}

speedTest('topiarist', topiaristFunc);
speedTest('manual', manualFunc);
speedTest('topiarist', topiaristFunc);
speedTest('manual', manualFunc);
