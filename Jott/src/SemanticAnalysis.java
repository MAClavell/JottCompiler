import java.util.HashMap;

public class SemanticAnalysis {

    private static final String IF="if";
    private static final String WHILE="while";
    private static final String FOR="for";

    private static Reference globalScope;
    private static Reference currentScope;

    public static void output(TreeNode root, Reference global){
        globalScope=global;
        program(root);
    }

    private static void program(TreeNode node){

        // Searches through the first stmt_list
        stmt_list(node.getChildren().get(0));
    }

    private static void stmt_list(TreeNode node){

        // Makes sure this list is not epsilon
        if(node.getChildren().get(0).getState().getState()!=State.stateType.EPSILON){

            // Searches through the associated statement
            stmt(node.getChildren().get(0));

            // Searches through the next stmt_list
            stmt_list(node.getChildren().get(1));
        }
    }

    private static void stmt(TreeNode node){

        // Evaluates the print statement
        if(node.getChildren().get(0).getState().getState() == State.stateType.PRINT){
            print(node.getChildren().get(0));
        }

        // Evaluates the asmt statement
        else if(node.getChildren().get(0).getState().getState() == State.stateType.ASMT){
            asmt_stmt(node.getChildren().get(0));
        }

        // Evaluates the if statement
        else if(node.getChildren().get(0).getState().getState()== State.stateType.ID &&
        node.getChildren().get(0).getState().getToken().getTokenText().equals(IF)){

        }

        // Evaluates the while statement
        else if(node.getChildren().get(0).getState().getState()== State.stateType.ID &&
                node.getChildren().get(0).getState().getToken().getTokenText().equals(WHILE)){

        }

        // Evaluates the for loop
        else if(node.getChildren().get(0).getState().getState()== State.stateType.ID &&
                node.getChildren().get(0).getState().getToken().getTokenText().equals(FOR)){

        }

        // Evaluates the <expr><end_statement> -- doesn't actually change the state
        else{

            // If the expr is an ID
            if(node.getChildren().get(0).getState().getState()== State.stateType.ID){
                String id=node.getChildren().get(0).getToken().getTokenText();

                // If it is a valid id
                if(globalScope.hasSymbol(id, node.getChildren().get(0).getState().getTokenIndex())){

                    // If the id is not null
                    if(globalScope.getScopedSymbol(id, node.getChildren().get(0).getState().getTokenIndex())!=null){
                        globalScope.getScopedSymbol(id, node.getChildren().get(0).getState().getTokenIndex());
                    }

                    // If the id is null
                    else{
                        LogError.log(LogError.ErrorType.RUNTIME, "Expected a non-null ID, got " +
                                        node.getToken().getTokenType()+" '"+node.getToken().getTokenText()+"'", node.getToken());
                    }
                }

                // If this is an invalid id
                else{
                    LogError.log(LogError.ErrorType.SYNTAX, "Invalid ID, got " +
                            node.getToken().getTokenType()+" '"+node.getToken().getTokenText()+"'", node.getToken());
                }
            }

            // If the expr is an i/d/s_expr
            else {
                TreeNode exprNode=node.getChildren().get(0).getChildren().get(0);

                // If the expression is an i_expr
                if (exprNode.getState().getState() == State.stateType.I_EXPR) {
                    i_expr(exprNode);
                }

                // If the expression is a d_expr
                else if (exprNode.getState().getState() == State.stateType.D_EXPR) {
                    d_expr(exprNode);
                }

                // If the expression is an s_expr
                else {
                    s_expr(exprNode);
                }
            }
        }
    }

