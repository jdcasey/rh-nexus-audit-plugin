package com.redhat.tools.nexus.capture;

public final class CaptureResourceConstants
{

    private CaptureResourceConstants()
    {
    }

    private static final String ID_CAPTURE_SOURCE = "capture-source";

    private static final String ID_BUILD_TAG = "build-tag";

    private static final String ID_USER = "user";

    public static final String PARAM_MODE = "mode";

    public static final String PARAM_FORMAT = "format";

    public static final String PARAM_BEFORE = "before";

    public static final String PARAM_SINCE = "since";

    public static final String PARAM_USER = ID_USER;

    public static final String PARAM_CAPTURE_SOURCE = ID_CAPTURE_SOURCE;

    public static final String PARAM_BUILD_TAG = ID_BUILD_TAG;

    public static final String PARAM_TEMPLATE = "template";

    public static final String PARAM_STRICT = "strict";

    public static final String ATTR_CAPTURE_SOURCE_REPO_ID = ID_CAPTURE_SOURCE;

    public static final String ATTR_BUILD_TAG_REPO_ID = ID_BUILD_TAG;

    public static final String ATTR_USER = ID_USER;

    public static final String ATTR_DATE = "date";

    public static final String PRIV_ACCESS = "nexus:capture-access";

    public static final String PRIV_EXTERNAL_RESOLVE = "nexus:capture-external-resolve";

    public static final String PRIV_LOG_ACCESS = "nexus:capture-log";

    public static final String PRIV_ADMIN = "nexus:capture-admin";

    public static final String PRIV_SETTINGS = "nexus:settings";

    public static final String PERM_EXTERNAL_GET = PRIV_EXTERNAL_RESOLVE + ":read";

}
