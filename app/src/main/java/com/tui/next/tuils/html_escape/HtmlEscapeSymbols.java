package com.tui.next.tuils.html_escape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class HtmlEscapeSymbols {

    static final int NCRS_BY_CODEPOINT_LEN = 0x2fff;

    final short[] NCRS_BY_CODEPOINT = new short[NCRS_BY_CODEPOINT_LEN];

    final Map<Integer,Short> NCRS_BY_CODEPOINT_OVERFLOW;

    static final char MAX_ASCII_CHAR = 0x7f;

    final byte[] ESCAPE_LEVELS = new byte[MAX_ASCII_CHAR + 2];

    final char[][] SORTED_NCRS;

    final int[] SORTED_CODEPOINTS;

    final int[][] DOUBLE_CODEPOINTS;

    static final short NO_NCR = (short) 0;

    static final HtmlEscapeSymbols HTML4_SYMBOLS;
    static final HtmlEscapeSymbols HTML5_SYMBOLS;

    static {

        HTML4_SYMBOLS = Html4EscapeSymbolsInitializer.initializeHtml4();
        HTML5_SYMBOLS = Html5EscapeSymbolsInitializer.initializeHtml5();

    }

    HtmlEscapeSymbols(final References references, final byte[] escapeLevels) {

        super();

        System.arraycopy(escapeLevels, 0, ESCAPE_LEVELS, 0, (0x7f + 2));

        final List<char[]> ncrs = new ArrayList<>(references.references.size() + 5);
        final List<Integer> codepoints = new ArrayList<>(references.references.size() + 5);
        final List<int[]> doubleCodepoints = new ArrayList<>(100);
        final Map<Integer,Short> ncrsByCodepointOverflow = new HashMap<>(20);

        for (final Reference reference : references.references) {

            final char[] referenceNcr = reference.ncr;
            final int[] referenceCodepoints = reference.codepoints;

            ncrs.add(referenceNcr);

            if (referenceCodepoints.length == 1) {

                final int referenceCodepoint = referenceCodepoints[0];
                codepoints.add(Integer.valueOf(referenceCodepoint));

            } else if (referenceCodepoints.length == 2) {

                doubleCodepoints.add(referenceCodepoints);

                codepoints.add(Integer.valueOf((-1) * doubleCodepoints.size()));

            } else {

                throw new RuntimeException(
                        "Unsupported codepoints #: " + referenceCodepoints.length + " for " + new String(referenceNcr));

            }

        }

        Arrays.fill(NCRS_BY_CODEPOINT, NO_NCR);

        SORTED_NCRS = new char[ncrs.size()][];
        SORTED_CODEPOINTS = new int[codepoints.size()];

        final List<char[]> ncrsOrdered = new ArrayList<>(ncrs);
        Collections.sort(ncrsOrdered, (o1, o2) -> compare(o1, o2, 0, o2.length));

        for (short i = 0; i < SORTED_NCRS.length; i++) {

            final char[] ncr = ncrsOrdered.get(i);
            SORTED_NCRS[i] = ncr;

            for (short j = 0; j  < SORTED_NCRS.length; j++) {

                if (Arrays.equals(ncr,ncrs.get(j))) {

                    final int cp = codepoints.get(j);
                    SORTED_CODEPOINTS[i] = cp;

                    if (cp > 0) {

                        if (cp < NCRS_BY_CODEPOINT_LEN) {

                            if (NCRS_BY_CODEPOINT[cp] == NO_NCR) {

                                NCRS_BY_CODEPOINT[cp] = i;
                            } else {
                                final int positionOfCurrent = positionInList(ncrs, SORTED_NCRS[NCRS_BY_CODEPOINT[cp]]);
                                final int positionOfNew = positionInList(ncrs, ncr);
                                if (positionOfNew < positionOfCurrent) {

                                    NCRS_BY_CODEPOINT[cp] = i;
                                }
                            }
                        } else {

                            ncrsByCodepointOverflow.put(Integer.valueOf(cp), Short.valueOf(i));
                        }
                    }

                    break;

                }

            }

        }

        if (ncrsByCodepointOverflow.size() > 0) {
            NCRS_BY_CODEPOINT_OVERFLOW = ncrsByCodepointOverflow;
        } else {
            NCRS_BY_CODEPOINT_OVERFLOW = null;
        }

        if (doubleCodepoints.size() > 0) {
            DOUBLE_CODEPOINTS = new int[doubleCodepoints.size()][];
            for (int i = 0; i < DOUBLE_CODEPOINTS.length; i++) {
                DOUBLE_CODEPOINTS[i] = doubleCodepoints.get(i);
            }
        } else {
            DOUBLE_CODEPOINTS = null;
        }

    }

    private static int positionInList(final List<char[]> list, final char[] element) {
        int i = 0;
        for (final char[] e : list) {
            if (Arrays.equals(e, element)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static int compare(final char[] ncr, final String text, final int start, final int end) {
        final int textLen = end - start;
        final int maxCommon = Math.min(ncr.length, textLen);
        int i;

        for (i = 1; i < maxCommon; i++) {
            final char tc = text.charAt(start + i);
            if (ncr[i] < tc) {
                if (tc == ';') {
                    return 1;
                }
                return -1;
            } else if (ncr[i] > tc) {
                if (ncr[i] == ';') {
                    return -1;
                }
                return 1;
            }
        }
        if (ncr.length > i) {
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        }
        if (textLen > i) {
            if (text.charAt(start + i) == ';') {
                return 1;
            }

            return - ((textLen - i) + 10);
        }
        return 0;
    }

    private static int compare(final char[] ncr, final char[] text, final int start, final int end) {
        final int textLen = end - start;
        final int maxCommon = Math.min(ncr.length, textLen);
        int i;

        for (i = 1; i < maxCommon; i++) {
            final char tc = text[start + i];
            if (ncr[i] < tc) {
                if (tc == ';') {
                    return 1;
                }
                return -1;
            } else if (ncr[i] > tc) {
                if (ncr[i] == ';') {
                    return -1;
                }
                return 1;
            }
        }
        if (ncr.length > i) {
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        }
        if (textLen > i) {
            if (text[start + i] == ';') {
                return 1;
            }

            return - ((textLen - i) + 10);
        }
        return 0;
    }

    static int binarySearch(final char[][] values,
                            final String text, final int start, final int end) {

        int low = 0;
        int high = values.length - 1;

        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;

        while (low <= high) {

            final int mid = (low + high) >>> 1;
            final char[] midVal = values[mid];

            final int cmp = compare(midVal, text, start, end);

            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else if (cmp < -10) {

                low = mid + 1;
                if (partialIndex == Integer.MIN_VALUE || partialValue < cmp) {
                    partialIndex = mid;
                    partialValue = cmp;
                }
            } else {

                return mid;
            }

        }

        if (partialIndex != Integer.MIN_VALUE) {

            return (-1) * (partialIndex + 10);
        }

        return Integer.MIN_VALUE;

    }

    static int binarySearch(final char[][] values,
                            final char[] text, final int start, final int end) {

        int low = 0;
        int high = values.length - 1;

        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;

        while (low <= high) {

            final int mid = (low + high) >>> 1;
            final char[] midVal = values[mid];

            final int cmp = compare(midVal, text, start, end);

            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else if (cmp < -10) {

                low = mid + 1;
                if (partialIndex == Integer.MIN_VALUE || partialValue < cmp) {
                    partialIndex = mid;
                    partialValue = cmp;
                }
            } else {

                return mid;
            }

        }

        if (partialIndex != Integer.MIN_VALUE) {

            return (-1) * (partialIndex + 10);
        }

        return Integer.MIN_VALUE;

    }

    static final class References {

        private final List<Reference> references = new ArrayList<>(200);

        References() {
            super();
        }

        void addReference(final int codepoint, final String ncr) {
            this.references.add(new Reference(ncr, new int[]{codepoint}));
        }

        void addReference(final int codepoint0, final int codepoint1, final String ncr) {
            this.references.add(new Reference(ncr, new int[] { codepoint0, codepoint1 }));
        }

    }

    private static final class Reference {

        private final char[] ncr;
        private final int[] codepoints;

        private Reference(final String ncr, final int[] codepoints) {
            super();
            this.ncr = ncr.toCharArray();
            this.codepoints = codepoints;
        }

    }

}
