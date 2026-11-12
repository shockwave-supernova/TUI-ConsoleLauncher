package com.tui.next.tuils.html_escape;

public enum HtmlEscapeType {

    HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(true, false, false),

    HTML4_NAMED_REFERENCES_DEFAULT_TO_HEXA(true, true, false),

    HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(true, false, true),

    HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA(true, true, true),

    DECIMAL_REFERENCES(false, false, false),

    HEXADECIMAL_REFERENCES(false, true, false);

    private final boolean useNCRs;
    private final boolean useHexa;
    private final boolean useHtml5;

    HtmlEscapeType(final boolean useNCRs, final boolean useHexa, final boolean useHtml5) {
        this.useNCRs = useNCRs;
        this.useHexa = useHexa;
        this.useHtml5 = useHtml5;
    }

    boolean getUseNCRs() {
        return this.useNCRs;
    }

    boolean getUseHexa() {
        return this.useHexa;
    }

    boolean getUseHtml5() {
        return this.useHtml5;
    }
}
