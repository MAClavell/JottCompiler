import com.sun.source.tree.Tree;

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
            else if (tokenStream.get(tokenIndex+1).getTokenType()==TokenType.Assign) {
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
                System.err.println("Parse error");
                System.exit(1);
            }
        }

        // Error
        else{
            System.err.println("Parse error");
            System.exit(1);
        }
    }
    private static void expr(TreeNode node) {
        i_expr(node, false);
    }
    private static void print(TreeNode node) {

    }
    private static void asmt(TreeNode node) {

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

    }
    private static void str(TreeNode node) {

    }
    private static void s_expr(TreeNode node) {

    }

}
