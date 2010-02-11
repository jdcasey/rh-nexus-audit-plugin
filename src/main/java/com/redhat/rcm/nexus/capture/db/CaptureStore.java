package com.redhat.rcm.nexus.capture.db;

import java.util.List;

public interface CaptureStore
{
    void record( final String user, final String buildTag, final String captureSource,
                 final List<String> processedRepositories, final String path, final boolean resolved )
        throws CaptureStoreException;
}
