package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An interface to helper-components that provides badges and links
 * as HTML-elements or as 'GitHub-Markdown'-fragments.
 * Unlike the interface {@link BadgeVersionProvider} - all getters of this interface could be used as properties.
 * <hr/>
 * It's recommended to use factory-methods {@link #of(BadgeVersionProvider, String)},
 * {@link #of(BadgeVersionProvider, CommitGroupMinor)} and {@link #of(BadgeVersionProvider, CommitGroupMajor)}
 * that peforms <i>currying</i> over the instance of {@link BadgeVersionProvider}.
 *
 * @see <a href="https://www.baeldung.com/scala/currying">
 *     (Baeldung) Currying in Scala
 * </a>
 * @see <a href="https://www.scalamatters.io/post/partially-applied-functions-and-currying">
 *     (Scala Matters) Partially Applied Functions And Currying
 * </a>
 */
@JsonPropertyOrder(alphabetic = true)
public interface BadgeProvider {

    /**
     * @return the comments for debug/test purposes (to see that in JSON/YAML dumps and at "Test-Site")
     */
    @JsonProperty("#comments")
    default String comments() {
        return "badge-provider";
    }

    /**
     * HTML-badge that could be inserted at <b>{@code th-tool}</b>-template in a following way:
     * {@snippet : [(${ hhh.xxx.badgeHtml })] },
     * where {@code hhh} is the name of parent helper-variable
     * and {@code xxx} is a property that corresponds to the type of that badge.
     *
     * @return the content of HTML-badge to be rendered
     */
    @JsonIgnore
    String getBadgeHtml();

    /**
     * 'GitHub-Markdown'-badge that could be inserted at <b>{@code th-tool}</b>-template in a following way:
     * {@snippet : [(${ hhh.xxx.badgeHtml })] },
     * where {@code hhh} is the name of parent helper
     * and {@code xxx} is a property that corresponds to the type of that badge.
     *
     * @return the content of HTML-badge to be rendered
     */
    String getBadgeMD();

    /**
     * A taget URL for rendered badge that could be used to render custom links
     * that is available at <b>{@code th-tool}</b>-template via following expression:
     * {@snippet : [(${ hhh.xxx.targetUrl })] },
     * where {@code hhh} is the name of parent helper
     * and {@code xxx} is a property that corresponds to the type of that badge.
     *
     * @return a target URL of the badge according to {@code versionStr} parameter
     */
    String getTargetUrl();

    /**
     * This fake-provider is used as a placeholder for cases when the badge is unavailable
     */
    BadgeProvider EMPTY = new BadgeProvider() {
        @Override
        public String getBadgeHtml() {
            return "";
        }
        @Override
        public String getBadgeMD() {
            return "";
        }
        @Override
        public String getTargetUrl() {
            return "!!! no target URL !!!";
        }
        @JsonValue
        @Override
        public String comments() {
            return "EMPTY badge-provider";
        }
    };

    /**
     * The <i>currying</i> wrapper over {@link BadgeVersionProvider},
     * where the first parameter of each method is {@code versionStr}.
     * <hr/>
     * This technique is called <i>currying</i> in such languages as Scala.
     *
     * @param badgeVersionProvider underlying instance of {@link BadgeVersionProvider}
     * @param versionStr he first parameter of each method in returning interface {@link BadgeProvider}
     * @return the implementation of {@link BadgeProvider} over the passed {@code badgeVersionProvider} and {@code versionStr}
     */
    static BadgeProvider of(BadgeVersionProvider badgeVersionProvider, String versionStr) {
        return new BadgeProvider() {
            @Override
            public String getBadgeHtml() {
                return badgeVersionProvider.badgeHtml(versionStr);
            }
            @Override
            public String getBadgeMD() {
                return badgeVersionProvider.badgeMD(versionStr);
            }
            @Override
            public String getTargetUrl() {
                return badgeVersionProvider.targetUrl(versionStr);
            }
        };
    }

    /**
     * The same as {@link BadgeProvider#of(BadgeVersionProvider, String)},
     * but {@link CommitGroupMinor} parameter is used to get the value of version.
     * <hr/>
     * This technique is called <i>currying</i> in such languages as Scala.
     *
     * @param badgeVersionProvider underlying instance of {@link BadgeVersionProvider}
     * @param minorGroup a minor commit-group to get the version from
     * @return the implementation of {@link BadgeProvider} over the passed {@code funcRec} and {@code minorGroup}
     */
    static BadgeProvider of(BadgeVersionProvider badgeVersionProvider, CommitGroupMinor minorGroup) {
        return of(badgeVersionProvider, "" + minorGroup.versionTag());
    }

    /**
     * The same as {@link BadgeProvider#of(BadgeVersionProvider, String)},
     * but {@link CommitGroupMajor} parameter is used to get the value of version.
     * <hr/>
     * This technique is called <i>currying</i> in such languages as Scala.
     *
     * @param badgeVersionProvider underlying instance of {@link BadgeVersionProvider}
     * @param majorGroup a major commit-group to get the version from
     * @return the implementation of {@link BadgeProvider} over the passed {@code funcRec} and {@code versionStr}
     */
    static BadgeProvider of(BadgeVersionProvider badgeVersionProvider, CommitGroupMajor majorGroup) {
        return of(badgeVersionProvider, "" + majorGroup.versionTag());
    }
}
