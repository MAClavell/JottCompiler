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

    /**
     *  Deals with parsing a stmt into its various parts
     * @param node the node to branch off of
     */
    private static void stmt(TreeNode node) {
        // If it is valid
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID) {

            String tokenText=tokenStream.get(tokenIndex).getTokenText();

            // The print statement
            if (tokenStream.get(tokenIndex).getTokenText().equals(PRINT)) {
                print(node.addTreeNode(new State(State.stateType.PRINT)));
            }

            // The if statement
            else if(tokenStream.get(tokenIndex).getTokenText().equals(IF)){
                node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                expr(node.addTreeNode(new State(State.stateType.EXPR)));
                node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));
                node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;

                if(tokenStream.get(tokenIndex).getTokenText().equals(ELSE)){
                    node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                    node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                    b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));
                    node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                }
            }

            // The while statement
            else if(tokenStream.get(tokenIndex).getTokenText().equals(WHILE)){
                node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                //i_expr(node.addTreeNode(new State(State.stateType.I_EXPR)), false);
                node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));
                node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }

            // The for statement
            else if(tokenStream.get(tokenIndex).getTokenText().equals(FOR)){
                node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                asmt(node.addTreeNode(new State(State.stateType.ASMT)));
                //i_expr(node.addTreeNode(new State(State.stateType.I_EXPR)), false);
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                r_asmt(node.addTreeNode(new State(State.stateType.R_ASMT)));
                node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                node.addTreeNode(new State(State.stateType.START_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                b_stmt_list(node.addTreeNode(new State(State.stateType.B_STMT_LIST)));
                node.addTreeNode(new State(State.stateType.END_BLCK, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;

            }

            // The assignment stmt
            else if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID) &&
                    tokenIndex+2 < tokenStream.size() && tokenStream.get(tokenIndex+2).getTokenType()==TokenType.Assign) {
                asmt(node.addTreeNode(new State(State.stateType.ASMT)));
            }

            // The reassignment stmt
            else if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)&&
                    tokenIndex+1<tokenStream.size()&&tokenStream.get(tokenIndex+1).getTokenType()==TokenType.Assign){
                r_asmt(node.addTreeNode(new State(State.stateType.R_ASMT)));
                node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
            }

            // The expression statement
            else if(Character.isLowerCase(tokenText.charAt(0)) ||
                    Character.isDigit(tokenText.charAt(0)) ||
                    tokenText.equals(CONCAT)|| tokenText.equals(CHARAT) ||
                    tokenText.equals("-") || tokenText.equals("+") || tokenText.equals(".") ||
                    tokenStream.get(tokenIndex).getTokenType().equals(TokenType.String)) {
                //Add expression
                expr(node.addTreeNode(new State(State.stateType.EXPR)));

                //Add end statement
                if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.EndStmt)) {
                    node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                }
                else LogError.log(LogError.ErrorType.SYNTAX, "Expected ';', got " +
                                tokenStream.get(tokenIndex).getTokenType()+" '"+tokenStream.get(tokenIndex).getTokenText()+"'",
                        tokenStream.get(tokenIndex));
            }

            // Error
            else{
                LogError.log(LogError.ErrorType.SYNTAX, "Invalid statement", tokenStream.get(tokenIndex));
            }
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
        }
    }

    private static void r_asmt(TreeNode node){
        node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;
        expr(node.addTreeNode(new State(State.stateType.EXPR)));
    }

    private static void expr(TreeNode node) {
        checkSize();

        // The expression starting with a str_literal (in s_expr)
        // The expression starting with concat (in s_expr)
        // The expression starting with charAt (in s_expr)
        if(isTokenTypeString(tokenStream.get(tokenIndex))) {
            node.addTreeNode(s_expr());
        }

        // If the first token is an integer or a -integer/+integer (in i_expr)
        else if(isTokenTypeInteger(tokenIndex)){
            node.addTreeNode(i_expr());
        }

        // If the first token is a double or a -double/+double (in d_expr)
        else if(isTokenTypeDouble(tokenIndex)){
            node.addTreeNode(d_expr(false));
        }

        // If it is an id
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID){

            if(globalScope.hasSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex))
            {
                // If the id is a double
                if(globalScope.getScopedSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex).getType()==
                        Symbol.variableType.DOUBLE){
                    node.addTreeNode(d_expr(false));
                }

                // If the id is an integer
                else if(globalScope.getScopedSymbol(tokenStream.get(tokenIndex).getTokenText(), tokenIndex).getType()==
                        Symbol.variableType.INTEGER){
                    node.addTreeNode(i_expr());
                }

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

        String tokenText = tokenStream.get(tokenIndex).getTokenText();

        //Get this variable's type
        Symbol.variableType varType = null;
        if (tokenText.equals(DOUBLE))
            varType = Symbol.variableType.DOUBLE;
        else if (tokenText.equals(INTEGER))
            varType = Symbol.variableType.INTEGER;
        else if (tokenText.equals(STRING))
            varType = Symbol.variableType.STRING;
            // An error
        else
            LogError.log(LogError.ErrorType.RUNTIME, "Unknown type '" + tokenText + "'",
                    tokenStream.get(tokenIndex));

        // asmt_stmt -> <type>  <id > = <expr ><end_statement>
        // Adds a terminal with the Type's name with the corresponding token
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        // Adds the id
        if (tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)) {
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));

            //Generate symbol
            Symbol s = null;
            switch (varType) {
                case DOUBLE:
                    s = new Symbol<Double>(Symbol.variableType.DOUBLE, tokenStream.get(tokenIndex).getTokenText());
                    break;
                case INTEGER:
                    s = new Symbol<Integer>(Symbol.variableType.INTEGER, tokenStream.get(tokenIndex).getTokenText());
                    break;
                case STRING:
                    s = new Symbol<String>(Symbol.variableType.STRING, tokenStream.get(tokenIndex).getTokenText());
                    break;
                default:
                    break;
            }
            Reference.addSymbol(globalScope, s, tokenIndex, tokenStream.get(tokenIndex).getTokenText());

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

        // Adds the expr
        switch (varType) {
            case DOUBLE:
                node.addTreeNode(d_expr(false));
                break;
            case INTEGER:
                node.addTreeNode(i_expr());
                break;
            case STRING:
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

    /**
     * Check if the current token is a double (with a sign if it exists)
     *      and push it to the passed in tree node
     * @param node Node to push the double too
     * @return boolean if an double was found
     */
    private static boolean dbl(TreeNode node) {
        TreeNode dblNode = new TreeNode(new State(State.stateType.DBL));
        boolean foundSign = false;

        if(isTokenSign(tokenStream.get(tokenIndex)))
        {
            dblNode.addTreeNode(new State(State.stateType.SIGN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            foundSign = true;
        }
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Double))
        {
            dblNode.addTreeNode(new State(State.stateType.DIGIT, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            node.addTreeNode(dblNode); //add to the real node
            return true;
        }

        if(foundSign)
            tokenIndex--;
        return false;

    }

    /**
     * Evaluate the upcoming tokens in the tokenstream to see if it is an d_expr
     * This function will add it to the send in tree node if it is
     * @param isNestedExpr FALSE by default. Flag to see if we are evaluating a nested d_expr
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode d_expr(boolean isNestedExpr) {
        //Local expression node to evaluate into
        TreeNode parentExprNode = new TreeNode(new State (State.stateType.D_EXPR));

        //Go back an index if we are nested
        if(isNestedExpr)
            tokenIndex--;

        //Check first parameter to see if it's an ID (skip over if we are nested)
        if(isNestedExpr || tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)){
            //Lookahead to see if there's an operator
            if(isTokenOp(tokenStream.get(tokenIndex+1))) {
                //Since this IS a math expression, add the ID as a SEPARATE D_EXPR
                if(!isNestedExpr) {
                    TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.D_EXPR));
                    idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                }
                tokenIndex++;

                //Find the third component of the D_EXPR
                return d_exprThirdComponent(parentExprNode);
            }
            //Since this is NOT a math expression, add the ID to THIS D_EXPR
            else {
                parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                return parentExprNode; //hit the REAL end of an D_EXPR
            }
        }

        //Check first parameter to see if it's a Double (skip over if we are nested)
        //Automatically adds integer to the node if one exists
        else if(isNestedExpr || dbl(parentExprNode)) {
            //See if there's an operator
            if(isTokenOp(tokenStream.get(tokenIndex))) {
                //IS a math expression
                //Find the third component of the D_EXPR
                return d_exprThirdComponent(parentExprNode);
            }
            //NOT a math expression
            else {
                return parentExprNode; //hit the REAL end of an D_EXPR
            }
        }
        else numExprError("Double");

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Evaluate the third component of an d_expr.
     * NEVER CALL THIS FUNCTION OUTSIDE OF d_expr()
     * @param parentExprNode The current expression node we are evaluating
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode d_exprThirdComponent(TreeNode parentExprNode)
    {
        //Add the operator to the D_EXPR
        parentExprNode.addTreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        //Find what the third token is
        boolean foundDouble = dbl(parentExprNode);
        boolean foundID = foundDouble ? false : tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID);
        if(foundID)
        {
            //Add ID to the D_EXPR
            TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.D_EXPR));
            idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }

        //Check for nested statements or add
        if(foundDouble || foundID)
        {
            //Check if there is ANOTHER operator, meaning we are nested
            if (isTokenOp(tokenStream.get(tokenIndex)))
            {
                //Create and run nested expression
                TreeNode res = d_expr(true);
                //Since we nested, we HAVE to add the previous expression to the FRONT of the D_EXPR
                //This is just how the grammar works
                TreeNode attachTo = res;
                while(attachTo.getChildren().get(0).getState().getState() != State.stateType.OP)
                    attachTo = attachTo.getChildren().get(0);

                attachTo.addTreeNodeToFront(parentExprNode);
                return res;
            }
            //Expression is finish, add it and return
            else {
                return parentExprNode; //hit the REAL end of an D_EXPR
            }
        }
        else numExprError("Double");

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Check if the current token is an integer (with a sign if it exists)
     *      and push it to the passed in tree node
     * @param node Node to push the integer too
     * @return boolean if an integer was found
     */
    private static boolean integer(TreeNode node) {
        TreeNode intNode = new TreeNode(new State(State.stateType.INT));
        boolean foundSign = false;

        if(isTokenSign(tokenStream.get(tokenIndex)))
        {
            intNode.addTreeNode(new State(State.stateType.SIGN, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            foundSign = true;
        }
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Integer))
        {
            intNode.addTreeNode(new State(State.stateType.DIGIT, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
            node.addTreeNode(intNode); //add to the real node
            return true;
        }

        if(foundSign)
            tokenIndex--;

        return false;
    }

    private static TreeNode i_expr() {
        TreeNode root = null;

        //Find string relations
        if(isTokenTypeString(tokenStream.get(tokenIndex))) {
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
        else if(isTokenTypeDouble(tokenIndex)) {
            TreeNode first = d_expr(false);
            if (first != null) {
                //Check for relative op and another d_expr
                if (isTokenRelOp(tokenStream.get(tokenIndex))) {
                    TreeNode relOp = new TreeNode(new State(State.stateType.REL_OP, tokenStream.get(tokenIndex), tokenIndex));
                    tokenIndex++;
                    TreeNode second = d_expr(false);
                    if (second != null) {
                        root = new TreeNode(new State(State.stateType.I_EXPR));
                        root.addTreeNode(first);
                        root.addTreeNode(relOp);
                        root.addTreeNode(second);
                    }
                }
            }
        }

        //Try normal or relative integer operation
        TreeNode first = i_expr_normal(false);
        if(first != null) {
            //Check for relative op and another i_expr
            if (isTokenRelOp(tokenStream.get(tokenIndex))) {
                TreeNode relOp = new TreeNode(new State(State.stateType.REL_OP, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                TreeNode second = i_expr();
                if (second != null) {
                    root = new TreeNode(new State(State.stateType.I_EXPR));
                    root.addTreeNode(first);
                    root.addTreeNode(relOp);
                    root.addTreeNode(second);
                }
            }
            else return first;
        }

        return root;
    }

    /**
     * Evaluate the upcoming tokens in the tokenstream to see if it is an i_expr
     * This function will add it to the send in tree node if it is
     * @param isNestedExpr FALSE by default. Flag to see if we are evaluating a nested i_expr
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode i_expr_normal(boolean isNestedExpr) {
        //Local expression node to evaluate into
        TreeNode parentExprNode = new TreeNode(new State (State.stateType.I_EXPR));

        //Go back an index if we are nested
        if(isNestedExpr)
            tokenIndex--;

        //Check first parameter to see if it's an ID (skip over if we are nested)
        if(isNestedExpr || tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)){
            //Lookahead to see if there's an operator
            if(isTokenOp(tokenStream.get(tokenIndex+1))) {
                //Since this IS a math expression, add the ID as a SEPARATE I_EXPR
                if(!isNestedExpr) {
                    TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.I_EXPR));
                    idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                }
                tokenIndex++;

                //Find the third component of the I_EXPR
                return i_exprThirdComponent(parentExprNode);
            }
            //Since this is NOT a math expression, add the ID to THIS I_EXPR
            else {
                parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
                tokenIndex++;
                return parentExprNode; //hit the REAL end of an I_EXPR
            }
        }

        //Check first parameter to see if it's an Integer (skip over if we are nested)
        //Automatically adds integer to the node if one exists
        else if(integer(parentExprNode)) {
            //See if there's an operator
            if(isTokenOp(tokenStream.get(tokenIndex))) {
                //IS a math expression
                //Find the third component of the I_EXPR
                return i_exprThirdComponent(parentExprNode);
            }
            //NOT a math expression
            else {
                return parentExprNode; //hit the REAL end of an I_EXPR
            }
        }
        else numExprError("Integer");

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Evaluate the third component of an i_expr.
     * NEVER CALL THIS FUNCTION OUTSIDE OF i_expr()
     * @param parentExprNode The current expression node we are evaluating
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode i_exprThirdComponent(TreeNode parentExprNode)
    {
        //Add the operator to the I_EXPR
        parentExprNode.addTreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex), tokenIndex));
        tokenIndex++;

        //Find what the third token is
        boolean foundID = tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID);
        if(foundID)
        {
            //Add ID to the I_EXPR
            TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.I_EXPR));
            idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
        }
        boolean foundInteger = foundID ? false : integer(parentExprNode);

        //Check for nested statements or add
        if(foundInteger || foundID)
        {
            //Check if there is ANOTHER operator or int, meaning we are nested
            if (isTokenOp(tokenStream.get(tokenIndex)))
            {
                //Create and run nested expression
                TreeNode res = i_expr_normal(true);
                //Since we nested, we HAVE to add the previous expression to the FRONT of the I_EXPR
                //This is just how the grammar works
                TreeNode attachTo = res;
                while(attachTo.getChildren().get(0).getState().getState() != State.stateType.OP)
                    attachTo = attachTo.getChildren().get(0);

                attachTo.addTreeNodeToFront(parentExprNode);
                return res;
            }
            //Expression is finished, return
            else {
                return parentExprNode; //hit the REAL end of an I_EXPR
            }
        }
        else numExprError("Integer");

        return null; //fail (should never reach here as we are exiting on errors)
    }

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
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID){
            parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex), tokenIndex));
            tokenIndex++;
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

    private static void checkSize(){
        if(tokenIndex>=tokenStream.size()){
            LogError.log(LogError.ErrorType.SYNTAX, "Unexpected EoF",
                    new Token("$$", TokenType.EoF, tokenStream.get(tokenIndex-1).getLineNum(),
                            tokenStream.get(tokenIndex-1).getColumnEnd()+1,
                            tokenStream.get(tokenIndex-1).getColumnEnd()+1));
        }
    }
}