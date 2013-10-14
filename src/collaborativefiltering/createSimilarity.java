package collaborativefiltering;

import java.io.*;
import java.sql.*;
import java.util.BitSet;
import java.util.HashMap;

public class createSimilarity {
    //private static BitSet[] bs = new BitSet[17420];
    static int user_condition[][] = new int[17420][1228];
    //private static HashMap<Integer, Integer>[] hm;

    public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException{
        createDS cds = new createDS();
        //bs = cds.createBitSet();
        user_condition = cds.createBitSetWithoutMS();
        //hm = cds.createStarHashMap();
        double min = 10000;
        double max = 0;
        double avg = 0;
        double total_one_user= 0;

        for(int i=1; i < user_condition.length; i++) {
                for(int j=0; j<user_condition[0].length;j++){
                        total_one_user += user_condition[i][j];
                }
                if(total_one_user > max)
                        max = total_one_user;
                if(total_one_user < min)
                        min = total_one_user;
                avg += total_one_user;
                total_one_user = 0;
        }
        avg = avg/17420;
        System.out.println(max);
        System.out.println(min);
        System.out.println(avg);

        return;
    }

    /*public static double computeSimilarity(int user1, int user2) {
            ArrayList<Integer> al = new ArrayList<Integer>();
            double user1_avg_rating = 0;
            double user2_avg_rating = 0;
            double numerator = 0;
            double denominator = 0;
            double de_part1 = 0;
            double de_part2 = 0;

            BitSet result = (BitSet)bs[user1].clone();
            result.and(bs[user2]);
            int index = 0;
            while(result.nextSetBit(index) != -1){
                    index = result.nextSetBit(index);
                    al.add(index);
                    //System.out.println(index);
                    index++;
            }

            for(int i=0; i<al.size(); i++){
                    user1_avg_rating += hm[user1].get(al.get(i));
                    user2_avg_rating += hm[user2].get(al.get(i));
            }
            user1_avg_rating /= al.size();
            user2_avg_rating /= al.size();

            for(int i=0; i<al.size(); i++){
                    numerator += (hm[user1].get(al.get(i)) - user1_avg_rating)*(hm[user2].get(al.get(i)) - user2_avg_rating);
                    de_part1 += (hm[user1].get(al.get(i)) - user1_avg_rating)*(hm[user1].get(al.get(i)) - user1_avg_rating);
                    de_part2 += (hm[user2].get(al.get(i)) - user2_avg_rating)*(hm[user2].get(al.get(i)) - user2_avg_rating);
            }
            denominator = Math.sqrt(de_part1*de_part2) + 0.00001;

            return (double)Math.round(numerator/denominator*10000)/10000;
    }*/
}

class createDS {
	public int[][] createBitSetWithoutMS() {
		//ArrayList<Integer>[] bs = new ArrayList[17420];
		int user_condition[][] = new int[17420][1230];
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/webscraping?user=root");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT ID, CONDITION_ID FROM user_id_condition_id ORDER BY ID");
			while(rs.next()){
				int userId = rs.getInt("ID");
				int conditionId = rs.getInt("CONDITION_ID");
				if(conditionId == 5) {
					user_condition[userId][0]=1;
					continue;
				}
				user_condition[userId][conditionId]=1;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return user_condition;
	}
	
	public BitSet[] createBitSet() {
		BitSet[] bs = new BitSet[17420];
		for(int i=0; i<bs.length;i++) {
			bs[i] = new BitSet();
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/webscraping?user=root");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT ID, CONDITION_ID FROM user_id_condition_id ORDER BY ID");
			while(rs.next()){
				int userId = rs.getInt("ID");
				int conditionId = rs.getInt("CONDITION_ID");
				if(conditionId == 5) {
					bs[userId].set(0);
					continue;
				}
				bs[userId].set(conditionId);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return bs;
	}
	
	public HashMap[] createStarHashMap(){
		HashMap<Integer, Integer>[] hm = new HashMap[80000];
		for(int i=0; i<hm.length;i++) {
			hm[i] = new HashMap<Integer, Integer>();
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/webscraping?user=root");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT ID, CONDITION_ID FROM user_id_condition_id ORDER BY ID");
			while(rs.next()){
				int userId = rs.getInt("ID");
				int conditionId = rs.getInt("CONDITION_ID");
				int star = 1;
				//System.out.println(userId + " " + restaurantId);
				hm[userId].put(conditionId, star);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hm;
	}
	
	public HashMap[] createDateHashMap(){
		HashMap<Integer, String>[] dhm = new HashMap[80000];
		for(int i=0; i<dhm.length;i++) {
			dhm[i] = new HashMap<Integer, String>();
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/recommender?user=root");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT USERID, RESTAURANT_ID, DATE FROM user_restaurant_date ORDER BY USERID");
			while(rs.next()){
				int userId = rs.getInt("USERID");
				int restaurantId = rs.getInt("RESTAURANT_ID");
				String date = rs.getString("DATE");
				//System.out.println(userId + " " + restaurantId);
				dhm[userId].put(restaurantId, date);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dhm;
	}
}