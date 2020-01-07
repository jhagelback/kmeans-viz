
import java.util.*;

/**
 * Visualization of K-Means Clustering
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class Main 
{
    // Instances in the dataset
    ArrayList<Instance> instances;
    // Max counts
    int[] max_counts;
    // Min counts
    int[] min_counts;
    // Randomizer
    Random rnd = new Random();
    // Clusters for k-means clustering
    Cluster[] clusters;
    // K-means visualization GUI
    VizGUI vg;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Main m = new Main();
        m.run_visualization();
    }
    
    /**
     * Runs k-means visualization.
     */
    private void run_visualization() {
        // The seed values to use in the visualization runs
        int[][] runs = {
            {1,42},
            {7,43},
            {8,44},
            {10,45},
            {11,46}
        };
        
        // Init the GUI
        vg = new VizGUI();
        vg.start();

        // Wait some time before start
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {}

        // Run visualization
        for (int i = 0; i < runs.length; i++) {
            // Get current seed values
            int seed1 = runs[i][0];
            int seed2 = runs[i][1];
            // Generate new random data
            random_data(seed1, seed2);
            // Run k-means
            kMeansClustering(4);
        }
    }
        
    /**
     * Randomizes k-means clustering data for visualization.
     */
    private void random_data(int seed1, int seed2) {
        rnd = new Random(seed1);
        
        Random r = new Random(seed2);
        instances = new ArrayList<>();
        
        // Create three clusters of Gaussian randomly placed instances
        int id_cnt = 1;
        for (int i = 0; i < 160; i++) {
            // Coordinate
            double[] c = random_coord(r, 200, 170);
            // Add instance
            Instance a = new Instance(c, id_cnt);
            instances.add(a);
            id_cnt++;
        }
        for (int i = 0; i < 160; i++) {
            // Coordinate
            double[] c = random_coord(r, 430, 270);
            // Add instance
            Instance a = new Instance(c, id_cnt);
            instances.add(a);
            id_cnt++;
        }
        for (int i = 0; i < 180; i++) {
            // Coordinate
            double[] c = random_coord(r, 300, 430);
            // Add instance
            Instance a = new Instance(c, id_cnt);
            instances.add(a);
            id_cnt++;
        }
        
        // Set min and max counts
        min_counts = new int[2];
        max_counts = new int[2];
        min_counts[0] = 40;
        min_counts[1] = 40;
        max_counts[0] = RenderPanel.w - 40;
        max_counts[1] = RenderPanel.h - 40;
    }
    
    /**
     * Generates a Gaussian random coordinate.
     * 
     * @param r Randomizer
     * @param sx Start x-coord
     * @param sy Start y-coord
     * @return Random coordinate
     */
    private double[] random_coord(Random r, double sx, double sy) {
        double[] c = new double[2];
        // Coordinates
        c[0] = sx + r.nextGaussian() * 60 + (r.nextDouble() * 20.0 - 10.0);
        c[1] = sy + r.nextGaussian() * 60 + (r.nextDouble() * 10.0 - 5.0);

        // Check bounds
        if (c[0] < 10) c[0] = 10;
        if (c[1] < 10) c[1] = 10;
        if (c[0] > RenderPanel.w - 10) c[0] = RenderPanel.w - 10;
        if (c[1] > RenderPanel.h - 10) c[1] = RenderPanel.h - 10;
        
        // Return coordinate
        return c;
    }
    
    
    /**
     * Generates a random instance.
     * 
     * @return Random instance.
     */
    private Instance rnd_instance() {
        Instance a = new Instance(2, 0);
        
        // Iterate over all attributes
        for (int i = 0; i < a.length(); i++) {
            // Generate random value
            int range = max_counts[i] - min_counts[i];
            int rnd_cnt = rnd.nextInt(range) + min_counts[i];
            // Update attribute
            a.set(i, rnd_cnt);
        }
        
        return a;
    }
    
    /**
     * Performs k-means clustering on the dataset.
     * 
     * @param n Number of clusters
     */
    public void kMeansClustering(int n) {
        vg.init(instances);
        
        // Generate n random clusters
        clusters = new Cluster[n];
        for (int i = 0; i < n; i++) {
            Instance rnda = rnd_instance();
            clusters[i] = new Cluster(i, rnda);
        }
        
        vg.place_centroids(clusters);
        
        // Iterate until assignments doesn't change
        boolean updated = true;
        int cnt = 0;
        while (updated) {
            updated = iterate();
            cnt++;
        }
        
        vg.done();
    }
    
    /**
     * Performs one iteration in k-means clustering. 
     * 
     * @return True if assignments have changed (continue iteration)
     */
    private boolean iterate() {
        // Reset cluster assignments
        for (Cluster c : clusters) {
            c.reset();
        }
        
        // Iterate over each instance to find the closest cluster
        for (Instance a : instances) {
            Cluster bestC = null;
            double bestD = 10000;
            
            for (Cluster c : clusters) {
                // Distance to cluster
                double dist = Instance.euclidean(a, c.a);
                // Check if new closest distance
                if (dist < bestD) {
                    bestD = dist;
                    bestC = c;
                }
            }
            
            //Assign article to best cluster
            bestC.assign(a);
        }
        
        // Recalculate centroids
        for (Cluster c : clusters) {
            c.recalc_center();
        }
        
        vg.iterate(clusters);
        
        // Check if current assignment matches previous
        for (Cluster c : clusters) {
            if (!c.match_previous()) {
                return true;
            }
        }
        
        return false;
    }
}
