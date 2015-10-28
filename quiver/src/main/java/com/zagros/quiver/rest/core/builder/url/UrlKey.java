
package com.zagros.quiver.rest.core.builder.url;

/**
 * Key for url entry.
 */
public class UrlKey {
    /*
     * Key value.
     */
    private String mValue;

    /*
     * Prefix that will be prepended before this entry (before key)
     */
    private String mPrefix = "";

    /*
     * Suffix that will be appended at the end of the entry after the value
     */
    private String mSuffix = "";

    /*
     * If this is true, then this key is used in query, otherwise it has only
     * informational purpose, and only value gets used. Keep in mind that suffix
     * and prefix is also used.
     */
    private boolean mIsUsedInUrl;

    /*
     * In case of null entry value, tells builder weather or not this argument
     * can be ignored. If the field is mandatory(mIsOptional = false), then
     * exception should be raised. Default is false, all fields are assumed
     * mandatory if not explicitly set as optional.
     */
    private boolean mIsOptional;

    /*
     * In case of null entry value, tells builder weather or not this argument
     * can be ignored. If the field is mandatory(mIsOptional = false), then
     * exception should be raised. Default is false, all fields are assumed
     * mandatory if not explicitly set as optional.
     */
    private boolean mIsUsedActionUrl;

    public UrlKey(String value) {
        mValue = value;
        mIsUsedInUrl = true;
        mIsOptional = false;
        mIsUsedActionUrl = false;
    }

    public UrlKey(String value, String prefix, String suffix, boolean isUsedInUrl,
                  boolean isOptional) {
        mValue = value;
        mPrefix = prefix;
        mSuffix = suffix;
        mIsUsedInUrl = isUsedInUrl;
        mIsOptional = isOptional;
        mIsUsedActionUrl = false;
    }

    public UrlKey(String value, String prefix, String suffix, boolean isUsedInUrl,
                  boolean isOptional, boolean isUsedActionUrl) {
        mValue = value;
        mPrefix = prefix;
        mSuffix = suffix;
        mIsUsedInUrl = isUsedInUrl;
        mIsOptional = isOptional;
        mIsUsedActionUrl = isUsedActionUrl;
    }

    public UrlKey usedInUrl(boolean isUsed) {
        mIsUsedInUrl = isUsed;
        return this;
    }

    public UrlKey optional(boolean isOptional) {
        mIsOptional = isOptional;
        return this;
    }

    public UrlKey suffix(String suffix) {
        mSuffix = suffix;
        return this;
    }

    public UrlKey prefix(String prefix) {
        mPrefix = prefix;
        return this;
    }

    public boolean isUsedInUrl() {
        return mIsUsedInUrl;
    }

    public boolean isOptional() {
        return mIsOptional;
    }

    public UrlKey setValue(String value) {
        mValue = value;
        return this;
    }

    public String getValue() {
        return mValue;
    }

    public UrlKey setPrefix(String prefix) {
        mPrefix = prefix;
        return this;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public UrlKey setSuffix(String suffix) {
        mSuffix = suffix;
        return this;
    }

    public String getSuffix() {
        return mSuffix;
    }

    public boolean isUsedActionUrl() {
        return mIsUsedActionUrl;
    }

    public UrlKey setIsUsedActionUrl(boolean aIsUsedActionUrl) {
        this.mIsUsedActionUrl = aIsUsedActionUrl;
        return this;
    }
}
