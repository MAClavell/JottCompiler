import java.util.HashMap;

enum ValidType{Integer, Double, String, Void}

public class Reference {

    private static int referenceNumber=0;

    private int startToken;
    private int endToken;
    private ValidType returnType;
    private HashMap<String, Symbol> scopedSymbols;
    private HashMap<String, Reference> scopes;

    public Reference(int startToken, ValidType returnType){
        this.startToken=startToken;
        this.scopedSymbols=new HashMap<String, Symbol>();
        this.scopes=new HashMap<String, Reference>();
        this.returnType = returnType;
    }

    public Reference(ValidType returnType){
        this.scopedSymbols=new HashMap<String, Symbol>();
        this.scopes=new HashMap<String, Reference>();
        this.returnType = returnType;
    }

    /**
     * Gets the start token
     * @return the start token
     */
    public int getStartToken(){
        return startToken;
    }

    /**
     * Gets the end token
     * @return the end token
     */
    public int getEndToken(){
        return endToken;
    }

    /**
     * Adds the end token index
     * @param token the token index of the end
     */
    private void addEndToken(int token){
        this.endToken=token;
    }

    /**
     * Adds a symbol to the currend scope
     * @param symbolName the name of the function
     * @param s the Symbol to add
     */
    private void addSymbolToScope(String symbolName, Symbol s){
        scopedSymbols.put(symbolName, s);
    }

    /**
     * Adds a reference to the scope
     * @param referenceName the name associated with the reference
     * @param newReference the new reference
     */
    private void addReferenceToScope(String referenceName, Reference newReference){
        scopes.put(referenceName, newReference);
    }

    /**
     * Gets the scope at a certain point
     * @param token the token of the reference
     * @return
     */
    private Reference getReferenceAt(int token){
        for(Reference r:scopes.values()){
            if(r.getStartToken()<=token && r.getEndToken()>=token){
                return r;
            }
        }
        return null;
    }

    /**
     * Gets the reference corresponding to a name. Will help with function calls
     * @param name the name of the function
     * @return a reference (function) with a certain name
     */
    public Reference getReferenceWithName(String name){
        // If the scope is within the reference
        if(scopes.get(name)!=null) {
            return scopes.get(name);
        }
        else{
            // Recursively look for the scope within the subscopes
            for(Reference r:scopes.values()){
                Reference target=r.getReferenceWithName(name);
                if(target!=null){
                    return target;
                }
            }
        }
        return null;
    }


    public void addSymbolAt(int token, String name, Symbol symbol){
        Reference scope=getReferenceAt(token);
        if(scope!=null){
            scope.addSymbolToScope(name, symbol);
        }
        else{
            scopedSymbols.put(name, symbol);
        }
    }

    /**
     * Gets the most scoped Symbol in the scope/subscopes
     * @param name the name of the token
     * @param token the token number that the token occurs on
     * @return the most scoped Symbol
     */
    public Symbol getScopedSymbol(String name, int token){
        // If this isn't the last scope
        if(scopes.size()==0){
            return scopedSymbols.get(name);
        }
        // Gets the symbol with the name in the scope
        Symbol symbol=scopedSymbols.get(name);

        // Gets the scope if it's there
        Reference scope=getReferenceAt(token);

        // Recursively calls the function to get its scoped symbol
        if(scope != null) {
            Symbol scopedSymbol = scope.getScopedSymbol(name, token);
            // There is a symbol with that name in a smaller scope if it is not null
            if(scopedSymbol!=null){
                symbol=scopedSymbol;
            }
        }
        return symbol;
    }

    /**
     * Get the return state of this scope
     * @return the return type
     */
    public ValidType getReturnType()
    {
        return returnType;
    }

    /**
     * Returns whether or not the symbol is in the reference or a subreference
     * @param name the name of the symbol
     * @param token the number of the token
     * @return
     */
    public boolean hasSymbol(String name, int token){
        return getScopedSymbol(name, token)!=null;
    }

    /**
     * Adds a new reference to the scope
     * @param start the start of the scope
     * @param name the name of the function
     */
    public Reference addReference(int start, String name, ValidType returnType){
        Reference r = new Reference(start, returnType);
        addReferenceToScope(name, r);
        referenceNumber++;
        return r;
    }

    /**
     * Adds a new reference to the scope
     * @param name the name of the function
     */
    public Reference addReference(String name, ValidType returnType){
        Reference r = new Reference(returnType);
        addReferenceToScope(name, r);
        referenceNumber++;
        return r;
    }

    /**
     * Set the start token of this scope
     * @param start index to start
     */
    public void setStartIndex(int start) {
        this.startToken = start;
    }

    /**
     * Adds the end brace to the reference
     * @param token the end of the current reference
     */
    public void endRecentReference(int token){
        Reference closestReference=null;
        for(Reference scope:scopes.values()){
            if(scope.getStartToken()<token && (closestReference==null || closestReference.getStartToken()<=scope.getStartToken())){
                closestReference=scope;
            }
        }
        if(closestReference==null){
            endToken=token;
        }
        else{
            closestReference.endRecentReference(token);
        }
    }

    /**
     * Adds a new symbol to the current scope
     * @param scope the scope to look through for the reference
     * @param s the symbol to add
     * @param token the current token
     * @param name the name of the symbol
     */
    public static void addSymbol(Reference scope, Symbol s, int token, String name){
        Reference scopeToAdd=scope.getReferenceAt(token);
        if(scopeToAdd==null){
            scopeToAdd=scope;
        }
        scopeToAdd.addSymbolToScope(name, s);
    }
}