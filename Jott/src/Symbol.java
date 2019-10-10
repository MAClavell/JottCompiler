public class Symbol <T>{
    enum variableType{INTEGER, DOUBLE, STRING}

    private variableType type;
    private String varName;
    private T value;

    public Symbol(variableType type, String varName){
        this.type=type;
        this.varName=varName;
    }

    public variableType getType(){
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
