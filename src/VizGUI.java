
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Main class for the k-means visualization.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class VizGUI {
    // The render panel
    RenderPanel rp;
    // The GUI frame
    JFrame frame;
    // State label
    JLabel state;
    
    /**
     * New visualization GUI.
     */
    public VizGUI() {
        
    }
    
    /**
     * Shows the GUI.
     */
    public void start() {
        rp = new RenderPanel();
        
        frame = new JFrame("K-Means Clustering");
        frame.setSize(RenderPanel.w + 20, RenderPanel.h + 60);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        
        // State label
        state = new JLabel(" ");
        state.setPreferredSize(new Dimension(RenderPanel.w, 20));
        state.setHorizontalAlignment(SwingConstants.CENTER);
        Font font = new Font("LucidaGrande", Font.BOLD, 16);  
        state.setFont(font);
        frame.getContentPane().add(state);
        
        // Render panel
        frame.getContentPane().add(rp);
        
        // Show frame
        frame.setVisible(true);
    }
    
    /**
     * Set GUI state to init.
     * 
     * @param instances The instances to show
     */
    public void init(ArrayList<Instance> instances) {
        state.setText("1. The data points");
        
        rp.clear();
        rp.init(instances);
        
        sleep(2000);
    }
    
    /**
     * Set GUI state to random centroid placement.
     * 
     * @param clusters The clusters with random centroids
     */
    public void place_centroids(Cluster[] clusters) {
        state.setText("2. Place 4 random centroids");
        
        rp.place_centroids(clusters);
        
        // Animate circles
        int cent_size = 9;
        int dir = 1;
        for (int i = 0; i < 24; i++)
        {
            cent_size += 2 * dir;
            rp.anim_centroid(cent_size);
            sleep(100);
            
            if (cent_size == 17) dir *= -1;
            if (cent_size == 9) dir *= -1;   
        }
        
        sleep(500);
    }
    
    /**
     * Updates the GUI after an iteration.
     * 
     * @param clusters New clusters to render
     */
    public void iterate(Cluster[] clusters) {
        state.setText("3. Move centroids to center of clusters and update assignments");
        
        rp.iterate(clusters);
        
        sleep(150);
    }
    
    /**
     * Sets GUI state to done.
     */
    public void done() {
        state.setText("4. Reached stable solution");
        
        rp.done();
        
        // Animate circles
        int cent_size = 9;
        int dir = 1;
        for (int i = 0; i < 48; i++)
        {
            cent_size += 2 * dir;
            rp.anim_centroid(cent_size);
            sleep(100);
            
            if (cent_size == 17) dir *= -1;
            if (cent_size == 9) dir *= -1;   
        }
        
        sleep(1000);
    }
    
    /**
     * GUI sleep.
     * 
     * @param ms Milliseconds to sleep
     */
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex) {
            
        }
    }
}
