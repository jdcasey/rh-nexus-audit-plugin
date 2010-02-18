package com.redhat.rcm.nexus.capture.store;

import java.util.List;

import org.sonatype.nexus.proxy.item.StorageItem;

import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureSessionQuery;
import com.redhat.rcm.nexus.capture.model.CaptureSessionRef;

public interface CaptureStore
{

    CaptureSession closeCurrentLog( String user, String buildTag, String captureSource )
        throws CaptureStoreException;

    void deleteLogs( CaptureSessionQuery query )
        throws CaptureStoreException;

    List<CaptureSessionRef> getLogs( CaptureSessionQuery query )
        throws CaptureStoreException;

    CaptureSession readLog( CaptureSessionRef ref )
        throws CaptureStoreException;

    CaptureSession readLatestLog( String user, String buildTag )
        throws CaptureStoreException;

    void logResolved( String user, String buildTag, String captureSource, List<String> processedRepositories,
                      String path, StorageItem item )
        throws CaptureStoreException;

    void logUnresolved( String user, String buildTag, String captureSource, List<String> processedRepositories,
                        String path )
        throws CaptureStoreException;
}
