/**
 * 
 */
package com.tbox.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ZhaoXiaolong
 * 2017-7-6下午3:16:53
 */
public class SqlFunctions {
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
}
