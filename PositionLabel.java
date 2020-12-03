/*
    Copyright (C) 2020, Sathira Silva. (E/17/331)

    PositionLabel is a derived class from JComponent used for painting the complex coordinates corresponding to the pixel pointed by the cursor.
*/

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class PositionLabel extends JComponent {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 800, HEIGHT = 800;

    // Coordinates of the cursor pointer inside the canvas
    private int x, y;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        // Ignore the cursor positions which are not in the Region Of Interest.
        if (x >= WIDTH || y >= HEIGHT) return;
        Complex curr = ComplexPlane.Plane[x][y];
        g.drawString(String.format("(%.4f, %.4f)", curr.getReal(), curr.getImaginary()), x, y);
    }

    // Method to set the x coordinate
    public void setX(int x) {
        this.x = x;
    }

    // Method to set the y coordinate
    public void setY(int y) {
        this.y = y;
    }
}
