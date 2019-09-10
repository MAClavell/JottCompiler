package src;

//Enum representing the type of token for the parser
enum TokenType {
    String, ID, Number, Assign, EndStmt, StartParen, EndParen,
    Power, Divide, Mult, Minus, Plus;
}

public class Token {

    //Token types
    private String tokenText;
    private TokenType type;
    private int lineNum;
    private int columnEnd;

    public Token()
    {

    }

    //Set the text for this token
    public void SetTokenText(String tokenText)
    {
        this.tokenText = tokenText;
    }
    //Return this token's text
    public String GetTokenText()
    {
        return tokenText;
    }

    //Set the type of token this is
    public void SetTokenType(TokenType type)
    {
        this.type = type;
    }
    //Get the type of token this is
    public TokenType GetTokenType()
    {
        return type;
    }
}
