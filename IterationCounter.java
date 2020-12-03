/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)

    IterationCounter class is a derived class from Runnable which is used to calculate the number of iterations corresponding to each pixel, in segments.
*/

public class IterationCounter implements Runnable {
    private static final int WIDTH = 800, HEIGHT = 800;
    
    private int x, y;

    public IterationCounter(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        for (int i = x; i < WIDTH && i < x + FractalDraw.SEG_SIZE; i++)
            for (int j = y; j < HEIGHT && j < y + FractalDraw.SEG_SIZE; j++)
                FractalDraw.iterationsCount[i][j] = FractalDraw.compPlane.countIterations(i, j);
    }
}
