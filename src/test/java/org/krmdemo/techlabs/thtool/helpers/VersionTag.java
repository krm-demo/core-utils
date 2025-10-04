package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Java-record is used to parse the git-tag that represents the version of maven-project,
 * which must look like {@code <major>.<minor>.<incremental>-<qualifier>}.
 *
 * @param major major part
 * @param minor minor part
 * @param incremental incremental part
 * @param qualifier qualifier part
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VersionTag(
    String major,
    String minor,
    String incremental,
    String qualifier // <-- TODO: BULL SHIT !!! The version-tag must not have qualifier
) {
    public boolean isPublicRelease() {
        return isValid() &&
            StringUtils.isBlank(incremental) &&
            StringUtils.isBlank(qualifier);
    }

    public boolean isInternalRelease() {
        return isValid() && StringUtils.isBlank(qualifier);
    }

    public boolean isSnapshot() {
        return isValid() &&
            StringUtils.isNotBlank(incremental) &&
            StringUtils.isNotBlank(qualifier);
    }

    public boolean isValid() {
        try {
            if (Integer.parseInt(major) < 0) {
                return false;
            }
            if (Integer.parseInt(minor) < 0) {
                return false;
            }
            if (StringUtils.isNotBlank(incremental) && Integer.parseInt(incremental) < 0) {
                return false;
            }
            if (StringUtils.isNotBlank(qualifier) && !QUALIFIER_SNAPSHOT.equals(qualifier)) {
                return false;
            }
        } catch (NumberFormatException nfEx) {
            // ignore the details - just return 'false'
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major);
        sb.append('.');
        sb.append(minor);
        if (StringUtils.isNotBlank(incremental)) {
            sb.append('.');
            sb.append(incremental);
        }
        if (StringUtils.isNotBlank(qualifier)) {
            sb.append('-');
            sb.append(qualifier);
        }
        return sb.toString();
    }

    private final static String QUALIFIER_SNAPSHOT = "SNAPSHOT";

    private final static Pattern PATTERN__TAG_NAME = Pattern.compile(
        "^(?:refs/tags/)?(?<major>[^.]*)\\.?(?<minor>[^.]*)?\\.?(?<incremental>[^-]*)?-?(?<qualifier>.*)$",
        Pattern.CASE_INSENSITIVE
    );

    public static VersionTag parse(String tagName) {
        if (StringUtils.isBlank(tagName)) {
            return null;
        }
        Matcher matcher = PATTERN__TAG_NAME.matcher(tagName);
        if (!matcher.matches()) {
            return null;
        }
        String major = matcher.group("major");
        String minor = matcher.group("minor");
        String incremental = matcher.group("incremental");
        String qualifier = matcher.group("qualifier");
        if (StringUtils.isBlank(major) || StringUtils.isBlank(minor)) {
            return null;
        } else {
            return new VersionTag(major, minor, incremental, qualifier);
        }
    }
}
