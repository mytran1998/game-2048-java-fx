package com.game;

import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class SlidingTiles extends Label {
    
    private int value;
    private Location location;
    private boolean merged;

    
    private SlidingTiles(int value) {
        // TO-DO. Step 6. Create tile 
        if(Game2048.STEP>=6){
            final int squareSize = Board.CELL_SIZE - 13;
            setMinSize(squareSize, squareSize);
            setMaxSize(squareSize, squareSize);
            setPrefSize(squareSize, squareSize);
            // TO-DO: Step 10. Load css and remove style
            if(Game2048.STEP<10){
                setStyle("-fx-background-color: #c9c9c9;");
            } else {
                getStyleClass().addAll("game-label", "game-tile-" + value);
            }
            setAlignment(Pos.CENTER);

            this.value = value;
            this.merged = false;
            setText(Integer.toString(value));
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    @Override
    public String toString() {
        return "Tile{" + "value=" + value + ", location=" + location + ", merged=" + merged + '}';
    }
    
    public static SlidingTiles newRandomTile() {
        // Step 7. Random value, 90% chance 2, 10% 4
        if(Game2048.STEP>=7){
            return newTile(new Random().nextDouble() < 0.9 ? 2 : 4);
        }
        return newTile(2);
    }
    
    public static SlidingTiles newTile(int value) {
        return new SlidingTiles(value);
    }
    
    public void merge(SlidingTiles another) {
        // TO-DO: Step 27. Add to tile's value the value of the tile to be merged to, 
        // set the text with the new value and replace the old style ‘game-title-“-value with the new one
        if(Game2048.STEP>=27){
            getStyleClass().remove("game-tile-" + value);
            this.value += another.getValue();
            setText(Integer.toString(value));
            merged = true;
            getStyleClass().add("game-tile-" + value);
        }
    }
    
    public boolean isMergeable(SlidingTiles anotherTile) {
        // TO-DO: Step 27. Check it this.tile can be merged with anotherTile 
        if(Game2048.STEP>=27){
            return anotherTile != null && getValue()==anotherTile.getValue();
        }
        return false;
    }        
}
