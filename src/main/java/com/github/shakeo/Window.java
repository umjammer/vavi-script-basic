
package com.github.shakeo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JPanel;

import vavi.util.Debug;


public class Window extends JFrame implements KeyListener, BasicParser.View {

    private static final long serialVersionUID = -8255319694373975038L;

    WindowPanel panel;
    final int BUF_COL = 64;
    final int BUF_ROW = 48;
    char buffer[] = new char[64 * 48];
    int input_start = 0;
    String lastSentence = "";
    int buf_ptr = 0;
    BasicMachine m;

    public Window() {
        Font font1 = null;
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, Window.class.getResourceAsStream("/8801.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font1);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException(e);
        }

        setTitle("BASIC");
        setSize(640, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
        panel = new WindowPanel(buffer);
//        panel.setFont(font1);
        add(panel);

        println("MY BASIC VER 0.1");
        println("COPYLIGHT (C) 2021 BY SHAKEO");
        println("");
        println("OK");
        input_start = buf_ptr;
        panel.setBufPtr(buf_ptr);

        setVisible(true);
    }

    public void addMachine(BasicMachine machine) {
        m = machine;
    }

    public void inputChar(char c) {
        print(String.valueOf(c));
        panel.repaint();
    }

    public void print(String arg) {
        for (char c : arg.toCharArray()) {
            if (buf_ptr >= BUF_ROW * BUF_COL) {
                lineFlush();
            }
            buffer[buf_ptr] = c;
            buf_ptr++;
        }
        panel.repaint();
    }

    public void println(String arg) {
        print(arg);
        buf_ptr += BUF_COL - (buf_ptr % BUF_COL);
        if (buf_ptr >= BUF_ROW * BUF_COL) {
            lineFlush();
        }
        panel.repaint();
    }

    public void backSpace() {
        if (buf_ptr != input_start) {
            buf_ptr--;
            buffer[buf_ptr] = ' ';
        }
        panel.repaint();
    }

    public void screenFlush() {
        for (int i = 0; i < BUF_ROW * BUF_COL; i++) {
            buffer[i] = ' ';
        }
        buf_ptr = 0;
        panel.repaint();
    }

    public void lineFlush() {
        for (int i = 0; i < BUF_ROW; i++) {
            if (i != BUF_ROW - 1) {
                for (int j = 0; j < BUF_COL; j++) {
                    buffer[i * BUF_COL + j] = buffer[(i + 1) * BUF_COL + j];
                }
            } else {
                for (int j = 0; j < BUF_COL; j++) {
                    buffer[i * BUF_COL + j] = ' ';
                }
            }
        }
        buf_ptr = (BUF_ROW - 1) * BUF_COL;
        input_start -= BUF_COL;
        panel.repaint();
    }

    private void readLastString() {
        for (int i = input_start; i < buf_ptr; i++) {
            lastSentence += String.valueOf(buffer[i]);
        }
    }

//     private void printBufferToStdout() {
//         for(int i = 0; i < BUF_ROW; i++){
//             for(int j = 0; j < BUF_COL; j++)
//                 System.out.print(buffer[i * BUF_ROW + j]);
//             System.out.println();
//         }
//     }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = Character.toUpperCase(e.getKeyChar());
Debug.println(Level.FINE, c);
        if (32 <= c && c <= 126) {
            inputChar(c);
        } else if (c == 10) {
            lastSentence = "";
            readLastString();
Debug.println(Level.FINE, "str=" + lastSentence);
            println("");
            m.execute(lastSentence);
            input_start = buf_ptr;
        } else if (c == 8) {
            backSpace();
        } else if (c == 9) {
            for (int i = 0; i < 4; i++)
                inputChar(' ');
        }
        panel.setBufPtr(buf_ptr);
        panel.repaint();
    }
}

class WindowPanel extends JPanel {

    private static final long serialVersionUID = 7273893358062563251L;

    Color background_color = new Color(0, 0, 0);

    char[] buffer;

    int buf_ptr;

    public WindowPanel(char[] buffer) {
        this.buffer = buffer;
    }

    public void setBufPtr(int ptr) {
        buf_ptr = ptr;
    }

    @Override
    public void paintComponent(Graphics g) {
Debug.println(Level.FINE, "paintComponent");
        super.paintComponent(g);
        flush(g);
        drawBuffer(g);
    }

    @Override
    public void update(Graphics g) {
Debug.println(Level.FINE, "update");
        drawBuffer(g);
    }

    private void drawBuffer(Graphics g) {
Debug.println(Level.FINE, "drawBuffer");
        g.setColor(Color.white);
        for (int i = 0; i < 48; i++) {
            for (int j = 0; j < 64; j++) {
                g.drawString(String.valueOf(buffer[i * 64 + j]), j * 10, i * 10 + 10);
                if (buf_ptr == i * 64 + j)
                    g.drawString(String.valueOf('|'), j * 10, i * 10 + 10);
            }
        }
    }

    public void flush(Graphics g) {
Debug.println(Level.FINE, "flush");
        g.setColor(background_color);
        g.fillRect(0, 0, 640, 485);
    }
}
