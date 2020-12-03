/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)

    Complex is a class that is used to manipulate complex numbers.
*/

public class Complex {
    private double real, imaginary;

    public Complex() {}

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    // Method to set the real part of the complex number
    public void setReal(double real) {
        this.real = real;
    }

    // Method to get the real part of the complex number
    public double getReal() {
        return this.real;
    }

    // Method to set the imaginary part of the complex number
    public void setImaginary(double imaginary) {
        this.imaginary = imaginary;
    }

    // Method to get the imaginary part of the complex number
    public double getImaginary() {
        return this.imaginary;
    }

    // Method to get the absolute value of the complex number
    public double abs() {
        return Math.sqrt(this.real * this.real + this.imaginary * this.imaginary);
    }

    // Method to add two complex numbers
    public void add(Complex c) {
        this.real += c.getReal();
        this.imaginary += c.getImaginary();
    }

    // Method to square the complex number
    public void square() {
        double x = this.real;
        this.real = x * x - this.imaginary * this.imaginary;
        this.imaginary = 2 * x * this.imaginary;
    }
}
