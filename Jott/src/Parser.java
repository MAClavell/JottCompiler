import java.util.ArrayList;

public class Parser {

    // The token index
    static int tokenIndex=0;
    // The list of tokens
    static ArrayList<Token> tokenStream;

    static Reference globalScope;
    //The text line by line
    static String[] lines;

    // Strings representing the different tokens for Strings
    public final static String PRINT="print";
    public final static String IF="if";
    public final static String ELSE="else";
    public final static String WHILE="while";
    public final static String FOR="for";
    public final static String CONCAT="concat";
    public final static String CHARAT="charAt";
    public final static String DOUBLE="Double";
    public final static String INTEGER="Integer";
    public final static String STRING="String";
    public final static String VOID="Void";
    public final static String RETURN="return";

    // TODO exit out if there is a parse error

    /**
     * The main parsing method called to parse the tokenStream into a parse tree
     * @param tokens the token stream to parse
     * @return the parse tree
     */
    public static TreeNode parse(ArrayList<Token> tokens, Reference global){
        // Sets the tokens
        tokenStream=tokens;
        globalScope=global;

        // The root node
        State state = new State(State.stateType.PROGRAM);
        TreeNode root = new TreeNode(state);

        //Starts the parsing process
        program(root);
        return root;
    }

    ///Predict Functions ------------------------------------------------------


    private static void program(TreeNode node){
        stmt_list(node.addTreeNode((new State((State.stateType.STMT_LIST)))));
        node.addTreeNode((new State((State.stateType.END_PROG))));
    }

