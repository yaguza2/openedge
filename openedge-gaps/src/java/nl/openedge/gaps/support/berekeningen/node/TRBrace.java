/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.berekeningen.node;

import nl.openedge.gaps.support.berekeningen.analysis.Analysis;

public final class TRBrace extends Token {

    public TRBrace(String text) {
        setText(text);
    }

    public TRBrace(String text, int line, int pos) {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    public Object clone() {
        return new TRBrace(getText(), getLine(), getPos());
    }

    public void apply(Switch sw) {
        ((Analysis) sw).caseTRBrace(this);
    }
}