var Ext = require( 'extjs' );
var br = require( 'br/Core' );
var WorkbenchComponent = require( 'br/workbench/ui/WorkbenchComponent' );
var ko = require( 'ko' );

/**
 * Creates an Ext Tree Model representing a Knockout View Model.
 * 
 * @param {Object} knockoutViewModel The object with Knockout observables for which
 *                 the tree model is to be built.
 */
function KnockoutModelTree( knockoutViewModel ) {
  if ( !knockoutViewModel )
  {
    throw "KnockoutModelTree expects a View Model";
  }
  this._viewModel = knockoutViewModel;
  var oTree = this._buildTree( this._viewModel );
  
  this.m_eElement = document.createElement("div");
  oTree.render(this.m_eElement);
}
br.implement( KnockoutModelTree, WorkbenchComponent );

/**
 * @see WorkbenchComponent.getElement
 */
KnockoutModelTree.prototype.getElement = function() {
  return this.m_eElement;
};

/**
 * Builds a tree for the given view model.
 *
 * @private
 */
KnockoutModelTree.prototype._buildTree = function( viewModel ) {
  var oTree = new Ext.tree.TreePanel({});
  this.m_oRootNode = new Ext.tree.TreeNode( { text : "Knockout View Model", expanded: true } );
  
  this._buildNodes(this.m_oRootNode, viewModel, true);

  oTree.setRootNode(this.m_oRootNode);
  return oTree;
};

function isPrivateVariable( key ) {
  return ( key.substr(0,2) === "m_" ||
           key.substr(0,1) === "_" );
}

/**
 * Are any properties on the object observable
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

function isObservableArray( obj ) {
  return ( ko.isObservable(obj) && obj.hasOwnProperty('removeAll')  );
}

/** @private */
KnockoutModelTree.prototype._addObservableToTree = function( treeNode, key, item, expandNode ) {
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

/** @private */
KnockoutModelTree.prototype._buildNodes = function( treeNode, viewModel, expandNode ) {
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
        label += ':' + value;
      }
      treeChildNode = new Ext.tree.TreeNode( { text : label, expanded: expandNode } );
      treeNode.appendChild( treeChildNode );
    }
  }
};

/** @private */
KnockoutModelTree.prototype._createListeners = function( observable, treeNode, key ) {
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
        label += ":" + newValue;
      }
      treeNode.setText( label );
    }
  } );
};

module.exports = KnockoutModelTree;
