public class LogError {
    static String[] lines;
    static String fileName;

    enum ErrorType{
        SYNTAX {
            @Override
            public String toString() {
                return "Syntax Error";
            }
        },
        RUNTIME {
            @Override
            public String toString() {
                return "Runtime Error";
            }
        }
    }

    public static void setupHandler(String[] ls, String fN) {
        lines = ls;
        fileName = fN;
    }

    public static void log(ErrorType eType, String message, Token eToken){
        // If it is at the very end of the file
        if(eToken.getLineNum()>lines.length){
            System.out.println(eType + ": " + message + ", " + "\"" +
                    lines[eToken.getLineNum() - 2] +
                    "\" (" + fileName + ":" + (eToken.getLineNum() - 1) +
                    "," + eToken.getColumnStart() + "-" + eToken.getColumnEnd() + ")");
        }

        // Otherwise
        else {
            System.out.println(eType + ": " + message + ", " + "\"" +
                    lines[eToken.getLineNum() - 1] +
                    "\" (" + fileName + ":" + eToken.getLineNum() +
                    "," + eToken.getColumnStart() + "-" + eToken.getColumnEnd() + ")");
        }
        System.exit(1);
    }

    public static void log(ErrorType eType, String message, int lineNumber, int columnStart, int columnEnd){
        // If it is at the very end of the file
        if(lineNumber>lines.length){
            System.out.println(eType + ": " + message + ", " + "\"" +
                    lines[lineNumber - 2] +
                    "\" (" + fileName + ":" + (lineNumber - 1) +
                    "," + columnStart + "-" + columnEnd + ")");
        }

        // Otherwise
        else
        {
            System.out.println(eType + ": " + message + ", " + "\"" +
                    lines[lineNumber - 1] +
                    "\" (" + fileName + ":" + lineNumber +
                    "," + columnStart + "-" + columnEnd + ")");
        }
        System.exit(1);
    }
}
