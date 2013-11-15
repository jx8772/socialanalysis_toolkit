package collaborativefiltering.utility;
/**
 *
 * @author xji
 */
public class Number {
    public static double getNDecimals(double number, int n) {
        int multiplier = (int) Math.pow(10, n);
        return (double)Math.round(number*multiplier)/multiplier;
    }
}
