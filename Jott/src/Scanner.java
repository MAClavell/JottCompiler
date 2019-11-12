import java.lang.reflect.Array;
import java.util.ArrayList;

public class Scanner {

    //Static variable for the token's type because of integer -> double transitions
    private static TokenType type;

    /**
     * Tokenize a string into Jott tokens
     * @param text  The text to tokenize
     * @return and ArrayList of the tokens created
     */
    public static ArrayList<Token> tokenize(String text, String fileName)
    {
        ArrayList<Token> tokens = new ArrayList<Token>();

        text += '\0';

        //Token tracking
        int lineNum = 1;
        int columnNum = 1;
        int columnStart = 1;
        StringBuilder textBuilder = new StringBuilder();
        type = null;
        boolean discardingComment = false;

        //Loop through all the characters and create tokens
        for(int i = 0; i < text.length(); i++)
        {
            //Get character and whether it is valid in the token
            char c = text.charAt(i);

            //Only scan for tokens if we aren't discarding a comment line
            if(!discardingComment)
            {
                //Start a new token if needed
                if (type == null)
                {
                    //Check for whitespace since we cannot start a new token with it
                    if (Character.isWhitespace(c))
                    {
                        //Move to next token
                        columnNum++;
                        columnStart = columnNum;
                        if (c == '\n')
                        {
                            lineNum++;
                            columnNum = 1;
                            columnStart = 1;
                        }
                        continue;
                    }

                    //Find a new token type
                    type = findTokenTypeFromChar(c);
                    if(type == TokenType.String && text.charAt(text.length()-1) != '"')
                    {
                            LogError.log(LogError.ErrorType.SYNTAX, "Missing \"", lineNum, columnStart, columnNum);
                            break;
                    }
                    if (type == null) //type is STILL null
                    {
                        LogError.log(LogError.ErrorType.SYNTAX, "Invalid character '" + c + "' found", lineNum, columnStart, columnNum);
                        break;
                    } else columnStart = columnNum; //start of a token
                }

                //Run scanner logic and get the result
                short result = isValidCharForTokenType(c, textBuilder);

                //Comment found, throw away the rest of this line
                if (result == 3)
                {
                    discardingComment = true;

                    //Reset
                    type = null;
                    textBuilder.setLength(0);
                }
                //Complete the token
                else if (result > 0)
                {
                    //Add the char to the token if we told the scanner to (manually)
                    if (result == 2)
                        textBuilder.append(c);
                    //If we DIDN'T manually tell the scanner to stop, we have to go back one char
                    //Hacky method for strings don't @me
                    else if(c != '\"')
                    {
                        i--;
                        columnNum--;
                    }

                    //Add token
                    tokens.add(new Token(textBuilder.toString(), type, lineNum, columnStart, columnNum));

                    //Reset
                    type = null;
                    textBuilder.setLength(0);
                }
                //Add character to current token
                else
                {
                    textBuilder.append(c);
                }
            }

            //Increment column and check for new line
            columnNum++;
            if(c == '\n')
            {
                lineNum++;
                columnNum = 1;
                columnStart = 1;
                discardingComment = false;
            }
        }

        return tokens;
    }

    /**
     * Check if an inputted char is a valid token
     * @param c The character to check
     * @param textBuilder The StringBuilder that is building this token's text
     * @return A short representing the success of the operation:
     *          0 = add this char to the token
     *          1 = complete the token
     *          2 = complete the token and append this char
     *          3 = comment was found
     */
    private static short isValidCharForTokenType(char c, StringBuilder textBuilder)
    {
        //Switch through all the types of tokens
        switch (type) {
            case String:
                if(c == '"' && textBuilder.length() != 0)
                {
                    textBuilder.deleteCharAt(0);
                }
                else return 0; //strings can contain all characters

            case ID:
                if(Character.isAlphabetic(c) || Character.isDigit(c))
                    return 0; //add to ID
                break;

            case Integer:
                if(Character.isDigit(c))
                    return 0;
                if(c == '.')
                {
                    type = TokenType.Double; //transition to a double
                    return 0;
                }
                break;

            case Double:
                if(Character.isDigit(c) || (c == '.' && textBuilder.length() == 0))
                    return 0;
                break;

            case Assign:
                if(c == '=')
                {
                    if(textBuilder.length() == 1)
                    {
                        type = TokenType.Eq; //transition to a double
                        return 2;
                    }
                    return 0;
                }
                break;

            case EndStmt:
                return 2; //can only contain 1 character

            case StartParen:
                return 2; //can only contain 1 character

            case EndParen:
                return 2; //can only contain 1 character

            case Power:
                return 2; //can only contain 1 character

            case Divide:
                if(c == '/' && textBuilder.length() == 1)
                    return 3;
                else if(c == '/')
                    return 0;
                break;

            case Mult:
                return 2; //can only contain 1 character

            case Minus:
                return 2; //can only contain 1 character

            case Plus:
                return 2; //can only contain 1 character

            case Comma:
                return 2; //can only contain 1 character

            case StartBlk:
                return 2; //can only contain 1 character

            case EndBlk:
                return 2; //can only contain 1 character

            case NotEq:
                if(c == '=' && textBuilder.length() == 1)
                    return 2;
                else if (c == '!' && textBuilder.length() == 0)
                    return 0;
                break;

            case Greater:
                if(c == '=' && textBuilder.length() == 1)
                {
                    type = TokenType.GreaterEq; //transition to >=
                    return 2;
                }
                else if (c == '>' && textBuilder.length() == 0)
                    return 0;
                break;

            case Less:
                if(c == '=' && textBuilder.length() == 1)
                {
                    type = TokenType.LessEq; //transition to <=
                    return 2;
                }
                else if (c == '<' && textBuilder.length() == 0)
                    return 0;
                break;

            case EoF:
                return 2;
        }

        return 1; //complete the token if we weren't told to save the char
    }

    /**
     * Get the correct token type from a token's starting character
     * @param c The first character in a new token
     * @return A TokenType of what token it is
     */
    private static TokenType findTokenTypeFromChar(char c)
    {
        switch(c)
        {
            case '"':
                return TokenType.String;
            case '.':
                return TokenType.Double;
            case '=':
                return TokenType.Assign;
            case ';':
                return TokenType.EndStmt;
            case '(':
                return TokenType.StartParen;
            case ')':
                return TokenType.EndParen;
            case '^':
                return TokenType.Power;
            case '/':
                return TokenType.Divide;
            case '*':
                return TokenType.Mult;
            case '-':
                return TokenType.Minus;
            case '+':
                return TokenType.Plus;
            case ',':
                return TokenType.Comma;
            case '{':
                return TokenType.StartBlk;
            case '}':
                return TokenType.EndBlk;
            case '!':
                return TokenType.NotEq;
            case '>':
                return TokenType.Greater;
            case '<':
                return TokenType.Less;
            case '\0':
                return TokenType.EoF;
            default:
                break; //check other for id and numbers
        }

        //Check for IDs and Integers
        if(Character.isAlphabetic(c))
            return TokenType.ID;
        else if(Character.isDigit(c))
            return TokenType.Integer;
        else return null; //fail
    }

}
