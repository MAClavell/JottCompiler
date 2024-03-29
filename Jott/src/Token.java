/**
 * Enum representing the type of token for the parser
 */
enum TokenType {
    String, ID, Integer, Double, Assign, EndStmt, StartParen, EndParen,
    Power, Divide, Mult, Minus, Plus, Comma, StartBlk, EndBlk, Eq, NotEq,
    Greater, GreaterEq, Less, LessEq, EoF;
}

/**
 * A class to contain specific token data
 */
public class Token {

    //Token types
    private String tokenText;
    private TokenType type;
    private int lineNum;
    private int columnStart;
    private int columnEnd;

    /**
     * Construct a token object
     * @param tokenText The text for this token
     * @param type The type of this token
     * @param lineNum The line number this token appears on
     * @param columnStart The column this token starts
     * @param columnEnd The column this token ends
     */
    public Token(String tokenText, TokenType type, int lineNum, int columnStart, int columnEnd)
    {
        this.tokenText = tokenText;
        this.type = type;
        this.lineNum = lineNum;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    /**
     * Get this token's text
     * @return string text of the token
     */
    public String getTokenText()
    {
        return tokenText;
    }
    /**
     * Get this token's type
     * @return TokenType type of the token
     */
    public TokenType getTokenType()
    {
        return type;
    }
    /**
     * Get this token's line number
     * @return int line number of the token
     */
    public int getLineNum()
    {
        return lineNum;
    }
    /**
     * Get this token's column start
     * @return int column this token starts
     */
    public int getColumnStart()
    {
        return columnStart;
    }
    /**
     * Get this token's column end
     * @return int column this token ends
     */
    public int getColumnEnd()
    {
        return columnEnd;
    }

    @Override
    public String toString() {
        return type + ": '" + tokenText + "', Line: " + lineNum + ", Columns: " + columnStart + "-" + columnEnd;
    }
}
