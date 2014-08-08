"use strict";

/**
* @module br/knockout/workbench/ExtJsTreeModelKnockoutAdaptor
*/

var Ext = require( 'extjs' );
var br = require( 'br/Core' );
var WorkbenchComponent = require( 'br/workbench/ui/WorkbenchComponent' );
var TreeSearcher = require( 'br/presenter/workbench/ui/TreeSearcher' );
var ko = require( 'ko' );

/**
* Creates an Ext Tree Model representing a Knockout View Model.
*
* @param {Object} knockoutViewModel The object with Knockout observables for which
*                 the tree model is to be built.
* @class
* @alias module:br/knockout/workbench/ExtJsTreeModelKnockoutAdaptor
* @extends br/workbench/ui/WorkbenchComponent
*/
function ExtJsTreeModelKnockoutAdaptor( knockoutViewModel ) {
  if ( !knockoutViewModel )
  {
    throw "ExtJsTreeModelKnockoutAdaptor expects a View Model";
  }
  this._rootNode = null;
  this._tree = null;

  this._viewModel = knockoutViewModel;
  this._buildTree( this._viewModel );

  this.m_eElement = document.createElement("div");
  this._tree.render(this.m_eElement);

  this._treeSearcher = new TreeSearcher(this._rootNode);
}
br.implement( ExtJsTreeModelKnockoutAdaptor, WorkbenchComponent );

/**
* Get the element for the ExtJsTreeModelKnockoutAdaptor
* @see br/workbench/ui/WorkbenchComponent.getElement
*/
ExtJsTreeModelKnockoutAdaptor.prototype.getElement = function() {
  return this.m_eElement;
};

/**
* Search the tree for the given value
* @param {String} sValue
*/
ExtJsTreeModelKnockoutAdaptor.prototype.search = function(sValue)
{
  this._treeSearcher.search(sValue);
};

/** @private */
ExtJsTreeModelKnockoutAdaptor.prototype._buildTree = function( viewModel ) {
  this._tree = new Ext.tree.TreePanel({});
  this._rootNode = new Ext.tree.TreeNode( { text : "Knockout View Model", expanded: true } );

  this._buildNodes(this._rootNode, viewModel, true);

  this._tree.setRootNode(this._rootNode);
};

function isPrivateVariable( key ) {
  return ( key.substr(0,2) === "m_" ||
           key.substr(0,1) === "_" );
}

/**
* Checks whether any properties on the object are observable
* @param {Object} obj
*/
function hasObservables( obj ) {
  var isObservable = false;
  for( var x in obj ) {
    isObservable = ko.isObservable( obj[ x ] );
    if( isObservable ) {
      break;
    }
  }
  return isObservable;
}

/**
* Checks whether the object is an observable array
* @param {Object} obj
*/
function isObservableArray( obj ) {
  return ( ko.isObservable(obj) && obj.hasOwnProperty('removeAll')  );
}

/** @private */
ExtJsTreeModelKnockoutAdaptor.prototype._addObservableToTree = function( treeNode, key, item, expandNode ) {
  var label = key;
  var value = item();
  var isArray = false;
  if (value !== undefined ) {
    isArray = isObservableArray( item );
    if( !isArray ) {
      label += ":" + value;
    }
  }
  var treeChildNode = new Ext.tree.TreeNode( { text : label, expanded: expandNode } );
  treeNode.appendChild( treeChildNode );

  this._createListeners( item, treeChildNode, key );
  if( isArray ) {
    this._buildNodes( treeChildNode, value, true);
  }
};

function getSafeJsonValue( obj ) {
  var val = obj;
  try {
    val = JSON.stringify( obj );
  }
  catch( e ){}
  return val;
}

/** @private */
ExtJsTreeModelKnockoutAdaptor.prototype._buildNodes = function( treeNode, viewModel, expandNode ) {
  for (var key in viewModel) {

    if( isPrivateVariable( key ) ) {
      continue;
    }

    // TODO: refactor like-crazy!
    var item = viewModel[ key ];
    var treeChildNode;
    var label;
    var value;
    if ( ko.isObservable( item ) ) {
      this._addObservableToTree( treeNode, key, item, expandNode );
    }
    else if( typeof item === 'object' && hasObservables( item ) ) {
      treeChildNode = new Ext.tree.TreeNode( { text : key, expanded: expandNode } );
      treeNode.appendChild( treeChildNode );
      this._buildNodes( treeChildNode, item, true );
    }
    else if( typeof item !== 'function' ) {
      label = key;
      value = viewModel[ key ];
      if( value !== undefined ) {
        label += ':' + getSafeJsonValue( value );
      }
      treeChildNode = new Ext.tree.TreeNode( { text : label, expanded: expandNode } );
      treeNode.appendChild( treeChildNode );
    }
  }
};

/** @private */
ExtJsTreeModelKnockoutAdaptor.prototype._createListeners = function( observable, treeNode, key ) {
  var self = this;
  observable.subscribe( function( newValue ) {

    var hasChildren = !!treeNode.firstChild;
    while( treeNode.firstChild ) {
      treeNode.removeChild( treeNode.firstChild );
    }
    if( hasChildren ) {
      self._buildNodes( treeNode, newValue, true );
      // when it had no children it was automatically collapsed
      treeNode.expand();
    }
    else {
      var label = key;
      if (newValue !== undefined) {
        label += ":" + getSafeJsonValue( newValue );
      }
      treeNode.setText( label );
    }
  } );
};

module.exports = ExtJsTreeModelKnockoutAdaptor;
