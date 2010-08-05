/*
 *  Copyright (c) 2010 Red Hat, Inc.
 *  
 *  This program is licensed to you under Version 3 only of the GNU
 *  General Public License as published by the Free Software 
 *  Foundation. This program is distributed in the hope that it will be 
 *  useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 *  PURPOSE.
 *  
 *  See the GNU General Public License Version 3 for more details.
 *  You should have received a copy of the GNU General Public License 
 *  Version 3 along with this program. 
 *  
 *  If not, see http://www.gnu.org/licenses/.
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
  
  var items = [];
        
  if ( this.halfSize == true ) {
    items.push({
      xtype: 'panel',
      layout: 'form',
      anchor: Sonatype.view.FIELD_OFFSET + ' -10',
      labelWidth: 90,
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
          fieldLabel: 'Added On',
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
        labelWidth: 90,
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
            fieldLabel: 'Added On',
            name: 'captured-on',
            anchor: Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
            allowBlank: true,
            readOnly: true
          },
        ]
    });
  }
  
  this.formPanel = new Ext.form.FormPanel(
  {
    autoScroll: true,
    border: false,
    frame: true,
    collapsible: false,
    collapsed: false,
    items: items
  } );

  Sonatype.repoServer.ArtifactAuditPanel.superclass.constructor.call( this, 
  {
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

Ext.extend( Sonatype.repoServer.ArtifactAuditPanel, Ext.Panel, 
{
  showArtifact: function( data )
  {
	if ( data.repoId != null && data.groupId != null && data.artifactId != null && data.version != null )
	{
		var serviceUrl = Sonatype.config.servicePath + '/audit/log/' 
		    + data.repoId 
		    + '/gav/' 
		    + data.groupId 
		    + '/' 
		    + data.artifactId 
		    + '/' 
		    + data.version;
		
		var query = '';
		if ( data.extension != null && data.extension != 'jar' )
		{
			query += 't=' + data.extension;
		}
		
		if ( data.classifier != null && data.classifier.length > 0 )
		{
			if ( query.length > 0 )
			{
				query += '&';
			}
			
			query += 'c=' + data.classifier;
		}
		
		if ( query.length > 0 )
		{
			query += '&';
		}
		
		// fail over to 'unknown' information.
		query += 'q=true';
		
		serviceUrl += '?' + query;
		
	    this.formPanel.getForm().doAction( 'sonatypeLoad', {
	      url: serviceUrl,
	      method: 'GET',
	      fpanel: this.formPanel
		} );
	}
  }
} );

Sonatype.Events.addListener('artifactContainerInit', function(artifactContainer) {
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
