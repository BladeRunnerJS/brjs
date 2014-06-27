/*
 * JsHamcrest v0.7.0
 * http://danielfm.github.com/jshamcrest/
 *
 * Library of matcher objects for JavaScript.
 *
 * Copyright (c) 2009-2013 Daniel Fernandes Martins
 * Licensed under the BSD license.
 *
 * Revision: 13d2f6ac0272b6b679eca295514fe69c0a84251a
 * Date:     Sat Jan 26 12:47:27 2013 -0200
 */

var JsHamcrest = {
  /**
   * Library version.
   */
  version: '0.7.0',

  /**
   * Delegate function, useful when used to create a matcher that has a value-equalTo semantic
   */
  EqualTo: function (func) {
    return function (matcherOrValue) {
      if (!JsHamcrest.isMatcher(matcherOrValue)) {
        return func(JsHamcrest.Matchers.equalTo(matcherOrValue));
      }
      return func(matcherOrValue);
    };
  },

  /**
   * Returns whether the given object is a matcher.
   */
  isMatcher: function(obj) {
    return obj instanceof JsHamcrest.SimpleMatcher;
  },

  /**
   * Returns whether the given arrays are equivalent.
   */
  areArraysEqual: function(array, anotherArray) {
    if (array instanceof Array || anotherArray instanceof Array) {
      if (array.length != anotherArray.length) {
        return false;
      }

      for (var i = 0; i < array.length; i++) {
        var a = array[i];
        var b = anotherArray[i];

        if (a instanceof Array || b instanceof Array) {
          if(!JsHamcrest.areArraysEqual(a, b)) {
            return false;
          }
        } else if (a != b) {
          return false;
        }
      }
      return true;
    } else {
      return array == anotherArray;
    }
  },

  /**
   * Returns whether the given Arrays are equivalent. This will return true if the objects
   * inside the Arrays are equivalent i.e. they dont have to be the same object reference.
   * Two objects with the same key value pairs will be equivalent eventhough they are not
   * the same object.
   *
   * @param {type} expected A map of expected values.
   * @param {type} actual A map of the actual values.
   * @returns {Boolean} A Boolean signifing if the two Arrays are equivalent, true if they are.
   */
  areArraysEquivalent: function(expected, actual)
  {
    if (expected.length !== actual.length)
    {
        return false;
    }

    for (var i = 0; i < expected.length; i++)
    {
        var a = expected[i];
        var b = actual[i];

        if(JsHamcrest.areTwoEntitiesEquivalent(a, b) === false)
        {
            return false;
        }
    }

    return true;
  },

  /**
   * Returns whether the given maps are equivalent. This will return true if the objects
   * inside the maps are equivalent i.e. they dont have to be the same object reference.
   * Two objects with the same key value pairs will be equivalent eventhough they are not
   * the same object.
   *
   * @param {type} expected A map of expected values.
   * @param {type} actual A map of the actual values.
   * @returns {Boolean} A Boolean signifing if the two maps are equivalent, true if they are.
   */
  areMapsEquivalent: function(expected, actual)
  {
    // we need to do this both ways in case both maps have undefined values (which makes counting the number
    // of keys a non-acceptable comparison).
    if(JsHamcrest.simpleMapCompare(expected, actual) && JsHamcrest.simpleMapCompare(actual, expected))
    {
        return true;
    }

    return false;
  },

  simpleMapCompare: function(firstMap, secondMap)
  {
    for(var item in firstMap)
    {
        if(firstMap.hasOwnProperty(item))
        {
            if(!JsHamcrest.areTwoEntitiesEquivalent(firstMap[item], secondMap[item])) return false;
        }
    }

    return true;
  },

  areTwoEntitiesEquivalent: function(expected, actual)
  {
    var expectedsMatcher = JsHamcrest.retreiveEntityMatcherFunction(expected);
    var actualsMatcher = JsHamcrest.retreiveEntityMatcherFunction(actual);

    if(expectedsMatcher === actualsMatcher && expectedsMatcher(expected, actual))
    {
        return true;
    }

    return false;
  },

  /**
   * Returns the function that would be used to compare the entity with an entity of the same type.
   *
   * @param {type} entity A JavaScript entity, this method will try and figure out what type.
   */
  retreiveEntityMatcherFunction: function(entity) {
    if ( (Array.isArray && Array.isArray(entity)) || entity instanceof Array ) return JsHamcrest.areArraysEquivalent;

    if(entity instanceof Boolean) return JsHamcrest.areBooleansEqual;

    if(entity instanceof Date) return JsHamcrest.areDatesEqual;

    if(entity instanceof Function) return JsHamcrest.areFunctionsEqual;

    if(entity instanceof Number || typeof entity === "number") return JsHamcrest.areNumbersEqual;

    if(entity instanceof String || typeof entity === "string") return JsHamcrest.areStringsEqual;

    if(entity instanceof RegExp) return JsHamcrest.areRegExpEqual;

    if(entity instanceof Error) return JsHamcrest.areErrorsEqual;

    if(typeof entity === "undefined") return JsHamcrest.areEntitiesUndefined;

    if(entity === null) return JsHamcrest.areEntitiesNull;

    if(entity.constructor === Object) return JsHamcrest.areMapsEquivalent;

    return JsHamcrest.areEntitiesStrictlyEquals;
  },

  /**
   * Simple comparator functions.
   *
   * @param {type} expected The Object that is expected to be present.
   * @param {type} actual The Object that is actually present.
   */
  areBooleansEqual: function(expected, actual) { return expected.toString() === actual.toString(); },

  areDatesEqual: function(expected, actual) {    return expected.toString() === actual.toString(); },

  areFunctionsEqual: function(expected, actual) {    return expected === actual; },

  areNumbersEqual: function(expected, actual) { return expected.valueOf() === actual.valueOf(); },

  areStringsEqual: function(expected, actual) { return expected.valueOf() === actual.valueOf(); },

  areRegExpEqual: function(expected, actual) { return expected.toString() === actual.toString(); },

  areErrorsEqual: function(expected, actual) { return expected.constructor === actual.constructor && expected.message === actual.message; },

  areEntitiesUndefined: function(expected, actual) { return expected === actual; },

  areEntitiesNull: function(expected, actual) { return expected === actual; },

  areEntitiesStrictlyEquals: function(expected, actual) { return expected === actual; },

  /**
   * Builds a matcher object that uses external functions provided by the
   * caller in order to define the current matching logic.
   */
  SimpleMatcher: function(params) {
    params = params || {};

    this.matches = params.matches;
    this.describeTo = params.describeTo;

    // Replace the function to describe the actual value
    if (params.describeValueTo) {
      this.describeValueTo = params.describeValueTo;
    }
  },

  /**
   * Matcher that provides an easy way to wrap several matchers into one.
   */
  CombinableMatcher: function(params) {
    // Call superclass' constructor
    JsHamcrest.SimpleMatcher.apply(this, arguments);

    params = params || {};

    this.and = function(anotherMatcher) {
      var all = JsHamcrest.Matchers.allOf(this, anotherMatcher);
      return new JsHamcrest.CombinableMatcher({
        matches: all.matches,

        describeTo: function(description) {
          description.appendDescriptionOf(all);
        }
      });
    };

    this.or = function(anotherMatcher) {
      var any = JsHamcrest.Matchers.anyOf(this, anotherMatcher);
      return new JsHamcrest.CombinableMatcher({
        matches: any.matches,

        describeTo: function(description) {
          description.appendDescriptionOf(any);
        }
      });
    };
  },

  /**
   * Class that builds assertion error messages.
   */
  Description: function() {
    var value = '';

    this.get = function() {
      return value;
    };

    this.appendDescriptionOf = function(selfDescribingObject) {
      if (selfDescribingObject) {
        selfDescribingObject.describeTo(this);
      }
      return this;
    };

    this.append = function(text) {
      if (text != null) {
        value += text;
      }
      return this;
    };

    this.appendLiteral = function(literal) {
      var undefined;
      if (literal === undefined) {
        this.append('undefined');
      } else if (literal === null) {
        this.append('null');
      } else if (literal instanceof Array) {
        this.appendValueList('[', ', ', ']', literal);
      } else if (typeof literal == 'string') {
        this.append('"' + literal + '"');
      } else if (literal instanceof Function) {
        this.append('Function' + (literal.name ? ' ' + literal.name : ''));
      } else {
        this.append(literal);
      }
      return this;
    };

    this.appendValueList = function(start, separator, end, list) {
      this.append(start);
      for (var i = 0; i < list.length; i++) {
        if (i > 0) {
          this.append(separator);
        }
        this.appendLiteral(list[i]);
      }
      this.append(end);
      return this;
    };

    this.appendList = function(start, separator, end, list) {
      this.append(start);
      for (var i = 0; i < list.length; i++) {
        if (i > 0) {
          this.append(separator);
        }
        this.appendDescriptionOf(list[i]);
      }
      this.append(end);
      return this;
    };
  }
};


