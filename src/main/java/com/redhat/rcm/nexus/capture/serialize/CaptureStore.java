package com.redhat.rcm.nexus.capture.serialize;

import java.util.Date;
import java.util.List;

import org.sonatype.nexus.proxy.item.StorageItem;

import com.redhat.rcm.nexus.capture.model.CaptureSession;

public interface CaptureStore
{

    CaptureSession closeCurrentLog( String user, String buildTag, String captureSource )
        throws CaptureStoreException;

    void expireLogs( String user, String buildTag, String captureSource, Date olderThan )
        throws CaptureStoreException;

    List<Date> getLogs( String user, String buildTag, String captureSource )
        throws CaptureStoreException;

    CaptureSession readLog( String user, String buildTag, String captureSource, Date startDate )
        throws CaptureStoreException;

    CaptureSession readLatestLog( String user, String buildTag, String captureSource )
        throws CaptureStoreException;

    void logResolved( String user, String buildTag, String captureSource, List<String> processedRepositories,
                      String path, StorageItem item )
        throws CaptureStoreException;

    void logUnresolved( String user, String buildTag, String captureSource, List<String> processedRepositories,
                        String path )
        throws CaptureStoreException;
}
