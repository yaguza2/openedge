/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.lexer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PushbackReader;

import nl.openedge.gaps.support.gapspath.node.EOF;
import nl.openedge.gaps.support.gapspath.node.TAnd;
import nl.openedge.gaps.support.gapspath.node.TAt;
import nl.openedge.gaps.support.gapspath.node.TBlank;
import nl.openedge.gaps.support.gapspath.node.TColon;
import nl.openedge.gaps.support.gapspath.node.TEq;
import nl.openedge.gaps.support.gapspath.node.TId;
import nl.openedge.gaps.support.gapspath.node.TLBrace;
import nl.openedge.gaps.support.gapspath.node.TLBracket;
import nl.openedge.gaps.support.gapspath.node.TOr;
import nl.openedge.gaps.support.gapspath.node.TQuote;
import nl.openedge.gaps.support.gapspath.node.TRBrace;
import nl.openedge.gaps.support.gapspath.node.TRBracket;
import nl.openedge.gaps.support.gapspath.node.TSlash;
import nl.openedge.gaps.support.gapspath.node.Token;

public class Lexer {

    protected Token token;

    protected State state = State.INITIAL;

    private PushbackReader in;

    private int line;

    private int pos;

    private boolean cr;

    private boolean eof;

    private final StringBuffer text = new StringBuffer();

    protected void filter() throws LexerException, IOException {
    }

    public Lexer(PushbackReader in) {
        this.in = in;

        if (gotoTable == null) {
            try {
                DataInputStream s = new DataInputStream(new BufferedInputStream(Lexer.class
                        .getResourceAsStream("lexer.dat")));

                // read gotoTable
                int length = s.readInt();
                gotoTable = new int[length][][][];
                for (int i = 0; i < gotoTable.length; i++) {
                    length = s.readInt();
                    gotoTable[i] = new int[length][][];
                    for (int j = 0; j < gotoTable[i].length; j++) {
                        length = s.readInt();
                        gotoTable[i][j] = new int[length][3];
                        for (int k = 0; k < gotoTable[i][j].length; k++) {
                            for (int l = 0; l < 3; l++) {
                                gotoTable[i][j][k][l] = s.readInt();
                            }
                        }
                    }
                }

                // read accept
                length = s.readInt();
                accept = new int[length][];
                for (int i = 0; i < accept.length; i++) {
                    length = s.readInt();
                    accept[i] = new int[length];
                    for (int j = 0; j < accept[i].length; j++) {
                        accept[i][j] = s.readInt();
                    }
                }

                s.close();
            } catch (Exception e) {
                throw new RuntimeException("The file \"lexer.dat\" is either missing or corrupted.");
            }
        }
    }

    public Token peek() throws LexerException, IOException {
        while (token == null) {
            token = getToken();
            filter();
        }

        return token;
    }

    public Token next() throws LexerException, IOException {
        while (token == null) {
            token = getToken();
            filter();
        }

        Token result = token;
        token = null;
        return result;
    }