/**
 * Describes the actual value to the given description. This method is optional
 * and, if it's not present, the actual value will be described as a JavaScript
 * literal.
 */
JsHamcrest.SimpleMatcher.prototype.describeValueTo = function(actual, description) {
  description.appendLiteral(actual);
};


// CombinableMatcher is a specialization of SimpleMatcher
JsHamcrest.CombinableMatcher.prototype = new JsHamcrest.SimpleMatcher();
JsHamcrest.CombinableMatcher.prototype.constructor = JsHamcrest.CombinableMatcher;
JsHamcrest.Matchers = {};

/**
 * The actual value must be any value considered truth by the JavaScript
 * engine.
 */
JsHamcrest.Matchers.truth = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual;
    },

    describeTo: function(description) {
      description.append('truth');
    }
  });
};

/**
 * Delegate-only matcher frequently used to improve readability.
 */
JsHamcrest.Matchers.is = JsHamcrest.EqualTo(function(matcher) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return matcher.matches(actual);
    },

    describeTo: function(description) {
      description.append('is ').appendDescriptionOf(matcher);
    }
  });
});

/**
 * The actual value must not match the given matcher or value.
 */
JsHamcrest.Matchers.not = JsHamcrest.EqualTo(function(matcher) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return !matcher.matches(actual);
    },

    describeTo: function(description) {
      description.append('not ').appendDescriptionOf(matcher);
    }
  });
});

