package org.krmdemo.techlabs.thtool.helpers;

import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * An interface to helper-components that provides badges and links
 * as HTML-elements or as 'GitHub-Markdown'-fragments.
 * The methods of this interface accept the parameter about the version - so they cannot be used as properties.
 * <hr/>
 * For pure getters an interface {@link BadgeProvider} should be used.
 */
public interface BadgeVersionProvider {

//    /**
//     * Getting the URL to badge-image according to the value of parameter {@code versionStr}.
//     * <hr/>
//     * It's mostly useful for tests to verify the static-badge of <a href="https://shields.io/">shields.io</a>
//     *
//     * @param versionStr string-representation of the project-version
//     * @return the URL to badge-image
//     */
//    String badgeImageUrl(String versionStr);

    /**
     * Getting the HTML-badge that could be inserted at <b>{@code th-tool}</b>-template,
     * according to the value of parameter {@code versionStr} in a following way:
     * {@snippet : [(${ hhh.xxx.badgeHtml(versionStr) })] },
     * where {@code hhh} is the name of parent helper-variable
     * and {@code xxx} is a property that corresponds to the type of that badge.
     *
     * @param versionStr string-representation of the project-version
     * @return the content of HTML-badge to be rendered
     */
    String badgeHtml(String versionStr);

    /**
     * The same as {@link #badgeHtml(String)}, but the value of version is taken from {@link CommitGroupMinor#versionTag()}
     *
     * @param minorGroup minor commit-group to get the project-version from
     * @return the content of HTML-badge to be rendered
     */
    default String badgeHtml(CommitGroupMinor minorGroup) {
        return badgeHtml("" + minorGroup.versionTag());
    }

    /**
     * The same as {@link #badgeHtml(String)}, but the value of version is taken from {@link CommitGroupMajor#versionTag()}
     *
     * @param majorGroup major commit-group to get the project-version from
     * @return the content of HTML-badge to be rendered
     */
    default String badgeHtml(CommitGroupMajor majorGroup) {
        return badgeHtml("" + majorGroup.versionTag());
    }

    /**
     * Getting the 'GitHub-Markdown'-badge that could be inserted at <b>{@code th-tool}</b>-template,
     * according to the value of parameter {@code versionStr} in a following way:
     * {@snippet : [(${ hhh.xxx.badgeHtml(versionStr) })] },
     * where {@code hhh} is the name of parent helper
     * and {@code xxx} is a property that corresponds to the type of that badge.
     *
     * @param versionStr string-representation of the project-version
     * @return the content of HTML-badge to be rendered
     */
    String badgeMD(String versionStr);

    /**
     * The same as {@link #badgeMD(String)}, but the value of version is taken from {@link CommitGroupMinor#versionTag()}
     *
     * @param minorGroup minor commit-group to get the project-version from
     * @return the content of HTML-badge to be rendered
     */
    default String badgeMD(CommitGroupMinor minorGroup) {
        return badgeMD("" + minorGroup.versionTag());
    }

    /**
     * The same as {@link #badgeMD(String)}, but the value of version is taken from {@link CommitGroupMajor#versionTag()}
     *
     * @param majorGroup major commit-group to get the project-version from
     * @return the content of HTML-badge to be rendered
     */
    default String badgeMD(CommitGroupMajor majorGroup) {
        return badgeMD("" + majorGroup.versionTag());
    }

    /**
     * Getting the taget URL for rendered badge that could be used to render custom links
     * that is available at <b>{@code th-tool}</b>-template via following expression:
     * {@snippet : [(${ hhh.xxx.targetUrl(versionStr) })] },
     * where {@code hhh} is the name of parent helper
     * and {@code xxx} is a property that corresponds to the type of that badge.
     *
     * @param versionStr string-representation of the project-version
     * @return a target URL of the badge according to {@code versionStr} parameter
     */
    String targetUrl(String versionStr);

    /**
     * A non-static version of <i>currying</i>factory-method {@link BadgeProvider#of(BadgeVersionProvider, String)}
     *
     * @param versionStr he first parameter of each method in returning interface {@link BadgeProvider}
     * @return the implementation of {@link BadgeProvider} over this interface with the predefined first parameter {@code versionStr}
     */
    default BadgeProvider of(String versionStr) {
        return BadgeProvider.of(this, versionStr);
    }

    /**
     * A non-static version of <i>currying</i>factory-method {@link BadgeProvider#of(BadgeVersionProvider, CommitGroupMinor)}
     *
     * @param minorGroup a minor commit-group to get the version from
     * @return the implementation of {@link BadgeProvider} over this interface with the predefined first parameter {@code minorGroup}
     */
    default BadgeProvider of(CommitGroupMinor minorGroup) {
        return BadgeProvider.of(this, minorGroup);
    }

    /**
     * A non-static version of <i>currying</i>factory-method {@link BadgeProvider#of(BadgeVersionProvider, CommitGroupMajor)}
     *
     * @param majorGroup a minor commit-group to get the version from
     * @return the implementation of {@link BadgeProvider} over this interface with the predefined first parameter {@code majorGroup}
     */
    default BadgeProvider of(CommitGroupMajor majorGroup) {
        return BadgeProvider.of(this, majorGroup);
    }

    /**
     * An implementation of interface {@link Function} with set of functions that outer-helper must provide
     *
     * @param badgeImageFunc a function that returns a URL to badge-image according to {@code versionStr} parameter
     * @param targetUrlFunc a function that returns a target URL of the badge according to {@code versionStr} parameter
     * @param altImageFunc a function that returns a value to {@code alt}-attribute to the HTML-tag {@code <img alt="..."/>}
     * @param titleImageFunc a function that returns a value to {@code title}-attribute to the HTML-tag {@code <a title="..."/>}
     */
    record FuncRec(
        Function<String, String> badgeImageFunc,
        Function<String, String> targetUrlFunc,
        Function<String, String> altImageFunc,
        Function<String, String> titleImageFunc
    ) implements BadgeVersionProvider {

        @Override
        public String badgeHtml(String versionStr) {
            String title = titleImageFunc == null ? "" : titleImageFunc.apply(versionStr);
            return String.format("""
                <a href="%s"%s>
                  <img alt="%s" src="%s" />
                </a>""",
                targetUrlFunc.apply(versionStr),
                isBlank(title) ? "" : " title=\"" + title + '"',
                altImageFunc.apply(versionStr),
                badgeImageFunc.apply(versionStr));
        }

        @Override
        public String badgeMD(String versionStr) {
            String title = titleImageFunc == null ? "" : titleImageFunc.apply(versionStr);
            return String.format("[![%s](%s)](%s%s)",
                altImageFunc.apply(versionStr),
                badgeImageFunc.apply(versionStr),
                targetUrlFunc.apply(versionStr),
                isBlank(title) ? "" : " \"" + title + '"'
            );
        }

        @Override
        public String targetUrl(String versionStr) {
            return targetUrlFunc.apply(versionStr);
        }
    }
}
