package com.game;

import javafx.scene.input.KeyCode;

public enum Direction {
    
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int y;
    private final int x;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Direction{" + "y=" + y + ", x=" + x + "} " + name();
    }
    
    public static Direction valueFor(KeyCode keyCode) {
        // TO-DO: Step 11. return the direction of the arrow
        if(Game2048.STEP>=11){
            return valueOf(keyCode.name());
        }
        return RIGHT;
    }
    
}