/**
 * The actual value must be equal to the given value.
 */
JsHamcrest.Matchers.equalTo = function(expected) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      if (expected instanceof Array || actual instanceof Array) {
        return JsHamcrest.areArraysEqual(expected, actual);
      }
      return actual == expected;
    },

    describeTo: function(description) {
      description.append('equal to ').appendLiteral(expected);
    }
  });
};

/**
 * Useless always-match matcher.
 */
JsHamcrest.Matchers.anything = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return true;
    },

    describeTo: function(description) {
      description.append('anything');
    }
  });
};

/**
 * The actual value must be null (or undefined).
 */
JsHamcrest.Matchers.nil = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual == null;
    },

    describeTo: function(description) {
      description.appendLiteral(null);
    }
  });
};

/**
 * The actual value must be the same as the given value.
 */
JsHamcrest.Matchers.sameAs = function(expected) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual === expected;
    },

    describeTo: function(description) {
      description.append('same as ').appendLiteral(expected);
    }
  });
};

/**
 * The actual value is a function and, when invoked, it should thrown an
 * exception with the given name.
 */
JsHamcrest.Matchers.raises = function(exceptionName) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actualFunction) {
      try {
        actualFunction();
      } catch (e) {
        if (e.name == exceptionName) {
          return true;
        } else {
          throw e;
        }
      }
      return false;
    },

    describeTo: function(description) {
      description.append('raises ').append(exceptionName);
    }
  });
};

/**
 * The actual value is a function and, when invoked, it should raise any
 * exception.
 */
JsHamcrest.Matchers.raisesAnything = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actualFunction) {
      try {
        actualFunction();
      } catch (e) {
        return true;
      }
      return false;
    },

    describeTo: function(description) {
      description.append('raises anything');
    }
  });
};

/**
 * Combinable matcher where the actual value must match both of the given
 * matchers.
 */
JsHamcrest.Matchers.both = JsHamcrest.EqualTo(function(matcher) {
  return new JsHamcrest.CombinableMatcher({
    matches: matcher.matches,
    describeTo: function(description) {
      description.append('both ').appendDescriptionOf(matcher);
    }
  });
});

/**
 * Combinable matcher where the actual value must match at least one of the
 * given matchers.
 */
