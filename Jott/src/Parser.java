import com.sun.source.tree.Tree;

import java.util.ArrayList;

public class Parser {

    // The token index
    static int tokenIndex=0;
    // The list of tokens
    static ArrayList<Token> tokenStream;
    //The text line by line
    static String[] lines;

    // Strings representing the different tokens for Strings
    public final static String PRINT="print";
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
    public static TreeNode parse(ArrayList<Token> tokens, String[] textSplitIntoLines){
        // Sets the tokens
        tokenStream=tokens;
        lines = textSplitIntoLines;

        // The root node
        State state = new State(State.stateType.PROGRAM);
        TreeNode root = new TreeNode(state);

        //Starts the parsing process
        program(root);
        return root;
    }

    // Done

    ///Predict Functions
    private static void program(TreeNode node){
       stmt_list(node.addTreeNode((new State((State.stateType.STMT_LIST)))));
       node.addTreeNode((new State((State.stateType.END_PROG))));
    }

    // DONE

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

            // The assignment stmt
            else if (tokenStream.get(tokenIndex+2).getTokenType()==TokenType.Assign) {
                asmt(node.addTreeNode(new State(State.stateType.ASMT)));
            }

            // The expression statement
            else if(Character.isLowerCase(tokenText.charAt(0)) ||
                    Character.isDigit(tokenText.charAt(0)) ||
                    tokenText.charAt(0)=='"' || tokenText.equals(CONCAT)|| tokenText.equals(CHARAT) ||
                    tokenText.equals("-") || tokenText.equals("+") || tokenText.equals(".")){
                expr(node.addTreeNode(new State(State.stateType.EXPR)));
                end_stmt(node);
            }

