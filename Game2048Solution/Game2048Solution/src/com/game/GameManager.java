package com.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;

public class GameManager extends Group {
    
    private Board board;
    private final List<Location> locations = new ArrayList<>();
    private final Map<Location, SlidingTiles> gameGrid = new HashMap<>();
    private final ParallelTransition parallelTransition = new ParallelTransition();
    private volatile boolean movingTiles = false;
    private int tilesWereMoved=0;
    private final Set<SlidingTiles> mergedToBeRemoved = new HashSet<>();
    
    public GameManager(){
        // TO-DO: Step 2. Create board and it to gameManager
        if(Game2048.STEP>=2){
            board=new Board();
            getChildren().add(board);
        }
        // TO-DO: Step 42. Add listener to reset game
        if(Game2048.STEP>=42){
            board.resetGameProperty().addListener((ov, b, b1) -> {
                if (b1) {
                    initializeGameGrid();
                    startGame();
                }
            });
        }
        
        // TO-DO: Step 15. call initilize gameGrid
        if(Game2048.STEP>=15){
            initializeGameGrid();
        }
        // TO-DO: Step 9. call start game to display a tile on the board
        if(Game2048.STEP>=9){
            startGame();
        }
    }

    @SuppressWarnings("unused")
	private void initializeGameGrid() {
        // TO-DO: Step 15. Clear the lists, add all locations, and call it before startGame
        if(Game2048.STEP>=15){
            gameGrid.clear();
            locations.clear();
            if(Game2048.STEP<25){
                for(int i=0; i<4; i++){
                    for(int j=0; j<4; j++){
                        Location location = new Location(i,j);
                        locations.add(location);
                        gameGrid.put(location, null);
                    }
                }
            } // TO-DO: Step 25. Use traverseGrid
            else if(Game2048.STEP>=25){
                GridOperator.traverseGrid((i, j) -> {
                    Location location = new Location(i,j);
                    locations.add(location);
                    gameGrid.put(location, null);
                    return 0;
                });
            }
        }
    }
    
    @SuppressWarnings("unused")
	private void startGame() {
        // TO-DO: Step 9. Create a new random tile at a random location
        if(Game2048.STEP>=9){
            SlidingTiles tile0 = SlidingTiles.newRandomTile();
            if(Game2048.STEP<16){
                tile0.setLocation(new Location(1,2));
                board.addTile(tile0);
            } else // TO-DO: Step 16. Create a new random tile at a random location
            {
                List<Location> locCopy=locations.stream().collect(Collectors.toList());
                Collections.shuffle(locCopy);
                tile0.setLocation(locCopy.get(0));
                gameGrid.put(tile0.getLocation(), tile0);
                SlidingTiles tile1 = SlidingTiles.newRandomTile();
                tile1.setLocation(locCopy.get(1));
                gameGrid.put(tile1.getLocation(), tile1);

                redrawTilesInGameGrid();
            }
        }
    }
    
    private void redrawTilesInGameGrid() {
        // TO-DO: Step 16. Add all valid tiles to board
        if(Game2048.STEP>=16){
            gameGrid.values().stream().filter(Objects::nonNull).forEach(board::addTile);
        }
    }
    
