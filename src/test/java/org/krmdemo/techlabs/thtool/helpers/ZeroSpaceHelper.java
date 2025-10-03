package org.krmdemo.techlabs.thtool.helpers;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to obfuscate the strings with zero-spaces.
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code zsh}.
 * <hr/>
 * The methods of this helper-class allows to inject {@code &ZeroWidthSpace;} or {@code &#8203;}
 * or {@code &#x200B;} in the middle of passed sensitive string. In most cases it's enough
 * the result string to be allowed to display or even to commit.
 * <hr/>
 * In order to partially violate the security rule, which prohibits displaying at console
 * or anywhere at screen the whole value of some sensitive data (like passwords, security tokens, ...),
 * corresponding string need to be obfuscated a little bit (but remains readable though).
 * It's not recommended practice, but it could very be helpful either for debug-needs
 * or when you are working on some kind of <i>Forget Password</i> functionality.
 */
public class ZeroSpaceHelper {

    final static String ZERO_SPACE_HTML_ENTITY__LITERAL = "&ZeroWidthSpace;";
    final static String ZERO_SPACE_HTML_ENTITY__DECIMAL = "&#8203;";
    final static String ZERO_SPACE_HTML_ENTITY__HEX = "&#x200B;";

    final String zeroSpaceStr;

    public ZeroSpaceHelper() {
        this(ZERO_SPACE_HTML_ENTITY__DECIMAL);
    }

    private ZeroSpaceHelper(String zeroSpaceStr) {
        this.zeroSpaceStr = zeroSpaceStr;
    }

    ZeroSpaceHelper useLiteral() {
        return new ZeroSpaceHelper(ZERO_SPACE_HTML_ENTITY__LITERAL);
    }

    ZeroSpaceHelper useHex() {
        return new ZeroSpaceHelper(ZERO_SPACE_HTML_ENTITY__HEX);
    }

    /**
     * Insert one Zero-Space input passed {@code inputString} at position {@code pos}.
     *
     * @param inputString the string to insert zero-space into
     * @param pos the position to insert from the beginning if positive or from the end if negative
     * @return obfuscated string (or unchanged if {@code inputString} is blank or {@code pos} is out of range)
     */
    public CharSequence insertInto(CharSequence inputString, int pos) {
        if (StringUtils.isBlank(inputString)) {
            return inputString;
        }
        final int len = inputString.length();
        if (pos < 0) {
            pos = len + pos;
        }
        if (pos <= 0 || pos >= len) {
            return inputString;
        }
        return inputString.subSequence(0, pos) + zeroSpaceStr + inputString.subSequence(pos, len);
    }

    /**
     * Apply {@link #insertInto(CharSequence, int)} two times from beginning and from the end
     *
     * @param inputString the string to insert two zero-spaces into
     * @param pos the position to insert from the beginning and from the end of {@code inputString}
     * @return obfuscated string (or unchanged if {@code inputString} is blank or {@code pos} is out of range)
     */
    public CharSequence insertPair(CharSequence inputString, int pos) {
        if (pos < 0) {
            pos = -pos;
        }
        int len = StringUtils.isBlank(inputString) ? 0 : inputString.length();
        CharSequence resultString = insertInto(inputString, pos);
        if (len - pos > pos) {
            resultString = insertInto(resultString, -pos);
        }
        return resultString;
    }

    /**
     * Apply {@link #insertPair(CharSequence, int)} with position {@code 4}
     *
     * @param inputString the string to insert two zero-spaces into
     * @return obfuscated string (or unchanged if {@code inputString} is blank or its length is less than {@code 5})
     */
    public CharSequence mask4(CharSequence inputString) {
        return insertPair(inputString, 4);
    }
}
