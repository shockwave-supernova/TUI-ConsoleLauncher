package com.tui.next.tuils.html_escape;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public final class HtmlEscape {

    public static String escapeHtml5(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml5Xml(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml4(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml4Xml(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml(final String text, final HtmlEscapeType type, final HtmlEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return HtmlEscapeUtil.escape(text, type, level);

    }

    public static void escapeHtml5(final String text, final Writer writer)
            throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml5Xml(final String text, final Writer writer)
            throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4(final String text, final Writer writer)
            throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4Xml(final String text, final Writer writer)
            throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml(final String text, final Writer writer, final HtmlEscapeType type, final HtmlEscapeLevel level)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        HtmlEscapeUtil.escape(new InternalStringReader(text), writer, type, level);

    }

    public static void escapeHtml5(final Reader reader, final Writer writer)
            throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml5Xml(final Reader reader, final Writer writer)
            throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4(final Reader reader, final Writer writer)
            throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4Xml(final Reader reader, final Writer writer)
            throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml(final Reader reader, final Writer writer, final HtmlEscapeType type, final HtmlEscapeLevel level)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        HtmlEscapeUtil.escape(reader, writer, type, level);

    }

    public static void escapeHtml5(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml5Xml(final char[] text, final int offset, final int len, final Writer writer)
                                      throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4Xml(final char[] text, final int offset, final int len, final Writer writer)
                                      throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeType type, final HtmlEscapeLevel level)
                                  throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        HtmlEscapeUtil.escape(text, offset, len, writer, type, level);

    }

    public static String unescapeHtml(final String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf('&') < 0) {

            return text;
        }
        return HtmlEscapeUtil.unescape(text);
    }

    public static void unescapeHtml(final String text, final Writer writer)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf('&') < 0) {

            writer.write(text);
            return;
        }

        HtmlEscapeUtil.unescape(new InternalStringReader(text), writer);

    }

    public static void unescapeHtml(final Reader reader, final Writer writer)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        HtmlEscapeUtil.unescape(reader, writer);

    }

    public static void unescapeHtml(final char[] text, final int offset, final int len, final Writer writer)
                                    throws IOException{

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        HtmlEscapeUtil.unescape(text, offset, len, writer);

    }

    private HtmlEscape() {
        super();
    }

    private static final class InternalStringReader extends Reader {

        private String str;
        private int length;
        private int next = 0;

        public InternalStringReader(final String s) {
            super();
            this.str = s;
            this.length = s.length();
        }

        @Override
        public int read() throws IOException {
            if (this.next >= length) {
                return -1;
            }
            return this.str.charAt(this.next++);
        }

        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                    ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            if (this.next >= this.length) {
                return -1;
            }
            int n = Math.min(this.length - this.next, len);
            this.str.getChars(this.next, this.next + n, cbuf, off);
            this.next += n;
            return n;
        }

        @Override
        public void close() throws IOException {
            this.str = null;
        }

    }

}
