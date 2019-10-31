import java.util.ArrayList;
import java.util.HashMap;

public class Reference {
    enum ReferenceType{GLOBAL, FUNCTION, IF, WHILE, FOR}

    private static int referenceNumber=0;

    private int startToken;
    private int endToken;
    private ReferenceType type;
    private HashMap<String, Symbol> scopedSymbols;
    private HashMap<String, Reference> scopes;

    public Reference(int startToken, ReferenceType referenceType){
        this.startToken=startToken;
        this.type=referenceType;
        this.scopedSymbols=new HashMap<String, Symbol>();
        this.scopes=new HashMap<String, Reference>();
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

    public void addEndToken(int token){
        this.endToken=token;
    }

    public void addSymbolToScope(String symbolName, Symbol s){
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
     * Gets the reference that has an end at a certain point
     * @param end the end that we wish to look for
     * @return the reference
     */
    public Reference getReferenceWithEnd(int end){
        for(Reference r:scopes.values()){
            Reference target=r.getReferenceWithEnd(end);
            if(r.getEndToken()==end){
                return r;
            }
            else if(target!=null){
                return target;
            }
        }
        return null;
    }

    /**
     * Gets the scope at a certain point
     * @param token the token of the reference
     * @return
     */
    public Reference getReferenceAt(int token){
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
        Symbol scopedSymbol=scope.getReferenceAt(token).getScopedSymbol(name, token);

        // There is a symbol with that name in a smaller scope if it is not null
        if(scopedSymbol!=null){
            symbol=scopedSymbol;
        }
        return symbol;
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
     * @param scope the scope to add a reference to
     * @param start the start of the scope
     * @param name the name of the function
     * @param type the type of the reference
     */
    public void addReference(Reference scope, int start, String name, ReferenceType type){
        if(type!=ReferenceType.FUNCTION){
            scope.addReferenceToScope(referenceNumber+"", new Reference(start, type));
            referenceNumber++;
        }
        else{
            scope.addReferenceToScope(name, new Reference(start, type));
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