    @SuppressWarnings("unused")
	public void move(Direction direction) {
        // TO-DO: Step 20. Quit if animation is playing
        if(Game2048.STEP>=20){
            synchronized (gameGrid) {
                if (movingTiles) {
                    return;
                }
            }
        }

        // TO-DO: Step 13: get a list of tiles, remove them from the board, 
        // create new tiles at an offset location if valid (limits, no other tile)
        if(Game2048.STEP>=13 && Game2048.STEP<18){
            List<SlidingTiles> tiles=board.getGridGroup().getChildren().stream()
                    .filter(g->g instanceof SlidingTiles).map(t->(SlidingTiles)t)
                    .collect(Collectors.toList());
            board.getGridGroup().getChildren().removeAll(tiles);
            tiles.forEach(t->{
                SlidingTiles newTile = SlidingTiles.newTile(t.getValue());
                final Location newLoc=t.getLocation().offset(direction);
                if(newLoc.isValidFor() && !tiles.stream().filter(t2->t2.getLocation().equals(newLoc)).findAny().isPresent()){
                    newTile.setLocation(newLoc);
                } else {
                    newTile.setLocation(t.getLocation());
                }
                board.addTile(newTile);
            });
        } 
        // TO-DO: Step 18. Use gameGrid instead of gridgroup
        // moving the existing tiles to the farthest location, and updating the map.
        // Note: the IntStreams are not well ordered for the moment
        if(Game2048.STEP>=18 && Game2048.STEP<25){
            IntStream.range(0, 4).boxed().forEach(i->{
                IntStream.range(0, 4).boxed().forEach(j->{
                    SlidingTiles t=gameGrid.get(new Location(i,j));
                    if(t!=null){
                        final Location newLoc=findFarthestLocation(t.getLocation(),direction);
                        if(!newLoc.equals(t.getLocation())){
                            if(Game2048.STEP<20){
                                board.moveTile(t, newLoc);
                            }  // TO-DO: Step 20. Animate tiles movement
                            else if(Game2048.STEP>=20){
                                parallelTransition.getChildren().add(animateExistingTile(t, newLoc));
                            }
                            gameGrid.put(newLoc, t);
                            gameGrid.replace(t.getLocation(),null);
                            t.setLocation(newLoc);
                        }
                    }
                });
            });
        }
        // TO-DO: Step 25. Replace the IntStreams with the traverseGrid method
        if(Game2048.STEP>=25){
            
            // TO-DO: Step 26. Sort grid before traversing it
            if(Game2048.STEP>=26){
                GridOperator.sortGrid(direction);
            }
            // TO-DO: Step 33. reset points
            if(Game2048.STEP>=33){
                board.setPoints(0);
            }
            if(Game2048.STEP<45){
                tilesWereMoved = GridOperator.traverseGrid((i,j)->{
                    SlidingTiles t=gameGrid.get(new Location(i,j));
                    if(t!=null){
                        final Location newLoc=findFarthestLocation(t.getLocation(),direction);
                        // TO-DO: Step 29. Get tile for an offset, check if it's a valid tile, not merged, and
                        // check if tiles can be merged 
                        if(Game2048.STEP>=29){
                            Location nextLocation = newLoc.offset(direction);
                            SlidingTiles tileToBeMerged = nextLocation.isValidFor() ? gameGrid.get(nextLocation) : null;
                            if (tileToBeMerged != null && !tileToBeMerged.isMerged() && t.isMergeable(tileToBeMerged)) {
                                tileToBeMerged.merge(t);
                                tileToBeMerged.toFront();
                                gameGrid.put(nextLocation, tileToBeMerged);
                                gameGrid.replace(t.getLocation(), null);
                                parallelTransition.getChildren().add(animateExistingTile(t, nextLocation));
                                parallelTransition.getChildren().add(animateMergedTile(tileToBeMerged));
                                mergedToBeRemoved.add(t);
                                // TO-DO: Step 33. add points
                                if(Game2048.STEP>=33){
                                    board.addPoints(tileToBeMerged.getValue());
                                }
                                // TO-DO: Step 38. Check for a winning tile
                                if(Game2048.STEP>=38){
                                    if(tileToBeMerged.getValue()==2048){
                                        System.out.println("You win!");
                                        // TO-DO: Step 41. set game win
                                         if(Game2048.STEP>=41){ 
                                            board.setGameWin(true);
                                        }
                                    }
                                }
                                return 1;
                            }
                        }
                        if(!newLoc.equals(t.getLocation())){
                            parallelTransition.getChildren().add(animateExistingTile(t, newLoc));
                            gameGrid.put(newLoc, t);
                            gameGrid.replace(t.getLocation(),null);
                            t.setLocation(newLoc);
                            return 1;
                        }
                    }
                    return 0;
                });
            } // TO-DO: Step 45: Use optionalTile to traverse the grid, using an atomicInteger to 
              // return the results
            else if(Game2048.STEP>=45){
                tilesWereMoved = GridOperator.traverseGrid((i, j) -> {
                    AtomicInteger result=new AtomicInteger();
                    optionalTile(new Location(i,j)).ifPresent(t1->{
                        final Location newLoc=findFarthestLocation(t1.getLocation(), direction);
                        Location nextLocation = newLoc.offset(direction); // calculates to a possible merge
                        optionalTile(nextLocation).filter(t2->t1.isMergeable(t2) && !t2.isMerged()).ifPresent(t2->{
                            t2.merge(t1);
                            t2.toFront();
                            gameGrid.put(nextLocation, t2);
                            gameGrid.replace(t1.getLocation(), null);
                            board.addPoints(t2.getValue());
                            if(t2.getValue()==2048){
                                board.setGameWin(true);
                            }
                            parallelTransition.getChildren().add(animateExistingTile(t1, nextLocation));
                            parallelTransition.getChildren().add(animateMergedTile(t2));
                            mergedToBeRemoved.add(t1);

                            result.set(1);
                        });

                        if(result.get()==0 && !newLoc.equals(t1.getLocation())){
                            parallelTransition.getChildren().add(animateExistingTile(t1, newLoc));
                            gameGrid.put(newLoc, t1);
                            gameGrid.replace(t1.getLocation(),null);
                            t1.setLocation(newLoc);
                            result.set(1);
                        } 
                    });
                    return result.get();
                });
            }
        }
        
        // TO-DO: Step 35. Call animate score
        if(Game2048.STEP>=35){
            board.animateScore();
        }
        
        // TO-DO: Step 20. Get a randomLocation, check not null, create random tile, add to board and to map
        if(Game2048.STEP>=20){
            parallelTransition.setOnFinished(e -> {
                synchronized (gameGrid) {
                    movingTiles = false;
                }
                // TO-DO: Step 30. Remove the tiles in the set from the gridGroup and clear the set. 
                // For all the tiles on the board: set to false their merged value
                if(Game2048.STEP>=30){
                    board.getGridGroup().getChildren().removeAll(mergedToBeRemoved);
                    mergedToBeRemoved.clear();
                    gameGrid.values().stream().filter(Objects::nonNull).forEach(t->t.setMerged(false));
                }
                
                // TO-DO: Step 23. Start animation and block movingTiles till it has finished
                if(Game2048.STEP>=23){
                    Location randomAvailableLocation = findRandomAvailableLocation();
                    if (randomAvailableLocation != null){
                        if(Game2048.STEP<25){
                            addAndAnimateRandomTile(randomAvailableLocation);
                        }  // TO-DO: Step 25. Check if tiles moved to allow new tile
                        else if(Game2048.STEP>=25){
                            if(tilesWereMoved>0){
                                addAndAnimateRandomTile(randomAvailableLocation);
                            }
                        }  
                    } else {
                        if(Game2048.STEP<37){
                            System.out.println("Game Over");
                        }
                        // TO-DO: Step 37. Game over only if no pair of mergeable tiles available
                        else if(Game2048.STEP>=37){
                            if(mergeMovementsAvailable()==0){
                                System.out.println("Game Over");
                                // TO-DO: Step 41. set game over
                                if(Game2048.STEP>=41){ 
                                    board.setGameOver(true);
                                }
                            }
                        }  
                    }
                }
                
            });
            synchronized (gameGrid) {
                movingTiles = true;
            }
            parallelTransition.play();
            parallelTransition.getChildren().clear();
        }
    }
    
