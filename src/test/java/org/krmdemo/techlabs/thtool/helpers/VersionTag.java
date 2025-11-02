package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Java-record is used to parse the <b>{@code git}</b>-tag that represents the version of maven-project,
 * which must look like {@code <major>.<minor>.<incremental>} (without qualifier !!!).
 * <hr/>
 * <u><i>Note:</i></u> <i>SNAPSHOT</i>-versions must not have tags - so, such versions of maven-project
 * (like {@code 21.09.002-SNAPSHOT}) are considered invalid as version-tags.
 *
 * @param major major part
 * @param minor minor part
 * @param incremental incremental part
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VersionTag(
    String major,
    String minor,
    String incremental
) {
    /**
     * @return {@code true} if this instance corresponds to PUBLIC-release version of a project (and otherwise - {@code false})
     */
    public boolean isPublicRelease() {
        return isValid() && StringUtils.isBlank(incremental);
    }

    /**
     * @return {@code true} if this instance corresponds to valid version-tag (and otherwise - {@code false})
     */
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
        return sb.toString();
    }

    private final static Pattern PATTERN__TAG_NAME = Pattern.compile(
        "^(?:refs/tags/)?(?<major>[^.]+)\\.(?<minor>[^.]+)\\.?(?<incremental>[^-]*)$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Parsing the {@link String}-representation of <b>{@code git}</b>-tag
     * and returning either the instance of {@link VersionTag}
     * or {@code null} if the passed tagName does not correspond to {@link #isValid() valid} version-tag.
     *
     * @param tagName {@link String}-representation of <b>{@code git}</b>-tag
     * @return the instance of class {@link VersionTag} that correspond to parsed {@code tagName}
     */
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
        if (StringUtils.isBlank(major) || StringUtils.isBlank(minor)) {
            return null;
        } else {
            return new VersionTag(major, minor, incremental);
        }
    }
}