    private static void print(TreeNode node){

        // Either going to be the stmt or id node
        TreeNode stmtNode=node.getChildren().get(2).getChildren().get(0);

        // If the child's expr's state is an id
        if(node.getChildren().get(2).getChildren().get(0).getState().getState() == State.stateType.ID){
            Symbol childSymbol=globalScope.getScopedSymbol(node.getChildren().get(2).getToken().getTokenText(),
                    node.getChildren().get(2).getState().getTokenIndex());
            if(childSymbol.getValue()!=null) {
                System.out.println(childSymbol.getValue());
            }
            else{
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        node.getToken().getTokenType()+" '"+node.getToken().getTokenText()+"'", node.getToken());
            }
        }
        else {

            // If the statment in the print is an int expression
            if(stmtNode.getState().getState()== State.stateType.I_EXPR){
                System.out.println(i_expr(stmtNode));
            }

            // If the statement in the print is a double expression
            else if(stmtNode.getState().getState()== State.stateType.D_EXPR){
                System.out.println(d_expr(stmtNode));
            }

            // If the statement in the print is a String expression
            else{
                System.out.println(s_expr(stmtNode));
            }
        }
    }

    private static void asmt_stmt(TreeNode node){

        TreeNode childNode=node.getChildren().get(0);
        String id=node.getChildren().get(1).getToken().getTokenText();
        TreeNode exprNode=node.getChildren().get(3);

        // An Integer assignment statement
        if(childNode.getToken().getTokenText().equals("Integer")){

            //If this is a valid id
            if(globalScope.hasSymbol(id, childNode.getState().getTokenIndex()) &&
                    globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).getType()
                            == Symbol.variableType.INTEGER){
                globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).changeValue(i_expr(exprNode));
            }
            else {
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        node.getChildren().get(1).getToken().getTokenType() + " '" +
                        node.getChildren().get(1).getToken().getTokenText() + "'", node.getChildren().get(1).getToken());
            }
        }

        // A Double assignment statement
        else if(childNode.getToken().getTokenText().equals("Double")){
            if(globalScope.hasSymbol(id, childNode.getState().getTokenIndex()) &&
                    globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).getType()
                            == Symbol.variableType.DOUBLE){
                globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).changeValue(d_expr(exprNode));
            }

            else {
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        node.getChildren().get(1).getToken().getTokenType() + " '" +
                        node.getChildren().get(1).getToken().getTokenText() + "'", node.getToken());
            }
        }

        // A String asignment statement
        else{
            if(globalScope.hasSymbol(id, childNode.getState().getTokenIndex()) &&
                    globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).getType()
                            == Symbol.variableType.STRING){
                globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).changeValue(s_expr(exprNode));
            }
            else {
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        node.getChildren().get(1).getToken().getTokenType() + " '" +
                        node.getChildren().get(1).getToken().getTokenText() + "'", node.getChildren().get(1).getToken());
            }
        }
    }

    private static boolean verifyIntID(TreeNode node){

        if(globalScope.hasSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex())){
            if(globalScope.hasSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex()) &&
                globalScope.getScopedSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex()).getType()==
                        Symbol.variableType.INTEGER){
                return true;
            }
        }
        return false;
    }

    private static int parseIntOperand(TreeNode node, int childNumber){
        TreeNode childNode=node.getChildren().get(childNumber);

        // If it is an id
        if(childNode.getState().getState()== State.stateType.ID){
            // Verifies it is defined, not null, and it is an integer
            if(verifyIntID(childNode)){
                return (Integer)globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                        childNode.getState().getTokenIndex()).getValue();
            }
            if(!globalScope.hasSymbol(childNode.getToken().getTokenText(), childNode.getState().getTokenIndex())) {
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
            else if(globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                    childNode.getState().getTokenIndex()) == null){
                LogError.log(LogError.ErrorType.SYNTAX, "Can not do operations on a null operand, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
            else{
                LogError.log(LogError.ErrorType.SYNTAX, "Expected an integer, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
        }

        // If it is an integer
        else if(childNode.getState().getState()== State.stateType.INT){

            // If it contains  sign
            if(childNode.getChildren().get(0).getState().getState()== State.stateType.SIGN){
                int operand=Integer.parseInt(childNode.getChildren().get(1).getToken().getTokenText());

                // Gets the sign
                if(childNode.getChildren().get(0).getToken().getTokenType()==TokenType.Minus){
                    operand*=-1;
                }
                return operand;
            }

            // If there is no sign
            else{
                return Integer.parseInt(childNode.getChildren().get(0).getToken().getTokenText());
            }
        }

        // If it is another i_expr
        else{
            return i_expr(childNode);
        }

        // This should never be reached
        return -1;
    }

    private static int i_expr(TreeNode node){
        // Gets the first operator
        int firstOp=parseIntOperand(node, 0);

        // There are multiple operators
        if(node.getChildren().size()>2) {

            // Gets the second operand
            int secondOp=parseIntOperand(node, 2);

            // Uses the sign number
            switch(node.getChildren().get(1).getToken().getTokenType()){
                case Minus:
                    return firstOp-secondOp;
                case Plus:
                    return firstOp+secondOp;
                case Mult:
                    return firstOp*secondOp;
                case Divide:
                    if(secondOp==0){
                        LogError.log(LogError.ErrorType.RUNTIME, "Divide by zero ", getLeftMostToken(node));
                    }
                    return firstOp/secondOp;
                case Power:
                    return (int) (Math.pow(firstOp, secondOp));
            }

            LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid operator, got " +
                    node.getChildren().get(1).getToken().getTokenType()+" '"+
                    node.getChildren().get(1).getToken().getTokenText()+"'", node.getChildren().get(1).getToken());
        }

        // It is just one operator
        else{
            return firstOp;
        }

        // This should never be reached
        return -1;
    }

    private static boolean verifyDoubleID(TreeNode node){
        if(globalScope.hasSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex())){
            if(globalScope.getScopedSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex()) != null &&
                    globalScope.getScopedSymbol(node.getToken().getTokenText(),
                            node.getState().getTokenIndex()).getType()== Symbol.variableType.DOUBLE){
                return true;
            }
        }
        return false;
    }

    private static double parseDoubleOperand(TreeNode node, int childNumber){

        TreeNode childNode=node.getChildren().get(childNumber);

        // If the child node is an ID
        if(childNode.getState().getState()== State.stateType.ID){
            if(verifyDoubleID(childNode)){
                return (Double)globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                        childNode.getState().getTokenIndex()).getValue();
            }
            if(!globalScope.hasSymbol(childNode.getToken().getTokenText(), childNode.getState().getTokenIndex())) {
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
            else if(globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                    childNode.getState().getTokenIndex()) == null){
                LogError.log(LogError.ErrorType.RUNTIME, "Can not do operations on a null operand, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
            else{
                LogError.log(LogError.ErrorType.RUNTIME, "Expected a double, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
        }

        // If the child node is a double
        else if(childNode.getState().getState()== State.stateType.DBL){

            // If there is a sign as the first value
            if(childNode.getChildren().get(0).getState().getState()== State.stateType.SIGN){
                double operand=Double.parseDouble(childNode.getChildren().get(1).getToken().getTokenText());

                if(childNode.getChildren().get(0).getToken().getTokenType()==TokenType.Minus){
                    operand*=-1;
                }

                return operand;
            }

            else{
                return Double.parseDouble(childNode.getChildren().get(0).getToken().getTokenText());
            }
        }

        // If the child node is a d_expr
        else{
            return d_expr(childNode);
        }

        // Should never get to this point
        return -1;
    }

    private static double d_expr(TreeNode node){
        double firstOp=parseDoubleOperand(node, 0);

        // If there is more than one operand
        if(node.getChildren().size()>2){

            // The value of the second operand
            double secondOp=parseDoubleOperand(node, 2);

            switch(node.getChildren().get(1).getToken().getTokenType()){
                case Minus:
                    return firstOp-secondOp;
                case Plus:
                    return firstOp+secondOp;
                case Mult:
                    return firstOp*secondOp;
                case Divide:
                    if(secondOp==0){
                        LogError.log(LogError.ErrorType.RUNTIME, "Divide by zero ", getLeftMostToken(node));
                    }
                    return firstOp/secondOp;
                case Power:
                    return Math.pow(firstOp, secondOp);
            }

        }

        // If there is only one operand
        else{
            return firstOp;
        }

        // Should never get to this point
        return -1;
    }

    private static boolean verifyStringID(TreeNode node){
        if(globalScope.hasSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex())){
            if(globalScope.getScopedSymbol(node.getToken().getTokenText(), node.getState().getTokenIndex()) != null &&
                    globalScope.getScopedSymbol(node.getToken().getTokenText(),
                            node.getState().getTokenIndex()).getType()== Symbol.variableType.STRING){
                return true;
            }
        }
        return false;
    }

    private static String s_expr(TreeNode node){

        TreeNode childNode=node.getChildren().get(0);

        // It is a str_literal
        if(childNode.getState().getState()== State.stateType.STR_LITERAL){

            // Return everything but the quotes
            return (String)childNode.getChildren().get(0).getToken().getTokenText().replace("\"", "");
        }

        // It is the concat function
        else if(childNode.getToken().getTokenText().equals("concat")){

            // Concats the strings without quotes
            return s_expr(node.getChildren().get(2))+ s_expr(node.getChildren().get(4));
        }

        // It is the charAt
        else if(childNode.getToken().getTokenText().equals("charAt")){
            return s_expr(node.getChildren().get(2)).charAt(i_expr(node.getChildren().get(4)))+"";
        }

        // It is an id
        else{
            if(verifyStringID(childNode)){
                return (String) globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                        childNode.getState().getTokenIndex()).getValue();
            }

            if(!globalScope.hasSymbol(childNode.getToken().getTokenText(), childNode.getState().getTokenIndex())) {
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
            else if(globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                    childNode.getState().getTokenIndex()) == null){
                LogError.log(LogError.ErrorType.RUNTIME, "Can not do operations on a null operand, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
            else{
                LogError.log(LogError.ErrorType.RUNTIME, "Expected a string, got " +
                        childNode.getToken().getTokenType() + " '" +
                        childNode.getToken().getTokenText() + "'", childNode.getToken());
            }
        }

        // Should never get to this point
        return "";
    }

    private static Token getLeftMostToken(TreeNode node){
        TreeNode current=node;
        while(current.getToken()==null){
            current=current.getChildren().get(0);
        }
        return current.getToken();
    }
    private static Token getRightMostToken(TreeNode node){
        TreeNode current=node;
        while(current.getToken()==null){
            current=current.getChildren().get(current.getChildren().size()-1);
        }
        return current.getToken();
    }
}

