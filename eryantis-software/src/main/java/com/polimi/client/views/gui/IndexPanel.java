package main.java.com.polimi.client.views.gui;

import javax.swing.*;
import java.awt.*;

public class IndexPanel extends JPanel {

    private Image background;
    private int index;
    private int width;
    private int height;

    /**
     * @param background background image of the panel
     * @param index index of the panel relative to the context
     * @param width
     * @param height
     */
    public IndexPanel(Image background, int index, int width, int height)
    {
        this.background = background;

        setSize(new Dimension(width, height));

        this.index = index;
        this.width = width;
        this.height = height;
    }

    /**
     * @return index
     */
    public int getIndex() {
        return index;
    }

    @Override
    protected void paintComponent(Graphics g)
    {   super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        //Scales the image

        if(background != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            g2d.drawImage(background, 0, 0, width, height, null);
            g2d.dispose();
        }
    }
}