JsHamcrest.Matchers.either = JsHamcrest.EqualTo(function(matcher) {
  return new JsHamcrest.CombinableMatcher({
    matches: matcher.matches,
    describeTo: function(description) {
      description.append('either ').appendDescriptionOf(matcher);
    }
  });
});

/**
 * All the given values or matchers should match the actual value to be
 * sucessful. This matcher behaves pretty much like the && operator.
 */
JsHamcrest.Matchers.allOf = function() {
  var args = arguments;
  if (args[0] instanceof Array) {
    args = args[0];
  }
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      for (var i = 0; i < args.length; i++) {
        var matcher = args[i];
        if (!JsHamcrest.isMatcher(matcher)) {
          matcher = JsHamcrest.Matchers.equalTo(matcher);
        }
        if (!matcher.matches(actual)) {
          return false;
        }
      }
      return true;
    },

    describeTo: function(description) {
      description.appendList('(', ' and ', ')', args);
    }
  });
};

/**
 * At least one of the given matchers should match the actual value. This
 * matcher behaves pretty much like the || (or) operator.
 */
JsHamcrest.Matchers.anyOf = function() {
  var args = arguments;
  if (args[0] instanceof Array) {
    args = args[0];
  }
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      for (var i = 0; i < args.length; i++) {
        var matcher = args[i];
        if (!JsHamcrest.isMatcher(matcher)) {
          matcher = JsHamcrest.Matchers.equalTo(matcher);
        }
        if (matcher.matches(actual)) {
          return true;
        }
      }
      return false;
    },

    describeTo: function(description) {
      description.appendList('(', ' or ', ')', args);
    }
  });
};

/**
 * The actual number must be greater than the expected number.
 */
JsHamcrest.Matchers.greaterThan = function(expected) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual > expected;
    },

    describeTo: function(description) {
      description.append('greater than ').appendLiteral(expected);
    }
  });
};

/**
 * The actual number must be greater than or equal to the expected number
 */
JsHamcrest.Matchers.greaterThanOrEqualTo = function(expected) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual >= expected;
    },

    describeTo: function(description) {
      description.append('greater than or equal to ').appendLiteral(expected);
    }
  });
};

/**
 * The actual number must be less than the expected number.
 */
JsHamcrest.Matchers.lessThan = function(expected) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual < expected;
    },

    describeTo: function(description) {
      description.append('less than ').appendLiteral(expected);
    }
  });
};

/**
 * The actual number must be less than or equal to the expected number.
 */
JsHamcrest.Matchers.lessThanOrEqualTo = function(expected) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual <= expected;
    },

    describeTo: function(description) {
      description.append('less than or equal to ').append(expected);
    }
  });
};

/**
 * The actual value must not be a number.
 */
JsHamcrest.Matchers.notANumber = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return isNaN(actual);
    },

    describeTo: function(description) {
      description.append('not a number');
    }
  });
};

/**
 * The actual value must be divisible by the given number.
 */
JsHamcrest.Matchers.divisibleBy = function(divisor) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual % divisor === 0;
    },

    describeTo: function(description) {
      description.append('divisible by ').appendLiteral(divisor);
    }
  });
};

/**
 * The actual value must be even.
 */
JsHamcrest.Matchers.even = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual % 2 === 0;
    },

    describeTo: function(description) {
      description.append('even');
    }
  });
};

/**
 * The actual number must be odd.
 */
JsHamcrest.Matchers.odd = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual % 2 !== 0;
    },

    describeTo: function(description) {
      description.append('odd');
    }
  });
};

/**
 * The actual number must be between the given range (inclusive).
 */
JsHamcrest.Matchers.between = function(start) {
  return {
    and: function(end) {
      var greater = end;
      var lesser = start;

      if (start > end) {
        greater = start;
        lesser = end;
      }

      return new JsHamcrest.SimpleMatcher({
        matches: function(actual) {
          return actual >= lesser && actual <= greater;
        },

        describeTo: function(description) {
          description.append('between ').appendLiteral(lesser)
              .append(' and ').appendLiteral(greater);
        }
      });
    }
  };
};

/**
 * The actual number must be close enough to *expected*, that is, the actual
 *  number is equal to a value within some range of acceptable error.
 */
