/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)
    
    The ComplexPlane class is the base class for the ComplexPlane objects. It contains methods to map the pixels in a a canvas of 800 x 800 to Complex values, methods
    to get and set the Region Of Interest of the complex plane and an abstract method to count iterations to create fractal sets with methods to get and set the
    maximum number of iterations required for the fractal computations.
*/

public abstract class ComplexPlane {
    private static final int WIDTH = 800, HEIGHT = 800;

    private double realMin = -1d, realMax = 1d, imagMin = -1d, imagMax = 1d;
    protected static Complex[][] Plane = new Complex[WIDTH][HEIGHT];
    protected int maxItr = 1000;

    // Method which maps complex numbers to each pixel on the canvas
    private void mapCanvas2ComplexPlane() {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                Plane[x][y] = pixel2Complex(x, y);
    }

    // Method which converts the pixel coordinates to complex numbers
    private Complex pixel2Complex(int x, int y) {
        Complex z = new Complex();
        // Calculated for unstretched zooming (to make the x and y axis in the same scale)
        double min = Math.min(this.realMax - this.realMin, this.imagMax - this.imagMin);
        z.setReal(this.realMin + x * min / (double) WIDTH + (this.realMax - this.realMin - min) / 2d);
        z.setImaginary(this.imagMax - y * min / (double) HEIGHT - (this.imagMax - this.imagMin - min) / 2d);
        return z;
    }

    public ComplexPlane() {
        mapCanvas2ComplexPlane();
    }

    public ComplexPlane(double realMin, double realMax, double imagMin, double imagMax) {
        this();
        this.realMin = realMin;
        this.realMax = realMax;
        this.imagMin = imagMin;
        this.imagMax = imagMax;
        mapCanvas2ComplexPlane();
    }

    public ComplexPlane(double realMin, double realMax, double imagMin, double imagMax, int maxItr) {
        this(realMin, realMax, imagMin, imagMax);
        this.maxItr = maxItr;
    }

    // Method to set the Region Of Intereset
    public void setROI(double realMin, double realMax, double imagMin, double imagMax) {
        this.realMin = realMin;
        this.realMax = realMax;
        this.imagMin = imagMin;
        this.imagMax = imagMax;
        mapCanvas2ComplexPlane();
    }

    // Method to get the Region Of Interest
    public double[] getROI() {
        return new double[] {realMin, realMax, imagMin, imagMax};
    }

    // Method to set the maximum number of iterations
    public void setMaxIterations(int maxItr) {
        this.maxItr = maxItr;
    }

    // Method to get the maximum number of iterations
    public int getMaxIterations() {
        return this.maxItr;
    }

    public abstract int countIterations(int x, int y);
}
