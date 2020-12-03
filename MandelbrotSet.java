/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)

    Derived class from ComplexPlane which overrides ots own countIterations method to make the Mandelbrot set.
*/

public class MandelbrotSet extends ComplexPlane {
    public MandelbrotSet() {
        super();
    }

    @Override
    public int countIterations(int x, int y) {
        int count = 0;
        Complex z = new Complex(0d, 0d);
        Complex _c = Plane[x][y];
        // Escape radius = 50
        while (count < maxItr && (z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary() < 100)) {
            z.square();
            z.add(_c);
            count++;
        }
        FractalDraw.smoothColors[x][y] = Math.log(1.5 + count - Math.log(Math.log(z.abs())) / Math.log(2)) / 3.4;
        return count;
    }
}