            // Error
            else{
                System.err.println("Parse error in stmt");
                System.exit(1);
            }
        }

        // Error
        else{
            System.err.println("Parse error in stmt");
            System.exit(1);
        }
    }

    private static void expr(TreeNode node) {
        // The expression starting with a str_literal (in s_expr)
        if(tokenStream.get(tokenIndex).getTokenText().charAt(0)=='"'){
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));
        }

        // The expression starting with concat (in s_expr)
        else if(tokenStream.get(tokenIndex).getTokenText().equals(CONCAT)){
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));
        }

        // The expression starting with charAt (in s_expr)
        else if(tokenStream.get(tokenIndex).getTokenText().equals(CHARAT)){
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));
        }

        // If the first token is an integer (in i_expr)
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Integer){
            i_expr(node.addTreeNode(new State(State.stateType.I_EXPR)), false);
        }

        // If the first token is a double (in d_expr)
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Double){
            d_expr(node.addTreeNode(new State(State.stateType.D_EXPR)), false);
        }

        // If the first token is any other id so long as it isn't a print
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID &&
                !tokenStream.get(tokenIndex).getTokenText().equals(PRINT)){
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        else {
            System.err.println("Error in expr");
            System.exit(1);
        }
    }

    private static void print(TreeNode node) {
        // Adds the print terminal
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
        tokenIndex++;

        // Adds the start parenthasis
        node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex)));
        tokenIndex++;

        // Adds the expression to print
        expr(node.addTreeNode(new State(State.stateType.EXPR)));

        // Adds the end parenthasis
        node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex)));
        tokenIndex++;

        // Adds the semicolon
        node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex)));
        tokenIndex++;
    }

    /**
     * Parses the assignment statment
     * @param node the node to build off from
     */
    private static void asmt(TreeNode node) {

        String tokenText=tokenStream.get(tokenIndex).getTokenText();

        // asmt_stmt -> Double  <id > = <d_expr ><end_statement>
        if(tokenText.equals("Double")){

            // Adds a terminal "Double" with the corresponding token
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the id
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the = terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the d_expr
            d_expr(node.addTreeNode(new State(State.stateType.D_EXPR)), false);

            // Adds the end_statement
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // asmt_stmt -> Integer  <id > = <i_expr ><end_statement>
        else if(tokenText.equals(INTEGER)){

            // Adds a terminal "Integer" with the corresponding token
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the id
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the = terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the i_expr
            i_expr(node, false);

            // Adds the end_statement
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // asmt_stmt -> String  <id > = <s_expr ><end_statement>
        else if(tokenText.equals(STRING)){

            // Adds the terminal "String" with the corresponding token
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the id
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the = terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the s_expr
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));

            // Adds the end_statement
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // An error
        else{
            System.err.println("Error in assignment statement");
            System.exit(1);
        }
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
            dblNode.addTreeNode(new State(State.stateType.SIGN));
            tokenIndex++;
            foundSign = true;
        }
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Double))
        {
            dblNode.addTreeNode(new State(State.stateType.DIGIT, tokenStream.get(tokenIndex)));
            tokenIndex++;
            node.addTreeNode(dblNode); //add to the real node
            return true;
        }

        if(foundSign)
            tokenIndex--;
        return false;

    }
    private static TreeNode d_expr(TreeNode node, boolean isNestedExpr) {
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
                    idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
                }
                tokenIndex++;

                //Find the third component of the D_EXPR
                return d_exprThirdComponent(node, parentExprNode);
            }
            //Since this is NOT a math expression, add the ID to THIS D_EXPR
            else {
                parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
                node.addTreeNode(parentExprNode); //add to topmost node
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
                return d_exprThirdComponent(node, parentExprNode);
            }
            //NOT a math expression
            else {
                node.addTreeNode(parentExprNode); //add to topmost node
                return parentExprNode; //hit the REAL end of an D_EXPR
            }
        }

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Evaluate the third component of an d_expr.
     * NEVER CALL THIS FUNCTION OUTSIDE OF d_expr()
     * @param node The topmost node to add the finished d_expr to
     * @param parentExprNode The current expression node we are evaluating
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode d_exprThirdComponent(TreeNode node, TreeNode parentExprNode)
    {
        //Add the operator to the D_EXPR
        parentExprNode.addTreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex)));
        tokenIndex++;

        //Find what the third token is
        boolean foundDouble = dbl(parentExprNode);
        boolean foundID = foundDouble ? false : tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID);
        if(foundID)
        {
            //Add ID to the D_EXPR
            TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.D_EXPR));
            idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        //Check for nested statements or add
        if(foundDouble || foundID)
        {
            //Check if there is ANOTHER operator, meaning we are nested
            if (isTokenOp(tokenStream.get(tokenIndex)))
            {
                //Create and run nested expression
                TreeNode res = d_expr(node, true);
                //Since we nested, we HAVE to add the previous expression to the FRONT of the D_EXPR
                //This is just how the grammar works
                res.addTreeNodeToFront(parentExprNode);
                return parentExprNode;
            }
            //Expression is finish, add it and return
            else {
                node.addTreeNode(parentExprNode); //add to topmost node
                return parentExprNode; //hit the REAL end of an D_EXPR
            }
        }

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Check if the current token is an integar (with a sign if it exists)
     *      and push it to the passed in tree node
     * @param node Node to push the integer too
     * @return boolean if an integer was found
     */
    private static boolean integer(TreeNode node) {
        TreeNode intNode = new TreeNode(new State(State.stateType.INT));
        boolean foundSign = false;

        if(isTokenSign(tokenStream.get(tokenIndex)))
        {
            intNode.addTreeNode(new State(State.stateType.SIGN));
            tokenIndex++;
            foundSign = true;
        }
        if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.Integer))
        {
            intNode.addTreeNode(new State(State.stateType.DIGIT, tokenStream.get(tokenIndex)));
            tokenIndex++;
            node.addTreeNode(intNode); //add to the real node
            return true;
        }

        if(foundSign)
            tokenIndex--;
        return false;
    }

    /**
     * Evaluate the upcoming tokens in the tokenstream to see if it is an i_expr
     * This function will add it to the send in tree node if it is
     * @param node The topmost node to add the finished i_expr to
     * @param isNestedExpr FALSE by default. Flag to see if we are evaluating a nested i_expr
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode i_expr(TreeNode node, boolean isNestedExpr) {
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
                    idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
                }
                tokenIndex++;

                //Find the third component of the I_EXPR
                return i_exprThirdComponent(node, parentExprNode);
            }
            //Since this is NOT a math expression, add the ID to THIS I_EXPR
            else {
                parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
                node.addTreeNode(parentExprNode); //add to topmost node
                tokenIndex++;
                return parentExprNode; //hit the REAL end of an I_EXPR
            }
        }

        //Check first parameter to see if it's an Integer (skip over if we are nested)
        //Automatically adds integer to the node if one exists
        else if(isNestedExpr || integer(parentExprNode)) {
            //See if there's an operator
            if(isTokenOp(tokenStream.get(tokenIndex))) {
                //IS a math expression
                //Find the third component of the I_EXPR
                return i_exprThirdComponent(node, parentExprNode);
            }
            //NOT a math expression
            else {
                node.addTreeNode(parentExprNode); //add to topmost node
                return parentExprNode; //hit the REAL end of an I_EXPR
            }
        }

        return null; //fail (should never reach here as we are exiting on errors)
    }

    /**
     * Evaluate the third component of an i_expr.
     * NEVER CALL THIS FUNCTION OUTSIDE OF i_expr()
     * @param node The topmost node to add the finished i_expr to
     * @param parentExprNode The current expression node we are evaluating
     * @return TreeNode finished expression (used for recursion and nested expressions)
     */
    private static TreeNode i_exprThirdComponent(TreeNode node, TreeNode parentExprNode)
    {
        //Add the operator to the I_EXPR
        parentExprNode.addTreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex)));
        tokenIndex++;

        //Find what the third token is
        boolean foundInteger = integer(parentExprNode);
        boolean foundID = foundInteger ? false : tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID);
        if(foundID)
        {
            //Add ID to the I_EXPR
            TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.I_EXPR));
            idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        //Check for nested statements or add
        if(foundInteger || foundID)
        {
            //Check if there is ANOTHER operator, meaning we are nested
            if (isTokenOp(tokenStream.get(tokenIndex)))
            {
                //Create and run nested expression
                TreeNode res = i_expr(node, true);
                //Since we nested, we HAVE to add the previous expression to the FRONT of the I_EXPR
                //This is just how the grammar works
                res.addTreeNodeToFront(parentExprNode);
                return parentExprNode;
            }
            //Expression is finish, add it and return
            else {
                node.addTreeNode(parentExprNode); //add to topmost node
                return parentExprNode; //hit the REAL end of an I_EXPR
            }
        }

        return null; //fail (should never reach here as we are exiting on errors)
    }

    private static void str_literal(TreeNode node) {
        // Adds the string
        node.addTreeNode(new State(State.stateType.STR, tokenStream.get(tokenIndex)));
        tokenIndex++;
    }
    /*private static void str(TreeNode node) {

        // Builds a string using the quote as a terminator
        while(!tokenStream.get(tokenIndex).getTokenText().equals("\"")){
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }
    }*/

    private static void s_expr(TreeNode node) {

        String tokenText=tokenStream.get(tokenIndex).getTokenText();

        // s_expr -> <str_literal>
        if(tokenText.charAt(0)=='"'){
            str_literal(node.addTreeNode(new State(State.stateType.STR_LITERAL)));
        }

        // s_expr -> concat <start_paren > <s_expr >, <s_expr > <end_paren>
        else if(tokenText.equals(CONCAT)){

            // Adds the concat terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // The start_paren terminal
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the first s_expr
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));

            // Adds the comma terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the second s_expr
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));

            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // s_expr -> charAt <start_paren > <s_expr >, <i_expr > <end_paren>
        else if(tokenText.equals(CHARAT)){

            // The charAt terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the start paren
            node.addTreeNode(new State(State.stateType.START_PAREN, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the s_expr
            s_expr(node.addTreeNode(new State(State.stateType.S_EXPR)));

            // Adds the comma terminal
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;

            // Adds the i_expr
            i_expr(node, false);

            // Adds the end paren
            node.addTreeNode(new State(State.stateType.END_PAREN, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // s_expr-> <id>
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID){
            node.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // Error
        else{
            System.err.println("Error in parsing");
            System.exit(1);
        }
    }

    private static void start_paren(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.StartParen) {
            node.addTreeNode(new State((State.stateType.START_PAREN)));
            tokenIndex++;
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void end_paren(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.EndParen) {
            node.addTreeNode(new State((State.stateType.END_PAREN)));
            tokenIndex++;
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void end_stmt(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.EndStmt) {
            node.addTreeNode(new State((State.stateType.END_STATEMENT)));
            tokenIndex++;
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }


    //HELPERS -----------------------------------------------------------------

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
     * Check if a token is either of the two signs
     * @param tok Token to check
     * @return boolean if it is a sign
     */
    private static boolean isTokenSign(Token tok)
    {
        return tok.getTokenType()==TokenType.Minus ||
                tok.getTokenType()==TokenType.Plus;
    }
}
