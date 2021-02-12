public class BasicLexer {
    
    String str;
    int ptr = 0;
    final String[] SYMBOL = {"+", "-", "*", "/", "%", ">", "<", ">=", "<=", "==", "!=", "!", "(", ")", "[", "]"};

    public BasicLexer(String str){
        this.str = str;
    }

    public String next(){
        /**
         * mode
         * 1: name mode
         * 2: num mode
         * 3: riteral mode
         * 4: symbol mode
         */
        String retval = "";
        int mode = 0;
        for(; ptr < str.length(); ptr++){
            char c = str.toCharArray()[ptr];
            if(isAlpha(c)){
                if(mode == 0){
                    mode = 1;
                }else if(mode != 1 && mode != 3){
                    break;
                }
                retval += String.valueOf(c);
            }else if(isNumeric(c)){
                if(mode == 0){
                    mode = 2;
                }else if(mode != 2 && mode != 3){
                    break;
                }
                retval += String.valueOf(c);
            }else if(c == ' '){
                if(mode == 3){
                    retval += String.valueOf(c);
                }else if(retval.length() != 0){
                    break;
                }
            }else{
                if(mode == 0){
                    if(c == '"'){
                        mode = 3;
                    }else{
                        mode = 4;
                    }
                }else if(c == '"'){
                    if(mode == 3){
                        
                    }
                }

            }
        }
        return retval;
    }

    private boolean isAlpha(char c){
        return (c >= 'A' && c <= 'Z');
    }

    private boolean isNumeric(char c){
        return (c >= '0' && c <= '9');
    }

}
