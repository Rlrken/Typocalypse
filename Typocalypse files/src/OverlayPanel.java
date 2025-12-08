import javax.swing.*;
import java.awt.*;

/**
 * Simple full-screen translucent overlay. Call setAlpha(0..1) to change opacity.
 */
public class OverlayPanel extends JPanel {
    private float alpha = 0f;

    public OverlayPanel() {
        setOpaque(false);
        // Let mouse events pass through by default; we don't consume events here.
        setFocusable(false);
    }

    public void setAlpha(double alpha) {
        this.alpha = (float) Math.max(0, Math.min(1, alpha));
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (alpha <= 0f) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