JsHamcrest.Matchers.closeTo = function(expected, delta) {
  if (!delta) {
    delta = 0;
  }

  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return (Math.abs(actual - expected) - delta) <= 0;
    },

    describeTo: function(description) {
      description.append('number within ')
          .appendLiteral(delta).append(' of ').appendLiteral(expected);
    }
  });
};

/**
 * The actual number must be zero.
 */
JsHamcrest.Matchers.zero = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual === 0;
    },

    describeTo: function(description) {
      description.append('zero');
    }
  });
};

/**
 * The actual string must be equal to the given string, ignoring case.
 */
JsHamcrest.Matchers.equalIgnoringCase = function(str) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual.toUpperCase() == str.toUpperCase();
    },

    describeTo: function(description) {
      description.append('equal ignoring case "').append(str).append('"');
    }
  });
};

/**
 * The actual string must have a substring equals to the given string.
 */
JsHamcrest.Matchers.containsString = function(str) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual.indexOf(str) >= 0;
    },

    describeTo: function(description) {
      description.append('contains string "').append(str).append('"');
    }
  });
};

/**
 * The actual string must start with the given string.
 */
JsHamcrest.Matchers.startsWith = function(str) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual.indexOf(str) === 0;
    },

    describeTo: function(description) {
      description.append('starts with ').appendLiteral(str);
    }
  });
};

/**
 * The actual string must end with the given string.
 */
JsHamcrest.Matchers.endsWith = function(str) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual.lastIndexOf(str) + str.length == actual.length;
    },

    describeTo: function(description) {
      description.append('ends with ').appendLiteral(str);
    }
  });
};

/**
 * The actual string must match the given regular expression.
 */
JsHamcrest.Matchers.matches = function(regex) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return regex.test(actual);
    },

    describeTo: function(description) {
      description.append('matches ').appendLiteral(regex);
    }
  });
};

/**
 * The actual string must look like an e-mail address.
 */
JsHamcrest.Matchers.emailAddress = function() {
  var regex = /^([a-z0-9_\.\-\+])+\@(([a-z0-9\-])+\.)+([a-z0-9]{2,4})+$/i;

  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return regex.test(actual);
    },

    describeTo: function(description) {
      description.append('email address');
    }
  });
};

/**
 * The actual value has a member with the given name.
 */
JsHamcrest.Matchers.hasMember = function(memberName, matcherOrValue) {
  var undefined;
  if (matcherOrValue === undefined) {
    matcherOrValue = JsHamcrest.Matchers.anything();
  } else if (!JsHamcrest.isMatcher(matcherOrValue)) {
    matcherOrValue = JsHamcrest.Matchers.equalTo(matcherOrValue);
  }

  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      if (actual && memberName in actual) {
        return matcherOrValue.matches(actual[memberName]);
      }
      return false;
    },

    describeTo: function(description) {
      description.append('has member ').appendLiteral(memberName)
        .append(' (').appendDescriptionOf(matcherOrValue).append(')');
    }
  });
};

/**
 * The actual value has a function with the given name.
 */
JsHamcrest.Matchers.hasFunction = function(functionName) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      if (actual) {
        return functionName in actual &&
            actual[functionName] instanceof Function;
      }
      return false;
    },

    describeTo: function(description) {
      description.append('has function ').appendLiteral(functionName);
    }
  });
};

/**
 * The actual value must be an instance of the given class.
 */
JsHamcrest.Matchers.instanceOf = function(clazz) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return !!(actual instanceof clazz);
    },

    describeTo: function(description) {
      var className = clazz.name ? clazz.name : 'a class';
      description.append('instance of ').append(className);
    }
  });
};

/**
 * The actual value must be an instance of the given type.
 */
JsHamcrest.Matchers.typeOf = function(typeName) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return (typeof actual == typeName);
    },

    describeTo: function(description) {
      description.append('typeof ').append('"').append(typeName).append('"');
    }
  });
};

/**
 * The actual value must be an object.
 */
JsHamcrest.Matchers.object = function() {
  return new JsHamcrest.Matchers.instanceOf(Object);
};

/**
 * The actual value must be a string.
 */
