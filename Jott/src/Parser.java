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
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void end_paren(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.EndParen) {
            node.addTreeNode(new State((State.stateType.END_PAREN)));
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void end_stmt(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.EndStmt) {
            node.addTreeNode(new State((State.stateType.END_STATEMENT)));
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    ///???
    private static void character(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID) {
            l_char(node.addTreeNode(new State((State.stateType.L_CHAR))));
        }
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Integer||tokenStream.get(tokenIndex).getTokenType()==TokenType.Double) {
            digit(node.addTreeNode(new State((State.stateType.DIGIT))));
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }

    }
    private static void l_char(TreeNode node) {
        if (tokenStream.get(tokenIndex).getTokenText().equals('a') ||
                tokenStream.get(tokenIndex).getTokenText().equals('b') ||
                tokenStream.get(tokenIndex).getTokenText().equals('c') ||
                tokenStream.get(tokenIndex).getTokenText().equals('d') ||
                tokenStream.get(tokenIndex).getTokenText().equals('e') ||
                tokenStream.get(tokenIndex).getTokenText().equals('f') ||
                tokenStream.get(tokenIndex).getTokenText().equals('g') ||
                tokenStream.get(tokenIndex).getTokenText().equals('h') ||
                tokenStream.get(tokenIndex).getTokenText().equals('i') ||
                tokenStream.get(tokenIndex).getTokenText().equals('j') ||
                tokenStream.get(tokenIndex).getTokenText().equals('k') ||
                tokenStream.get(tokenIndex).getTokenText().equals('l') ||
                tokenStream.get(tokenIndex).getTokenText().equals('m') ||
                tokenStream.get(tokenIndex).getTokenText().equals('n') ||
                tokenStream.get(tokenIndex).getTokenText().equals('o') ||
                tokenStream.get(tokenIndex).getTokenText().equals('p') ||
                tokenStream.get(tokenIndex).getTokenText().equals('q') ||
                tokenStream.get(tokenIndex).getTokenText().equals('r') ||
                tokenStream.get(tokenIndex).getTokenText().equals('s') ||
                tokenStream.get(tokenIndex).getTokenText().equals('t') ||
                tokenStream.get(tokenIndex).getTokenText().equals('u') ||
                tokenStream.get(tokenIndex).getTokenText().equals('v') ||
                tokenStream.get(tokenIndex).getTokenText().equals('w') ||
                tokenStream.get(tokenIndex).getTokenText().equals('x') ||
                tokenStream.get(tokenIndex).getTokenText().equals('y') ||
                tokenStream.get(tokenIndex).getTokenText().equals('z')) {
            node.addTreeNode(new State(State.stateType.L_CHAR));
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }

    }
    private static void u_char(TreeNode node) {
        if (tokenStream.get(tokenIndex).getTokenText().equals('A') ||
                tokenStream.get(tokenIndex).getTokenText().equals('B') ||
                tokenStream.get(tokenIndex).getTokenText().equals('C') ||
                tokenStream.get(tokenIndex).getTokenText().equals('D') ||
                tokenStream.get(tokenIndex).getTokenText().equals('E') ||
                tokenStream.get(tokenIndex).getTokenText().equals('F') ||
                tokenStream.get(tokenIndex).getTokenText().equals('G') ||
                tokenStream.get(tokenIndex).getTokenText().equals('H') ||
                tokenStream.get(tokenIndex).getTokenText().equals('I') ||
                tokenStream.get(tokenIndex).getTokenText().equals('J') ||
                tokenStream.get(tokenIndex).getTokenText().equals('K') ||
                tokenStream.get(tokenIndex).getTokenText().equals('L') ||
                tokenStream.get(tokenIndex).getTokenText().equals('M') ||
                tokenStream.get(tokenIndex).getTokenText().equals('N') ||
                tokenStream.get(tokenIndex).getTokenText().equals('O') ||
                tokenStream.get(tokenIndex).getTokenText().equals('P') ||
                tokenStream.get(tokenIndex).getTokenText().equals('Q') ||
                tokenStream.get(tokenIndex).getTokenText().equals('R') ||
                tokenStream.get(tokenIndex).getTokenText().equals('S') ||
                tokenStream.get(tokenIndex).getTokenText().equals('T') ||
                tokenStream.get(tokenIndex).getTokenText().equals('U') ||
                tokenStream.get(tokenIndex).getTokenText().equals('V') ||
                tokenStream.get(tokenIndex).getTokenText().equals('W') ||
                tokenStream.get(tokenIndex).getTokenText().equals('X') ||
                tokenStream.get(tokenIndex).getTokenText().equals('Y') ||
                tokenStream.get(tokenIndex).getTokenText().equals('Z')) {
            node.addTreeNode(new State(State.stateType.U_CHAR));
        }
        else {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void digit(TreeNode node) {
        boolean dFound = false;
        for(int i = 0; i < 10; i++) {
            if (tokenStream.get(tokenIndex).getTokenText().equals(""+i)) {
                node.addTreeNode(new State(State.stateType.DIGIT));
                dFound = true;
                i = 10;
            }
        }
        if(!dFound) {
            node.addTreeNode(new State(State.stateType.EPSILON));
        }

    }
    private static void sign(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Minus ||
                tokenStream.get(tokenIndex).getTokenType()==TokenType.Plus) {
            node.addTreeNode(new State((State.stateType.SIGN)));
        }
        ///???
        else if(tokenStream.get(tokenIndex).getTokenType()==TokenType.Integer || tokenStream.get(tokenIndex).getTokenType()==TokenType.Double) {
            digit(node.addTreeNode(new State((State.stateType.DIGIT))));
        }
        else{
            node.addTreeNode(new State(State.stateType.EPSILON));
        }
    }
    private static void id(TreeNode node) {
        if(tokenStream.get(tokenIndex).getTokenType()==TokenType.ID) {
            ///???
            l_char(node.addTreeNode(new State((State.stateType.L_CHAR))));
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
            else if(((int)(tokenText.charAt(0))>= 97 && (int)(tokenText.charAt(0)) <= 122) ||
                    ((int)(tokenText.charAt(0))>= 48 && (int)(tokenText.charAt(0)) <= 57) ||
                    tokenText.charAt(0)=='"' || tokenText.equals("concat")|| tokenText.equals("charAt") ||
                    tokenText.equals("-") || tokenText.equals("+") || tokenText.equals(".")){
                expr(node.addTreeNode(new State(State.stateType.EXPR)));
                end_stmt(node.addTreeNode(new State(State.stateType.END_STATEMENT)));
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
    ///???
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
    private static void integer(TreeNode node) {

    }
    private static void i_expr(TreeNode node) {

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

        //Builds a string using the quote as a terminator
        while(!tokenStream.get(tokenIndex).equals("\"")){
            node.addTreeNode(new State(State.stateType.TERMINAL, tokenStream.get(tokenIndex)));
            tokenIndex++;
        }
    }
    private static void s_expr(TreeNode node) {

    }

}
