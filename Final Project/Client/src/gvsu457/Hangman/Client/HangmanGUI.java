package gvsu457.Hangman.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * Created by Administrator on 11/23/2016.
 */
public class HangmanGUI extends JFrame implements ActionListener {
    public DefaultListModel listModel = new DefaultListModel();
    public Socket socket;
    private JButton defineWordButton;
    private JTextField defineWordField;
    private JButton guessLetterButton;
    private JTextField guessLetterField;
    private JTextPane imagePanel;
    private JPanel mainPanel;
    private JList letterList;
    public DataInputStream in;
    public DataOutputStream out;
    public String theWord;
    public static HangmanClientLogic hangmanClientLogic;


    public HangmanGUI(String username) {

        super(username);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        letterList.setModel(listModel);
        pack();
        setVisible(true);

        defineWordButton.addActionListener(this);
        guessLetterButton.addActionListener(this);
    }

    public static void main(String[] args) {
        hangmanClientLogic = new HangmanClientLogic();
    }

    public void actionPerformed(ActionEvent e) {

        JComponent event = (JComponent) e.getSource();

        if (event == guessLetterButton) {
            //hangmanClientLogic.sendWordToServer(guessLetterField.getText());
        } else if (event == defineWordButton) {
            if (defineWordButton.getText() != null) {
                hangmanClientLogic.sendWordToServer(defineWordField.getText());
            }
        }
    }

    public void setAllFieldsEnabled(boolean bool) {
        defineWordButton.setEnabled(bool);
        defineWordField.setEnabled(bool);
        guessLetterButton.setEnabled(bool);
        guessLetterField.setEnabled(bool);
    }

    public void setGuessingFieldsEnabled(boolean bool) {
        guessLetterButton.setEnabled(bool);
        guessLetterField.setEnabled(bool);
    }

    public void setImagePanel(ImageIcon image) {
        imagePanel.setText("");
        imagePanel.insertIcon(image);
    }
    
}
