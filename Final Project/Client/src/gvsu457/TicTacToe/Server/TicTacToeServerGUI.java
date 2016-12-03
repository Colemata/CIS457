package gvsu457.TicTacToe.Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by taylor.coleman on 11/24/2016.
 */
public class TicTacToeServerGUI extends JFrame implements ActionListener {
    public JButton button1;
    public JButton button2;
    public JButton button3;
    public JButton button4;
    public JButton button5;
    public JButton button6;
    public JButton button7;
    public JButton button8;
    public JButton button9;
    public JPanel panel1;
    private Image xImg;
    private Image oImg;

    /**
     * Shortcut for image directory
     */
    public static String IMAGE_DIR = System.getProperty("user.dir") + File.separator + ".." + File.separator + "images";

    public static TicTacToeServerLogic ticTacToeServerLogic;

    public TicTacToeServerGUI(String username, TicTacToeServerLogic ticTacToeServerLogic) {

        super(username);
        this.ticTacToeServerLogic = ticTacToeServerLogic;
        try {
            xImg = ImageIO.read(getClass().getResource("o.png"));
            xImg = xImg.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            oImg = ImageIO.read(getClass().getResource("x.png"));
            oImg = oImg.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            System.out.println("IOException");
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                ticTacToeServerLogic.closeSockets();
                dispose();
            }
        });

        setContentPane(panel1);

        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);
        button5.addActionListener(this);
        button6.addActionListener(this);
        button7.addActionListener(this);
        button8.addActionListener(this);
        button9.addActionListener(this);

        setVisible(true);
        pack();

        setButtonsEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JComponent event = (JComponent) e.getSource();

        if (event == button1) {
            ticTacToeServerLogic.setSpotForUser(0, 0);
            ticTacToeServerLogic.sendDataToOtherPlayer(1);
            button1.setIcon(new ImageIcon(oImg));
        } else if (event == button2) {
            ticTacToeServerLogic.setSpotForUser(0, 1);
            ticTacToeServerLogic.sendDataToOtherPlayer(2);
            button2.setIcon(new ImageIcon(oImg));
        } else if (event == button3) {
            ticTacToeServerLogic.setSpotForUser(0, 2);
            ticTacToeServerLogic.sendDataToOtherPlayer(3);
            button3.setIcon(new ImageIcon(oImg));
        } else if (event == button4) {
            ticTacToeServerLogic.setSpotForUser(1, 0);
            ticTacToeServerLogic.sendDataToOtherPlayer(4);
            button4.setIcon(new ImageIcon(oImg));
        } else if (event == button5) {
            ticTacToeServerLogic.setSpotForUser(1, 1);
            ticTacToeServerLogic.sendDataToOtherPlayer(5);
            button5.setIcon(new ImageIcon(oImg));
        } else if (event == button6) {
            ticTacToeServerLogic.setSpotForUser(1, 2);
            ticTacToeServerLogic.sendDataToOtherPlayer(6);
            button6.setIcon(new ImageIcon(oImg));
        } else if (event == button7) {
            ticTacToeServerLogic.setSpotForUser(2, 0);
            ticTacToeServerLogic.sendDataToOtherPlayer(7);
            button7.setIcon(new ImageIcon(oImg));
        } else if (event == button8) {
            ticTacToeServerLogic.setSpotForUser(2, 1);
            ticTacToeServerLogic.sendDataToOtherPlayer(8);
            button8.setIcon(new ImageIcon(oImg));
        } else if (event == button9) {
            ticTacToeServerLogic.setSpotForUser(2, 2);
            ticTacToeServerLogic.sendDataToOtherPlayer(9);
            button9.setIcon(new ImageIcon(oImg));
        }
    }

    public void setButtonImageForOtherPlayer(int buttonNum) {
        switch (buttonNum) {
            case 1:
                button1.setIcon(new ImageIcon(xImg));
                break;
            case 2:
                button2.setIcon(new ImageIcon(xImg));
                break;
            case 3:
                button3.setIcon(new ImageIcon(xImg));
                break;
            case 4:
                button4.setIcon(new ImageIcon(xImg));
                break;
            case 5:
                button5.setIcon(new ImageIcon(xImg));
                break;
            case 6:
                button6.setIcon(new ImageIcon(xImg));
                break;
            case 7:
                button7.setIcon(new ImageIcon(xImg));
                break;
            case 8:
                button8.setIcon(new ImageIcon(xImg));
                break;
            case 9:
                button9.setIcon(new ImageIcon(xImg));
                break;
        }
    }

    public void setButtonsEnabled(boolean val) {
        button1.setEnabled(val);
        button2.setEnabled(val);
        button3.setEnabled(val);
        button4.setEnabled(val);
        button5.setEnabled(val);
        button6.setEnabled(val);
        button7.setEnabled(val);
        button8.setEnabled(val);
        button9.setEnabled(val);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-16777216));
        panel1.setPreferredSize(new Dimension(350, 350));
        button1 = new JButton();
        button1.setText("");
        panel1.add(button1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button2 = new JButton();
        button2.setText("");
        panel1.add(button2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button3 = new JButton();
        button3.setText("");
        panel1.add(button3, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button5 = new JButton();
        button5.setText("");
        panel1.add(button5, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button6 = new JButton();
        button6.setText("");
        panel1.add(button6, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button7 = new JButton();
        button7.setText("");
        panel1.add(button7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button8 = new JButton();
        button8.setText("");
        panel1.add(button8, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button9 = new JButton();
        button9.setText("");
        panel1.add(button9, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
        button4 = new JButton();
        button4.setText("");
        panel1.add(button4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), new Dimension(100, 100), new Dimension(100, 100), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
