/*
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
Sonatype.repoServer.ArtifactAuditPanel = function( config ) {
  var config = config || {};
  var defaultConfig = {
    halfSize: false
  };
  Ext.apply( this, config, defaultConfig );
  
  this.sp = Sonatype.lib.Permissions;
  
  this.linkDivId = Ext.id();
  this.linkLabelId = Ext.id();
  
//  alert( this.linkDivId );
  
  var items = [];
        
  if ( this.halfSize == true ) {
    items.push({
      xtype: 'panel',
      layout: 'form',
      anchor: Sonatype.view.FIELD_OFFSET + ' -10',
      labelWidth: 70,
      items: [
        { 
          xtype: 'textfield',
          fieldLabel: 'Owner',
          name: 'owner',
          anchor: Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
          allowBlank: true,
          readOnly: true
        },
        {
          xtype: 'textfield',
          fieldLabel: 'Date Uploaded',
          name: 'captured-on',
          anchor: Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
          allowBlank: true,
          readOnly: true
        },
      ]
    });
  }
  else {
    items.push({
      xtype: 'panel',
        layout: 'form',
        anchor: Sonatype.view.FIELD_OFFSET + ' -10',
        labelWidth: 70,
        items: [
          { 
            xtype: 'textfield',
            fieldLabel: 'Owner',
            name: 'owner',
            anchor: Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
            allowBlank: true,
            readOnly: true
          },
          {
            xtype: 'textfield',
            fieldLabel: 'Date Uploaded',
            name: 'captured-on',
            anchor: Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
            allowBlank: true,
            readOnly: true
          },
        ]
    });
  }
  
  this.formPanel = new Ext.form.FormPanel( {
    autoScroll: true,
    border: false,
    frame: true,
    collapsible: false,
    collapsed: false,
    items: items
  } );

  Sonatype.repoServer.ArtifactAuditPanel.superclass.constructor.call( this, {
    title: 'Audit Information',
    layout: 'fit',
    collapsible: false,
    collapsed: false,
    split: true,
    frame: false,
    autoScroll: true,

    items: [
      this.formPanel
    ]
  } );
};

Ext.extend( Sonatype.repoServer.ArtifactAuditPanel, Ext.Panel, {
//  formatDownloadLink: function( data ) {
//  	var pomLink = data.pomLink;
//  	var artifactLink = data.artifactLink;
//  
//  	var links = [];
//  	if ( pomLink ) {
//  		links.push( this.makeDownloadLink( pomLink, 'pom' ) );
//  	}
//  	if ( artifactLink ) {
//  		links.push( this.makeDownloadLink( artifactLink, 'artifact' ) );
//  	}
//  	return links.join(', ');
//  },
//  
//  makeDownloadLink: function( url, title ) {
//    return String.format( '<a target="_blank" href="{0}">{1}</a>', url, title );
//  },

  printAll: function( obj ) {
	    var str = '';
	    for (var memb in obj)
	    str += memb + ' = ' + obj[memb] + '\n';

	    alert( str );
  },
	
  showArtifact: function( data ) {
	this.printAll( data );
	
	var serviceUrl = Sonatype.config.servicePath + '/audit/log/' + data.repoId + '/gav/' + data.groupId + '/' + data.artifactId + '/' + data.version;
	var query = '';
	if ( data.extension != null )
	{
		query += 't=' + data.extension;
	}
	
	if ( query.length > 0 )
	{
		query += '&';
	}
	
	if ( data.classifier != null )
	{
		query += 'c=' + data.classifier;
	}
	
	if ( query.length > 0 )
	{
		serviceUrl += '?' + query;
	}
	
	alert( "Loading: " + serviceUrl );
	
    this.formPanel.getForm().doAction( 'sonatypeLoad', {
      url: serviceUrl,
      method: 'GET',
      fpanel: this.formPanel
	} );
	  
    // TODO: setup the content of the panel for this artifact/path/whatever.
//    this.formPanel.form.setValues( data );
    
    if ( this.sp.checkPermission( 'nexus:artifact', this.sp.READ) ) {
        // TODO: setup any link to the file...not sure what we need here, though.
//	    var linkLabel = document.getElementById( this.linkLabelId );
//	    var linkDiv = document.getElementById( this.linkDivId );
//	    var linkHtml = this.formatDownloadLink( data );
//	    if ( empty || linkHtml.length == 0 ) {
//	    	linkLabel.innerHTML = '';
//	    } else {
//	    	linkLabel.innerHTML = 'Download: ';
//	    	linkDiv.innerHTML =  linkHtml;
//	    }
    }
  }
} );

//Sonatype.Events.addListener( 'fileNodeClickedEvent', function( node, passthru ) {
//  if ( passthru 
//      && passthru.container
//      && passthru.container.artifactContainer ) 
//  {
//	  // FIXME: These are all ZERO when using the repo browser!!!
//	  alert( passthru.container.artifactContainer.items.getCount() );
//      passthru.container.artifactContainer.expand();
//  }
//});

Sonatype.Events.addListener('artifactContainerInit', function(artifactContainer) {
  alert( "new artifact container created.");
  
  artifactContainer.add( new Sonatype.repoServer.ArtifactAuditPanel( { 
    name: 'ArtifactAuditPanel',
    tabTitle: 'Audit Information',
    halfSize: artifactContainer.halfSize
  } ) );
});

Sonatype.Events.addListener('artifactContainerUpdate', function(artifactContainer, data) {
  var panel = artifactContainer.find( 'name', 'ArtifactAuditPanel' )[0];
  
  if ( data == null ) {
    panel.showArtifact( {
      groupId: '',
      artifactId: '',
      version: ''
    } );
  }
  else {
    panel.showArtifact( data );
  }
});