    protected Token getToken() throws IOException, LexerException {
        int dfa_state = 0;

        int start_pos = pos;
        int start_line = line;

        int accept_state = -1;
        int accept_token = -1;
        int accept_length = -1;
        int accept_pos = -1;
        int accept_line = -1;

        int[][][] gotoTable = Lexer.gotoTable[state.id()];
        int[] accept = Lexer.accept[state.id()];
        text.setLength(0);

        while (true) {
            int c = getChar();

            if (c != -1) {
                switch (c) {
                case 10:
                    if (cr) {
                        cr = false;
                    } else {
                        line++;
                        pos = 0;
                    }
                    break;
                case 13:
                    line++;
                    pos = 0;
                    cr = true;
                    break;
                default:
                    pos++;
                    cr = false;
                    break;
                }
                ;

                text.append((char) c);

                do {
                    int oldState = (dfa_state < -1) ? (-2 - dfa_state) : dfa_state;

                    dfa_state = -1;

                    int[][] tmp1 = gotoTable[oldState];
                    int low = 0;
                    int high = tmp1.length - 1;

                    while (low <= high) {
                        int middle = (low + high) / 2;
                        int[] tmp2 = tmp1[middle];

                        if (c < tmp2[0]) {
                            high = middle - 1;
                        } else if (c > tmp2[1]) {
                            low = middle + 1;
                        } else {
                            dfa_state = tmp2[2];
                            break;
                        }
                    }
                } while (dfa_state < -1);
            } else {
                dfa_state = -1;
            }

            if (dfa_state >= 0) {
                if (accept[dfa_state] != -1) {
                    accept_state = dfa_state;
                    accept_token = accept[dfa_state];
                    accept_length = text.length();
                    accept_pos = pos;
                    accept_line = line;
                }
            } else {
                if (accept_state != -1) {
                    switch (accept_token) {
                    case 0: {
                        Token token = new0(start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 1: {
                        Token token = new1(start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 2: {
                        Token token = new2(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 3: {
                        Token token = new3(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 4: {
                        Token token = new4(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 5: {
                        Token token = new5(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 6: {
                        Token token = new6(start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 7: {
                        Token token = new7(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 8: {
                        Token token = new8(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 9: {
                        Token token = new9(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 10: {
                        Token token = new10(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 11: {
                        Token token = new11(start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    case 12: {
                        Token token = new12(getText(accept_length), start_line + 1, start_pos + 1);
                        pushBack(accept_length);
                        pos = accept_pos;
                        line = accept_line;
                        return token;
                    }
                    }
                } else {
                    if (text.length() > 0) {
                        throw new LexerException("[" + (start_line + 1) + "," + (start_pos + 1)
                                + "]" + " Unknown token: " + text);
                    } else {
                        EOF token = new EOF(start_line + 1, start_pos + 1);
                        return token;
                    }
                }
            }
        }
    }

    Token new0(int line, int pos) {
        return new TAnd(line, pos);
    }

    Token new1(int line, int pos) {
        return new TOr(line, pos);
    }

    Token new2(String text, int line, int pos) {
        return new TLBracket(text, line, pos);
    }

    Token new3(String text, int line, int pos) {
        return new TRBracket(text, line, pos);
    }

    Token new4(String text, int line, int pos) {
        return new TLBrace(text, line, pos);
    }

    Token new5(String text, int line, int pos) {
        return new TRBrace(text, line, pos);
    }

    Token new6(int line, int pos) {
        return new TSlash(line, pos);
    }

    Token new7(String text, int line, int pos) {
        return new TAt(text, line, pos);
    }

    Token new8(String text, int line, int pos) {
        return new TColon(text, line, pos);
    }

    Token new9(String text, int line, int pos) {
        return new TId(text, line, pos);
    }

    Token new10(String text, int line, int pos) {
        return new TEq(text, line, pos);
    }

    Token new11(int line, int pos) {
        return new TQuote(line, pos);
    }

    Token new12(String text, int line, int pos) {
        return new TBlank(text, line, pos);
    }

    private int getChar() throws IOException {
        if (eof) { return -1; }

        int result = in.read();

        if (result == -1) {
            eof = true;
        }

        return result;
    }

    private void pushBack(int acceptLength) throws IOException {
        int length = text.length();
        for (int i = length - 1; i >= acceptLength; i--) {
            eof = false;

            in.unread(text.charAt(i));
        }
    }

    protected void unread(Token token) throws IOException {
        String text = token.getText();
        int length = text.length();

        for (int i = length - 1; i >= 0; i--) {
            eof = false;

            in.unread(text.charAt(i));
        }

        pos = token.getPos() - 1;
        line = token.getLine() - 1;
    }

    private String getText(int acceptLength) {
        StringBuffer s = new StringBuffer(acceptLength);
        for (int i = 0; i < acceptLength; i++) {
            s.append(text.charAt(i));
        }

        return s.toString();
    }

    private static int[][][][] gotoTable;

    /*
     * { { // INITIAL {{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {39,
     * 39, 5}, {46, 46, 6}, {47, 47, 7}, {48, 57, 8}, {58, 58, 9}, {61, 61, 10},
     * {64, 64, 11}, {65, 90, 12}, {91, 91, 13}, {93, 93, 14}, {95, 95, 15},
     * {97, 97, 16}, {98, 110, 17}, {111, 111, 18}, {112, 122, 17}, {123, 123,
     * 19}, {125, 125, 20}, }, {{9, 32, -2}, }, {{9, 32, -2}, }, {{9, 9, 1},
     * {10, 10, 21}, {13, 32, -2}, }, {{9, 32, -2}, }, {}, {{46, 46, 22}, {48,
     * 57, 23}, {65, 90, 24}, {95, 95, 25}, {97, 122, 26}, }, {}, {{46, 122,
     * -8}, }, {}, {}, {}, {{46, 122, -8}, }, {}, {}, {{46, 122, -8}, }, {{46,
     * 95, -8}, {97, 109, 26}, {110, 110, 27}, {111, 122, 26}, }, {{46, 122,
     * -8}, }, {{46, 95, -8}, {97, 113, 26}, {114, 114, 28}, {115, 122, 26}, },
     * {}, {}, {{9, 32, -2}, }, {{46, 122, -8}, }, {{46, 122, -8}, }, {{46, 122,
     * -8}, }, {{46, 122, -8}, }, {{46, 122, -8}, }, {{46, 95, -8}, {97, 99,
     * 26}, {100, 100, 29}, {101, 122, 26}, }, {{46, 122, -8}, }, {{46, 122,
     * -8}, }, } };
     */

    private static int[][] accept;

    /*
     * { // INITIAL {-1, 12, 12, 12, 12, 11, 9, 6, 9, 8, 10, 7, 9, 2, 3, 9, 9,
     * 9, 9, 4, 5, 12, 9, 9, 9, 9, 9, 9, 1, 0, }, };
     */

    public static class State {

        public final static State INITIAL = new State(0);

        private int id;

        private State(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }
    }
}