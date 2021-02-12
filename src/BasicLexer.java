public class BasicLexer {

    /**
     * test driver
     */
    public static void main(String[] args) {
        BasicLexer b = new BasicLexer("PRINT \"AA A\"/140433+B");
        int s;
        while((s = b.getToken()) != BasicLexer.END){
            System.out.println(s);
            System.out.println("Token = " + String.valueOf(
                s == BasicLexer.NAME ? b.getStringValue() :
                s == BasicLexer.STRING ? b.getStringValue() :
                s == BasicLexer.NUMBER ? b.numberIsReal() ? b.getRealValue() : b.getIntegetValue() : 
                s == BasicLexer.OPERATOR ? b.getStringValue() :
                s == BasicLexer.END ? "" : ""
            ));
        }
    }

    public static final int NAME = 0, NUMBER = 1, STRING = 2, OPERATOR = 3, END = -1;

    private String str;
    private int ptr = 0;
    private final String[] OPERATOR_LIST = {"+", "-", "*", "/", "%", ">", "<", ">=", "<=", "==", "!=", "!", "(", ")", "[", "]"};
    private String string_val = "";
    private int int_val;
    private double real_val;

    private boolean isreal = false;

    public BasicLexer(String str){
        this.str = str.trim();
    }

    public int getToken(){
        String buf = "";
        int mode = 0;
        boolean escape = false;
        isreal = false;

        if(ptr >= str.length()) return END;

        char c = str.toCharArray()[ptr];

        while(c == ' ') c = str.toCharArray()[++ptr];

        mode  = isAlpha(c)          ? 1 : //name mode
                isNumeric(c)        ? 2 : //num mode
                isOperatorSymbol(c) ? 3 : //symbol mode
                c == '"'            ? 4 : //riteral mode
                -1;                       //?????
        
        buf += String.valueOf(c);
        ptr++;

        String attached_str = str + " ";

        for(; ptr < attached_str.length(); ptr++){
            c = attached_str.toCharArray()[ptr];
            switch(mode){
            case 1:
                if(!isAlpha(c) && !isNumeric(c) && c != '_'){
                    string_val = buf;
                    return NAME;
                }
                buf += String.valueOf(c);
                break;
            case 2:
                if(!isNumeric(c) && c != '.'){
                    if(isreal){
                        real_val = Double.valueOf(buf);
                    }else{
                        int_val = Integer.valueOf(buf);
                    }
                    return NUMBER;
                }
                if(c == '.') isreal = true;
                buf += String.valueOf(c);
                break;
            case 3:
                if(!isOperator(buf + String.valueOf(c))){
                    string_val = buf;
                    return OPERATOR;
                }
                buf += String.valueOf(c);
                break;
            case 4:
                if(!escape){
                    if(c == '\\'){
                        escape = true;
                    }else if(c == '"'){
                        ptr++;
                        string_val = buf + "\"";
                        return STRING;
                    }
                    buf += String.valueOf(c);
                }else{
                    escape = false;
                }
                break;
            }
        }

        return mode == 1 ? NAME :
               mode == 2 ? NUMBER :
               mode == 3 ? OPERATOR :
               mode == 4 ? STRING :
               END;
    }

    public String getStringValue(){
        return string_val;
    }

    public int getIntegetValue(){
        return int_val;
    }

    public double getRealValue(){
        return real_val;
    }

    public boolean numberIsReal(){
        return isreal;
    }

    private boolean isAlpha(char c){
        return (c >= 'A' && c <= 'Z');
    }

    private boolean isNumeric(char c){
        return (c >= '0' && c <= '9');
    }

    private boolean isOperatorSymbol(char c){
        return  c == '+' || c == '-' ||
                c == '*' || c == '/' ||
                c == '%' || c == '<' ||
                c == '>' || c == '=' ||
                c == '!' || c == '(' ||
                c == ')' || c == '[' ||
                c == ']';
    }

    private boolean isOperator(String t){
        for(String o : OPERATOR_LIST){
            if(o.equals(t))
                return true;
        }
        return false;
    }
}
