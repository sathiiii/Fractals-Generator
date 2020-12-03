/**
 * Copyright (C) 2020, Sathira Silva. (E/17/331)
 *
 * Following is a program that displays fractals from a user specified Complex set (either Mandelbro set or Julia set) within a user specified range.
 * 
 * All the classes that's been used for this program are listed below:
 * 
 * @see FractalDraw.java
 * @see PositionLabel.java
 * @see ComplexPlane.java
 * @see MandelbrotSet.java
 * @see JuliaSet.java
 * @see IterationCounter.java
 * @see Complex.java
 * 
 * Usage:
 * java Fractal Mandelbrot min(real) max(real) min(imaginary) max(imaginary) to print the Mandelbrot set in the specified range
 * java Fractal Julia C_real C_imaginary to print the Julia set in the range of ((-1, 1), (-1, 1))
 */

import java.awt.Cursor;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class Fractal {
    // Method to validate arguments
    private static void validateArguments(String[] args) {
        if (args[0].equals("Mandelbrot") || args[0].equals("Julia")) {
            // Check if numeric
            for (int i = 1; i < args.length; i++)
                if (!args[i].matches("-?\\d+(\\.\\d+)?")) {
                    printUsage("Invalid usage of arguments");
                    System.exit(0);
                }
        } else {
            // Handle invalid arguments
            printUsage("Invalid fractal type provided");
            System.exit(0);
        }
    }

    // Method to print an error message followed by the usage of the program
    private static void printUsage(String errMessage) {
        System.out.printf("\u001B[31mError: %s.\033[0m\n", errMessage);
        System.out.println("Usage: java Fractal Mandelbrot [REALMIN REALMAX IMAGMIN IMAGMAX] [MAXITR]");
        System.out.println("   or: java Fractal Julia [C_REAL C_IMAG] [MAXITR]\n");
        System.out.println(
                "The first argument must be the type of the fractal (either the Mandelbrot set or the Filled Julia set) to be drawn and is required.\n");
        System.out.println(
                "If the first argument provided is Mandelbrot, the following arguments REALMIN, REALMAX, IMAGMIN, IMAGMAX which specifies the Region Of Interest\nand MAXITR which specifies the maximum number of iterations are optional.");
        System.out.println(
                "If not provided the default values of the Region Of Interest are -1, 1, -1, 1 and the default value of MAXITR is 1000.\n");
        System.out.println(
                "If the first argument provided is Julia, the following arguments C_REAL C_IMAG which specifies the real and imaginary values of the constant\ncomplex number and MAXITR are optional.");
        System.out.println(
                "If not provided the default values of the C_REAL C_IMAG are -0.4, 0.6 and the default value of MAXITR is 1000.");
    }

    public static void main(String[] args) {
        // If the user has not provided any argument show the usage of the program.
        if (args.length == 0) {
            printUsage("Missing FractalType operand");
            System.exit(0);
        }

        // Create a new instance of the ComplexPlane (either MandelbrotSet or Juliaset)
        // Initially an instance of MandelbrotSet is assigned but may be changed
        // depending on the user requested type of the fractal
        ComplexPlane fractalPlane = new MandelbrotSet();

        validateArguments(args);

        if (args[0].equals("Mandelbrot")) {
            // Set the relevant fields of the MandelbrotSet object
            if (args.length >= 5)
                fractalPlane.setROI(Double.parseDouble(args[1]), Double.parseDouble(args[2]),
                        Double.parseDouble(args[3]), Double.parseDouble(args[4]));
            if (args.length == 6)
                fractalPlane.setMaxIterations(Integer.parseInt(args[5]));
        } else if (args[0].equals("Julia")) {
            // If the user has requested the Julia set assign a new instance of JuliaSet to
            // the fractalPlane ComplexPlane type variable
            fractalPlane = new JuliaSet();
            // Set the relevant fields of the JuliaSet object
            if (args.length >= 3)
                ((JuliaSet) fractalPlane).setConstant(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            if (args.length == 4)
                fractalPlane.setMaxIterations(Integer.parseInt(args[3]));
        }

        // Create an instance of the JPanel object: FractalDraw
        FractalDraw fDraw = new FractalDraw(fractalPlane);

        try {
            fDraw.init(); // Call the init method of the JPanel object to start the relevant threads for the pre-computation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Call showAxes method of the FractalDraw object to make the real and imaginary
        // axes visible
        fDraw.showAxes();

        // Create a new instance of a JFrame object
        JFrame frame = new JFrame("Fractals Generator: " + args[0] + " Set");
        // Set the content pane of the frame JFrame object to be the fDraw JPanel object
        frame.setContentPane(fDraw);
        // Set the size of the content of the frame to be its preferred size
        frame.pack();

        // Create a new instance of the posLabel JComponent which shows the complex
        // number pointed by the cursor
        PositionLabel posLabel = new PositionLabel();
        // Add the posLabel object to the layered pane of the root pane of frame object
        frame.getRootPane().getLayeredPane().add(posLabel, JLayeredPane.DRAG_LAYER);
        // Set the boundaries of the posLabel object within the frame object
        posLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        // Add an event listener to the frame object to trigger the cursor motion
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                posLabel.setX(e.getX());
                posLabel.setY(e.getY());
                // Repaint the string in the posLabel object at each cursor motion
                posLabel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                posLabel.setX(e.getX());
                posLabel.setY(e.getY());
                // Repaint the string in the posLabel object at each cursor motion
                posLabel.repaint();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Change the cursor to the crosshair cursor
        frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        // Make the window fixed
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        // Make the window visible
        frame.setVisible(true);
    }
}
