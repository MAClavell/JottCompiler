import java.util.ArrayList;

public class Parser {

    // The token index
    static int tokenIndex=0;
    // The list of tokens
    static ArrayList<Token> tokenStream;

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

    private static void stmt_list(TreeNode node){
        if(tokenStream.get(tokenIndex)!=) {
            // Add a statement to the tree and branch off to parse this left hand side
            stmt(node.addTreeNode(new State((State.stateType.STMT))));

            // Add a statement to the tree and branch off to parse this right hand side
            stmt_list(node.addTreeNode(new State(State.stateType.STMT_LIST)));
        }
        else{
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void end_paren(TreeNode node) {

    }
    private static void end_stmt(TreeNode node) {

    }
    private static void character(TreeNode node) {

    }
    private static void l_char(TreeNode node) {

    }
    private static void u_char(TreeNode node) {

    }
    private static void digit(TreeNode node) {

    }
    private static void sign(TreeNode node) {

    }
    private static void id(TreeNode node) {

    }
    private static void stmt(TreeNode node) {

    }
    private static void expr(TreeNode node) {

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
    private static void integer(TreeNode node) {

    }
    private static void i_expr(TreeNode node) {

    }
    private static void str_literal(TreeNode node) {

    }
    private static void str(TreeNode node) {

    }
    private static void s_expr(TreeNode node) {

    }

}