JsHamcrest.Matchers.string = function() {
  return new JsHamcrest.Matchers.typeOf('string');
};

/**
 * The actual value must be a number.
 */
JsHamcrest.Matchers.number = function() {
  return new JsHamcrest.Matchers.typeOf('number');
};

/**
 * The actual value must be a boolean.
 */
JsHamcrest.Matchers.bool = function() {
  return new JsHamcrest.Matchers.typeOf('boolean');
};

/**
 * The actual value must be a function.
 */
JsHamcrest.Matchers.func = function() {
  return new JsHamcrest.Matchers.instanceOf(Function);
};

/**
 * The actual value should be an array and it must contain at least one value
 * that matches the given value or matcher.
 */
JsHamcrest.Matchers.hasItem = JsHamcrest.EqualTo(function(matcher) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      // Should be an array
      if (!(actual instanceof Array)) {
        return false;
      }

      for (var i = 0; i < actual.length; i++) {
        if (matcher.matches(actual[i])) {
          return true;
        }
      }
      return false;
    },

    describeTo: function(description) {
      description.append('array contains item ')
          .appendDescriptionOf(matcher);
      }
  });
});

/**
 * The actual value should be an array and the given values or matchers must
 * match at least one item.
 */
JsHamcrest.Matchers.hasItems = function() {
  var items = [];
  for (var i = 0; i < arguments.length; i++) {
    items.push(JsHamcrest.Matchers.hasItem(arguments[i]));
  }
  return JsHamcrest.Matchers.allOf(items);
};

/**
 * The actual value should be an array and the given value or matcher must
 * match all items.
 */
JsHamcrest.Matchers.everyItem = JsHamcrest.EqualTo(function(matcher) {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      // Should be an array
      if (!(actual instanceof Array)) {
        return false;
      }

      for (var i = 0; i < actual.length; i++) {
        if (!matcher.matches(actual[i])) {
          return false;
        }
      }
      return true;
    },

    describeTo: function(description) {
      description.append('every item ')
          .appendDescriptionOf(matcher);
    }
  });
});

/**
 * The given array must contain the actual value.
 */
JsHamcrest.Matchers.isIn = function() {
  var equalTo = JsHamcrest.Matchers.equalTo;

  var args = arguments;
  if (args[0] instanceof Array) {
    args = args[0];
  }

  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      for (var i = 0; i < args.length; i++) {
        if (equalTo(args[i]).matches(actual)) {
          return true;
        }
      }
      return false;
    },

    describeTo: function(description) {
      description.append('one of ').appendLiteral(args);
    }
  });
};

/**
 * Alias to 'isIn' matcher.
 */
JsHamcrest.Matchers.oneOf = JsHamcrest.Matchers.isIn;

/**
 * The actual value should be an array and it must be empty to be sucessful.
 */
JsHamcrest.Matchers.empty = function() {
  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return actual.length === 0;
    },

    describeTo: function(description) {
      description.append('empty');
    }
  });
};

/**
 * The length of the actual value value must match the given value or matcher.
 */
JsHamcrest.Matchers.hasSize = JsHamcrest.EqualTo(function(matcher) {
  var getSize = function(actual) {
    var size = actual.length;
    if (size === undefined && typeof actual === 'object') {
      size = 0;
      for (var key in actual)
        size++;
    }
    return size;
  };

  return new JsHamcrest.SimpleMatcher({
    matches: function(actual) {
      return matcher.matches(getSize(actual));
    },

    describeTo: function(description) {
      description.append('has size ').appendDescriptionOf(matcher);
    },

    describeValueTo: function(actual, description) {
      description.append(getSize(actual));
    }
  });
});

JsHamcrest.Matchers.equivalentMap = function(expected) {
    return new JsHamcrest.SimpleMatcher({
        matches: function(actual)
        {
            if(JsHamcrest.retreiveEntityMatcherFunction(actual) === JsHamcrest.areMapsEquivalent &&
                    JsHamcrest.retreiveEntityMatcherFunction(expected) === JsHamcrest.areMapsEquivalent)
            {
                return JsHamcrest.areMapsEquivalent(expected, actual);
            }
           
            return false; //The passed in objects aren't maps.
        },

        describeTo: function(description) {
            description.append('map equivalent to ').appendLiteral(expected);
        }
    });
};

