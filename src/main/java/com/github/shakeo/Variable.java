package com.github.shakeo;

public class Variable{

    public static final int TYPE_INT = 1, TYPE_REAL = 2, TYPE_STRING = 3;
    public String name;
    private int type = 0;
    private int int_val;
    private double real_val;
    private String str_val;

    public Variable(String name){
        this.name = name;
    }

    public Variable(){

    }

    public void store(int val){
        type = TYPE_INT;
        int_val = val;
    }

    public void store(double val){
        type = TYPE_REAL;
        real_val = val;
    }

    public void store(String val){
        type = TYPE_STRING;
        str_val = val;
    }

    public void store(Variable var){
        this.type = var.type;
        this.int_val = var.int_val;
        this.real_val = var.real_val;
        this.str_val = var.str_val;
    }

    public String getName(){
        return name;
    }

    public int getType(){
        return type;
    }

    public Integer getIntegerValue(){
        if(type != TYPE_INT) return null;
        return Integer.valueOf(int_val);
    }

    public Double getRealValue(){
        if(type != TYPE_REAL) return null;
        return Double.valueOf(real_val);
    }

    public String getStringValue(){
        if(type != TYPE_STRING) return null;
        return str_val;
    }

    public Variable add(Variable o){
        Variable retval = new Variable();
        if(this.getType() == Variable.TYPE_STRING || o.getType() == Variable.TYPE_STRING){
            retval.store(this.toString() + this.toString());
        }else if(this.getType() == Variable.TYPE_REAL || o.getType() == Variable.TYPE_REAL){
            retval.store(
                (double)( this.getType() == Variable.TYPE_REAL ? this.getRealValue() : this.getIntegerValue() ) + 
                (double)( o.getType() == Variable.TYPE_REAL ? o.getRealValue() : this.getIntegerValue() )
            );
        }else{
            retval.store(this.getIntegerValue() + o.getIntegerValue());
        }
        return retval;
    }

    public Variable sub(Variable o){
        Variable retval = new Variable();
        if(this.getType() == Variable.TYPE_STRING || o.getType() == Variable.TYPE_STRING){
            return null;
        }else if(this.getType() == Variable.TYPE_REAL || o.getType() == Variable.TYPE_REAL){
            retval.store(
                (double)( this.getType() == Variable.TYPE_REAL ? this.getRealValue() : this.getIntegerValue() ) -
                (double)( o.getType() == Variable.TYPE_REAL ? o.getRealValue() : this.getIntegerValue() )
            );
        }else{
            retval.store(this.getIntegerValue() - o.getIntegerValue());
        }
        return retval;
    }

    public Variable mul(Variable o){
        Variable retval = new Variable();
        if(this.getType() == Variable.TYPE_STRING || o.getType() == Variable.TYPE_STRING){
            return null;
        }else if(this.getType() == Variable.TYPE_REAL || o.getType() == Variable.TYPE_REAL){
            retval.store(
                (double)( this.getType() == Variable.TYPE_REAL ? this.getRealValue() : this.getIntegerValue() ) *
                (double)( o.getType() == Variable.TYPE_REAL ? o.getRealValue() : this.getIntegerValue() )
            );
        }else{
            retval.store(this.getIntegerValue() * o.getIntegerValue());
        }
        return retval;
    }

    public Variable div(Variable o){
        Variable retval = new Variable("temp");
        if(this.getType() == Variable.TYPE_STRING || o.getType() == Variable.TYPE_STRING){
            return null;
        }else if(this.getType() == Variable.TYPE_REAL || o.getType() == Variable.TYPE_REAL){
            if((double)( o.getType() == Variable.TYPE_REAL ? o.getRealValue() : this.getIntegerValue() ) == 0.0){
                return new Variable("_0div");
            }
            retval.store(
                (double)( this.getType() == Variable.TYPE_REAL ? this.getRealValue() : this.getIntegerValue() ) /
                (double)( o.getType() == Variable.TYPE_REAL ? o.getRealValue() : this.getIntegerValue() )
            );
        }else{
            if(o.getIntegerValue() == 0){
                return new Variable("_0div");
            }
            retval.store(this.getIntegerValue() / o.getIntegerValue());
        }
        return retval;
    }

    public Variable pm(){
        Variable retval = new Variable();
        if(this.getType() == Variable.TYPE_STRING){
            return null;
        }else if(this.getType() == Variable.TYPE_REAL){
            retval.store(this.getRealValue() * (-1.0));
        }else{
            retval.store(this.getIntegerValue() * (-1));
        }
        return retval;
    }

    public Variable relation(Variable o, String op){
        Variable retval = new Variable();
        if(this.getType() != o.getType()){
            return null;
        }else if(this.getType() == TYPE_INT){
            switch(op){
                case "==": retval.store(this.getIntegerValue() == o.getIntegerValue() ? 1 : 0); break;
                case "!=": retval.store(this.getIntegerValue() != o.getIntegerValue() ? 1 : 0); break;
                case ">" : retval.store(this.getIntegerValue() >  o.getIntegerValue() ? 1 : 0); break;
                case "<" : retval.store(this.getIntegerValue() <  o.getIntegerValue() ? 1 : 0); break;
                case ">=": retval.store(this.getIntegerValue() >= o.getIntegerValue() ? 1 : 0); break;
                case "<=": retval.store(this.getIntegerValue() <= o.getIntegerValue() ? 1 : 0); break;
            }
        }else if(this.getType() == TYPE_REAL){
            switch(op){
                case "==": retval.store(this.getRealValue() == o.getRealValue() ? 1 : 0); break;
                case "!=": retval.store(this.getRealValue() != o.getRealValue() ? 1 : 0); break;
                case ">" : retval.store(this.getRealValue() >  o.getRealValue() ? 1 : 0); break;
                case "<" : retval.store(this.getRealValue() <  o.getRealValue() ? 1 : 0); break;
                case ">=": retval.store(this.getRealValue() >= o.getRealValue() ? 1 : 0); break;
                case "<=": retval.store(this.getRealValue() <= o.getRealValue() ? 1 : 0); break;
            }
        }else if(this.getType() == TYPE_STRING){
            switch(op){
                case "==": retval.store(this.getStringValue().equals(o.getStringValue()) ? 1 : 0); break;
                default: return null;
            }
        }
        return retval;
    }

    public Variable bitOperation(Variable o, String op){
        Variable retval = new Variable();
        if(this.getType() == Variable.TYPE_STRING || o.getType() == Variable.TYPE_STRING){
            return null;
        }else if(this.getType() == Variable.TYPE_REAL || o.getType() == Variable.TYPE_REAL){
            return null;
        }else{
            switch(op){
                case "AND": retval.store(this.getIntegerValue() & o.getIntegerValue()); break;
                case "OR": retval.store(this.getIntegerValue() | o.getIntegerValue()); break;
                case "XOR" : retval.store(this.getIntegerValue() ^ o.getIntegerValue()); break;
            }
        }
        return retval;
    }

    @Override
    public String toString(){
        if(type == TYPE_INT){
            return String.valueOf(int_val);
        }else if(type == TYPE_REAL){
            return String.valueOf(real_val);
        }else if(type == TYPE_STRING){
            return String.valueOf(str_val);
        }
        return null;
    }

    public Object unparse() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isArray() {
        // TODO Auto-generated method stub
        return false;
    }
}