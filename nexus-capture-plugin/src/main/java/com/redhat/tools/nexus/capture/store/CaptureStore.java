package com.redhat.tools.nexus.capture.store;

import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.plugin.Managed;

import com.redhat.tools.nexus.capture.model.CaptureSession;
import com.redhat.tools.nexus.capture.model.CaptureSessionRef;

import javax.inject.Singleton;

import java.util.List;

@Managed
@Singleton
public interface CaptureStore
{

    CaptureSessionRef closeCurrentLog( String user, String buildTag )
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
