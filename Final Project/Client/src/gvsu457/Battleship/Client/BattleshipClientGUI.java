package gvsu457.Battleship.Client;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by Administrator on 11/27/2016.
 */
public class BattleshipClientGUI extends JFrame implements ActionListener {

    public JPanel topLevelPanel;
    public JPanel shipPanel;
    public JPanel guessPanel;
    public JPanel checkBoxPanel1;
    public JPanel checkBoxPanel2;
    public JPanel mainPanel;
    public JPanel topLeftPanel;
    public JPanel topRightPanel;
    public JButton[][] selectionButtons = new JButton[10][10];
    public JButton[][] guessButtons = new JButton[10][10];
    public int[][] myGameBoard = new int[10][10];
    public int[][] guessBoard = new int[10][10];

    private JCheckBox box1;
    private JCheckBox box2;
    private JCheckBox box3;
    private JCheckBox box4;
    private JCheckBox box5;

    private JCheckBox theirBox1;
    private JCheckBox theirBox2;
    private JCheckBox theirBox3;
    private JCheckBox theirBox4;
    private JCheckBox theirBox5;


    private JLabel yourShipsLabel;
    private JLabel theirShipsLabel;

    public Battleship ship1 = new Battleship(2, false, 0, false, 1);
    public Battleship ship2 = new Battleship(3, false, 0, false, 2);
    public Battleship ship3 = new Battleship(3, false, 0, false, 3);
    public Battleship ship4 = new Battleship(4, false, 0, false, 4);
    public Battleship ship5 = new Battleship(5, false, 0, false, 5);

    public Battleship theirShip1 = new Battleship(2, false, 0, false, 1);
    public Battleship theirShip2 = new Battleship(3, false, 0, false, 2);
    public Battleship theirShip3 = new Battleship(3, false, 0, false, 3);
    public Battleship theirShip4 = new Battleship(4, false, 0, false, 4);
    public Battleship theirShip5 = new Battleship(5, false, 0, false, 5);

    public BattleshipClientLogic battleshipClientLogic;

    public ArrayList<Battleship> battleshipsArray = new ArrayList<Battleship>();

    public ArrayList<Battleship> theirShipArray = new ArrayList<Battleship>();

    public gameStatus theGameStatus = gameStatus.initialSetup;

