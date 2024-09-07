package components;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color borderColor;

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        this.borderColor = new Color(131, 126, 253); // Default border color
        setOpaque(false); // Ensures the panel is transparent
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint(); // Repaint to update the border color
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1)); // Thickness of the border
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }
}
