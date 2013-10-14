package collaborativefiltering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/3/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Date implements Comparable<Date> {
    private static final int[] DAYS = { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    private static final List<String> MONTHS = Arrays.asList("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

    private int month;   // month (between 1 and 12)
    private int day;     // day   (between 1 and DAYS[month]
    private int year;    // year
    public boolean PatientLikeMeDateValid;

    public boolean isPatientLikeMeDateValid() {
        return PatientLikeMeDateValid;
    }

    public void setPatientLikeMeDateValid(boolean patientLikeMeDateValid) {
        PatientLikeMeDateValid = patientLikeMeDateValid;
    }

    public Date() {

    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date(int month, int day, int year) {
        if (!isValid(month, day, year)) throw new IllegalArgumentException("Invalid date");
        this.month = month;
        this.day   = day;
        this.year  = year;
    }

    public static Date parsePatientsLikeMeDate (String date) {
        Date d = new Date();

        if(!isPatientsLikeMeDateValid(date)) {
            d.setPatientLikeMeDateValid(false);
            return d;
        } else {
            String[] fields = date.split(" ");
            if(fields.length == 2) {
                d.setDay(15);  //set to the middle of month
                d.setMonth(MONTHS.indexOf(fields[0]));
                d.setYear(Integer.parseInt(fields[1]));
                d.setPatientLikeMeDateValid(true);
            } else if (fields.length == 1) {
                //set to the middle of year
                d.setDay(30);
                d.setMonth(6);

                d.setYear(Integer.parseInt(fields[0]));
                d.setPatientLikeMeDateValid(true);
            } else {
                d.setPatientLikeMeDateValid(false);
            }
            return d;
        }
    }

    private static boolean isPatientsLikeMeDateValid (String date) {
        if(date.equals("?"))
            return false;
        else if(date.equals(""))
            return false;
        else if(date.equals("Diagnosed"))
            return false;
        else if(date.equals("Undiagnosed"))
            return false;

        return true;
    }

    // is the given date valid?
    private static boolean isValid(int m, int d, int y) {
        if (m < 1 || m > 12)      return false;
        if (d < 1 || d > DAYS[m]) return false;
        if (m == 2 && d == 29 && !isLeapYear(y)) return false;
        return true;
    }

    /**
     * Is year y a leap year?
     * @return true if y is a leap year; false otherwise
     */
    private static boolean isLeapYear(int y) {
        if (y % 400 == 0) return true;
        if (y % 100 == 0) return false;
        return y % 4 == 0;
    }

    public int compareTo(Date that) {
        if (this.year  < that.year)  return -1;
        if (this.year  > that.year)  return +1;
        if (this.month < that.month) return -1;
        if (this.month > that.month) return +1;
        if (this.day   < that.day)   return -1;
        if (this.day   > that.day)   return +1;
        return 0;
    }
}
