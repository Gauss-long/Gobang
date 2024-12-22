package service;

import model.Board;
import model.Step;
import ui.BoardUI;
import ui.ButtonPanel;
import ui.FrameUI;

public class Review {
    private static int currentStep = 0;
    private static final Review instance = new Review();
    private final Board board = Board.getInstance();
    private Review(){
    }
    public static Review getInstance(){
        return instance;
    }
    public void startReview(){
        ButtonPanel.getInstance().undoButton.setEnabled(false);
        ButtonPanel.getInstance().admitDefeatButton.setEnabled(false);
        ButtonPanel.getInstance().drawButton.setEnabled(false);
        ButtonPanel.getInstance().goButton.setEnabled(true);
        ButtonPanel.getInstance().backButton.setEnabled(false);
        ButtonPanel.getInstance().quitButton.setEnabled(true);

        board.clearBoard();
        BoardUI.getInstance().repaint();
    }
    public void goStep(){
        if(currentStep<board.Moves.size()){
            if(currentStep==board.Moves.size()-1)ButtonPanel.getInstance().goButton.setEnabled(false);
            ButtonPanel.getInstance().backButton.setEnabled(true);
            Step step = board.Moves.get(currentStep++);
            board.board[step.x][step.y]=step.piece;
            BoardUI.getInstance().repaint();
        }
    }
    public void backStep(){
        if(currentStep>0){
            if(currentStep==1)ButtonPanel.getInstance().backButton.setEnabled(false);
            ButtonPanel.getInstance().goButton.setEnabled(true);
            Step step = board.Moves.get((currentStep--)-1);
            board.board[step.x][step.y]=0;
            BoardUI.getInstance().repaint();
        }
    }
    public void quit(){
        currentStep=0;
        Music.getInstance().stop(1);
        Music.getInstance().play(0);
        FrameUI.getInstance().cardLayout.show(FrameUI.getInstance().mainPanel, "Menu");
    }
}
