/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author dell
 */
public class TextUtils {

    /**
     * Utility Function to process Escape Sequence elements in any String (or,
     * Text).
     *
     * @param str_obj The String to be processed.
     * @return Processed String.
     * @throws IllegalArgumentException if an error occurs during processing
     * escape sequence.
     */
    public static final String translateEscapes(Object str_obj) throws IllegalArgumentException {
        String str = str_obj.toString();
        if (str.isEmpty()) {
            return "";
        }
        char[] chars = str.toCharArray();
        int length = chars.length;
        int from = 0;
        int to = 0;
        while (from < length) {
            char ch = chars[from++];
            if (ch == '\\') {
                ch = from < length ? chars[from++] : '\0';
                switch (ch) {
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case 'e':
                        ch = 27;
                        break;
                    case '\'':
                    case '\"':
                    case '\\':
                        // as is
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                        int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);
                        int code = ch - '0';
                        while (from < limit) {
                            ch = chars[from];
                            if (ch < '0' || '7' < ch) {
                                break;
                            }
                            from++;
                            code = (code << 3) | (ch - '0');
                        }
                        ch = (char) code;
                        break;
                    case 'u':
                        String token = "";
                        int max = Integer.min(from + 4, length);
                        while (from < max) {
                            ch = chars[from];
                            if ("0123456789abcdefABCDEF".indexOf(ch) == -1) {
                                break;
                            }
                            from++;
                            token += ch;
                        }
                        if (token.length() != 4) {
                            throw new IllegalArgumentException(String.format(
                                "Invalid escape sequence: \\u%s", token));
                        }
                        ch = (char) Integer.parseInt(token, 16);
                        break;
                    default:
                        throw new IllegalArgumentException(String.format(
                                "Invalid escape sequence: \\%c \\\\u%04X",
                                ch, (int) ch));
                }
            }

            chars[to++] = ch;
        }

        return new String(chars, 0, to);
    }
}
