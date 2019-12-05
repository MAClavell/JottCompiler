public class Symbol <T>{

    private ValidType type;
    private String varName;
    private T value;

    public Symbol(ValidType type, String varName){
        this.type=type;
        this.varName=varName;
    }

    public ValidType getType(){
        return type;
    }

    public String getVarName(){
        return varName;
    }

    public void changeValue(T newValue){
        value=newValue;
    }

    public T getValue(){
        return value;
    }

    public boolean sameType(Symbol other){
        return this.type==other.type;
    }
}