JsHamcrest.Matchers.equivalentArray = function(expected) {
    return new JsHamcrest.SimpleMatcher({
        matches: function(actual)
        {
            if (expected instanceof Array && actual instanceof Array)
            {
                return JsHamcrest.areArraysEquivalent(expected, actual);
            }
           
            return false; //The passed in objects aren't Arrays.
        },

        describeTo: function(description) {
            description.append('array equivalent to ').appendLiteral(expected);
        }
    });
};
JsHamcrest.Operators = {};

/**
 * Returns those items of the array for which matcher matches.
 */
JsHamcrest.Operators.filter = function(array, matcherOrValue) {
  if (!(array instanceof Array) || matcherOrValue == null) {
    return array;
  }
  if (!(matcherOrValue instanceof JsHamcrest.SimpleMatcher)) {
    matcherOrValue = JsHamcrest.Matchers.equalTo(matcherOrValue);
  }

  var result = [];
  for (var i = 0; i < array.length; i++) {
    if (matcherOrValue.matches(array[i])) {
      result.push(array[i]);
    }
  }
  return result;
};

/**
 * Generic assert function.
 */
JsHamcrest.Operators.assert = function(actualValue, matcherOrValue, options) {
  options = options ? options : {};
  var description = new JsHamcrest.Description();

  if (matcherOrValue == null) {
    matcherOrValue = JsHamcrest.Matchers.truth();
  } else if (!JsHamcrest.isMatcher(matcherOrValue)) {
    matcherOrValue = JsHamcrest.Matchers.equalTo(matcherOrValue);
  }

  if (options.message) {
    description.append(options.message).append('. ');
  }

  description.append('Expected ');
  matcherOrValue.describeTo(description);

  if (!matcherOrValue.matches(actualValue)) {
    description.passed = false;
    description.append(' but was ');
    matcherOrValue.describeValueTo(actualValue, description);
    if (options.fail) {
      options.fail(description.get());
    }
  } else {
    description.append(': Success');
    description.passed = true;
    if (options.pass) {
      options.pass(description.get());
    }
  }
  return description;
};

/**
 * Delegate function, useful when used along with raises() and raisesAnything().
 */
JsHamcrest.Operators.callTo = function() {
  var func = [].shift.call(arguments);
  var args = arguments;
  return function() {
    return func.apply(this, args);
  };
}

/**
 * Integration utilities.
 */

