public class BasicMachine {

    BasicMemory mem = new BasicMemory();
    Window w = new Window();
    
    public BasicMachine(){
        w.addMachine(this);
    }

    public void execute(String inputline){
        BasicParser parser = new BasicParser(w);
        int linenum;
        if((linenum = getSpecifiedLine(inputline)) != -1){
            mem.program_memory[linenum] = trimLineNumber(inputline);
            w.println(mem.program_memory[linenum]);
        }else{
            parser.exec(inputline, mem, -1);
        }
    }

    private int getSpecifiedLine(String sentence){
        int linenum = 0;
        if(sentence.length() == 0) return -1;
        for(char c : sentence.split(" ")[0].toCharArray()){
            if(!(c >= '0' && c <= '9')){
                return -1;
            }
            linenum *= 10;
            linenum += Integer.valueOf(String.valueOf(c));
        }
        return linenum;
    }

    private String trimLineNumber(String sentence){
        String buf = "";
        String[] splitted_sentence = sentence.split(" ");
        for(int i = 1; i < splitted_sentence.length; i++){
            buf += splitted_sentence[i];
            buf += " ";
        }
        return buf;
    }
}
