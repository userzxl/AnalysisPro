/**
 * 
 */
package com.tbox.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.tbox.service.Functions;

/**
 * @author ZhaoXiaolong
 * 2017-7-6下午3:16:53
 */
public class SqlFunctions {
	public Functions functions ;
	/**
	 * 验证sim 0:没有sim;1:有sim,离线;2:有sim，在线;
	 */
	public int validateSim(String simCode) {
		DBConn dbConn = new DBConn(
				"select * from bms_terminal a where a.sim ='" + simCode + "'");
		ResultSet result = null;
		int flag = 0;
		try {
			result = dbConn.pst.executeQuery();
			if (result.next()) {
				if (result.getString("offline") == "1") {
					flag = 2;
					System.out.println("有该sim但是在线");
				} else {
					flag = 1;
					System.out.println("有该sim并且离线");
				}

			} else {
				flag = 0;
				System.out.println("没有该sim,sim:" + simCode);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		return flag;
	}
	/**
	 * 获取会话密钥
	 * @param simCode
	 * @return
	 */
	public String getSessionKey(String simCode) {
		DBConn dbConn = new DBConn(
				"select * from bms_terminal a where a.sim ='" + simCode + "'");
		ResultSet result = null;
		String sessionKey = "";
		try {
			result = dbConn.pst.executeQuery();
			if (result.next()) {
				sessionKey = result.getString("session_key");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		return sessionKey;
	}
	
	/**
	 * 获取根密钥
	 * @param simCode
	 * @return
	 */
	public String getRootKey(String simCode) {
		DBConn dbConn = new DBConn(
				"select * from bms_terminal a where a.sim ='" + simCode + "'");
		ResultSet result = null;
		String rootKey = "";
		try {
			result = dbConn.pst.executeQuery();
			if (result.next()) {
				rootKey = result.getString("root_key");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		return rootKey;
	}
	
	/**
	 * 修改终端会话密钥;
	 */
	public int updateSessionKey(String hex,String simCode) {

		DBConn dbConn = new DBConn(
				"update bms_terminal a set a.session_key = '"+hex+"' where a.sim ='" + simCode + "'");
		int result = 0;
		try {
			result = dbConn.pst.executeUpdate();
			if (result == 1) {
				System.out.println("修改成功");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		return result;
	}
	/**
	 * 获取报警设置
	 */
	public String[] getAlarmSet(String simCode) {

		DBConn dbConn = new DBConn(
				"select * from bms_terminal a where a.sim ='" + simCode + "'");
		ResultSet result = null;
		String[] alarmSet = new String[3];
		alarmSet[0] = "0";
		try {
			result = dbConn.pst.executeQuery();
			if (result.next()) {
				if(result.getString("cfd").equals("1")){
					alarmSet[0] = "1";
					alarmSet[1] = result.getString("grab_rate");
					alarmSet[2] = (Integer.parseInt(result.getString("ku_temperature"))+55)*10+"";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		if(alarmSet[0] == "1"){
			DBConn dbConn1 = new DBConn(
					"update bms_terminal a set a.cfd = '0'");
			
			try {
				dbConn1.pst.executeUpdate();
				System.out.println("自定义参数设置发送");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				dbConn1.close();
				System.out.println("关闭数据库连接");
			}
		}
		return alarmSet;
	}
	/**
	 * 修改实时监控信息;添加监控记录，添加报警记录
	 * double humi = 0 ;湿度
		double temp = 0 ;温度
		double SOC = 0 ;SOC
		double SOH = 0 ; SOH
		int vibrateA = 0 ;震动报警
		int smokeA = 0 ;烟雾报警
		int tempA = 0 ;温度报警
	 */
	public void updateBmsMonitor(String simCode,double humi,double temp,double SOC,double SOH,int shockA,int smokeA) {
		DBConn dbConn = new DBConn(
				"select a.* ,b.ku_temperature from bms_monitor a LEFT JOIN bms_terminal b on b.code = a. battery_code where b.sim  ='" 
				+ simCode + "' and b.del_flag = 0)");
		ResultSet result = null;
        String highTemp = "";
        String lowTemp = "";       
        int tempA = 0;
        String terminalCode = "";
        Date date = new Date();       
		Timestamp timeStamep = new Timestamp(date.getTime());
		try {
			result = dbConn.pst.executeQuery();
			if (result.next()) {
				terminalCode = result.getString("battery_code");
				highTemp = result.getString("high");
				lowTemp = result.getString("low");
				if (highTemp!=""&&highTemp!=null) {
					if(Double.parseDouble(highTemp)<temp){
						highTemp = temp+"";
					}
				}
				if(lowTemp!=""&&lowTemp!=null){
					if(Double.parseDouble(lowTemp)>temp){
						lowTemp = temp+"";
					}
				}
				if(temp>Double.parseDouble(result.getString("ku_temperature"))){
					tempA = 1;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		
		//修改实时监控信息
		updateRealTimeBmsMonitor(terminalCode, highTemp, lowTemp, temp, SOC, SOH, humi, shockA, smokeA, tempA, timeStamep);

		
	}

	/**
	 * 修改实时监控信息;
	 * double humi = 0 ;湿度
		double temp = 0 ;温度
		double SOC = 0 ;SOC
		double SOH = 0 ; SOH
		int vibrateA = 0 ;震动报警
		int smokeA = 0 ;烟雾报警
		int tempA = 0 ;温度报警
	 */
	public void updateRealTimeBmsMonitor(String terminalCode,String highTemp, String lowTemp,double temp,
										double SOC,double SOH,double humi,int shockA,int smokeA,int tempA,Timestamp timeStamep) {
		String police_type = "0"; //是否报警 0 未报警;1 已报警
		
		if(shockA == 1 ||smokeA == 1||tempA == 1){
			police_type = "1";
		}
		DBConn dbConn = new DBConn(
				"update bms_monitor a set a.soc = '"+(SOC+"")+"',a.soh = '"+(SOH+"")+"',a.low = '"+lowTemp+"',a.high = '"+highTemp+
				"',a.temperature = '"+(temp+"")+"',a.humidity = '"+(humi+"")+"',a.shock = '"+(shockA+"")+"',a.smoke = '"+(smokeA+"")+
				"',a.update_date = '"+timeStamep+"',a.police_type = '"+police_type+"', where a.battery_code ='" + terminalCode + "' and a.del_flag = '0'");
		int result1 = 0;
		try {
			result1 = dbConn.pst.executeUpdate();
			if (result1 == 1) {
				System.out.println("修改成功实时监控信息成功");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		//添加监控记录
		insertMonitorHistory(terminalCode, temp, SOC, SOH, humi, shockA, smokeA, tempA, timeStamep);
		
	}
	/**
	 * 添加监控记录
	 * @param terminal_code
	 * @param temp
	 * @param SOC
	 * @param SOH
	 * @param humi
	 * @param shockA
	 * @param smokeA
	 * @param tempA
	 * @param timeStamep
	 * @return
	 */
	public void insertMonitorHistory(String terminalCode,double temp,
			double SOC,double SOH,double humi,int shockA,int smokeA,int tempA,Timestamp timeStamep) {
		String police_type = "0"; //是否报警 0 未报警;1 已报警
		String uuid = functions.getUUID();//获取UUID
		if(shockA == 1 ||smokeA == 1||tempA == 1){
			police_type = "1";
		}
		DBConn dbConn = new DBConn(
		"INSERT INTO bms_monitor_history (Id,battery_code,soc,soh,temperature,shock,humidity,smoke,create_date,del_flag) VALUES('"+uuid+"','"+terminalCode+"','"+(SOC+"")+
		"','"+(SOH+"")+"','"+(temp+"")+"','"+(shockA+"")+"','"+(humi+"")+"','"+(smokeA+"")+"','"+(smokeA+"")+"','"+(timeStamep+"")+"','0')");
		int result1 = 0;
		try {
			result1 = dbConn.pst.executeUpdate();
		if (result1 == 1) {
			System.out.println("添加信息成功");
		}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		//添加报警记录
		if(shockA == 1){
			insertBmsWarn(uuid, terminalCode, "3", "震动报警", timeStamep);
		}
		if(smokeA == 1){
			insertBmsWarn(uuid, terminalCode, "3", "烟雾报警", timeStamep);
		}
		if(tempA == 1){
			insertBmsWarn(uuid, terminalCode, "3", "温度报警:"+temp, timeStamep);
		}

		
	}
	/**
	 * 添加报警记录
	 * @param terminal_code
	 * @param timeStamep
	 * 报警类型：1温度 2湿度 3震动 4烟雾
	 * @return
	 */
	public void insertBmsWarn(String monitorId,String terminalCode,String warnType,String warnInfo,
			Timestamp timeStamep) {
		String uuid = functions.getUUID();//获取UUID
		DBConn dbConn = new DBConn(
		"INSERT INTO bms_warn (Id,monitor_id,terminal_code,warn_type,warn_info,create_date,del_flag) VALUES('"+uuid+"','"+monitorId+"','"+terminalCode+"','"
		+warnType+"','"+warnInfo+"','"+(timeStamep+"")+"','0')");
		int result1 = 0;
		try {
			result1 = dbConn.pst.executeUpdate();
		if (result1 == 1) {
			System.out.println("添加信息成功");
		}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		
	}
	/**
	 * 修改终端离线状态;bms_terminal offline 0 是;1 否
	 * 
	 */
	public void updateOffline(String simCode,String offline) {
		DBConn dbConn = new DBConn(
				"update bms_terminal a set a.offline = '"+(offline+"")+"' where a.sim ='" + simCode + "' and a.del_flag = '0'");
		int result1 = 0;
		try {
			result1 = dbConn.pst.executeUpdate();
			if (result1 == 1) {
				System.out.println("修改终端状态成功");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		
	}
	
}