JsHamcrest.Integration = (function() {

  var self = this;

  return {

    /**
     * Copies all members of an object to another.
     */
    copyMembers: function(source, target) {
      if (arguments.length == 1) {
        target = source;
        JsHamcrest.Integration.copyMembers(JsHamcrest.Matchers, target);
        JsHamcrest.Integration.copyMembers(JsHamcrest.Operators, target);
      } else if (source) {
        for (var method in source) {
          if (!(method in target)) {
            target[method] = source[method];
          }
        }
      }
    },

    /**
     * Adds the members of the given object to JsHamcrest.Matchers
     * namespace.
     */
    installMatchers: function(matchersNamespace) {
      var target = JsHamcrest.Matchers;
      JsHamcrest.Integration.copyMembers(matchersNamespace, target);
    },

    /**
     * Adds the members of the given object to JsHamcrest.Operators
     * namespace.
     */
    installOperators: function(operatorsNamespace) {
      var target = JsHamcrest.Operators;
      JsHamcrest.Integration.copyMembers(operatorsNamespace, target);
    },

    /**
     * Uses the web browser's alert() function to display the assertion
     * results. Great for quick prototyping.
     */
    WebBrowser: function() {
      JsHamcrest.Integration.copyMembers(self);

      self.assertThat = function (actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            alert('[FAIL] ' + message);
          },
          pass: function(message) {
            alert('[SUCCESS] ' + message);
          }
        });
      };
    },

    /**
     * Uses the Rhino's print() function to display the assertion results.
     * Great for prototyping.
     */
    Rhino: function() {
      JsHamcrest.Integration.copyMembers(self);

      self.assertThat = function (actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            print('[FAIL] ' + message + '\n');
          },
          pass: function(message) {
            print('[SUCCESS] ' + message + '\n');
          }
        });
      };
    },

    /**
     * JsTestDriver integration.
     */
    JsTestDriver: function(params) {
      params = params ? params : {};
      var target = params.scope || self;

      JsHamcrest.Integration.copyMembers(target);

      // Function called when an assertion fails.
      function fail(message) {
        var exc = new Error(message);
        exc.name = 'AssertError';

        try {
          // Removes all jshamcrest-related entries from error stack
          var re = new RegExp('jshamcrest.*\.js\:', 'i');
          var stack = exc.stack.split('\n');
          var newStack = '';
          for (var i = 0; i < stack.length; i++) {
            if (!re.test(stack[i])) {
              newStack += stack[i] + '\n';
            }
          }
          exc.stack = newStack;
        } catch (e) {
          // It's okay, do nothing
        }
        throw exc;
      }

      // Assertion method exposed to JsTestDriver.
      target.assertThat = function (actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: fail
        });
      };
    },
    
    /**
     * NodeUnit (Node.js Unit Testing) integration.
     */
     
    Nodeunit: function(params) {
      params = params ? params : {};
      var target = params.scope || global;
        
      JsHamcrest.Integration.copyMembers(target);
        
      target.assertThat = function(actual, matcher, message, test) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            test.ok(false, message);
          },
          pass: function(message) {
            test.ok(true, message);
          }
        });
      };
    },

    /**
     * JsUnitTest integration.
     */
    JsUnitTest: function(params) {
      params = params ? params : {};
      var target = params.scope || JsUnitTest.Unit.Testcase.prototype;

      JsHamcrest.Integration.copyMembers(target);

      // Assertion method exposed to JsUnitTest.
      target.assertThat = function (actual, matcher, message) {
        var self = this;

        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            self.fail(message);
          },
          pass: function() {
            self.pass();
          }
        });
      };
    },

    /**
     * YUITest (Yahoo UI) integration.
     */
    YUITest: function(params) {
      params = params ? params : {};
      var target = params.scope || self;

      JsHamcrest.Integration.copyMembers(target);

      target.Assert = YAHOO.util.Assert;

      // Assertion method exposed to YUITest.
      YAHOO.util.Assert.that = function(actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            YAHOO.util.Assert.fail(message);
          }
        });
      };
    },

    /**
     * QUnit (JQuery) integration.
     */
    QUnit: function(params) {
      params = params ? params : {};
      var target = params.scope || self;

      JsHamcrest.Integration.copyMembers(target);

      // Assertion method exposed to QUnit.
      target.assertThat = function(actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            QUnit.ok(false, message);
          },
          pass: function(message) {
            QUnit.ok(true, message);
          }
        });
      };
    },

    /**
     * jsUnity integration.
     */
    jsUnity: function(params) {
      params = params ? params : {};
      var target = params.scope || jsUnity.env.defaultScope;
      var assertions = params.attachAssertions || false;

      JsHamcrest.Integration.copyMembers(target);

      if (assertions) {
        jsUnity.attachAssertions(target);
      }

      // Assertion method exposed to jsUnity.
      target.assertThat = function(actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            throw message;
          }
        });
      };
    },

    /**
     * Screw.Unit integration.
     */
    screwunit: function(params) {
      params = params ? params : {};
      var target = params.scope || Screw.Matchers;

      JsHamcrest.Integration.copyMembers(target);

      // Assertion method exposed to Screw.Unit.
      target.assertThat = function(actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            throw message;
          }
        });
      };
    },

    /**
     * Jasmine integration.
     */
    jasmine: function(params) {
      params = params ? params : {};
      var target = params.scope || self;

      JsHamcrest.Integration.copyMembers(target);

      // Assertion method exposed to Jasmine.
      target.assertThat = function(actual, matcher, message) {
        return JsHamcrest.Operators.assert(actual, matcher, {
          message: message,
          fail: function(message) {
            jasmine.getEnv().currentSpec.addMatcherResult(
              new jasmine.ExpectationResult({passed:false, message:message})
            );
          },
          pass: function(message) {
            jasmine.getEnv().currentSpec.addMatcherResult(
              new jasmine.ExpectationResult({passed:true, message:message})
            );
          }
        });
      };
    }
  };
})();

if (typeof exports !== "undefined") exports.JsHamcrest = JsHamcrest;