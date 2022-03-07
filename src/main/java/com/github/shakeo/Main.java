package com.github.shakeo;


public class Main{

    public static void main(String[] args) {
        Window w = new Window();
        BasicMachine machine = new BasicMachine(w);
        w.addMachine(machine);
    }
}