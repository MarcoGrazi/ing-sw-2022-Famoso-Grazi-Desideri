package main.java.com.polimi.client.views.gui;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private Image background;

    /**
     * @param background image to set as background
     */
    public BackgroundPanel(Image background)
    {
        this.background = background;
        setLayout( new BorderLayout() );
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        //Scales the image
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(background.getWidth(this), background.getHeight(this));
    }
}
