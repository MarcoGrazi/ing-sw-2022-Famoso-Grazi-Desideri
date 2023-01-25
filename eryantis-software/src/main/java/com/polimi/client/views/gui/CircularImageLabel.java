package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CircularImageLabel extends JLabel {
    private Color color;
    private Color textColor;
    private Image mageIcon;
    private Image circleMask;
    private int index;
    private int width;
    private int height;

    /**
     * @param mage background image
     * @param width
     * @param height
     * @param path path of the background
     */
    public CircularImageLabel(Image mage, int width, int height, String path) {
        mageIcon = mage;

        ImageIcon circle = Utils.getImageIconFromPath(path, "CircleSquare.png");
        Image newImg = circle.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        circle = new ImageIcon(newImg);
        circleMask = circle.getImage();

        this.color = null;
        this.textColor = Color.BLACK;
        this.index = -1;
        this.width = width;
        this.height = height;
    }

    /**
     * @param width
     * @param height
     * @param color background color
     * @param path path of background
     */
    public CircularImageLabel(int width, int height, Color color, String path) {
        circleMask = null;

        ImageIcon circle = Utils.getImageIconFromPath(path, "CircleSquare.png");
        Image newImg = circle.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        circle = new ImageIcon(newImg);
        circleMask = circle.getImage();

        this.color = color;
        this.textColor = Color.BLACK;
        this.index = -1;
        this.width = width;
        this.height = height;
    }

    /**
     * @param index
     * sets index property
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @param mage
     * changes the mage icon
     */
    public void setMageIcon(Image mage) {
        this.mageIcon = mage;
        repaint();
    }

    /**
     * @param color
     * changes background color
     *
     */
    public void setColour(Color color) {
        this.color = color;
        repaint();
    }

    /**
     * @param color
     * sets text colour
     */
    public void setTextColour(Color color) {
        this.textColor = color;
        repaint();
    }

    /**
     * @return index
     */
    public int getIndex() {
        return index;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if(mageIcon == null) {
            Ellipse2D.Double border = new Ellipse2D.Double(0,0, height/2, width/2);
            border.setFrame(0, height/4, width/2, height/2);
            g2d.setClip(border);

            g2d.setColor(color);
            g2d.fillOval(0, height/4, width/2, height/2);
            g2d.drawImage(circleMask, 0, height/4, width/2, height/2, this);

            if(!getText().equals("")) {
                g2d.setColor(textColor);
                setFont(new Font("Serif", Font.BOLD, 13));
                g2d.drawString(getText(), 12,33);
            }
            g2d.dispose();
        } else {
            Ellipse2D.Double border = new Ellipse2D.Double(0,0, height, width);
            border.setFrame(0, 0, width, height);
            g2d.setClip(border);

            g2d.drawImage(mageIcon, 0, 0, width, height, this);
            g2d.drawImage(circleMask, 0, 0, width, height, this);
            if(!getText().equals("")) {
                g2d.setColor(textColor);
                setFont(new Font("Serif", Font.BOLD, 12));
                g2d.drawString(getText(), 10,18);
            }
            g2d.dispose();
        }

    }
}
