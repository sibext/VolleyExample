package com.sibext.volleyexample;

public class BallPosition {
    private int posX, posY;
    private String name;

    public BallPosition(String name, int posX, int posY){
        this.name = name;
        this.posX = posX;
        this.posY = posY;
    }

    public String getName() {
        return name;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosY() {
        return posY;
    }
}
