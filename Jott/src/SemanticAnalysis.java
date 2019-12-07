import java.util.HashMap;
import java.util.ArrayList;

public class SemanticAnalysis {

    private static final String IF="if";
    private static final String WHILE="while";
    private static final String FOR="for";

    private static Reference globalScope;
    private static Reference currentScope;

    private static ArrayList<String> toPrint;

    public static void output(TreeNode root, Reference global){
        toPrint = new ArrayList<String>();
        globalScope=global;
        program(root);

        //Print all statements
        for (String str : toPrint)
        {
            System.out.println(str);
        }
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

        State.stateType stateType=node.getChildren().get(0).getState().getState();

        if(stateType== State.stateType.TYPE){
            functDec(node, node.getChildren().get(1).getToken().getTokenText());
        }

        else if(stateType == State.stateType.F_CALL){
            funct_call(node.getChildren().get(0));
        }

        // Evaluates the print statement
        else if(node.getChildren().get(0).getState().getState() == State.stateType.PRINT){
            print(node.getChildren().get(0));
        }

        // Evaluates the asmt statement
        else if(node.getChildren().get(0).getState().getState() == State.stateType.ASMT){
            asmt_stmt(node.getChildren().get(0));
        }

        else if(node.getChildren().get(0).getState().getState()== State.stateType.R_ASMT){
            r_asmt(node.getChildren().get(0));
        }

        // Evaluates the if statement
        else if(node.getChildren().get(0).getState().getState()== State.stateType.IF_STMT){

            TreeNode grandchildNode=node.getChildren().get(0);

            if_stmt(grandchildNode);
        }

        // Evaluates the while statement
        else if(node.getChildren().get(0).getState().getState()== State.stateType.WHILE_LOOP){

            TreeNode childNode=node.getChildren().get(0);

            while_loop(childNode);
        }

        // Evaluates the for loop
        else if(node.getChildren().get(0).getState().getState()== State.stateType.FOR_LOOP){
            TreeNode childNode=node.getChildren().get(0);

            for_loop(childNode);
        }

        // Evaluates the <expr><end_statement> -- doesn't actually change the state
        else{

            // If the expr is an ID
            if(node.getChildren().get(0).getState().getState()== State.stateType.ID){
                String id=node.getChildren().get(0).getToken().getTokenText();

                // If it is a function call
                if(node.getChildren().get(2).getState().getState()== State.stateType.START_PAREN){

                    // The function call with no parameters
                    if(node.getChildren().get(3).getState().getState()== State.stateType.END_PAREN){

                    }

                    // The function call with some parameters
                    else if(node.getChildren().get(4).getState().getState()== State.stateType.END_PAREN){

                    }

                    else{
                        LogError.log(LogError.ErrorType.SYNTAX,
                                "Bad function syntax",
                                node.getChildren().get(0).getToken());
                    }

                }

                // If it is a valid id
                else if(globalScope.hasSymbol(id, node.getChildren().get(0).getState().getTokenIndex())){

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

    private static void if_stmt(TreeNode node){
        // If the expression is an i_expr
        if(node.getChildren().get(2).getChildren().get(0).getState().getState()== State.stateType.I_EXPR){
            if(i_expr(node.getChildren().get(2))!=0){
                b_stmt_list(node.getChildren().get(5));
            }
            else if(node.getChildren().size()>7){
                b_stmt_list(node.getChildren().get(9));
            }
        }

        // If the expression is a d_expr
        else if(node.getChildren().get(2).getChildren().get(0).getState().getState()== State.stateType.D_EXPR){
            if(d_expr(node.getChildren().get(2))!=0){
                b_stmt_list(node.getChildren().get(5));
            }
            else if(node.getChildren().size()>7){
                b_stmt_list(node.getChildren().get(9));
            }
        }

        // If the expression is a s_expr
        else{
            if(s_expr(node.getChildren().get(2).getChildren().get(0))!=null){
                b_stmt_list(node.getChildren().get(5));
            }
            else if(node.getChildren().size()>7){
                b_stmt_list(node.getChildren().get(9));
            }
        }
    }

    private static void while_loop(TreeNode node){
        // If the expression is an i_expr
        if(node.getChildren().get(2).getState().getState()== State.stateType.I_EXPR){
            while(i_expr(node.getChildren().get(2))!=0){
                b_stmt_list(node.getChildren().get(5));
            }
        }

        // If the expression is a d_expr
        else if(node.getChildren().get(2).getState().getState()== State.stateType.D_EXPR){
            while(d_expr(node.getChildren().get(2))!=0){
                b_stmt_list(node.getChildren().get(5));
            }
        }

        // If the expression is a s_expr
        else{
            while(s_expr(node.getChildren().get(2))!=null){
                b_stmt_list(node.getChildren().get(5));
            }
        }
    }

    private static void for_loop(TreeNode node){
        TreeNode childNode=node.getChildren().get(3);

        asmt_stmt(node.getChildren().get(2));

        // If the expression is an i_expr
        if(childNode.getState().getState()== State.stateType.I_EXPR){
            while(i_expr(childNode)!=0){
                b_stmt_list(node.getChildren().get(8));
                r_asmt(node.getChildren().get(5));
            }
        }

        // If the expression is a d_expr
        else if(childNode.getState().getState()== State.stateType.D_EXPR){
            while(d_expr(childNode)!=0){
                b_stmt_list(node.getChildren().get(8));
                r_asmt(node.getChildren().get(5));
            }
        }

        // If the expression is a s_expr
        else{
            while(s_expr(childNode)!=null){
                b_stmt_list(node.getChildren().get(8));
                r_asmt(node.getChildren().get(5));
            }
        }
    }

    private static void functDec(TreeNode node, String name){
        Reference function=globalScope.getReferenceWithName(name);

        if(node.getChildren().get(3).getState().getState()== State.stateType.P_LIST){
            fc_p_list(node.getChildren().get(3), function, 0);
        }

        // If the code starts at 5
        if(node.getChildren().get(5).getState().getState()== State.stateType.F_STMT){
            function.addFunctionCode(node.getChildren().get(5));
        }

        // Otherwise code starts at 6
        else{
            function.addFunctionCode(node.getChildren().get(6));
        }
    }

    private static void funct_call(TreeNode node){
        Reference r=globalScope.getReferenceWithName(node.getChildren().get(0).getToken().getTokenText());
        TreeNode getFunctNode=r.getFunctionCode();
        HashMap<String, Symbol> parameters=new HashMap<>();
        //r.clearFunctionSymbols();
        if(node.getChildren().get(2).getChildren().size()>0){
            p_list(node.getChildren().get(2), r, 0, parameters);
        }
        r.addFrameStack(r.getPararmeters(), r.getSymbols());
        r.addParameters(parameters);
        f_stmt(getFunctNode, r);
        r.setSymbols(r.deleteFrameStack());
    }

    private static void p_list(TreeNode node, Reference r, int num, HashMap<String, Symbol> parameters){
        String param=r.getParameter(num);
        parameters.put(param, new Symbol(r.getSymbol(param).getType(), param));
        if(node.getChildren().get(0).getState().getState()== State.stateType.I_EXPR){
            parameters.get(param).changeValue(i_expr(node.getChildren().get(0)));
        }
        else if(node.getChildren().get(0).getState().getState()== State.stateType.D_EXPR){
            parameters.get(param).changeValue(d_expr(node.getChildren().get(0)));
        }
        else if(node.getChildren().get(0).getState().getState()== State.stateType.S_EXPR){
            parameters.get(param).changeValue(s_expr(node.getChildren().get(0)));
        }
        else if(node.getChildren().get(0).getState().getState()== State.stateType.F_CALL){
            funct_call(node.getChildren().get(0));
            Reference calledFunction=globalScope.getReferenceWithName(node.getChildren().get(0).getChildren().get(0).getToken().getTokenText());
            parameters.get(param).changeValue(calledFunction.getReturnedValue().getValue());
        }
        if(node.getChildren().size()==3){
            p_list(node.getChildren().get(2), r, ++num, parameters);
        }
    }

    private static void fc_p_list(TreeNode node, Reference func, int num){
        func.addParameter(node.getChildren().get(1).getToken().getTokenText(), num);
        if(node.getChildren().size()==4){
            fc_p_list(node.getChildren().get(3), func, num+1);
        }
    }

    private static void f_stmt(TreeNode node, Reference function){
        if(node.getChildren().get(0).getState().getState()== State.stateType.STMT){
            TreeNode asmt=node.getChildren().get(0).getChildren().get(0);
            if(asmt.getState().getState()== State.stateType.ASMT){

                String id=asmt.getChildren().get(1).getToken().getTokenText();

                switch(asmt.getChildren().get(0).getToken().getTokenText()){
                    case "Integer":
                        function.getSymbols().put(id, new Symbol(ValidType.Integer, id));
                        break;
                    case "Double":
                        function.getSymbols().put(id, new Symbol(ValidType.Double, id));
                        break;
                    case "String":
                        function.getSymbols().put(id, new Symbol(ValidType.String, id));
                }
            }
            stmt(node.getChildren().get(0));
            if(node.getChildren().get(1).getState().getState()== State.stateType.F_STMT){
                f_stmt(node.getChildren().get(1), function);
            }
        }
        else if(node.getChildren().get(0).getState().getState()== State.stateType.RETURN){
            TreeNode n=node.getChildren().get(1).getChildren().get(0);
            switch(n.getState().getState()){
                case I_EXPR:
                    function.getReturnedValue().changeValue(i_expr(n));
                    break;
                case D_EXPR:
                    function.getReturnedValue().changeValue(d_expr(n));
                    break;
                case S_EXPR:
                    function.getReturnedValue().changeValue(s_expr(n));
                    break;
                default:
            }
        }
    }

    private static void b_stmt_list(TreeNode node){
        b_stmt(node.getChildren().get(0));
        if(node.getChildren().size()==2){
            b_stmt_list(node.getChildren().get(1));
        }
    }

    private static void b_stmt(TreeNode node){
        TreeNode childNode=node.getChildren().get(0);

        if(childNode.getChildren().get(0).getState().getState()== State.stateType.F_CALL){
            funct_call(childNode.getChildren().get(0));
        }

        // The reassignment state
        else if(childNode.getState().getState()== State.stateType.R_ASMT){
            r_asmt(childNode);
        }

        // The print state
        else if(childNode.getState().getState()== State.stateType.PRINT){
            print(childNode);
        }

        else if(childNode.getState().getState()== State.stateType.IF_STMT){
            if_stmt(childNode);
        }

        else if(childNode.getState().getState()== State.stateType.WHILE_LOOP){
            while_loop(childNode);
        }

        else if(childNode.getState().getState()== State.stateType.FOR_LOOP){
            for_loop(childNode);
        }

        // The expression state
        else{
            TreeNode grandchildNode=childNode.getChildren().get(0);

            // The grandchildNode is an i_expr
            if(grandchildNode.getState().getState()== State.stateType.I_EXPR){
                i_expr(grandchildNode);
            }

            // The grandchildNode is a d_expr
            else if(grandchildNode.getState().getState()== State.stateType.D_EXPR){
                d_expr(grandchildNode);
            }

            // The grandchildNode is an s_expr
            else{
                s_expr(grandchildNode);
            }
        }
    }

    private static void r_asmt(TreeNode node){
        TreeNode childNode=node.getChildren().get(0);
        TreeNode grandchildNode=node.getChildren().get(2).getChildren().get(0);

        // Gets the symbol from the symbol table
        Symbol scopedSymbol=globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                childNode.getState().getTokenIndex());
        if(scopedSymbol==null){
            LogError.log(LogError.ErrorType.RUNTIME, "Invalid reassignment", childNode.getToken());
        }
        else if(scopedSymbol.getType()== ValidType.Integer &&
                grandchildNode.getState().getState()== State.stateType.I_EXPR){
            scopedSymbol.changeValue(i_expr(grandchildNode));
        }
        else if(scopedSymbol.getType()== ValidType.Double &&
                grandchildNode.getState().getState()== State.stateType.D_EXPR){
            scopedSymbol.changeValue(d_expr(grandchildNode));
        }
        else if(scopedSymbol.getType()== ValidType.String &&
                grandchildNode.getState().getState()== State.stateType.S_EXPR){
            scopedSymbol.changeValue(s_expr(grandchildNode));
        }
        else{
            String gotType = "an unknown type";
            switch (grandchildNode.getState().getState())
            {
                case I_EXPR:
                    gotType = "Integer";
                    break;
                case D_EXPR:
                    gotType = "Double";
                    break;
                case S_EXPR:
                    gotType = "String";
                    break;
                default:
                    break;
            }
            LogError.log(LogError.ErrorType.SYNTAX, "Invalid reassignment, expected " + scopedSymbol.getType()
                    + " got " + gotType
                    , childNode.getToken());
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
                toPrint.add(String.valueOf(childSymbol.getValue()));
            }
            else{
                LogError.log(LogError.ErrorType.SYNTAX, "Expected a valid ID, got " +
                        node.getToken().getTokenType()+" '"+node.getToken().getTokenText()+"'", node.getToken());
            }
        }
        else {

            // If the statment in the print is an int expression
            if(stmtNode.getState().getState()== State.stateType.I_EXPR){
                toPrint.add(String.valueOf(i_expr(stmtNode)));
            }

            // If the statement in the print is a double expression
            else if(stmtNode.getState().getState()== State.stateType.D_EXPR){
                toPrint.add(String.valueOf(d_expr(stmtNode)));
            }

            // If the statement in the print is a String expression
            else if(stmtNode.getState().getState()== State.stateType.S_EXPR){
                toPrint.add(String.valueOf(s_expr(stmtNode)));
            }

            // If it is a function call
            else if(stmtNode.getState().getState()== State.stateType.F_CALL){
                Reference r=globalScope.getReferenceWithName(stmtNode.getChildren().get(0).getToken().getTokenText());

                funct_call(stmtNode);
                if(r.getReturnType()!= ValidType.Void){
                    toPrint.add(String.valueOf(r.getReturnedValue().getValue()));
                }

                else{
                    //LogError.log(LogError.ErrorType.SYNTAX, "Invalid ");
                }
            }
        }
    }

    private static void asmt_stmt(TreeNode node){

        TreeNode childNode=node.getChildren().get(0);
        String id=node.getChildren().get(1).getToken().getTokenText();
        TreeNode exprNode=node.getChildren().get(3);

        if(exprNode.getState().getState()== State.stateType.F_CALL){
            funct_call(exprNode);

            Symbol s=globalScope.getReferenceWithName(exprNode.getChildren().get(0).getToken().getTokenText()).getReturnedValue();

            if(globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).getType()==s.getType()){
                globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).changeValue(s.getValue());
            }
            else{
                LogError.log(LogError.ErrorType.SYNTAX,
                        globalScope.getScopedSymbol(id,
                                childNode.getState().getTokenIndex()).getType()+" expected but got "+s.getType(),
                        node.getChildren().get(1).getToken());
            }
        }

        // An Integer assignment statement
        else if(childNode.getToken().getTokenText().equals("Integer")){

            //If this is a valid id
            if(globalScope.hasSymbol(id, childNode.getState().getTokenIndex()) &&
                    globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).getType()
                            == ValidType.Integer){
                if(exprNode.getState().getState()== State.stateType.F_CALL){
                    funct_call(exprNode);
                    globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).changeValue(
                            globalScope.getReferenceWithName(exprNode.getChildren().get(0).getToken().getTokenText()).getReturnedValue()
                    );
                }
                else {
                    globalScope.getScopedSymbol(id, childNode.getState().getTokenIndex()).changeValue(i_expr(exprNode));
                }
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
                            == ValidType.Double){
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
                            == ValidType.String){
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
                        ValidType.Integer){
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
                String type = globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                        childNode.getState().getTokenIndex()).getType().toString();
                LogError.log(LogError.ErrorType.SYNTAX, "Expected an Integer, got " +
                        type + " '" +
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

        if(node.getChildren().get(0).getState().getState()== State.stateType.S_EXPR
                && node.getChildren().get(2).getState().getState()== State.stateType.S_EXPR
                && node.getChildren().get(1).getState().getState()== State.stateType.REL_OP){
            String firstOp=s_expr(node.getChildren().get(0));
            String secondOp=s_expr(node.getChildren().get(2));
            switch(node.getChildren().get(1).getToken().getTokenType()){
                case Less:
                    int index=0;
                    while(index<firstOp.length() && index<secondOp.length()){
                        if(firstOp.charAt(index)<secondOp.charAt(index)){
                            return 1;
                        }
                        index++;
                    }
                    if(firstOp.length()<secondOp.length()){
                        return 1;
                    }
                    return 0;
                case Greater:
                    index=0;
                    while(index<firstOp.length() && index<secondOp.length()){
                        if(firstOp.charAt(index)>secondOp.charAt(index)){
                            return 1;
                        }
                        index++;
                    }
                    if(firstOp.length()>secondOp.length()){
                        return 1;
                    }
                    return 0;
                case LessEq:
                    index=0;
                    while(index<firstOp.length() && index<secondOp.length()){
                        if(firstOp.charAt(index)<secondOp.charAt(index)){
                            return 1;
                        }
                        index++;
                    }
                    if(firstOp.length()<=secondOp.length()){
                        return 1;
                    }
                    return 0;
                case GreaterEq:
                    index=0;
                    while(index>firstOp.length() && index>secondOp.length()){
                        if(firstOp.charAt(index)<secondOp.charAt(index)){
                            return 1;
                        }
                        index++;
                    }
                    if(firstOp.length()>=secondOp.length()){
                        return 1;
                    }
                    return 0;
                case Eq:
                    index=0;
                    while(index<firstOp.length() && index<secondOp.length()){
                        if(firstOp.charAt(index)!=secondOp.charAt(index)){
                            return 0;
                        }
                        index++;
                    }
                    if(firstOp.length()==secondOp.length()){
                        return 1;
                    }
                case NotEq:
                    index=0;
                    while(index<firstOp.length() && index<secondOp.length()){
                        if(firstOp.charAt(index)!=secondOp.charAt(index)){
                            return 1;
                        }
                        index++;
                    }
                    if(firstOp.length()!=secondOp.length()){
                        return 1;
                    }
                    return 0;
            }
        }

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
                case Less:
                    if(firstOp<secondOp){
                        return 1;
                    }
                    return 0;
                case Greater:
                    if(firstOp>secondOp){
                        return 1;
                    }
                    return 0;
                case LessEq:
                    if(firstOp<=secondOp){
                        return 1;
                    }
                    return 0;
                case GreaterEq:
                    if(firstOp>=secondOp){
                        return 1;
                    }
                    return 0;
                case Eq:
                    if(firstOp==secondOp){
                        return 1;
                    }
                    return 0;
                case NotEq:
                    if(firstOp!=secondOp){
                        return 1;
                    }
                    return 0;
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
                            node.getState().getTokenIndex()).getType()== ValidType.Double){
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
                String type = globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                        childNode.getState().getTokenIndex()).getType().toString();
                LogError.log(LogError.ErrorType.RUNTIME, "Expected a Double, got " +
                        type + " '" +
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
                case Less:
                    if(firstOp<secondOp){
                        return 1;
                    }
                    return 0;
                case Greater:
                    if(firstOp>secondOp){
                        return 1;
                    }
                    return 0;
                case LessEq:
                    if(firstOp<=secondOp){
                        return 1;
                    }
                    return 0;
                case GreaterEq:
                    if(firstOp>=secondOp){
                        return 1;
                    }
                    return 0;
                case Eq:
                    if(firstOp==secondOp){
                        return 1;
                    }
                    return 0;
                case NotEq:
                    if(firstOp!=secondOp){
                        return 1;
                    }
                    return 0;
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
                            node.getState().getTokenIndex()).getType()== ValidType.String){
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
                String type = globalScope.getScopedSymbol(childNode.getToken().getTokenText(),
                        childNode.getState().getTokenIndex()).getType().toString();
                LogError.log(LogError.ErrorType.RUNTIME, "Expected a String, got " +
                        type + " '" +
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

