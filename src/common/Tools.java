package common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Tools {

//	public static List<String> convertMaptoStringsList(Map<String, List<Object>> map) {
//
//		   List<String>  listOfValue = new ArrayList<String>();
//		   for(Map m: map){ // loop through the maps
//		       listOfValue.addAll(map.values()); // append the values in listOfValue
//		   }
//	}


	public static ArrayList<String> disassemble(ResultSet rs, int NumOfCol) {
		ArrayList<String> returnString = new ArrayList<String>();
		try {
			while (rs.next()) {
				// Attention ! must run from 1 to NumOfCol (Not from zero)
				for (int i = 1; i <= NumOfCol; i++)
					returnString.add(rs.getString(i));
			}
			rs.close();
			return returnString;
		} catch (SQLException e) {
			System.out.println("Tools.disassemble() - int NumOfCol Error");
			e.printStackTrace();
		}
		return returnString;
	}

	// Idan : I changed it from Map<String , List<Object>>
	public static Map<Object, List<Object>> resultSetToMap(ResultSet rs) throws SQLException {
		ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
		int columns = md.getColumnCount();
		Map<Object, List<Object>> map = new HashMap<>(columns);
		for (int i = 1; i <= columns; ++i) {
			map.put(md.getColumnName(i), new ArrayList<>());
		}
		while (rs.next()) {
			for (int i = 1; i <= columns; ++i) {
				map.get(md.getColumnName(i)).add(rs.getObject(i));
			}
		}

		return map;
	}
	
}
