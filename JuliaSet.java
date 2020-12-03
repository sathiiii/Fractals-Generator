/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)

    Derived class from ComplexPlane which overrides ots own countIterations method to make the Julia set.
*/

public class JuliaSet extends ComplexPlane {
    private Complex constant = new Complex(-0.4d, 0.6d);

    public JuliaSet() { 
        super();
    }

    public JuliaSet(double real, double imaginary) {
        this.constant = new Complex(real, imaginary);
    }

    public JuliaSet(double real, double imaginary, int maxItr) {
        this(real, imaginary);
        this.maxItr = maxItr;
    }

    public void setConstant(double real, double imaginary) {
        this.constant = new Complex(real, imaginary);
    }

    @Override
    public int countIterations(int x, int y) {
        int count = 0;
        Complex z = new Complex(Plane[x][y].getReal(), Plane[x][y].getImaginary());
        // Escape radius = 50
        while (count < maxItr && (z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary() < 100)) {
            z.square();
            z.add(this.constant);
            count++;
        }
        FractalDraw.smoothColors[x][y] = Math.log(1.5 + count - Math.log(Math.log(z.abs())) / Math.log(2)) / 3.4;
        return count;
    }
}
