import java.util.ArrayList;

public class Reference {
    private int startToken;
    private int endToken;

    public Reference(int startToken, int endToken){
        this.startToken=startToken;
        this.endToken=endToken;
    }

    public int getStartToken(){
        return startToken;
    }

    public int getEndToken(){
        return endToken;
    }

    public static int getStartWithEnd(ArrayList<Reference> references, int endToken){
        for(Reference reference:references){
            if(reference.getEndToken()==endToken){
                return reference.getStartToken();
            }
        }
        return -1;
    }
}
