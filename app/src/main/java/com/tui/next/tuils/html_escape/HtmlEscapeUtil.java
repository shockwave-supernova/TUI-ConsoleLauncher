package com.tui.next.tuils.html_escape;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

final class HtmlEscapeUtil {

    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3_UPPER = 'X';
    private static final char REFERENCE_HEXA_PREFIX3_LOWER = 'x';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static final char REFERENCE_SUFFIX = ';';

    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private HtmlEscapeUtil() {
        super();
    }

    static String escape(final String text, final HtmlEscapeType escapeType, final HtmlEscapeLevel escapeLevel) {

        if (text == null) {
            return null;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useHtml5 = escapeType.getUseHtml5();
        final boolean useNCRs = escapeType.getUseNCRs();
        final boolean useHexa = escapeType.getUseHexa();

        final HtmlEscapeSymbols symbols =
                (useHtml5? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS);

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            if (c <= HtmlEscapeSymbols.MAX_ASCII_CHAR && level < symbols.ESCAPE_LEVELS[c]) {
                continue;
            }

            if (c > HtmlEscapeSymbols.MAX_ASCII_CHAR
                    && level < symbols.ESCAPE_LEVELS[HtmlEscapeSymbols.MAX_ASCII_CHAR + 1]) {
                continue;
            }

            final int codepoint = Character.codePointAt(text, i);

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 20);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            if (Character.charCount(codepoint) > 1) {

                i++;
            }

            readOffset = i + 1;

            if (useNCRs) {

                if (codepoint < symbols.NCRS_BY_CODEPOINT_LEN) {

                    final short ncrIndex = symbols.NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex != symbols.NO_NCR) {

                        strBuilder.append(symbols.SORTED_NCRS[ncrIndex]);
                        continue;
                    }

                } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null) {

                    final Short ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint));
                    if (ncrIndex != null) {
                        strBuilder.append(symbols.SORTED_NCRS[ncrIndex.shortValue()]);
                        continue;
                    }

                }

            }

            if (useHexa) {
                strBuilder.append(REFERENCE_HEXA_PREFIX);
                strBuilder.append(Integer.toHexString(codepoint));
            } else {
                strBuilder.append(REFERENCE_DECIMAL_PREFIX);
                strBuilder.append(String.valueOf(codepoint));
            }
            strBuilder.append(REFERENCE_SUFFIX);

        }

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }

    static void escape(
            final Reader reader, final Writer writer, final HtmlEscapeType escapeType, final HtmlEscapeLevel escapeLevel)
            throws IOException {

        if (reader == null) {
            return;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useHtml5 = escapeType.getUseHtml5();
        final boolean useNCRs = escapeType.getUseNCRs();
        final boolean useHexa = escapeType.getUseHexa();

        final HtmlEscapeSymbols symbols =
                (useHtml5? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS);

        int c1, c2;

        c2 = reader.read();

        while (c2 >= 0) {

            c1 = c2;
            c2 = reader.read();

            if (c1 <= HtmlEscapeSymbols.MAX_ASCII_CHAR && level < symbols.ESCAPE_LEVELS[c1]) {
                writer.write(c1);
                continue;
            }

            if (c1 > HtmlEscapeSymbols.MAX_ASCII_CHAR
                    && level < symbols.ESCAPE_LEVELS[HtmlEscapeSymbols.MAX_ASCII_CHAR + 1]) {
                writer.write(c1);
                continue;
            }

            final int codepoint = codePointAt((char)c1, (char)c2);

            if (Character.charCount(codepoint) > 1) {

                c1 = c2;
                c2 = reader.read();
            }

            if (useNCRs) {

                if (codepoint < symbols.NCRS_BY_CODEPOINT_LEN) {

                    final short ncrIndex = symbols.NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex != symbols.NO_NCR) {

                        writer.write(symbols.SORTED_NCRS[ncrIndex]);
                        continue;
                    }

                } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null) {

                    final Short ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint));
                    if (ncrIndex != null) {
                        writer.write(symbols.SORTED_NCRS[ncrIndex.shortValue()]);
                        continue;
                    }

                }

            }

            if (useHexa) {
                writer.write(REFERENCE_HEXA_PREFIX);
                writer.write(Integer.toHexString(codepoint));
            } else {
                writer.write(REFERENCE_DECIMAL_PREFIX);
                writer.write(String.valueOf(codepoint));
            }
            writer.write(REFERENCE_SUFFIX);

        }

    }

    static void escape(final char[] text, final int offset, final int len, final Writer writer,
                       final HtmlEscapeType escapeType, final HtmlEscapeLevel escapeLevel)
                       throws IOException {

        if (text == null || text.length == 0) {
            return;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useHtml5 = escapeType.getUseHtml5();
        final boolean useNCRs = escapeType.getUseNCRs();
        final boolean useHexa = escapeType.getUseHexa();

        final HtmlEscapeSymbols symbols =
                (useHtml5? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS);

        final int max = (offset + len);

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            if (c <= symbols.MAX_ASCII_CHAR && level < symbols.ESCAPE_LEVELS[c]) {
                continue;
            }

            if (c > symbols.MAX_ASCII_CHAR && level < symbols.ESCAPE_LEVELS[symbols.MAX_ASCII_CHAR + 1]) {
                continue;
            }

            final int codepoint = Character.codePointAt(text, i);

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            if (Character.charCount(codepoint) > 1) {

                i++;
            }

            readOffset = i + 1;

            if (useNCRs) {

                if (codepoint < symbols.NCRS_BY_CODEPOINT_LEN) {

                    final short ncrIndex = symbols.NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex != symbols.NO_NCR) {

                        writer.write(symbols.SORTED_NCRS[ncrIndex]);
                        continue;
                    }

                } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null) {

                    final Short ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint));
                    if (ncrIndex != null) {
                        writer.write(symbols.SORTED_NCRS[ncrIndex.shortValue()]);
                        continue;
                    }

                }

            }

            if (useHexa) {
                writer.write(REFERENCE_HEXA_PREFIX);
                writer.write(Integer.toHexString(codepoint));
            } else {
                writer.write(REFERENCE_DECIMAL_PREFIX);
                writer.write(String.valueOf(codepoint));
            }
            writer.write(REFERENCE_SUFFIX);

        }

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }

    static int translateIllFormedCodepoint(final int codepoint) {
        switch (codepoint) {
            case 0x00: return 0xFFFD;
            case 0x80: return 0x20AC;
            case 0x82: return 0x201A;
            case 0x83: return 0x0192;
            case 0x84: return 0x201E;
            case 0x85: return 0x2026;
            case 0x86: return 0x2020;
            case 0x87: return 0x2021;
            case 0x88: return 0x02C6;
            case 0x89: return 0x2030;
            case 0x8A: return 0x0160;
            case 0x8B: return 0x2039;
            case 0x8C: return 0x0152;
            case 0x8E: return 0x017D;
            case 0x91: return 0x2018;
            case 0x92: return 0x2019;
            case 0x93: return 0x201C;
            case 0x94: return 0x201D;
            case 0x95: return 0x2022;
            case 0x96: return 0x2013;
            case 0x97: return 0x2014;
            case 0x98: return 0x02DC;
            case 0x99: return 0x2122;
            case 0x9A: return 0x0161;
            case 0x9B: return 0x203A;
            case 0x9C: return 0x0153;
            case 0x9E: return 0x017E;
            case 0x9F: return 0x0178;
            default: break;
        }
        if (codepoint >= 0xD800 && codepoint <= 0xDFFF) {
            return 0xFFFD;
        } else if (codepoint > 0x10FFFF) {
            return 0xFFFD;
        } else {
          return codepoint;
        }
    }

    static int parseIntFromReference(final String text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text.charAt(i);
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result *= radix;
            if (result < 0) {
                return 0xFFFD;
            }
            result += n;
            if (result < 0) {
                return 0xFFFD;
            }
        }
        return result;
    }

    static int parseIntFromReference(final char[] text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text[i];
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result *= radix;
            if (result < 0) {
                return 0xFFFD;
            }
            result += n;
            if (result < 0) {
                return 0xFFFD;
            }
        }
        return result;
    }

    static String unescape(final String text) {

        if (text == null) {
            return null;
        }

        final HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            if (c != REFERENCE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = 0;

            if (c == REFERENCE_PREFIX) {

                final char c1 = text.charAt(i + 1);

                if (c1 == '\u0020' ||
                    c1 == '\n' ||
                    c1 == '\u0009' ||
                    c1 == '\u000C' ||
                    c1 == '\u003C' ||
                    c1 == '\u0026') {

                    continue;

                } else if (c1 == REFERENCE_NUMERIC_PREFIX2) {

                    if (i + 2 >= max) {

                        continue;
                    }

                    final char c2 = text.charAt(i + 2);

                    if ((c2 == REFERENCE_HEXA_PREFIX3_LOWER || c2 == REFERENCE_HEXA_PREFIX3_UPPER) && (i + 3) < max) {

                        int f = i + 3;
                        while (f < max) {
                            final char cf = text.charAt(f);
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 3)) <= 0) {

                            continue;
                        }

                        codepoint = parseIntFromReference(text, i + 3, f, 16);
                        referenceOffset = f - 1;

                        if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                            referenceOffset++;
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                    } else if (c2 >= '0' && c2 <= '9') {

                        int f = i + 2;
                        while (f < max) {
                            final char cf = text.charAt(f);
                            if (!(cf >= '0' && cf <= '9')) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) <= 0) {

                            continue;
                        }

                        codepoint = parseIntFromReference(text, i + 2, f, 10);
                        referenceOffset = f - 1;

                        if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                            referenceOffset++;
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                    } else {

                        continue;
                    }

                } else {

                    int f = i + 1;
                    while (f < max) {
                        final char cf = text.charAt(f);
                        if (!((cf >= 'a' && cf <= 'z') || (cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9'))) {
                            break;
                        }
                        f++;
                    }

                    if ((f - (i + 1)) <= 0) {

                        continue;
                    }

                    if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                        f++;
                    }

                    final int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, text, i, f);
                    if (ncrPosition >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                    } else if (ncrPosition == Integer.MIN_VALUE) {

                        continue;
                    } else if (ncrPosition < -10) {

                        final int partialIndex = (-1) * (ncrPosition + 10);
                        final char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                        codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                        f -= ((f - i) - partialMatch.length);
                    } else {

                        throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                    }

                    referenceOffset = f - 1;

                }

            }

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            i = referenceOffset;
            readOffset = i + 1;

            if (codepoint > '\uFFFF') {
                strBuilder.append(Character.toChars(codepoint));
            } else if (codepoint < 0) {

                final int[] codepoints = symbols.DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                if (codepoints[0] > '\uFFFF') {
                    strBuilder.append(Character.toChars(codepoints[0]));
                } else {
                    strBuilder.append((char) codepoints[0]);
                }
                if (codepoints[1] > '\uFFFF') {
                    strBuilder.append(Character.toChars(codepoints[1]));
                } else {
                    strBuilder.append((char) codepoints[1]);
                }
            } else {
                strBuilder.append((char)codepoint);
            }

        }

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }

    static void unescape(final Reader reader, final Writer writer) throws IOException {

        if (reader == null) {
            return;
        }

        final HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;

        char[] escapes = new char[10];
        int escapei = 0;

        int c1, c2, ce;

        c2 = reader.read();

        while (c2 >= 0) {

            c1 = c2;
            c2 = reader.read();

            escapei = 0;

            if (c1 != REFERENCE_PREFIX || c2 < 0) {
                writer.write(c1);
                continue;
            }

            int codepoint = 0;

            if (c1 == REFERENCE_PREFIX) {

                if (c2 == '\u0020' ||
                        c2 == '\n' ||
                        c2 == '\u0009' ||
                        c2 == '\u000C' ||
                        c2 == '\u003C' ||
                        c2 == '\u0026') {

                    writer.write(c1);
                    continue;

                } else if (c2 == REFERENCE_NUMERIC_PREFIX2) {

                    final int c3 = reader.read();

                    if (c3 < 0) {

                        writer.write(c1);
                        writer.write(c2);
                        c1 = c2;
                        c2 = c3;
                        continue;
                    }

                    if ((c3 == REFERENCE_HEXA_PREFIX3_LOWER || c3 == REFERENCE_HEXA_PREFIX3_UPPER)) {

                        ce = reader.read();
                        while (ce >= 0) {
                            if (!((ce >= '0' && ce <= '9') || (ce >= 'A' && ce <= 'F') || (ce >= 'a' && ce <= 'f'))) {
                                break;
                            }
                            if (escapei == escapes.length) {

                                final char[] newEscapes = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                                escapes = newEscapes;
                            }
                            escapes[escapei] = (char) ce;
                            ce = reader.read();
                            escapei++;
                        }

                        if (escapei == 0) {

                            writer.write(c1);
                            writer.write(c2);
                            writer.write(c3);
                            c1 = c3;
                            c2 = ce;
                            continue;
                        }

                        c1 = escapes[escapei - 1];
                        c2 = ce;

                        codepoint = parseIntFromReference(escapes, 0, escapei, 16);

                        if (c2 == REFERENCE_SUFFIX) {

                            c1 = c2;
                            c2 = reader.read();
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                        escapei = 0;

                    } else if (c3 >= '0' && c3 <= '9') {

                        ce = c3;
                        while (ce >= 0) {
                            if (!(ce >= '0' && ce <= '9')) {
                                break;
                            }
                            if (escapei == escapes.length) {

                                final char[] newEscapes = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                                escapes = newEscapes;
                            }
                            escapes[escapei] = (char) ce;
                            ce = reader.read();
                            escapei++;
                        }

                        if (escapei == 0) {

                            writer.write(c1);
                            writer.write(c2);
                            c1 = c2;
                            c2 = c3;
                            continue;
                        }

                        c1 = escapes[escapei - 1];
                        c2 = ce;

                        codepoint = parseIntFromReference(escapes, 0, escapei, 10);

                        if (c2 == REFERENCE_SUFFIX) {

                            c1 = c2;
                            c2 = reader.read();
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                        escapei = 0;

                    } else {

                        writer.write(c1);
                        writer.write(c2);
                        c1 = c2;
                        c2 = c3;
                        continue;
                    }

                } else {

                    ce = c2;
                    while (ce >= 0) {
                        if (!((ce >= '0' && ce <= '9') || (ce >= 'A' && ce <= 'Z') || (ce >= 'a' && ce <= 'z'))) {
                            break;
                        }
                        if (escapei == escapes.length) {

                            final char[] newEscapes = new char[escapes.length + 4];
                            System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                            escapes = newEscapes;
                        }
                        escapes[escapei] = (char) ce;
                        ce = reader.read();
                        escapei++;
                    }

                    if (escapei == 0) {

                        writer.write(c1);
                        continue;
                    }

                    if (escapei + 2 >= escapes.length) {

                        final char[] newEscapes = new char[escapes.length + 4];
                        System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                        escapes = newEscapes;
                    }

                    System.arraycopy(escapes, 0, escapes, 1, escapei);
                    escapes[0] = (char) c1;
                    escapei++;

                    if (ce == REFERENCE_SUFFIX) {

                        escapes[escapei++] = (char) ce;
                        ce = reader.read();
                    }

                    c1 = escapes[escapei - 1];
                    c2 = ce;

                    final int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, escapes, 0, escapei);
                    if (ncrPosition >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                        escapei = 0;
                    } else if (ncrPosition == Integer.MIN_VALUE) {

                        writer.write(escapes, 0, escapei);
                        continue;
                    } else if (ncrPosition < -10) {

                        final int partialIndex = (-1) * (ncrPosition + 10);
                        final char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                        codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                        System.arraycopy(escapes, partialMatch.length, escapes, 0, (escapei - partialMatch.length));
                        escapei -= partialMatch.length;
                    } else {

                        throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                    }

                }

            }

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else if (codepoint < 0) {

                final int[] codepoints = symbols.DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                if (codepoints[0] > '\uFFFF') {
                    writer.write(Character.toChars(codepoints[0]));
                } else {
                    writer.write((char) codepoints[0]);
                }
                if (codepoints[1] > '\uFFFF') {
                    writer.write(Character.toChars(codepoints[1]));
                } else {
                    writer.write((char) codepoints[1]);
                }
            } else {
                writer.write((char)codepoint);
            }

            if (escapei > 0) {
                writer.write(escapes, 0, escapei);
                escapei = 0;
            }

        }

    }

    static void unescape(final char[] text, final int offset, final int len, final Writer writer)
                         throws IOException {

        if (text == null) {
            return;
        }

        final HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;

        final int max = (offset + len);

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            if (c != REFERENCE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = 0;

            if (c == REFERENCE_PREFIX) {

                final char c1 = text[i + 1];

                if (c1 == '\u0020' ||
                        c1 == '\n' ||
                        c1 == '\u0009' ||
                        c1 == '\u000C' ||
                        c1 == '\u003C' ||
                        c1 == '\u0026') {

                    continue;

                } else if (c1 == REFERENCE_NUMERIC_PREFIX2) {

                    if (i + 2 >= max) {

                        continue;
                    }

                    final char c2 = text[i + 2];

                    if ((c2 == REFERENCE_HEXA_PREFIX3_LOWER || c2 == REFERENCE_HEXA_PREFIX3_UPPER) && (i + 3) < max) {

                        int f = i + 3;
                        while (f < max) {
                            final char cf = text[f];
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 3)) <= 0) {

                            continue;
                        }

                        codepoint = parseIntFromReference(text, i + 3, f, 16);
                        referenceOffset = f - 1;

                        if ((f < max) && text[f] == REFERENCE_SUFFIX) {
                            referenceOffset++;
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                    } else if (c2 >= '0' && c2 <= '9') {

                        int f = i + 2;
                        while (f < max) {
                            final char cf = text[f];
                            if (!(cf >= '0' && cf <= '9')) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) <= 0) {

                            continue;
                        }

                        codepoint = parseIntFromReference(text, i + 2, f, 10);
                        referenceOffset = f - 1;

                        if ((f < max) && text[f] == REFERENCE_SUFFIX) {
                            referenceOffset++;
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                    } else {

                        continue;
                    }

                } else {

                    int f = i + 1;
                    while (f < max) {
                        final char cf = text[f];
                        if (!((cf >= 'a' && cf <= 'z') || (cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9'))) {
                            break;
                        }
                        f++;
                    }

                    if ((f - (i + 1)) <= 0) {

                        continue;
                    }

                    if ((f < max) && text[f] == REFERENCE_SUFFIX) {
                        f++;
                    }

                    final int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, text, i, f);
                    if (ncrPosition >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                    } else if (ncrPosition == Integer.MIN_VALUE) {

                        continue;
                    } else if (ncrPosition < -10) {

                        final int partialIndex = (-1) * (ncrPosition + 10);
                        final char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                        codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                        f -= ((f - i) - partialMatch.length);
                    } else {

                        throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                    }

                    referenceOffset = f - 1;

                }

            }

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            i = referenceOffset;
            readOffset = i + 1;

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else if (codepoint < 0) {

                final int[] codepoints = symbols.DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                if (codepoints[0] > '\uFFFF') {
                    writer.write(Character.toChars(codepoints[0]));
                } else {
                    writer.write((char) codepoints[0]);
                }
                if (codepoints[1] > '\uFFFF') {
                    writer.write(Character.toChars(codepoints[1]));
                } else {
                    writer.write((char) codepoints[1]);
                }
            } else {
                writer.write((char) codepoint);
            }

        }

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }

    private static int codePointAt(final char c1, final char c2) {
        if (Character.isHighSurrogate(c1)) {
            if (c2 >= 0) {
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
            }
        }
        return c1;
    }

}
