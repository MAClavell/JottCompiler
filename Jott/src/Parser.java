import java.util.ArrayList;

public class Parser {

    static int tokenIndex=0;

    /**
     * The main parsing method called to parse the tokenStream into a parse tree
     * @param tokenStream the token stream to parse
     * @return the parse tree
     */
    public static TreeNode parse(ArrayList<Token> tokenStream){
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

    private static void stmt_list(TreeNode node, ArrayList<Token> token){
        stmt(node.addTreeNode(new State((State.stateType.STMT))));
        stmt_list(node.addTreeNode(new State(State.stateType.STMT_LIST)));
        switch(token.get(tokenIndex).getTokenType()){

        }
    }
    public void end_paren(TreeNode node) {

    }
    public void end_stmt(TreeNode node) {

    }
    public void character(TreeNode node) {

    }
    public void l_char(TreeNode node) {

    }
    public void u_char(TreeNode node) {

    }
    public void digit(TreeNode node) {

    }
    public void sign(TreeNode node) {

    }
    public void id(TreeNode node) {

    }
    private static void stmt(TreeNode node) {

    }
    public void expr(TreeNode node) {

    }
    public void print(TreeNode node) {

    }
    public void asmt(TreeNode node) {

    }
    public void op(TreeNode node) {

    }
    public void dbl(TreeNode node) {

    }
    public void d_expr(TreeNode node) {

    }
    public void integer(TreeNode node) {

    }
    public void i_expr(TreeNode node) {

    }
    public void str_literal(TreeNode node) {

    }
    public void str(TreeNode node) {

    }
    public void s_expr(TreeNode node) {

    }

}
