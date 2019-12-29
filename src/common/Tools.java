package common;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import common.entity.StageName;

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
	/**
	 * 
	 * @author Ira Goor 
	 * method purpose:convert String from DB to Enum StageName
	 *
	 * @param str
	 * @return
	 */
	public static StageName convertStringToStageName(String str)
	{
		String name=str.toUpperCase();
		if(name.equals("EVALUATION"))
			return StageName.EVALUATION;
		else if(name.equals("DECISION"))
			return StageName.DECISION;
		else if(name.equals("EXECUTION"))
			return StageName.EXECUTION;
		else if(name.equals("VALIDATION"))
			return StageName.VALIDATION;
		else if(name.equals("CLOUSRE"))
			return StageName.CLOUSRE;
		else
			return StageName.INIT;
		
	}
	public static ZonedDateTime convertDateSQLToZoned(Date sqlDate)
	{
		ZonedDateTime t=ZonedDateTime.of(sqlDate.toLocalDate().atStartOfDay(),  ZoneId.systemDefault());
		return t;
	}
	public static int convertStageNameToInt(StageName name)
	{
		switch(name)
		{
		case EVALUATION:
			return 0;
		case DECISION:
			return 1;
		case EXECUTION:
			return 2;
		case VALIDATION:
			return 3;
		case CLOUSRE:
			return 4;
			default:
				return -1;
		
		}
	}
}