    public void setOpponentGameArray(int[][] array) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                guessBoard[i][j] = array[i][j];
            }
        }
        setNextStatus();
    }

    public void setMissedElement(int row, int col) {
        //guessButtons[row][col].setEnabled(false);
        //guessBoard[row][col] = -9;
    }

    public void setHitElement(int row, int col, int shipIDhit) {
        guessButtons[row][col].setEnabled(false);
        selectionButtons[row][col].setText("X");
        //guessBoard[row][col] = shipIDhit;
        PerformShipHitLogic(shipIDhit);
        boolean isGameOver = CheckForAllShipsSunk();
        if (isGameOver) {
            battleshipClientLogic.sendLossReportToOtherPlayer();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You lose!!!");
        }
    }

    private boolean CheckForAllShipsSunk() {
        for (Battleship ship : battleshipsArray) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    private void PerformShipHitLogic(int shipIDhit) {
        for (Battleship ship : battleshipsArray) {
            if (ship.getShipID() == shipIDhit) {
                ship.setHitCount(ship.getHitCount() + 1);
                if (ship.getHitCount() == ship.getSize()) {
                    ship.setSunk(true);
                    switch (ship.getShipID()) {
                        case 1:
                            box1.setSelected(false);
                            break;
                        case 2:
                            box2.setSelected(false);
                            break;
                        case 3:
                            box3.setSelected(false);
                            break;
                        case 4:
                            box4.setSelected(false);
                            break;
                        case 5:
                            box5.setSelected(false);
                            break;
                    }
                }
            }
        }
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    public enum gameStatus {
        initialSetup,
        placingShipOnePieceOne,
        placingShipOneFinalPiece,
        placingShipTwoPieceOne,
        placingShipThreePieceOne,
        placingShipTwoFinalPiece,
        placingShipThreeFinalPiece,
        placingShipFourPieceOne,
        placingShipFourFinalPiece,
        placingShipFiveFinalPiece,
        placingShipFivePieceOne,
        donePlacingPieces,
        waitingForOpponentTurn,
        itIsOurTurn;
    }

    public BattleshipClientGUI(String username, BattleshipClientLogic battleshipClientLogic) {

        super(username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        topLevelPanel = new JPanel(new FlowLayout());
        shipPanel = new JPanel(new GridLayout(10, 10));
        shipPanel.setBorder(new TitledBorder(new LineBorder(Color.BLUE, 5), "Ship Board"));
        guessPanel = new JPanel(new GridLayout(10, 10));
        guessPanel.setBorder(new TitledBorder(new LineBorder(Color.YELLOW, 5), "Guess Board"));
        checkBoxPanel1 = new JPanel();
        checkBoxPanel1.setLayout(new BoxLayout(checkBoxPanel1, BoxLayout.PAGE_AXIS));
        checkBoxPanel2 = new JPanel();
        checkBoxPanel2.setLayout(new BoxLayout(checkBoxPanel2, BoxLayout.PAGE_AXIS));
        topLeftPanel = new JPanel();
        topRightPanel = new JPanel();

        box1 = new JCheckBox("Cruiser    (2)");
        box1.setEnabled(false);
        box2 = new JCheckBox("Submarine  (3)");
        box2.setEnabled(false);
        box3 = new JCheckBox("Cruiser    (3)");
        box3.setEnabled(false);
        box4 = new JCheckBox("Battleship (4)");
        box4.setEnabled(false);
        box5 = new JCheckBox("Carrier    (5)");
        box5.setEnabled(false);
        box1.setSelected(true);
        box2.setSelected(true);
        box3.setSelected(true);
        box4.setSelected(true);
        box5.setSelected(true);

        theirBox1 = new JCheckBox("Cruiser    (2)");
        theirBox1.setEnabled(false);
        theirBox2 = new JCheckBox("Submarine  (3)");
        theirBox2.setEnabled(false);
        theirBox3 = new JCheckBox("Cruiser    (3)");
        theirBox3.setEnabled(false);
        theirBox4 = new JCheckBox("Battleship (4)");
        theirBox4.setEnabled(false);
        theirBox5 = new JCheckBox("Carrier    (5)");
        theirBox5.setEnabled(false);
        theirBox1.setSelected(true);
        theirBox2.setSelected(true);
        theirBox3.setSelected(true);
        theirBox4.setSelected(true);
        theirBox5.setSelected(true);

        yourShipsLabel = new JLabel("Your Ships Remaining");
        theirShipsLabel = new JLabel("Their Ships Remaining");

        checkBoxPanel1.add(yourShipsLabel);
        checkBoxPanel1.add(box1);
        checkBoxPanel1.add(box2);
        checkBoxPanel1.add(box3);
        checkBoxPanel1.add(box4);
        checkBoxPanel1.add(box5);

        checkBoxPanel2.add(theirShipsLabel);
        checkBoxPanel2.add(theirBox1);
        checkBoxPanel2.add(theirBox2);
        checkBoxPanel2.add(theirBox3);
        checkBoxPanel2.add(theirBox4);
        checkBoxPanel2.add(theirBox5);

        battleshipsArray.add(ship1);
        battleshipsArray.add(ship2);
        battleshipsArray.add(ship3);
        battleshipsArray.add(ship4);
        battleshipsArray.add(ship5);

        theirShipArray.add(theirShip1);
        theirShipArray.add(theirShip2);
        theirShipArray.add(theirShip3);
        theirShipArray.add(theirShip4);
        theirShipArray.add(theirShip5);

        //setLayout(new GridLayout(10, 10));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                selectionButtons[i][j] = new JButton();
                selectionButtons[i][j].addActionListener(this);
                selectionButtons[i][j].setPreferredSize(new Dimension(50, 50));
                shipPanel.add(selectionButtons[i][j]);
                myGameBoard[i][j] = -1;
                guessBoard[i][j] = -1;
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                guessButtons[i][j] = new JButton();
                guessButtons[i][j].addActionListener(this);
                guessButtons[i][j].setPreferredSize(new Dimension(50, 50));
                guessButtons[i][j].setEnabled(false);
                guessPanel.add(guessButtons[i][j]);
            }
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                battleshipClientLogic.closeSockets();
                dispose();
            }
        });

        topLevelPanel.add(checkBoxPanel1);
        topLevelPanel.add(shipPanel);
        topLevelPanel.add(checkBoxPanel2);

        topLevelPanel.add(guessPanel);
        add(topLevelPanel);
        setVisible(true);
        pack();

        //at this point the game is all set up, so we can move on to placing 4 status.
        theGameStatus = gameStatus.placingShipOnePieceOne;

        //battleshipClientLogic = new BattleshipClientLogic();
        this.battleshipClientLogic = battleshipClientLogic;

        JOptionPane.showMessageDialog(this, "Place size 5 ship please!");

    }

    @Override
    public void actionPerformed(ActionEvent event) {

        JComponent e = (JComponent) event.getSource();

        if (theGameStatus == gameStatus.placingShipOnePieceOne) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        setAllButtonsEnabled(i, j, false);
                        setButtonsAroundSelectionEnabled(i, j, true, ship5.getSize());
                        selectionButtons[i][j].setText("" + ship5.getShipID());
                        myGameBoard[i][j] = ship5.getShipID();
                    }
                }
            }
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipOneFinalPiece) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        selectionButtons[i][j].setText("" + ship5.getShipID());
                        myGameBoard[i][j] = ship5.getShipID();
                    }
                }
            }

            fillGapsBetweenButtonsForShipID(ship5.getShipID());
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipTwoPieceOne) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        setAllButtonsEnabled(i, j, false);
                        setButtonsAroundSelectionEnabled(i, j, true, ship4.getSize());
                        selectionButtons[i][j].setText("" + ship4.getShipID());
                        myGameBoard[i][j] = ship4.getShipID();
                    }
                }
            }
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipTwoFinalPiece) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        selectionButtons[i][j].setText("" + ship4.getShipID());
                        myGameBoard[i][j] = ship4.getShipID();
                    }
                }
            }

            fillGapsBetweenButtonsForShipID(ship4.getShipID());
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipThreePieceOne) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        setAllButtonsEnabled(i, j, false);
                        setButtonsAroundSelectionEnabled(i, j, true, ship3.getSize());
                        selectionButtons[i][j].setText("" + ship3.getShipID());
                        myGameBoard[i][j] = ship3.getShipID();
                    }
                }
            }
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipThreeFinalPiece) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        selectionButtons[i][j].setText("" + ship3.getShipID());
                        myGameBoard[i][j] = ship3.getShipID();
                    }
                }
            }

            fillGapsBetweenButtonsForShipID(ship3.getShipID());
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipFourPieceOne) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        setAllButtonsEnabled(i, j, false);
                        setButtonsAroundSelectionEnabled(i, j, true, ship2.getSize());
                        selectionButtons[i][j].setText("" + ship2.getShipID());
                        myGameBoard[i][j] = ship2.getShipID();
                    }
                }
            }
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipFourFinalPiece) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        selectionButtons[i][j].setText("" + ship2.getShipID());
                        myGameBoard[i][j] = ship2.getShipID();
                    }
                }
            }

            fillGapsBetweenButtonsForShipID(ship2.getShipID());
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipFivePieceOne) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        setAllButtonsEnabled(i, j, false);
                        setButtonsAroundSelectionEnabled(i, j, true, ship1.getSize());
                        selectionButtons[i][j].setText("" + ship1.getShipID());
                        myGameBoard[i][j] = ship1.getShipID();
                    }
                }
            }
            setNextStatus();
        } else if (theGameStatus == gameStatus.placingShipFiveFinalPiece) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == selectionButtons[i][j]) {
                        selectionButtons[i][j].setText("" + ship1.getShipID());
                        myGameBoard[i][j] = ship1.getShipID();
                    }
                }
            }

            fillGapsBetweenButtonsForShipID(ship1.getShipID());
            setNextStatus();
        } else if (theGameStatus == gameStatus.itIsOurTurn) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e == guessButtons[i][j]) {
                        int shipID = guessBoard[i][j];
                        if (shipID > 0) {
                            guessButtons[i][j].setText("" + getShipSizeForID(shipID));
                            PerformHitForOpponentShip(guessBoard[i][j]);
                            CheckIfWeHaveSunkAnyOpponentShip();
                            guessBoard[i][j] = -9;
                        } else if (shipID == -1) {
                            guessButtons[i][j].setText("M");
                            guessBoard[i][j] = -9;
                        }
                        battleshipClientLogic.sendGuessToOtherPlayer(shipID, i, j);
                    }
                }
            }
        }
        //printGameBoard();
    }

    private void CheckIfWeHaveSunkAnyOpponentShip() {
        for (Battleship ship : theirShipArray) {
            if (ship.isSunk()) {
                switch (ship.getShipID()) {
                    case 1:
                        theirBox1.setSelected(false);
                        break;
                    case 2:
                        theirBox2.setSelected(false);
                        break;
                    case 3:
                        theirBox3.setSelected(false);
                        break;
                    case 4:
                        theirBox4.setSelected(false);
                        break;
                    case 5:
                        theirBox5.setSelected(false);
                        break;

                }
            }
        }
    }

    private void PerformHitForOpponentShip(int shipID) {
        for (Battleship ship : theirShipArray) {
            if (ship.getShipID() == shipID) {
                ship.setHitCount(ship.getHitCount() + 1);
                if (ship.getHitCount() == ship.getSize()) {
                    ship.setSunk(true);
                }
            }
        }
    }

    private void setButtonsAroundSelectionEnabled(int row, int col, boolean enabled, int shipSize) {
        try {
            if (!isThereACollision(row, col, shipSize, "down")) {
                selectionButtons[row + shipSize - 1][col].setEnabled(enabled);
            }
        } catch (IndexOutOfBoundsException indexOOB) {

        }
        try {
            if (!isThereACollision(row, col, shipSize, "up")) {
                selectionButtons[row - shipSize + 1][col].setEnabled(enabled);
            }
        } catch (IndexOutOfBoundsException indexOOB) {

        }
        try {
            if (!isThereACollision(row, col, shipSize, "right")) {
                selectionButtons[row][col + shipSize - 1].setEnabled(enabled);
            }
        } catch (IndexOutOfBoundsException indexOOB) {

        }
        try {
            if (!isThereACollision(row, col, shipSize, "left")) {
                selectionButtons[row][col - shipSize + 1].setEnabled(enabled);
            }
        } catch (IndexOutOfBoundsException indexOOB) {

        }
    }

    private boolean isThereACollision(int row, int col, int shipSize, String up) {

        switch (up) {
            case "up":
                for (int i = 0; i < shipSize; i++) {
                    if (myGameBoard[row - i][col] > 0) {
                        return true;
                    }
                }
                return false;
            case "down":
                for (int i = 0; i < shipSize; i++) {
                    if (myGameBoard[row + i][col] > 0) {
                        return true;
                    }
                }
                break;
            case "left":
                for (int i = 0; i < shipSize; i++) {
                    if (myGameBoard[row][col - i] > 0) {
                        return true;
                    }
                }
                break;
            case "right":
                for (int i = 0; i < shipSize; i++) {
                    if (myGameBoard[row][col + i] > 0) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    private void fillGapsBetweenButtonsForShipID(int shipID) {
        boolean wasRow = false;
        int elementCountPerRow = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (myGameBoard[i][j] == shipID) {
                    elementCountPerRow++;
                }
            }
            if (elementCountPerRow >= 2) {
                fillGapsForRow(i, shipID, elementCountPerRow);
                wasRow = true;
            }
            elementCountPerRow = 0;
        }

        if (!wasRow) {
            int elementCountPerCol = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (myGameBoard[j][i] == shipID) {
                        elementCountPerCol++;
                    }
                }
                if (elementCountPerCol >= 2) {
                    fillGapsForCol(i, shipID, elementCountPerRow);
                }
                elementCountPerRow = 0;
            }
        }
    }

    private void fillGapsForCol(int i, int shipID, int currentCount) {
        int shipSize = getShipSizeForID(shipID);
        int remainingPiecesToPlace = shipSize - currentCount;
        boolean wasFirstElementSeen = false;
        for (int j = 0; j < 10; j++) {
            if (remainingPiecesToPlace > 0) {
                if (wasFirstElementSeen && myGameBoard[j][i] != shipID) {
                    myGameBoard[j][i] = shipID;
                    selectionButtons[j][i].setText("" + shipID);
                    remainingPiecesToPlace--;

                } else if (wasFirstElementSeen && myGameBoard[j][i] == shipID) {
                    //we are done.
                    return;
                } else {
                    if (myGameBoard[j][i] == shipID) {
                        wasFirstElementSeen = true;
                    }
                }
            }
        }

    }

    private void fillGapsForRow(int i, int shipID, int currentCount) {
        int shipSize = getShipSizeForID(shipID);
        int remainingPiecesToPlace = shipSize - currentCount;
        boolean wasFirstElementSeen = false;
        for (int j = 0; j < 10; j++) {
            if (remainingPiecesToPlace > 0) {
                if (wasFirstElementSeen && myGameBoard[i][j] != shipID) {
                    myGameBoard[i][j] = shipID;
                    selectionButtons[i][j].setText("" + shipID);
                    remainingPiecesToPlace--;

                } else if (wasFirstElementSeen && myGameBoard[i][j] == shipID) {
                    //we are done.
                    return;
                } else {
                    if (myGameBoard[i][j] == shipID) {
                        wasFirstElementSeen = true;
                    }
                }
            }
        }

//        if (checkIfShipPlacementCompleteForCurrentStatus()) {
//            //go to next status...
//            setNextStatus();
//        }
    }

    private int getShipSizeForID(int shipID) {
        for (Battleship ship : battleshipsArray) {
            if (ship.getShipID() == shipID) {
                return ship.getSize();
            }
        }
        return 0;
    }

    //if the status changes we should renable buttons that aren't placed pieces too
    private void setNextStatus() {
        if (theGameStatus == gameStatus.placingShipOnePieceOne) {
            theGameStatus = gameStatus.placingShipOneFinalPiece;
        } else if (theGameStatus == gameStatus.placingShipOneFinalPiece) {
            setAllButtonsWithoutPlacementEnabled();
            JOptionPane.showMessageDialog(this, "Now pick the 4 piece ship!");
            theGameStatus = gameStatus.placingShipTwoPieceOne;
        } else if (theGameStatus == gameStatus.placingShipTwoPieceOne) {
            theGameStatus = gameStatus.placingShipTwoFinalPiece;
        } else if (theGameStatus == gameStatus.placingShipTwoFinalPiece) {
            setAllButtonsWithoutPlacementEnabled();
            JOptionPane.showMessageDialog(this, "Now pick the 3 piece ship!");
            theGameStatus = gameStatus.placingShipThreePieceOne;
        } else if (theGameStatus == gameStatus.placingShipThreePieceOne) {
            theGameStatus = gameStatus.placingShipThreeFinalPiece;
        } else if (theGameStatus == gameStatus.placingShipThreeFinalPiece) {
            setAllButtonsWithoutPlacementEnabled();
            JOptionPane.showMessageDialog(this, "Now pick the 3 piece ship!");
            theGameStatus = gameStatus.placingShipFourPieceOne;
        } else if (theGameStatus == gameStatus.placingShipFourPieceOne) {
            theGameStatus = gameStatus.placingShipFourFinalPiece;
        } else if (theGameStatus == gameStatus.placingShipFourFinalPiece) {
            setAllButtonsWithoutPlacementEnabled();
            JOptionPane.showMessageDialog(this, "Now pick the 2 piece ship!");
            theGameStatus = gameStatus.placingShipFivePieceOne;
        } else if (theGameStatus == gameStatus.placingShipFivePieceOne) {
            theGameStatus = gameStatus.placingShipFiveFinalPiece;
        } else if (theGameStatus == gameStatus.placingShipFiveFinalPiece) {
            setAllButtonsWithoutPlacementEnabled();
            JOptionPane.showMessageDialog(this, "You are finished placing pieces!");
            theGameStatus = gameStatus.donePlacingPieces;
            setAllGuessButtonsWithoutGuessesEnabled(true);
            battleshipClientLogic.SendGameBoardDataToOtherPlayer(myGameBoard);
            battleshipClientLogic.GetGameBoardFromOpponent();
        } else if (theGameStatus == gameStatus.donePlacingPieces) {
            theGameStatus = gameStatus.waitingForOpponentTurn;
            setAllGuessButtonsWithoutGuessesEnabled(false);
        }

        //setAllButtonsWithoutPlacementEnabled();
    }

    public boolean isCurrentStatusWaitingForOtherPlayer() {
        return theGameStatus == gameStatus.waitingForOpponentTurn;
    }

    public void setAllGuessButtonsWithoutGuessesEnabled(boolean value) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (guessBoard[i][j] == -9) {
                    guessButtons[i][j].setEnabled(false);
                } else {
                    guessButtons[i][j].setEnabled(value);
                }
            }
        }
    }

    public void switchPlayerTurn() {
        if (theGameStatus == gameStatus.waitingForOpponentTurn) {
            theGameStatus = gameStatus.itIsOurTurn;
            setAllGuessButtonsWithoutGuessesEnabled(true);
        } else if (theGameStatus == gameStatus.itIsOurTurn) {
            theGameStatus = gameStatus.waitingForOpponentTurn;
            setAllGuessButtonsWithoutGuessesEnabled(false);
        }
    }

    private void setAllButtonsWithoutPlacementEnabled() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (myGameBoard[i][j] == -1) {
                    selectionButtons[i][j].setEnabled(true);
                } else {
                    selectionButtons[i][j].setEnabled(false);
                }
            }
        }
    }

    private int getHowPiecesHaveBeenLayedForShipSize(int shipSize) {
        int retVal = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (myGameBoard[i][j] == shipSize) {
                    retVal++;
                }
            }
        }
        return retVal;
    }

    private void setAllButtonsEnabled(int row, int col, boolean enabled) {

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // if (i != row && j != col) {
                selectionButtons[i][j].setEnabled(enabled);
                // }
            }
        }
    }

    private void printGameBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // if (i != row && j != col) {
                System.out.print(myGameBoard[i][j] + " ");
                // }
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

}