    private Location findFarthestLocation(Location location, Direction direction) {
        Location farthest=location;
        // TO-DO: Step 17. Search for the farthest location in the direction of movement
        // with no tile and inside the grid
        if(Game2048.STEP>=17){
            do {
                farthest = location;
                location = farthest.offset(direction);
            } while (location.isValidFor() && gameGrid.get(location)==null);
        }
        return farthest;
    }

    private Timeline animateExistingTile(SlidingTiles tile, Location newLocation) {
        Timeline timeline = new Timeline();
        // TO-DO: Step 19. Animate tiles movement from actual location to new location in 65ms
        if(Game2048.STEP>=19){
            KeyValue kvX = new KeyValue(tile.layoutXProperty(),
                    newLocation.getLayoutX(Board.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);
            KeyValue kvY = new KeyValue(tile.layoutYProperty(),
                    newLocation.getLayoutY(Board.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);

            KeyFrame kfX = new KeyFrame(Duration.millis(65), kvX);
            KeyFrame kfY = new KeyFrame(Duration.millis(65), kvY);

            timeline.getKeyFrames().add(kfX);
            timeline.getKeyFrames().add(kfY);
        }
        return timeline;
    }
    
    private Location findRandomAvailableLocation() {
        Location location=null;
        // TO-DO: Step 21. From empty tiles remaining, get a random position
        if(Game2048.STEP>=21){
            List<Location> availableLocations = locations.stream().filter(l -> gameGrid.get(l) == null)
                .collect(Collectors.toList());

            if (availableLocations.isEmpty()) {
                return null;
            }

            Collections.shuffle(availableLocations);
            location = availableLocations.get(0);
        }
        return location;
    }
    
    private void addAndAnimateRandomTile(Location randomLocation) {
        // TO-DO: Step 22. Scale from 0 to 1 in 125 ms the new tile added to the board
        if(Game2048.STEP>=22){
            SlidingTiles tile = SlidingTiles.newRandomTile();
            tile.setLocation(randomLocation);
            tile.setScaleX(0); 
            tile.setScaleY(0);
            board.addTile(tile);
            gameGrid.put(tile.getLocation(), tile);

            final ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(125), tile);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.setInterpolator(Interpolator.EASE_OUT);
            // TO-DO: Step 37. After last movement on full grid, check if there are movements available
            if(Game2048.STEP>=37){
                scaleTransition.setOnFinished(e -> {
                    if (gameGrid.values().parallelStream().noneMatch(Objects::isNull) && mergeMovementsAvailable()==0 ) {
                        System.out.println("Game Over");
                        // TO-DO: Step 41. set game over
                        if(Game2048.STEP>=41){ 
                            board.setGameOver(true);
                        }
                    }
                });
            }
            scaleTransition.play();
        }
    }
    
    @SuppressWarnings("unused")
	private SequentialTransition animateMergedTile(SlidingTiles tile) {
        // TO-DO: Step 28. sequential animation, with two scale animations, 
        // from 1 to 1.2, ease_in, and from 1.2 to 1 ease_out, in 80 ms each
        if(Game2048.STEP>=28){
            final ScaleTransition scale0 = new ScaleTransition(Duration.millis(80), tile);
            scale0.setToX(1.2);
            scale0.setToY(1.2);
            scale0.setInterpolator(Interpolator.EASE_IN);

            final ScaleTransition scale1 = new ScaleTransition(Duration.millis(80), tile);
            scale1.setToX(1.0);
            scale1.setToY(1.0);
            scale1.setInterpolator(Interpolator.EASE_OUT);

            return new SequentialTransition(scale0, scale1);
        }
        return new SequentialTransition();
    }
    
    @SuppressWarnings("unused")
	private int mergeMovementsAvailable() {
        final AtomicInteger numMergeableTile = new AtomicInteger();
        // TO-DO: Step 36. Traverse grid in two directions, looking for pairs of mergeable tiles
        if(Game2048.STEP>=36){
            Stream.of(Direction.UP, Direction.LEFT).parallel().forEach(direction -> {
                GridOperator.traverseGrid((x, y) -> {
                    Location thisloc = new Location(x, y);
                    if(Game2048.STEP<43){
                        SlidingTiles t1=gameGrid.get(thisloc);
                        if(t1!=null){
                            Location nextLoc=thisloc.offset(direction);
                            if(nextLoc.isValidFor()){
                                SlidingTiles t2=gameGrid.get(nextLoc);
                                if(t2!=null && t1.isMergeable(t2)){
                                    numMergeableTile.incrementAndGet();
                                }
                            }
                        }
                    } // TO-DO: Step 44. Use optionalTile to find pairs of mergeable tiles
                    else if(Game2048.STEP>=44){
                        optionalTile(thisloc).ifPresent(t1->{
                            optionalTile(thisloc.offset(direction)).filter(t2->t1.isMergeable(t2))
                                    .ifPresent(t2->numMergeableTile.incrementAndGet());
                        });
                    }
                    return 0;
                });
            });
        }
        
        return numMergeableTile.get();
    }
    
    @SuppressWarnings("unused")
	private Optional<SlidingTiles> optionalTile(Location loc) { 
        // TO-DO: Step 43. Return an Optional of nullable from a given location on the map gameGrid
        if(Game2048.STEP>=43){
            return Optional.ofNullable(gameGrid.get(loc)); 
        }
        return null;
    }
}
