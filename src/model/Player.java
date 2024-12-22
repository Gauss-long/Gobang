package model;

import java.awt.event.MouseEvent;

public class Player {
    public String id;
    public String name;
    public int piece;
    public boolean isAI;
    public Player(String id,String name,int piece,boolean isAI){
        this.id=id;
        this.name=name;
        this.piece=piece;
        this.isAI=isAI;
    }

    public int[] Move(MouseEvent e, int MARGIN, int CELL_SIZE, int[][] board){
        int x = Math.round((e.getX() - MARGIN) / (float) CELL_SIZE);
        int y = Math.round((e.getY() - MARGIN) / (float) CELL_SIZE);
        int[] a = new int[2];
        a[0]=x;a[1]=y;
        return a;
    }
    public boolean AIacceptDraw(){
        AIPlayer p =new AIPlayer("","",-1);
        return p.acceptDraw();
    }

}