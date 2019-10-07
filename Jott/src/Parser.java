import java.util.ArrayList;

public class Parser {

    // The token index
    static int tokenIndex=0;
    // The list of tokens
    static ArrayList<Token> tokenStream;

    // Strings representing the different tokens for Strings
    public final static String PRINT="print";

    // TODO exit out if there is a parse error

    /**
     * The main parsing method called to parse the tokenStream into a parse tree
     * @param tokens the token stream to parse
     * @return the parse tree
     */
    public static TreeNode parse(ArrayList<Token> tokens){
        tokenStream=tokens;
        State state = new State(State.stateType.PROGRAM);
        TreeNode root = new TreeNode(state);
        program(root);
        return root;
    }

    ///Predict Functions
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
    private static void sign(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Minus ||
                tokenStream.get(tokenIndex).getTokenType()==TokenType.Plus) {
            node.addTreeNode(new State((State.stateType.SIGN)));
        }
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
                    tokenText.charAt(0)=='"' || tokenText.equals("concat")|| tokenText.equals("charAt") ||
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
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID){
            l_char(node.addTreeNode(new State((State.stateType.L_CHAR))));
        }
        else if(tokenStream.get(tokenIndex).getTokenText().equals('"')){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else if(tokenStream.get(tokenIndex).getTokenText().equals("concat")){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else if(tokenStream.get(tokenIndex).getTokenText().equals("charAt")){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Minus){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Plus){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Integer || tokenStream.get(tokenIndex).getTokenType()==TokenType.Double){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else if(tokenStream.get(tokenIndex).getTokenText().equals('.')){
            node.addTreeNode(new State(State.stateType.EXPR));
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void print(TreeNode node) {
        // Adds the print terminal
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
        tokenIndex++;

        // Adds the start parenthasis
        start_paren(node.addTreeNode(new State(State.stateType.START_PAREN)));

        // Adds the expression to print
        expr(node.addTreeNode(new State(State.stateType.EXPR)));

        // Adds the end parenthasis
        end_paren(node.addTreeNode(new State(State.stateType.END_PAREN)));

        // Adds the semicolon
        end_stmt(node.addTreeNode(new State(State.stateType.END_STATEMENT)));
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
            d_expr(node.addTreeNode(new State(State.stateType.D_EXPR)));

            // Adds the end_statement
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // asmt_stmt -> Integer  <id > = <i_expr ><end_statement>
        else if(tokenText.equals("Integer")){

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
            i_expr(node.addTreeNode(new State(State.stateType.I_EXPR)));

            // Adds the end_statement
            node.addTreeNode(new State(State.stateType.END_STATEMENT, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }

        // asmt_stmt -> String  <id > = <s_expr ><end_statement>
        else if(tokenText.equals("String")){

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
    private static void op(TreeNode node) {

    }
    private static void dbl(TreeNode node) {

    }
    private static void d_expr(TreeNode node) {

    }
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
            node.addTreeNode(intNode);
            return true;
        }

        if(foundSign)
            tokenIndex--;
        return false;
    }
    private static boolean isTokenOp(Token curr)
    {
        return(curr.getTokenType()==TokenType.Minus||
                curr.getTokenType()==TokenType.Plus||
                curr.getTokenType()==TokenType.Mult||
                curr.getTokenType()==TokenType.Divide||
                curr.getTokenType()==TokenType.Power);

    }
    private static boolean isTokenSign(Token curr)
    {
        return(curr.getTokenType()==TokenType.Minus||
                curr.getTokenType()==TokenType.Plus);

    }

    private static class NestedExprResult{
        public boolean succeeded;
        public TreeNode eNode;

        NestedExprResult(boolean succeeded, TreeNode eNode)
        {
            this.succeeded = succeeded;
            this.eNode = eNode;
        }
    };
    private static NestedExprResult i_expr(TreeNode node, boolean isNestedExpr) {
        TreeNode parentExprNode = new TreeNode(new State (State.stateType.I_EXPR));

        if(isNestedExpr)
            tokenIndex--;

        if(isNestedExpr || tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)){
            if(isTokenOp(tokenStream.get(tokenIndex+1))){
                if(!isNestedExpr){
                    TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.I_EXPR));
                    idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
                }
                tokenIndex++;
                return i_exprThirdComponent(node, parentExprNode);
            }
            else {
                parentExprNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
                node.addTreeNode(parentExprNode);
                tokenIndex++;
                return new NestedExprResult(true, parentExprNode);
            }
        }
        else if(isNestedExpr || integer(parentExprNode)) { //adds integer to the node if one exists
            if(isTokenOp(tokenStream.get(tokenIndex))){
                return i_exprThirdComponent(node, parentExprNode);
            }
            else {
                node.addTreeNode(parentExprNode);
                return new NestedExprResult(true, parentExprNode);
            }
        }

        return new NestedExprResult(false, null);
    }
    private static NestedExprResult i_exprThirdComponent(TreeNode node, TreeNode parentExprNode)
    {
        parentExprNode.addTreeNode(new State(State.stateType.OP, tokenStream.get(tokenIndex)));
        tokenIndex++;
        if(integer(parentExprNode))
        {
            if(isTokenOp(tokenStream.get(tokenIndex))){
                NestedExprResult res = i_expr(node, true);
                if(res.succeeded) {
                    res.eNode.addTreeNodeToFront(parentExprNode);
                    return new NestedExprResult(true, parentExprNode);
                }
            }
            else
            {
                node.addTreeNode(parentExprNode);
                return new NestedExprResult(true, parentExprNode);
            }
        }
        else if(tokenStream.get(tokenIndex).getTokenType().equals(TokenType.ID)){
            TreeNode idNode = parentExprNode.addTreeNode(new State(State.stateType.I_EXPR));
            idNode.addTreeNode(new State(State.stateType.ID, tokenStream.get(tokenIndex)));
            tokenIndex++;

            if(isTokenOp(tokenStream.get(tokenIndex))){
                NestedExprResult res = i_expr(node, true);
                if(res.succeeded) {
                    res.eNode.addTreeNodeToFront(parentExprNode);
                    return new NestedExprResult(true, parentExprNode);
                }
            }
            else
            {
                node.addTreeNode(parentExprNode);
                return new NestedExprResult(true, parentExprNode);
            }
        }
        return new NestedExprResult(false, null);
    }

    private static void str_literal(TreeNode node) {
        // Gets the starting quote terminal
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
        tokenIndex++;

        // Adds the string
        str(node.addTreeNode(new State(State.stateType.STR)));

        // Gets the ending quote terminal
        node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
        tokenIndex++;
    }
    private static void str(TreeNode node) {

        // Builds a string using the quote as a terminator
        while(!tokenStream.get(tokenIndex).getTokenText().equals("\"")){
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }
    }

    private static void s_expr(TreeNode node) {

        String tokenText=tokenStream.get(tokenIndex).getTokenText();

        // s_expr -> <str_literal>
        if(tokenText.equals("\"")){
            str_literal(node.addTreeNode(new State(State.stateType.STR_LITERAL)));
        }

        // s_expr -> concat <start_paren > <s_expr >, <s_expr > <end_paren>
        else if(tokenText.equals("concat")){

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
        else if(tokenText.equals("charAt")){

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
            i_expr(node.addTreeNode(new State(State.stateType.I_EXPR)));

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

}
