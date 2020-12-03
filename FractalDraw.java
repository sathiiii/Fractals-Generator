/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)

    FractalDraw is the base class for painting inside the GUI.

    The number of iterations for each complex number corresponding to each pixel is calculated prior to the painting. The calculation is divided into batches of
    SEG_SIZE and handled to some Threads to compute them parallely. The computed iteration counts are stored in the iterationsCount array.

    An object of the ComplexPlane is used as a member of the FractalDraw class so that the relevant derived class object of the ComplexPlane (MandelbrotSet or JuliaSet) 
    can be set explicitly and the FractalDraw class can communicate directly with the ComplexPlane object to set or get its attributes.

    There are two colouring schemes used to colour the fractals:
    (01). Smooth Colouring scheme (which is very similar to the coloring scheme used in the Mandelbrot set picture on Wikipedia)
    (02). Gradient Colouring scheme (Gradient colour bands with smooth edges)
*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FractalDraw extends JPanel {
    private static final long serialVersionUID = 1L;
    // Width and Height of the canvas
    private static final int WIDTH = 800, HEIGHT = 800;
    // The size of the pixel area segment that one thread handles
    public static final int SEG_SIZE = 100;

    // Used to store 16 points of a gradient which is used by the getColor2 method
    private Color[] gradientMap = new Color[16];
    // Array of threads which is used to pre-compute the iterationsCount array
    private Thread[] iterationCounters = new Thread[WIDTH * HEIGHT / (SEG_SIZE * SEG_SIZE)];
    // ready is set to true when all the iterationCounters Threads have been
    // completed
    // The boolean variable show is used whether or not to paint the cartesian axes
    // The saveImage flag is used to write the BufferedImage into a file only once
    private boolean ready = false, show = false, saveImage = true;

    // To store the number of iterations taken by each complex number corresponding
    // to each pixel on the canvas
    public static int[][] iterationsCount = new int[WIDTH][HEIGHT];
    // To store the potential values which is computed to generate smooth spectrums
    // of colors
    /**
     * @see MandelbrotSet#countIterations(int, int) or
     * @see JuliaSet#countIterations(int, int) for more details.
     */
    public static double[][] smoothColors = new double[WIDTH][HEIGHT];
    // ComplexPlane object which is used to generate both MandelbrotSet and JuliaSet
    public static ComplexPlane compPlane;
    // The BufferedImage which the fracts plot get painted into
    private BufferedImage bufImage;

    // Smooth coloring scheme
    private Color getColor1(int x, int y) {
        if (iterationsCount[x][y] < compPlane.getMaxIterations()) {
            double red = 0, green = 0, blue = 0;
            if (smoothColors[x][y] < 1.0) {
                red = Math.pow(smoothColors[x][y], 4);
                green = Math.pow(smoothColors[x][y], 2.5);
                blue = smoothColors[x][y];
            } else {
                smoothColors[x][y] = Math.max(0, 2 - smoothColors[x][y]);
                red = smoothColors[x][y];
                green = Math.pow(smoothColors[x][y], 1.5);
                blue = Math.pow(smoothColors[x][y], 3);
            }
            return new Color((float) red, (float) green, (float) blue, 1.0f);
        }
        return Color.BLACK;
    }

    // Gradient coloring scheme with anit-aliased (kind of) edges
    private Color getColor2(int x, int y) {
        if (iterationsCount[x][y] < compPlane.getMaxIterations()) {
            Color c = gradientMap[iterationsCount[x][y] % 16];
            if (x > 0 && y > 0) {
                // Color of the pixel left to the current pixel
                Color c1 = gradientMap[iterationsCount[x - 1][y] % 16];
                // Color of the pixel above the current pixel
                Color c2 = gradientMap[iterationsCount[x][y - 1] % 16];
                Color avg = new Color((c1.getRed() + c.getRed()) / 2, (c1.getGreen() + c.getGreen()) / 2,
                        (c1.getBlue() + c.getBlue()) / 2, 255);
                return new Color((c2.getRed() + avg.getRed()) / 2, (c2.getGreen() + avg.getGreen()) / 2,
                        (c2.getBlue() + avg.getBlue()) / 2, 255);
            }
            if (x > 0) {
                // Color of the pixel left to the current pixel
                Color c1 = gradientMap[iterationsCount[x - 1][y] % 16];
                return new Color((c1.getRed() + c.getRed()) / 2, (c1.getGreen() + c.getGreen()) / 2,
                        (c1.getBlue() + c.getBlue()) / 2, 255);
            }
            if (y > 0) {
                // Color of the pixel above the current pixel
                Color c1 = gradientMap[iterationsCount[x][y - 1] % 16];
                return new Color((c1.getRed() + c.getRed()) / 2, (c1.getGreen() + c.getGreen()) / 2,
                        (c1.getBlue() + c.getBlue()) / 2, 255);
            }
            return c;
        }
        return Color.BLACK;
    }

    // Default constructor for a FractalDraw object
    public FractalDraw() {
        // Set the default size of the canvas
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Initialize the gradient points
        gradientMap[0] = new Color(66, 30, 15);
        gradientMap[1] = new Color(25, 7, 26);
        gradientMap[2] = new Color(9, 1, 47);
        gradientMap[3] = new Color(4, 4, 73);
        gradientMap[4] = new Color(0, 7, 100);
        gradientMap[5] = new Color(12, 44, 138);
        gradientMap[6] = new Color(24, 82, 177);
        gradientMap[7] = new Color(57, 125, 209);
        gradientMap[8] = new Color(134, 181, 229);
        gradientMap[9] = new Color(211, 236, 248);
        gradientMap[10] = new Color(241, 233, 191);
        gradientMap[11] = new Color(248, 201, 95);
        gradientMap[12] = new Color(255, 170, 0);
        gradientMap[13] = new Color(204, 128, 0);
        gradientMap[14] = new Color(153, 87, 0);
        gradientMap[15] = new Color(106, 52, 3);
        bufImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    public FractalDraw(ComplexPlane _compPlane) {
        this();
        compPlane = _compPlane;
    }

    // Method used to start the threads in the iterationCounters array
    public void init() throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int x = 0; x < WIDTH; x += SEG_SIZE)
            for (int y = 0; y < HEIGHT; y += SEG_SIZE) {
                // (x, y) is flattened into 1D using the formula: (WIDTH / SEG_SIZE) * x /
                // SEG_SIZE + y / SEG_SIZE
                iterationCounters[(WIDTH / SEG_SIZE) * x / SEG_SIZE + y / SEG_SIZE] = new Thread(
                        new IterationCounter(x, y));
                iterationCounters[(WIDTH / SEG_SIZE) * x / SEG_SIZE + y / SEG_SIZE].start();
            }
        // Synchronization: Wait until all the threads get completed
        for (int i = 0; i < iterationCounters.length; i++)
            iterationCounters[i].join();
        long end = System.currentTimeMillis();
        System.out.printf("The pre-calculation took %dms to execute\n", end - start);
        // Pre-computation is ready
        ready = true;
    }

    // Method to switch the axes on
    public void showAxes() {
        this.show = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics G = bufImage.getGraphics();
        // Wait until all the threads complete
        while (!ready);
        // Paint each pixel to a BufferedImage using the pre-computed values
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                G.setColor(getColor1(x, y));
                G.fillRect(x, y, 1, 1);
            }
        }
        if (saveImage) {
            try {
                saveImage("mandelbrot_1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveImage = false;
        // Then transfer the drawn BufferedImage into the JPanel
        g.drawImage(bufImage, 0, 0, this);
        // Paint the axes
        if (show) {
            g.setColor(Color.RED);
            double[] ROI = compPlane.getROI();
            if (ROI[2] <= 0)
                for (int x = 0; x < WIDTH; x++)
                    g.fillRect(x, (int) (-ROI[2] * WIDTH / (ROI[3] - ROI[2])), 1, 1);
            if (ROI[0] <= 0)
                for (int y = 0; y < HEIGHT; y++)
                    g.fillRect((int) (-ROI[0] * WIDTH / (ROI[1] - ROI[0])), y, 1, 1);
        }
    }

    // Write the BufferedImage to a file
    public void saveImage(String filename) throws IOException {
        File outputfile = new File(filename + ".png");
        ImageIO.write(bufImage, "png", outputfile);
        System.out.println("Fractals were written into the file successfully!");
    }
}
