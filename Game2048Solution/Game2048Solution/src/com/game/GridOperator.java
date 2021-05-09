
package com.game;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridOperator {
    
    private static final List<Integer> traversalX = IntStream.range(0, 4).boxed().collect(Collectors.toList());
    private static final List<Integer> traversalY = IntStream.range(0, 4).boxed().collect(Collectors.toList());
    
    public static int traverseGrid(IntBinaryOperator func) {
        AtomicInteger at = new AtomicInteger();
        // TO-DO: Step 24. Traverse grid, applyinf the functional to every cell, returning the 
        // accumulated result
        if(Game2048.STEP>=24){
            traversalX.forEach(x -> {
                traversalY.forEach(y -> {
                    at.addAndGet(func.applyAsInt(x, y));
                });
            });
        }
        return at.get();
    }

    public static void sortGrid(Direction direction){
        // TO-DO: Step 26. Sort TraversalX, traversalY, so for Right or Down directions 
        // they are taken in reverse order
        if(Game2048.STEP>=26){
            Collections.sort(traversalX, direction.equals(Direction.RIGHT) ? Collections.reverseOrder() : Integer::compareTo);
            Collections.sort(traversalY, direction.equals(Direction.DOWN)? Collections.reverseOrder() : Integer::compareTo);
        }
    }
}
