public class State {
    //The type of state that the node represents
    enum stateType{
        PROGRAM, STMT_LIST, B_STMT_LIST, START_PAREN, END_PAREN, START_BLCK, END_BLCK, END_STATEMENT, DIGIT,
        SIGN, ID, STMT, B_STMT, IF_STMT, WHILE_LOOP, FOR_LOOP, R_ASMT, EXPR, PRINT, ASMT, OP, REL_OP, DBL, D_EXPR, INT, I_EXPR, STR_LITERAL,
        STR, S_EXPR, F_STMT, P_LIST, FC_P_LIST, F_CALL, TYPE, END_PROG, EPSILON, TERMINAL, RETURN;
    }

    //The State that the parse tree is in
    private stateType state;
    //If there is a final token, this is where it will go
    private Token token;
    // The current index of the token. Used for scope
    private int tokenIndex;

    /**
     * The constructor for the state in a parse tree
     * @param state the current state that the parse tree is in
     */
    public State(stateType state){
        this.state=state;
        this.token=null;
    }

    /**
     * An alternative coonstructor for the state in a parse tree
     * @param state the representation of the state for the parse tree
     * @param token the current token if the state is a terminal
     */
    public State(stateType state, Token token, int tokenIndex){
        this.state=state;
        this.tokenIndex=tokenIndex;
        this.token=token;
    }

    /**
     * A getter for the state of the current node of the parse tree
     * @return the current node's state in the parse tree
     */
    public stateType getState(){
        return state;
    }

    /**
     * A getter for the token in  the current node of the parse tree
     * @return the current node's (terminal) token in the parse tree
     */
    public Token getToken(){
        return token;
    }

    public int getTokenIndex(){
        return tokenIndex;
    }

    /**
     * Gets wheter or not the state is a terminal
     * @return whether or not the state is a terminal
     */
    public boolean isTerminal(){
        if(token!=null){
            return true;
        }
        else{
            return false;
        }
    }

}
