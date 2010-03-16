package com.redhat.tools.nexus.protocol;

public class ProtocolConstants
{

    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd+HH-mm-ssZ";

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

    public static final String REMOTE_URL_FIELD = "remote-url";

    private static final String SERVICE_BASEURI = "/service/local";

    private static final String CAPTURE_RESOURCE_BASE_FRAGMENT = "/capture";

    public static final String REPOSITORY_CONTENT_URLPART = "/content";

    public static final String ADMIN_CONFIG_RESOURCE_FRAGMENT = CAPTURE_RESOURCE_BASE_FRAGMENT + "/admin/config";

    public static final String ADMIN_LOGS_RESOURCE_FRAGMENT = CAPTURE_RESOURCE_BASE_FRAGMENT + "/admin/logs";

    public static final String MY_LOGS_RESOURCE_FRAGMENT = CAPTURE_RESOURCE_BASE_FRAGMENT + "/my/logs";

    public static final String LOG_RESOURCE_FRAGMENT = CAPTURE_RESOURCE_BASE_FRAGMENT + "/log";

    public static final String RESOLVE_RESOURCE_FRAGMENT = CAPTURE_RESOURCE_BASE_FRAGMENT + "/resolve";

    public static final String RESOLVE_RESOURCE_BASEURI = SERVICE_BASEURI + RESOLVE_RESOURCE_FRAGMENT;

    public static final String LOG_RESOURCE_BASEURI = SERVICE_BASEURI + LOG_RESOURCE_FRAGMENT;

    public static final String ADMIN_CONFIG_RESOURCE_BASEURI = SERVICE_BASEURI + ADMIN_CONFIG_RESOURCE_FRAGMENT;

    public static final String ADMIN_LOGS_RESOURCE_BASEURI = SERVICE_BASEURI + ADMIN_LOGS_RESOURCE_FRAGMENT;

    public static final String MY_LOGS_RESOURCE_BASEURI = SERVICE_BASEURI + MY_LOGS_RESOURCE_FRAGMENT;

    public static final String REPOSITORY_RESOURCE_BASEURI = SERVICE_BASEURI + "/repositories";

}
