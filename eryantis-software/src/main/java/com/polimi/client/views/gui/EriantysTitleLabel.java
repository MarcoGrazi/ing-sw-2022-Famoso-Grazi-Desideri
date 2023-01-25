package main.java.com.polimi.client.views.gui;

import javax.swing.*;
import java.awt.*;

public class EriantysTitleLabel extends JLabel {

    /**
     * @param text
     * sets the title in main screen
     */
    public EriantysTitleLabel(String text) {
        super(text);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        float[] fractions = new float[30];
        Color[] colors = new Color[30];
        for (int i = 0; i < colors.length; i++) {
            fractions[i] = ((float)i) / 30;
            float hue = fractions[i];
            colors[i] = Color.getHSBColor(hue, 1f, 1f);
        }

        LinearGradientPaint lpg = new LinearGradientPaint(
                new Point(0, 0),
                new Point(getWidth(), 0),
                fractions,
                colors);

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(lpg);
        g2d.setFont(new Font("Serif", Font.PLAIN, 150));
        g2d.drawString(getText(), 0, 150);
        g2d.dispose();
    }
}
