package com.redhat.rcm.nexus.capture.ui;

import javax.inject.Named;

import org.sonatype.nexus.proxy.item.StorageFileItem;

@Named( "XY" )
public class XYVirusScanner
    implements VirusScanner
{
    public boolean hasVirus( StorageFileItem file )
    {
        // DO THE JOB HERE
        System.out.println( "Kung fu VirusScanner --- scanning for viruses on item: " + file.getPath() );

        // simulating virus hit by having the filename contain the "infected" string
        return file.getName().contains( "infected" );
    }

}
