package com.redhat.rcm.nexus.protocol;

public class ProtocolConstants
{

    public static final String CATALOG_FILENAME = "catalog.json";

    public static final String CATALOG_ROOT = "catalog";

    public static final String SESSION_ROOT = "session";

    public static final String SESSION_REF_ROOT = "session-ref";

    public static final String TARGET_ROOT = "target";

    public static final String CAPTURE_SOURCE_FIELD = "external-source";

    public static final String BUILD_TAG_FIELD = "build-tag";

    public static final String START_DATE_FIELD = "started-on";

    public static final String LAST_UPDATE_FIELD = "last-updated";

    public static final String RESOLVED_ON_FIELD = "resolved-on";

    public static final String CHECKED_REPOS_FIELD = "checked-repositories";

    public static final String RESOLVED_REPO_FIELD = "resolved-from";

    public static final String DATE_FIELD = "date";

    public static final String RESOURCE_URI_FIELD = "resourceuri";

    private static final String SERVICE_BASEURI = "/service/local";

    private static final String CAPTURE_SVC_BASEURI = SERVICE_BASEURI + "/capture";

    public static final String REPOSITORY_CONTENT_URLPART = "/content";

    public static final String LOG_RESOURCE_BASEURI = CAPTURE_SVC_BASEURI + "/log";

    public static final String REPOSITORY_RESOURCE_BASEURI = SERVICE_BASEURI + "/repositories";

    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd+HH-mm-ssZ";
}
