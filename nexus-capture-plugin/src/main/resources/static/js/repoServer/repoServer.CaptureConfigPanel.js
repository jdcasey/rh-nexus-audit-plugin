function printallmembers(obj) {
    var str = '';
    for (var memb in obj)
    str += memb + ' = ' + obj[memb] + '\n';

    return str;
}

Sonatype.repoServer.CaptureConfigPanel = function(config) {
    var config = config || {};
    var defaultConfig = {
        title: 'Capture Configuration'
    };

    Ext.apply(this, config, defaultConfig);

    this.servicePath = {
        captureConfig: Sonatype.config.servicePath + '/capture/admin/config',
    };

    this.referenceData = {
		captureConfig: {
    		captureSourceRepoId: ''
    	}
    };

    //Reader and datastore that queries the server for the list of repository groups
    this.repositoryGroupReader = new Ext.data.JsonReader(
		{root: 'data', id: 'id'},
	    [
	     {name: 'id'},
	     {name: 'name', sortType: Ext.data.SortTypes.asUCString},
	    ]
	);
    
    this.repositoryGroupDataStore = new Ext.data.Store({
        url: Sonatype.config.repos.urls.groups,
        reader: this.repositoryGroupReader,
        sortInfo: {
            field: 'name',
            direction: 'ASC'
        },
        autoLoad: true
    });
    
    this.formConfig = {
        region: 'center',
        trackResetOnLoad: true,
        autoScroll: true,
        border: false,
        frame: true,
        collapsible: false,
        collapsed: false,
        labelWidth: 200,
        layoutConfig: {
            labelSeparator: ''
        },

        items: [
        {
            xtype: 'combo',
            tpl: '<tpl for="."><div ext:qtip="[{id}] - {Sonatype.config.host}{Sonatype.config.content.groups}/{id}" class="x-combo-list-item">{name}</div></tpl>',
            fieldLabel: 'Capture Source (Repository Group)',
            itemCls: 'required-field',
            helpText: 'Select a repository group from which to resolve external (captured) artifacts.',
            name: 'captureSourceRepoId',
            width: 200,
            store: this.repositoryGroupDataStore,
            valueField: 'id',
            displayField: 'name',
            editable: false,
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            emptyText: 'Select...',
            selectOnFocus: true,
            allowBlank: false,
            listeners: {
//	        	load: {
//        	        fn: function( combo, record, index ) {
//        	            if ( this.referenceData.captureConfig.captureSourceRepoId.length > 0 ) {
//            	            combo.selectByValue( this.referenceData.captureConfig.captureSourceRepoId, true );
//        	            }
//        	            
//        	            alert( "Selecting: " + this.referenceData.captureConfig.captureSourceRepoId );
//        			},
//        			scope: this
//	        	},
	            select: {
	                fn: function(combo, record, index) {
	        	    	this.referenceData.captureConfig.captureSourceRepoId = record.data.id;
                    },
	                scope: this
	            }
            }       
        }],
        
        buttons: [
        {
            text: 'Save',
            handler: this.saveButtonHandler,
            disabled: true,
            scope: this
        },
        {
            text: 'Cancel',
            handler: this.cancelButtonHandler,
            scope: this
        }]
    }
    
    var config = Ext.apply({}, this.formConfig);
    this.formPanel = new Ext.FormPanel(config);

    Sonatype.repoServer.CaptureConfigPanel.superclass.constructor.call(this, {
        autoScroll: false,
        layout: 'border',
        items: [
        this.formPanel
        ]
    });

    this.formPanel.on('beforerender', this.beforeRenderHandler, this.formPanel);
    this.formPanel.on( 'afterlayout', this.afterLayoutHandler, this, { single: true } );
    this.formPanel.form.on( 'actioncomplete', this.actionCompleteHandler, this );
    this.formPanel.form.on( 'actionfailed', this.actionFailedHandler, this.formPanel );
};


Ext.extend(Sonatype.repoServer.CaptureConfigPanel, Ext.Panel, {
	
    beforeRenderHandler: function(formInfoObj) {
        var sp = Sonatype.lib.Permissions;
        if (sp.checkPermission('nexus:settings', sp.EDIT)) {
            this.buttons[0].disabled = false;
        }
    },
    
    actionCompleteHandler : function( form, action ) {
        if ( action.type == 'sonatypeLoad' ) {
          
//          var csComponent = this.find( 'name', 'captureSourceRepoId' )[0];
//          
//          if ( !Ext.isEmpty( csComponent.getValue() ) ) {
//        	  this.referenceData.captureConfig.captureSourceRepoId = csComponent.getValue();
//          }
        }
      },
      
      actionFailedHandler : function( form, action ) {
	    if ( action.failureType == null ) {
	      Sonatype.utils.connectionError( action.response, null, null, action.options );
	    }
	    else if(action.failureType == Ext.form.Action.CLIENT_INVALID){
	      Sonatype.MessageBox.alert('Missing or Invalid Fields', 'Please change the missing or invalid fields.').setIcon(Sonatype.MessageBox.WARNING);
	    }
	    else if(action.failureType == Ext.form.Action.CONNECT_FAILURE){
	      Sonatype.utils.connectionError( action.response, 'There is an error communicating with the server.' )
	    }
	    else if(action.failureType == Ext.form.Action.LOAD_FAILURE){
	      Sonatype.MessageBox.alert('Load Failure', 'The data failed to load from the server.').setIcon(Sonatype.MessageBox.ERROR);
	    }
	  },
	  
    saveButtonHandler : function() {
        var form = this.formPanel.form;

        if ( ! form.isValid() ) return;
        
        form.doAction('sonatypeSubmit', {
          method: 'PUT',
          url: this.servicePath.captureConfig,
          waitMsg: 'Updating Capture configuration...',
          fpanel: this.formPanel,
          serviceDataObj: this.referenceData.captureConfig
        });
      },
      
      cancelButtonHandler : function() {
        Sonatype.view.mainTabPanel.remove( this.id, true );
      },
      
      afterLayoutHandler : function(){
	  
	    // invoke form data load
	    this.formPanel.getForm().doAction( 'sonatypeLoad', {
	      url: this.servicePath.captureConfig,
	      method: 'GET',
	      fpanel: this.formPanel
	    } );
	  },
});

Sonatype.Events.addListener('nexusNavigationInit',
function(nexusPanel) {
    nexusPanel.add({
        enabled: Sonatype.lib.Permissions.checkPermission(
        'nexus:capture-admin', Sonatype.lib.Permissions.READ),
        sectionId: 'st-nexus-config',
        title: 'Capture Configuration',
        tabId: 'capture-configuration',
        tabCode: Sonatype.repoServer.CaptureConfigPanel
    });
});
