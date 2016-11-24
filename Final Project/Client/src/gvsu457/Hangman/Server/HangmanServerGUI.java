package gvsu457.Hangman.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 11/23/2016.
 */
public class HangmanServerGUI extends JFrame implements ActionListener {
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
    public static HangmanServerLogic hangmanServerLogic;


    public HangmanServerGUI(String username) {

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
        hangmanServerLogic = new HangmanServerLogic();
    }

    public void actionPerformed(ActionEvent e) {

        JComponent event = (JComponent) e.getSource();

        if (event == guessLetterButton) {
            hangmanServerLogic.guessLetter(guessLetterField.getText());
            listModel.addElement(guessLetterField.getText());
        } else if (event == defineWordButton) {
            if (defineWordButton.getText() != null) {
                //HangmanServerLogic.sendWordToServer(defineWordField.getText());
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
