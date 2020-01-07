
/**
 * Class representing an example/instance in the dataset.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class Instance 
{
    double[] values;
    int id;
    
    /**
     * Create new exmpty instance.
     *
     * @param no_attr Number of attributes for this instance
     * @param id Id of this instances
     */
    public Instance(int no_attr, int id) {
        this.values = new double[no_attr];
        this.id = id;
    }

    /**
     * Create new instance from values.
     * 
     * @param x_val Input 
     @param id Id of this instances
     */
    public Instance(double[] vals, int id) {
        this.values = vals;
        this.id = id;
    }
    
    /**
     * Returns the value of the attribute at the specified index.
     * 
     * @param index Index of the attribute
     * @return Attribute value
     */
    public double get(int index) {
        return values[index];
    }
    
    /**
     * Sets the value of the attribute at the specified index.
     * 
     * @param index Index of the attribute
     * @param val Attribute value
     */
    public void set(int index, double val) {
        values[index] = val;
    }

    /**
     * Returns the number of attributes for this instance.
     *
      @return Number of attributes
     */
    public int length() {
        return values.length;
    }

    /**
     * Euclidean distance between two instances.
     * 
     * @param a First instance
     * @param b Second instance
     * @return Euclidean distance
     */
    public static double euclidean(Instance a, Instance b) {
        double sum = 0;
        
        // Iterate over all attributes in the instances
        for (int i = 0; i < a.length(); i++) {
            sum += Math.pow(a.get(i) - b.get(i), 2);
        }
        
        return Math.sqrt(sum);
    } 
}