    /**
     * Deals with the parsing of a stmt list into either a stmt and stmt list or an epsilon
     * @param node the node to parse
     */
    private static void stmt_list(TreeNode node){

        // Checks if the EoF is next. If not, add stmt and stmt_list
        if(tokenStream.get(tokenIndex).getTokenType()!=TokenType.EoF) {
            // Add a statement to the tree and branch off to parse this left hand side
            stmt(node.addTreeNode(new State((State.stateType.STMT))));

            // Add a statement to the tree and branch off to parse this right hand side
            stmt_list(node.addTreeNode(new State(State.stateType.STMT_LIST)));
        }

        // Adds an epsilon otherwise
        else{
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }

    private static void if_stmt(TreeNode node){
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

        expr(node.addTreeNode(new State(State.stateType.EXPR)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartBlk)) {
            node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '{', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndBlk)) {
            node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '}', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenText().equals(ELSE)){
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartBlk)) {
                node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected '{', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));

            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndBlk)) {
                node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected '}', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));
        }
    }

    private static void while_loop(TreeNode node){
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        node.addTreeNode(i_expr());

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartBlk)) {
            node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '{', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndBlk)) {
            node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '}', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));
    }

    private static void for_loop(TreeNode node){
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        asmt(node.addTreeNode(new State(State.stateType.ASMT)));

        node.addTreeNode(i_expr());

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        r_asmt(node.addTreeNode(new State(State.stateType.R_ASMT)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartBlk)) {
            node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '{', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndBlk)) {
            node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '}', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));
    }

    private static void functDec(TreeNode node)
    {
        ValidType returnType = null;
        //Add the function type
        if(isTokenTypeId(tokenStream.get(tokenIndex)))
        {
            node.addTreeNode(new State(State.stateType.TYPE, tokenStream.get(tokenIndex), tokenIndex));
            //Find return type
            returnType = getTypeFromString(tokenStream.get(tokenIndex).getTokenText());
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected a Type, got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        String functionName = null;
        //Add the function name
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID))
        {
            functionName = tokenStream.get(tokenIndex).getTokenText();
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected an ID function name, got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        //Create scope and add paremeters
        Reference funcScope = globalScope.addReference(functionName, returnType);
        funcScope.setEndIndex(tokenStream.size());
        if(isTokenTypeId(tokenStream.get(tokenIndex))) {
            p_list(node.addTreeNode(new State(State.stateType.P_LIST)), funcScope);
        }

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartBlk)) {
            node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '{', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        // f_stmt
        funcScope.setStartIndex(tokenIndex);
        f_stmt(node.addTreeNode(new State(State.stateType.F_STMT)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndBlk)) {
            node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '}', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        funcScope.endRecentReference(tokenIndex-1);
    }

    /**
     *  Deals with parsing stmts within a function along with the return value
     * @param node the node to branch off of
     */
    private static void f_stmt(TreeNode node) {
        Token token = tokenStream.get(tokenIndex);

        // Checks if is return stmt
        if (tokenStream.get(tokenIndex).getTokenText().equals(RETURN)) {
            node.addTreeNode(new State(State.stateType.RETURN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;

            expr(node.addTreeNode(new State(State.stateType.EXPR)));

            //Add end statement
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            // Recursive call
            f_stmt(node.addTreeNode(new State(State.stateType.F_STMT)));
        }
        else if (token.getTokenType() != TokenType.EndBlk) {
            stmt(node.addTreeNode(new State(State.stateType.STMT)));

            // Recursive call
            f_stmt(node.addTreeNode(new State(State.stateType.F_STMT)));
        }
        // Adds an epsilon otherwise
        else{
            //globalScope.endRecentReference(tokenIndex);

            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }

    /**
     * Deals with the parsing of parameters in a function definition
     * @param node the node to parse
     */
    private static void p_list(TreeNode node, Reference funcScope){
        // Checks if the EndParen is next. If not, recursive call.
        if(isTokenTypeId(tokenStream.get(tokenIndex))) {

            //Add type
            node.addTreeNode(new State(State.stateType.TYPE, tokenStream.get(tokenIndex), tokenIndex));
            ValidType varType = getTypeFromString(tokenStream.get(tokenIndex).getTokenText());
            tokenIndex++;

            //Can't be void
            if(varType == ValidType.Void) {
                LogError.log(LogError.ErrorType.SYNTAX, "Type 'Void' cannot be used as a variable type",
                        tokenStream.get(tokenIndex));
            }

            // Check for ID
            if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)) {
                node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                //Add parameter to scope
                addVariableToScope(varType, funcScope);
                tokenIndex++;
            }
            else {
                LogError.log(LogError.ErrorType.RUNTIME, "Unknown id '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                        tokenStream.get(tokenIndex));
            }


            // Check for COMMA
            if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Comma)) {
                node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;

                //Recurse
                p_list(node.addTreeNode(new State(State.stateType.P_LIST)), funcScope);
            }
        }
    }

    /**
     * Deals with the parsing of parameters being passed into a function
     * @param node the node to parse
     */
    private static void fc_p_list(TreeNode node){
        // Checks if the EndParen is next. If not, create an expression
        if(tokenStream.get(tokenIndex).getTokenType() != TokenType.EndParen) {

            // Check for EXPRESSION
            expr(node);

            // Check for COMMA
            if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Comma)) {
                node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;

                //Recurse
                fc_p_list(node.addTreeNode(new State(State.stateType.P_LIST)));
            }
        }
    }

    private static TreeNode functCall()
    {
        TreeNode node = new TreeNode(new State(State.stateType.F_CALL));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)) {
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected an ID <name>, got " +
                    tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
            tokenStream.get(tokenIndex));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        fc_p_list(node.addTreeNode(new State(State.stateType.P_LIST)));

        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));
        return node;
    }

    /**
     *  Deals with parsing a stmt into its various parts
     * @param node the node to branch off of
     */
    private static void stmt(TreeNode node) {
        String tokenText = tokenStream.get(tokenIndex).getTokenText();

        // If it is valid
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID)
        {
            // The print statement
            if (tokenStream.get(tokenIndex).getTokenText().equals(PRINT))
            {
                print(node.addTreeNode(new State(State.stateType.PRINT)));
            }

            // The if statement
            else if (tokenStream.get(tokenIndex).getTokenText().equals(IF))
            {
                if_stmt(node.addTreeNode(new State(State.stateType.IF_STMT)));
            }

            // The while statement
            else if (tokenStream.get(tokenIndex).getTokenText().equals(WHILE))
            {
                while_loop(node.addTreeNode(new State(State.stateType.WHILE_LOOP)));
            }

            // The for statement
            else if (tokenStream.get(tokenIndex).getTokenText().equals(FOR))
            {
                for_loop(node.addTreeNode(new State(State.stateType.FOR_LOOP)));

            }

            // The assignment stmt
            else if (tokenIndex + 2 < tokenStream.size() && tokenStream.get(tokenIndex + 2).getTokenType() == TokenType.Assign)
            {
                asmt(node.addTreeNode(new State(State.stateType.ASMT)));
            }
            // The reassignment stmt
            else if (tokenIndex + 1 < tokenStream.size() && tokenStream.get(tokenIndex + 1).getTokenType() == TokenType.Assign)
            {
                r_asmt(node.addTreeNode(new State(State.stateType.R_ASMT)));
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }

            // The Function Declaration
            else if (isTokenTypeId(tokenStream.get(tokenIndex)))
            {
                functDec(node);
            }
            // The Function Call
            else if (isFunctCall(tokenIndex))
            {
                node.addTreeNode(functCall());
                if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
                    node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                }
                else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                                tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                        tokenStream.get(tokenIndex));
            }
            // Error
            else
            {
                LogError.log(LogError.ErrorType.SYNTAX, "Invalid statement", tokenStream.get(tokenIndex));
            }
        }
        // The expression statement
        else if(Character.isLowerCase(tokenText.charAt(0)) ||
                Character.isDigit(tokenText.charAt(0)) ||
                tokenText.equals(CONCAT)|| tokenText.equals(CHARAT) ||
                tokenText.equals("-") || tokenText.equals("+") || tokenText.equals(".") ||
                tokenStream.get(tokenIndex).getTokenType().equals(TokenType.String))
        {
            //Add expression
            expr(node.addTreeNode(new State(State.stateType.EXPR)));

            //Add end statement
            if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt))
            {
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            } else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                            tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                    tokenStream.get(tokenIndex));
        }

        // Error
        else{
            LogError.log(LogError.ErrorType.SYNTAX, "Invalid statement" , tokenStream.get(tokenIndex));
        }
    }

    private static void b_stmt_list(TreeNode node){
        b_stmt(node.addTreeNode(new State(State.stateType.B_STMT)));
        if(tokenStream.get(tokenIndex).getTokenType()!=TokenType.EndBlk){
            b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));
        }
    }

    private static void b_stmt(TreeNode node){
        if(tokenStream.get(tokenIndex).getTokenText().equals(PRINT)){
            print(node.addTreeNode(new State(State.stateType.PRINT)));
        }

        else if(tokenStream.get(tokenIndex).getTokenText().equals(IF)){
            if_stmt(node.addTreeNode(new State(State.stateType.IF_STMT)));
        }

        else if(tokenStream.get(tokenIndex).getTokenText().equals(WHILE)){
            while_loop(node.addTreeNode(new State(State.stateType.WHILE_LOOP)));
        }

        else if(tokenStream.get(tokenIndex).getTokenText().equals(FOR)){
            for_loop(node.addTreeNode(new State(State.stateType.FOR_LOOP)));
        }

        else if(tokenStream.get(tokenIndex+1).getTokenType()==TokenType.Assign){
            r_asmt(node.addTreeNode(new State(State.stateType.R_ASMT)));
            if(tokenStream.get(tokenIndex).getTokenType()==TokenType.EndStmt) {
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
            }
            else{
                LogError.log(LogError.ErrorType.SYNTAX, "Invalid statement", tokenStream.get(tokenIndex));
            }
            tokenIndex++;
        }

        else{
            expr(node.addTreeNode(new State(State.stateType.EXPR)));
            if(tokenStream.get(tokenIndex).getTokenType()==TokenType.EndStmt) {
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
            }
            else{
                LogError.log(LogError.ErrorType.SYNTAX, "Invalid statement", tokenStream.get(tokenIndex));
            }
            tokenIndex++;
        }
    }

    private static void r_asmt(TreeNode node)
    {
        if (tokenStream.get(tokenIndex).getTokenType() == TokenType.ID)
        {
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        } else LogError.log(LogError.ErrorType.SYNTAX, "Expected a variable name, got " +
                        tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                tokenStream.get(tokenIndex));

        if (tokenStream.get(tokenIndex).getTokenType() == TokenType.Assign)
        {
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        } else LogError.log(LogError.ErrorType.SYNTAX, "Expected '=', got " +
                        tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                tokenStream.get(tokenIndex));

        expr(node.addTreeNode(new State(State.stateType.EXPR)));
    }

    private static void expr(TreeNode node) {
        checkSize();

        if (globalScope.getReferenceWithName(tokenStream.get(tokenIndex).getTokenText())!=null &&
                tokenStream.get(tokenIndex).getTokenType()!=TokenType.String) {
            node.addTreeNode(functCall());
        }

        // If the first token is an integer or a -integer/+integer (in i_expr)
        // Or if there is a relative op at tokenIndex + 1
        else if(isTokenTypeInteger(tokenIndex) || isTokenRelOp(tokenStream.get(tokenIndex+1))){
            node.addTreeNode(i_expr());
        }

        // The expression starting with a str_literal (in s_expr)
        // The expression starting with concat (in s_expr)
        // The expression starting with charAt (in s_expr)
        else if(isTokenTypeString(tokenStream.get(tokenIndex))) {
            node.addTreeNode(s_expr());
        }

        // If the first token is a double or a -double/+double (in d_expr)
        else if(isTokenTypeDouble(tokenIndex)){
            node.addTreeNode(d_expr());
        }

        // If it is an id
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID){

            if(globalScope.hasSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex))
            {
                // If the id is an integer or has a relational op
                if(globalScope.getScopedSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex).getType()==
                        ValidType.Integer ||
                        isTokenRelOp(tokenStream.get(tokenIndex+1))){
                    node.addTreeNode(i_expr());
                }
                // If the id is a double
                else if(globalScope.getScopedSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex).getType()==
                        ValidType.Double){
                    node.addTreeNode(d_expr());
                }
                //Strings
                else{
                    node.addTreeNode(s_expr());
                }
            }
            //Variable does not exists
            else LogError.log(LogError.ErrorType.RUNTIME, "Unknown variable '" +
                            tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));
        }

        //Invalid expression
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected an Expression, got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));
    }

    private static void print(TreeNode node) {
        // Adds the print terminal
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        checkSize();

        // Adds the start parenthesis
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        // Adds the expression to print
        expr(node.addTreeNode(new State(State.stateType.EXPR)));

        // Adds the end parenthesis
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));

        // Adds the semicolon
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                        tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                tokenStream.get(tokenIndex));
    }

    /**
     * Parses the assignment statment
     * @param node the node to build off from
     */
    private static void asmt(TreeNode node) {

        //Get this variable's type
        ValidType varType = getTypeFromString(tokenStream.get(tokenIndex).getTokenText());
        if(varType == ValidType.Void) {
            LogError.log(LogError.ErrorType.SYNTAX, "Type 'Void' cannot be used as a variable type",
                    tokenStream.get(tokenIndex));
        }

        // asmt_stmt -> <type>  <id > = <expr ><end_statement>
        // Adds a terminal with the Type's name with the corresponding token
        node.addTreeNode(new State(State.stateType.TYPE, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        // Adds the id and variable to the scope
        Symbol s = null;
        if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)) {
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            addVariableToScope(varType, globalScope);
            tokenIndex++;
        } else LogError.log(LogError.ErrorType.SYNTAX, "Expected a variable name, got " +
                        tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                tokenStream.get(tokenIndex));

        // Adds the = terminal
        if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Assign)) {
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        } else LogError.log(LogError.ErrorType.SYNTAX, "Expected '=', got " +
                        tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                tokenStream.get(tokenIndex));

        Reference r=globalScope.getReferenceWithName(tokenStream.get(tokenIndex).getTokenText());

        if(r==null) {
            // Adds the expr
            switch (varType) {
                case Double:
                    node.addTreeNode(d_expr());
                    break;
                case Integer:
                    node.addTreeNode(i_expr());
                    break;
                case String:
                    node.addTreeNode(s_expr());
                    break;
                default:
                    break;
            }
            // Adds the end_statement
            if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            } else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                            tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                    tokenStream.get(tokenIndex));
        }

        else{
            node.addTreeNode(functCall());
            // Adds the end_statement
            if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                            tokenStream.get(tokenIndex).getTokenType() + " '" + tokenStream.get(tokenIndex).getTokenText() + "'",
                    tokenStream.get(tokenIndex));
        }
    }


    ///NUMBERS ---------------------------------------------------------------

    /**
     * Return a TreeNode of an double (w/ or w/o a sign) (increments tokenIndex)
     * @param numType the statetype of the type of number to find
     * @param numTokenType the tokentype of the number to find
     * @return TreeNode of that double, null if none was found
     */
    private static TreeNode findNumNode(State.stateType numType, TokenType numTokenType) {
        TreeNode numNode = new TreeNode(new State(numType));
        boolean foundSign = false;

        if(isTokenSign(tokenStream.get(tokenIndex)))
        {
            numNode.addTreeNode(new State(State.stateType.SIGN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            foundSign = true;
        }
        if(tokenStream.get(tokenIndex).getTokenType().equals(numTokenType))
        {
            numNode.addTreeNode(new State(State.stateType.DIGIT, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            return numNode;
        }

        if(foundSign)
            tokenIndex--;

        return null;
    }

    /**
     * Return a TreeNode of either a number or id (increments tokenIndex)
     * @param numExprType the statetype of the type of number_expr to find
     * @param numType the statetype of the type of number to find
     * @param numTokenType the tokentype of the number to find
     * @param addIExprToID add an extra numType_expr node to an id if one is found
     * @param addExprToNum add an extra numType_expr node to a num if one is found
     * @return TreeNode of that number or id, null if none was found
     */
    private static TreeNode findNumOrIDNode(State.stateType numExprType, State.stateType numType, TokenType numTokenType,
                                            boolean addIExprToID, boolean addExprToNum)
    {
        TreeNode first = null;
        boolean idFound = false;
        Token token = tokenStream.get(tokenIndex);
        if(token.getTokenType().equals(TokenType.ID))
        {
            //Check for function call first
            if(isFunctCall(tokenIndex))
            {
                first = new TreeNode(new State(numType));
                first.addTreeNode(functCall());
            }
            //Normal ID instead
            else
            {
                first = new TreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                idFound = true;
                tokenIndex++;
            }
        }
        else first = findNumNode(numType, numTokenType);

        //Add optional d_exprs
        if((addIExprToID && idFound) || (addExprToNum && !idFound))
        {
            TreeNode temp = new TreeNode(new State(numExprType));
            temp.addTreeNode(first);
            first = temp;
        }

        return first;
    }


    ///DOUBLES ---------------------------------------------------------------

    /**
     * Evaluate the upcoming tokens in the tokenstream to see if it is double math and evaluate it
     * @return TreeNode finished expression
     */
    private static TreeNode d_expr() {
        //Local expression node to evaluate into
        TreeNode root = new TreeNode(new State (State.stateType.D_EXPR));

        //Find what the first token is
        TreeNode first = findNumOrIDNode(State.stateType.D_EXPR, State.stateType.DBL, TokenType.Double,
                false, false);

        if(first != null)
        {
            if(isTokenOp(tokenStream.get(tokenIndex)))
            {
                //Add an extra d_expr to the ID in this case
                if(first.getState().getState() == State.stateType.ID)
                {
                    TreeNode temp = new TreeNode(new State(State.stateType.D_EXPR));
                    temp.addTreeNode(first);
                    first = temp;
                }
                return d_exprFollowingComponent(root, first);
            }
            else
            {
                root.addTreeNode(first);
                return root;
            }
        }
        else numExprError("Double");

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Evaluate the following component of an d_expr.
     * @param root The current root expression node we are evaluating
     * @param first The first part of this statement
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode d_exprFollowingComponent(TreeNode root, TreeNode first)
    {
        //Add the operator to the D_EXPR
        TreeNode op = new TreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        //Find what the following token is
        TreeNode second = findNumOrIDNode(State.stateType.D_EXPR, State.stateType.DBL, TokenType.Double,
                true, false);

        //Check for nested statements or add
        if(second != null)
        {
            root.addTreeNode(first);
            root.addTreeNode(op);
            root.addTreeNode(second);
            //If there is another operator
            if(isTokenOp(tokenStream.get(tokenIndex)))
                return d_exprFollowingComponent(new TreeNode(new State(State.stateType.D_EXPR)), root);
            //If we are at the end
            return root;
        }
        else numExprError("Double");

        return null; //fail (should never reach here as we are exiting on errors)
    }


    ///INTEGERS ---------------------------------------------------------------

    private static TreeNode i_expr() {
        TreeNode root = null;

        //Get what symbol type this ID is
        ValidType varType = ValidType.Integer;
        Token token = tokenStream.get(tokenIndex);
        if(token.getTokenType() == TokenType.ID)
        {
            //ID is a function call
            if(isFunctCall(tokenIndex)) {
                Reference r = globalScope.getReferenceWithName(token.getTokenText());
                if(r != null) {
                    varType = r.getReturnType();
                }
                else LogError.log(LogError.ErrorType.RUNTIME, "Unknown function '" +
                                tokenStream.get(tokenIndex).getTokenText() + "'",
                        tokenStream.get(tokenIndex));
            }
            //ID is a variable
            else
            {
                if (globalScope.hasSymbol(token.getTokenText(), tokenIndex))
                    varType = globalScope.getScopedSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex).getType();
                    //Variable does not exists
                else LogError.log(LogError.ErrorType.RUNTIME, "Unknown variable '" +
                                tokenStream.get(tokenIndex).getTokenText() + "'",
                        tokenStream.get(tokenIndex));
            }
        }

        //Find string relations
        if(isTokenTypeString(tokenStream.get(tokenIndex)) || varType == ValidType.String) {
            TreeNode first = s_expr();
            if (first != null) {
                //Check for relative op and another s_expr
                if (isTokenRelOp(tokenStream.get(tokenIndex))) {
                    TreeNode relOp = new TreeNode(new State(State.stateType.REL_OP, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                    TreeNode second = s_expr();
                    if (second != null) {
                        root = new TreeNode(new State(State.stateType.I_EXPR));
                        root.addTreeNode(first);
                        root.addTreeNode(relOp);
                        root.addTreeNode(second);
                    }
                }
            }
        }

        //Find double relations
        else if(isTokenTypeDouble(tokenIndex) || varType == ValidType.Double) {
            TreeNode first = d_expr();
            if (first != null) {
                //Check for relative op and another d_expr
                if (isTokenRelOp(tokenStream.get(tokenIndex))) {
                    TreeNode relOp = new TreeNode(new State(State.stateType.REL_OP, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                    TreeNode second = findNumOrIDNode(State.stateType.D_EXPR, State.stateType.DBL, TokenType.Double,
                            true, true);
                    if (second != null) {
                        root = new TreeNode(new State(State.stateType.I_EXPR));
                        root.addTreeNode(first);
                        root.addTreeNode(relOp);
                        root.addTreeNode(second);
                    }
                }
            }
        }

        //Integers are special because normal integer operations still need to occur
        //Try normal integer operations and integer relational operatons
        else
        {
            TreeNode first = i_exprNormal();
            if (first != null) {
                //Check for relative op and another i_expr
                if (isTokenRelOp(tokenStream.get(tokenIndex))) {
                    root = i_exprIntRelationFollowingComponent(new TreeNode(new State(State.stateType.I_EXPR)), first);
                }
                //Didn't find a relational operation
                else root = first;
            }
            //Found nothing at all
            else return null;
        }

        //If there more math to do after we get initial operation
        while(isTokenOp(tokenStream.get(tokenIndex)) || isTokenRelOp(tokenStream.get(tokenIndex)))
        {
            // check if there's any integer math after dbl or string
            if (isTokenOp(tokenStream.get(tokenIndex)))
            {
                TreeNode newRoot = new TreeNode(new State(State.stateType.I_EXPR));
                TreeNode nestedMath = i_exprFollowingComponent(newRoot, root);
                if (nestedMath != null)
                    root = nestedMath;
            }
            if (isTokenRelOp(tokenStream.get(tokenIndex)))
            {
                TreeNode newRoot = new TreeNode(new State(State.stateType.I_EXPR));
                TreeNode nestedRelations = i_exprIntRelationFollowingComponent(newRoot, root);
                if (nestedRelations != null)
                    root = nestedRelations;
            }
        }
        return root;
    }

    /**
     * Evaluate the following components of an i_expr relational operator.
     * @param root The current root expression node we are evaluating
     * @param first The first part of this statement
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode i_exprIntRelationFollowingComponent(TreeNode root, TreeNode first)
    {
        TreeNode relOp = new TreeNode(new State(State.stateType.REL_OP, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;
        root = new TreeNode(new State(State.stateType.I_EXPR));

        TreeNode second = findNumOrIDNode(State.stateType.I_EXPR, State.stateType.INT,  TokenType.Integer,
                true, true);
        if (second != null) {
            root.addTreeNode(first);
            root.addTreeNode(relOp);
            root.addTreeNode(second);
            if(isTokenRelOp(tokenStream.get(tokenIndex)))
                return i_exprIntRelationFollowingComponent(new TreeNode(new State(State.stateType.I_EXPR)), root);
            return root;
        }

        return null;
    }

    /**
     * Evaluate the upcoming tokens in the tokenstream to see if it is integer math and evaluate it
     * @return TreeNode finished expression
     */
    private static TreeNode i_exprNormal() {
        //Local expression node to evaluate into
        TreeNode root = new TreeNode(new State (State.stateType.I_EXPR));

        //Find what the first token is
        TreeNode first = findNumOrIDNode(State.stateType.I_EXPR, State.stateType.INT,  TokenType.Integer,
                false, false);

        if(first != null)
        {
            if(isTokenOp(tokenStream.get(tokenIndex)))
            {
                //Add an extra i_expr to the ID in this case
                if(first.getState().getState() == State.stateType.ID)
                {
                    TreeNode temp = new TreeNode(new State(State.stateType.I_EXPR));
                    temp.addTreeNode(first);
                    first = temp;
                }
                return i_exprFollowingComponent(root, first);
            }
            else
            {
                root.addTreeNode(first);
                return root;
            }
        }
        else numExprError("Integer");

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Evaluate the following components of an i_exprNormal.
     * @param root The current root expression node we are evaluating
     * @param first The first part of this statement
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode i_exprFollowingComponent(TreeNode root, TreeNode first)
    {
        //Add the operator to the I_EXPR
        TreeNode op = new TreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        //Find what the following token is
        TreeNode second = findNumOrIDNode(State.stateType.I_EXPR, State.stateType.INT,  TokenType.Integer,
                true, false);

        //Check for nested statements or add
        if(second != null)
        {
            root.addTreeNode(first);
            root.addTreeNode(op);
            root.addTreeNode(second);
            //If there is another operator
            if(isTokenOp(tokenStream.get(tokenIndex)))
                return i_exprFollowingComponent(new TreeNode(new State(State.stateType.I_EXPR)), root);
            //If we are at the end
            return root;
        }
        else numExprError("Integer");

        return null; //fail (should never reach here as we are exiting on errors)
    }


    ///STRINGS ---------------------------------------------------------------

    private static void str_literal(TreeNode node) {
        // Adds the string
        node.addTreeNode(new State(State.stateType.STR, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;
    }

    private static TreeNode s_expr() {

        String tokenText=tokenStream.get(tokenIndex).getTokenText();
        //Local expression node to evaluate into
        TreeNode parentExprNode = new TreeNode(new State (State.stateType.S_EXPR));

        // s_expr -> <str_literal>
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.String)){
            str_literal(parentExprNode.addTreeNode(new State(State.stateType.STR_LITERAL)));
            return parentExprNode;
        }

        // s_expr -> concat <start_paren > <s_expr >, <s_expr > <end_paren>
        else if(tokenText.equals(CONCAT)){

            // Adds the concat terminal
            parentExprNode.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;

            // The start_paren terminal
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
                parentExprNode.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            // Adds the first s_expr
            parentExprNode.addTreeNode(s_expr());

            // Adds the comma terminal
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Comma)) {
                parentExprNode.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected ',', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            // Adds the second s_expr
            parentExprNode.addTreeNode(s_expr());

            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
                parentExprNode.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            return parentExprNode;
        }

        // s_expr -> charAt <start_paren > <s_expr >, <i_expr > <end_paren>
        else if(tokenText.equals(CHARAT)){

            // The charAt terminal
            parentExprNode.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;

            // Adds the start paren
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.StartParen)) {
                parentExprNode.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected '(', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            // Adds the s_expr
            parentExprNode.addTreeNode(s_expr());

            // Adds the comma terminal
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Comma)) {
                parentExprNode.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected ',', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            // Adds the i_expr
            parentExprNode.addTreeNode(i_expr());

            // Adds the end paren
            if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndParen)) {
                parentExprNode.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            else LogError.log(LogError.ErrorType.SYNTAX, "Expected ')', got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));

            return parentExprNode;
        }

        // s_expr-> <id>
        // s_expr-> str-> funcCall
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID){
            if(isFunctCall(tokenIndex))
            {
                TreeNode str = parentExprNode.addTreeNode(new State(State.stateType.STR));
                str.addTreeNode(functCall());
            }
            else {
                parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }
            return parentExprNode;
        }

        // Error
        else{
            LogError.log(LogError.ErrorType.SYNTAX, "Expected a String, got " +
                            tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                    tokenStream.get(tokenIndex));
        }

        return null;
    }


    ///HELPERS ---------------------------------------------------------------

    /**
     * Helper method for erroring in a i_expr or d_expr (cuts down on duplicate code)
     * * @param type The type of expression this is (Integer or Double)
     */
    private static void numExprError(String type)
    {
        Token t = tokenStream.get(tokenIndex);

        //Checks that if 3 signs in a row that it reports that the 3rd sign is the error not the 2nd sign
        //EX: '3 + + -5' the error should report the '-' as wrong, not the '+'
        if(isTokenSign(tokenStream.get(tokenIndex)) && !tokenStream.get(tokenIndex+1).getTokenType().equals(TokenType.ID))
        {
            t = tokenStream.get(tokenIndex+1);
        }

        LogError.log(LogError.ErrorType.SYNTAX, "Expected an "+type+" or ID, got "+t.getTokenType()+" '"+t.getTokenText()+"'", t);
    }

    /**
     * Check if a token is any of the operators
     * @param tok Token to check
     * @return boolean if it is an operator token
     */
    private static boolean isTokenOp(Token tok)
    {
        return tok.getTokenType()==TokenType.Minus ||
                tok.getTokenType()==TokenType.Plus ||
                tok.getTokenType()==TokenType.Mult ||
                tok.getTokenType()==TokenType.Divide ||
                tok.getTokenType()==TokenType.Power;

    }

    /**
     * Check if a token is any of the relational operators
     * @param tok Token to check
     * @return boolean if it is an relational operator token
     */
    private static boolean isTokenRelOp(Token tok)
    {
        return tok.getTokenType()==TokenType.Eq ||
                tok.getTokenType()==TokenType.NotEq ||
                tok.getTokenType()==TokenType.Greater ||
                tok.getTokenType()==TokenType.GreaterEq ||
                tok.getTokenType()==TokenType.Less ||
                tok.getTokenType()==TokenType.LessEq;
    }

    private static boolean isTokenTypeString(Token tok) {
        return (tok.getTokenType().equals(TokenType.String) ||
                tok.getTokenText().equals(CONCAT) ||
                tok.getTokenText().equals(CHARAT));
    }

    private static boolean isTokenTypeInteger(int index) {
        return (tokenStream.get(index).getTokenType()==TokenType.Integer ||
                ((tokenStream.get(index).getTokenType()==TokenType.Minus ||
                        tokenStream.get(index).getTokenType()==TokenType.Plus) &&
                        tokenStream.get(index+1).getTokenType()==TokenType.Integer));
    }

    private static boolean isTokenTypeDouble(int index) {
        return (tokenStream.get(index).getTokenType()==TokenType.Double ||
                ((tokenStream.get(index).getTokenType()==TokenType.Minus ||
                        tokenStream.get(index).getTokenType()==TokenType.Plus) &&
                        tokenStream.get(index+1).getTokenType()==TokenType.Double));
    }

    /**
     * Check if a token is either of the two signs
     * @param tok Token to check
     * @return boolean if it is a sign
     */
    private static boolean isTokenSign(Token tok)
    {
        return tok.getTokenType()==TokenType.Minus ||
                tok.getTokenType()==TokenType.Plus;
    }

    /**
     * Check if a token is an type ID
     * @param tok Token to check
     * @return boolean if it is a token id
     */
    private static boolean isTokenTypeId(Token tok)
    {
        if(tok.getTokenType() == TokenType.ID) {
            String tokenText = tok.getTokenText();
            return tokenText.equals(STRING) || tokenText.equals(INTEGER)
                    || tokenText.equals(DOUBLE) || tokenText.equals(VOID);
        }
        return false;
    }

    /**
     * Check if a function call is starting
     * @param index Token to start the check at
     * @return boolean if a function call is starting
     */
    private static boolean isFunctCall(int index)
    {
        return tokenStream.get(index).getTokenType() == TokenType.ID &&
            tokenStream.get(index+1).getTokenType() == TokenType.StartParen;
    }

    /**
     * Parse a string for a valid type
     * @param text text to parse
     * @return type found
     */
    private static ValidType getTypeFromString(String text)
    {
        if (text.equals(DOUBLE))
            return ValidType.Double;
        else if (text.equals(INTEGER))
            return ValidType.Integer;
        else if (text.equals(STRING))
            return ValidType.String;
        else if (text.equals(VOID))
            return ValidType.Void;

        //Error
        LogError.log(LogError.ErrorType.SYNTAX, "Unknown type '" + text + "'",
                tokenStream.get(tokenIndex));
        return null;
    }

    /**
     * Add a variable to a scope
     * @param varType type the variable is
     * @param scope scope to add it to
     */
    private static void addVariableToScope(ValidType varType, Reference scope)
    {
        Symbol s = null;
        //Generate symbol
        switch (varType) {
            case Double:
                s = new Symbol<Double>(ValidType.Double, tokenStream.get(tokenIndex).getTokenText());
                break;
            case Integer:
                s = new Symbol<Integer>(ValidType.Integer, tokenStream.get(tokenIndex).getTokenText());
                break;
            case String:
                s = new Symbol<String>(ValidType.String, tokenStream.get(tokenIndex).getTokenText());
                break;
            default:
                break;
        }
        //Add the variable to scope
        Reference.addSymbol(scope, s, tokenIndex, tokenStream.get(tokenIndex).getTokenText());
    }

    private static void checkSize(){
        if(tokenIndex>=tokenStream.size()){
            LogError.log(LogError.ErrorType.SYNTAX, "Unexpected EoF",
                    new Token("$$", TokenType.EoF, tokenStream.get(tokenIndex-1).getLineNum(),
                            tokenStream.get(tokenIndex-1).getColumnEnd()+1,
                            tokenStream.get(tokenIndex-1).getColumnEnd()+1));
        }
    }
}