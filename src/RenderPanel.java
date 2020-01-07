
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * Render GUI panel for the k-means visualization.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class RenderPanel extends JPanel {
    // Clusters to show
    Cluster[] clusters;
    // List of instances
    ArrayList<Instance> instances;
    // Color for each cluster
    Color[] colors;
    // History of centroid positions
    HashMap<Color, ArrayList<Coord>> hist;
    // Size of render area
    public static int w = 680;
    public static int h = 580;
    // Render state
    int state = 0;
    // Size of centroid circle
    int cent_size = 9;
    
    /**
     * Class to hold (x,y) coordinates.
     */
    private class Coord {
        int x;
        int y;
        int w;
        
        /**
         * New coordinate.
         * 
         * @param x X-coord
         * @param y Y-coord
         */
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
            this.w = 0;
        }
        
        /**
         * New coordinate from instance.
         * 
         * @param inst The instance
         * @param w Width of rendered circle
         */
        public Coord(Instance inst, int w) {  
            double nx = inst.get(0) - w/2;
            double ny = inst.get(1) - w/2;
            this.x = (int)Math.round(nx);
            this.y = (int)Math.round(ny);
            this.w = w;
        }
    }
    
    /**
     * New render panel.
     */
    public RenderPanel() {
        // Define render colors
        colors = new Color[4];
        colors[0] = new Color(58, 44, 247); //Blue
        colors[1] = new Color(196, 55, 27); //Red
        colors[2] = new Color(35, 196, 27); //Green
        colors[3] = new Color(219, 158, 26); //Orange
        
        // Centroid history
        hist = new HashMap<>();
        
        // Set state
        state = 0;
        
        // Set panel size
        this.setPreferredSize(new Dimension(w, h));
    }
    
    /**
     * Clears the rendering.
     */
    public void clear() {
        hist = new HashMap<>();
        clusters = null;
        instances = null;
        state = 0;
        
        updateUI();
        repaint();
    }
    
    /**
     * Set GUI state to init.
     * 
     * @param instances The instances to show
     */
    public void init(ArrayList<Instance> instances) {
        this.instances = instances;
        state = 1;
        
        updateUI();
        repaint();
    }
    
    /**
     * Set GUI state to random centroid placement.
     * 
     * @param c The clusters with random centroids
     */
    public void place_centroids(Cluster[] c) {
        clusters = c;
        state = 2;
        
        updateUI();
        repaint();
    }
    
    /**
     * Updates the GUI after an iteration.
     * 
     * @param c New clusters to render
     */
    public void iterate(Cluster[] c) {
        clusters = c;
        state = 3;
        
        updateUI();
        repaint();
    }
    
    /**
     * Sets GUI state to done.
     */
    public void done() {
        state = 4;
        
        updateUI();
        repaint();
    }
    
    /**
     * Animates centroid circle by changing size.
     * 
     * @param size Size of circle
     */
    public void anim_centroid(int size) {
        this.cent_size = size;
        updateUI();
        repaint();
    }
    
    @Override
    public void paint(Graphics gn) {
        Graphics2D g = (Graphics2D)gn;
        
        // Background
        g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        // Show data points only for state 1 and 2
        if (state == 1 || state == 2) {
            for (Instance inst : instances) {
                g.setColor(Color.black);
                Coord ac = new Coord(inst, 5);
                g.fillOval(ac.x, ac.y, ac.w, ac.w);
            }
        }
        // State 2: Show random clusters placement
        if (state == 2) {
            for (int cnt = 0; cnt < clusters.length; cnt++) {
                // Get cluster
                Cluster c = clusters[cnt];
                // Show centroid
                Coord center = new Coord(c.a, cent_size);
                g.setColor(colors[cnt]);
                g.fillOval(center.x, center.y, center.w, center.w);
            }
        }
        
        // Show iterations for state 3 and 4
        if (state == 3 || state == 4) {
            // Iterate over each cluster
            for (int cnt = 0; cnt < clusters.length; cnt++) {
                // Get cluster
                Cluster c = clusters[cnt];

                // Position of centroid
                Coord center = new Coord(c.a, cent_size);

                // Render lines between instances and centroids
                for (Instance inst : c.instances) {
                    Coord ac = new Coord(inst, 5);
                    drawFilledLine(g, center, ac);
                }

                // Render history of how the centroids move
                if (hist.containsKey(colors[cnt])) {
                    ArrayList<Coord> chist = hist.get(colors[cnt]);
                    for (int i = 0; i < chist.size(); i++) {
                        // Calculate fading color
                        Color fade = get_color(colors[cnt], 8 * (i + 1));
                        g.setColor(fade);
                        // Render history point
                        Coord p = chist.get(i);
                        g.fillOval(p.x, p.y, 7, 7);
                    }
                }

                // Instances color
                Color instcol = get_color(colors[cnt], -60);
                g.setColor(instcol);
                // Render all instances for this centroid
                for (Instance inst : c.instances) {
                    Coord ac = new Coord(inst, 5);
                    g.fillOval(ac.x, ac.y, ac.w, ac.w);
                }

                // Render centroid
                g.setColor(colors[cnt]);
                g.fillOval(center.x, center.y, center.w, center.w);

                // Add centroid to history
                if (!hist.containsKey(colors[cnt])) {
                    hist.put(colors[cnt], new ArrayList<>());
                }
                ArrayList<Coord> chist = hist.get(colors[cnt]);
                if (add(chist, center)) {
                    chist.add(0, center);
                }
            }
        }
    }
    
    /**
     * Returns a darker or lighter color.
     * 
     * @param c Base color
     * @param diff Difference (negative for darker, positive for lighter)
     * @return New color
     */
    private Color get_color(Color c, int diff) {
        int cr = clamp(c.getRed() + diff, 0, 255);
        int cg = clamp(c.getGreen() + diff, 0, 255);
        int cb = clamp(c.getBlue() + diff, 0, 255);
        return new Color(cr, cg, cb);
    }
    
    /**
     * Clamps an integer value between min and max.
     * 
     * @param v The value
     * @param min Min
     * @param max Max
     * @return Clamped value
     */
    private int clamp(int v, int min, int max) {
        if (v < min) return min;
        else if (v > max) return max;
        else return v;
    }
    
    /**
     * Checks if a centroid coordinate shall be added to the cluster's history. We don't
     * add if the coordinate only moved a short distance.
     * 
     * @param h Cluster's history
     * @param center Current centroid coordinate
     * @return True if add to history, false otherwise
     */
    private boolean add(ArrayList<Coord> h, Coord center)
    {
        // Always add the first coordinate
        if (h.isEmpty()) return true;
        // Get absolut difference from previous coordinate
        Coord p0 = h.get(0);
        int diff = Math.abs(p0.x - center.x) + Math.abs(p0.y - center.y);
        // Check if difference is large enough
        return diff > 4;
    }
        
    /**
     * Draws a gray line.
     * 
     * @param g Graphics2D
     * @param c1 Start coordinates
     * @param c2 End coordinates
     */
    private void drawFilledLine(Graphics2D g, Coord c1, Coord c2) {
        g.setColor(new Color(230, 230, 230));
        g.setStroke(new BasicStroke(1));
        g.drawLine(c1.x + c1.w / 2, c1.y + c1.w / 2, c2.x + c2.w / 2, c2.y + c2.w / 2);
    }
}
