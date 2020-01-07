
import java.util.*;

/**
 * Represents a cluster in K-means Clustering.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class Cluster 
{
    // Cluster id
    int id;
    // Randomly genereated instance (centroid)
    public Instance a;
    // Instances belonging to this cluster
    public ArrayList<Instance> instances;
    // Previous cluster (used to stop iterating when no more changes are made)
    ArrayList<Integer> prevMatch;
    
    /**
     * Create a new cluster.
     * 
     * @param id Cluster id
     * @param a Randomly genereated instance (centroid)
     */
    public Cluster(int id, Instance a) {
        this.id = id;
        this.a = a;
        instances = new ArrayList<>();
    }
    
    /**
     * Assigns an instance to this cluster.
     * 
     * @param inst The instance to assign
     */
    public void assign(Instance inst) {
        instances.add(inst);
    }
    
    /**
     * Resets all assignments for this cluster.
     */
    public void reset() {
        // Set previous assignment
        prevMatch = new ArrayList<>();
        for (Instance a : instances) {
            prevMatch.add(a.id);
        }
        Collections.sort(prevMatch);
        
        // Reset assignments
        instances.clear();
    }
    
    /**
     * Checks if the new assignment matches the previous assignment.
     * 
     * @return True if matches, false otherwise
     */
    public boolean match_previous() {
        // Check if all articles were in previous assignment
        for (Instance a : instances) {
            if (!prevMatch.contains(a.id)) {
                // Not found in previous - new assignment
                return false;
            }
        }
        // Identical assignment
        return true;
    }
    
    /**
     * Recalculates the centroid to be in the center of the cluster.
     */
    public void recalc_center() {
        
        // Previous position (used for visualization)
        double oX = a.get(0);
        double oY = a.get(1);
        
        // Iterate over all attributes
        for (int i = 0; i < a.length(); i++) {
            
            // Iterate over all instances in this cluster to calculate average word count
            double avg = 0;
            for (Instance a2 : instances) {
                avg += a2.get(i);
            }
            avg /= instances.size();
            
            // Set new word count
            a.set(i, avg);
        }
        
        // New position (used for visualization)
        double nX = a.get(0);
        double nY = a.get(1);
        
        // For visualization, limit the length of the steps instead of moving
        // directly to the center.
        double m = Math.sqrt( Math.pow(oX-nX, 2) + Math.pow(oY-nY, 2) );
        if (m > 10) {
            a.set(0, oX + 10.0 * (nX-oX) / m);
            a.set(1, oY + 10.0 * (nY-oY) / m);
        }
    }
}
