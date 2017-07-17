/**
 * 
 */
package com.tbox.discard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author ZhaoXiaolong
 * 2017-7-7下午3:11:42
 */
public class Test3 {
	public static void outputExcelData() throws IOException {  
        /**给List存值*/  
        List result = new ArrayList();  
        User user = new User();  
        user.setId("1");  
        user.setName("yfli");  
        result.add(user);  
        User user2 = new User();  
        user2.setId("1");  
        user2.setName("zhangjie");  
        result.add(user2);  
        User user3 = new User();  
        user3.setId("1");  
        user3.setName("lzhang");  
        User user4 = new User();  
        user4.setId("1");  
        user4.setName("lzhang");  
        result.add(user4);   
        User user5 = new User();  
        user5.setId("1");  
        user5.setName("lzhang");  
        result.add(user5);   
        String fileName = "F:\\sfData.xls";  
        //首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象        
        WritableWorkbook wwb = Workbook.createWorkbook(new File(fileName));  
        File dbfFile = new File(fileName);  
        if (!dbfFile.exists() || dbfFile.isDirectory()) {  
            dbfFile.createNewFile();  
        }  
        int totle = result.size();//获取List集合的size  
        int mus = 2;//每个工作表格最多存储2条数据（注：excel表格一个工作表可以存储65536条）  
        int avg = totle / mus;  
        for (int i = 0; i < avg + 1; i++) {  
            WritableSheet ws = wwb.createSheet("列表" + (i + 1), i);  //创建一个可写入的工作表    
            //添加表头  
            try {
				ws.addCell(new Label(0, 0, "序号"));   
				ws.addCell(new Label(1, 0, "姓名"));
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            int num = i * mus;   
            int index = 0;   
            for (int m = num; m < result.size(); m++) {  
                if (index == mus) {//判断index == mus的时候跳出当前for循环  
                    break;  
                }  
                User use = (User) result.get(m);  
//将生成的单元格添加到工作表中   
//（这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行）       
                try {
					ws.addCell(new Label(0, index + 1, use.getId()));  
					ws.addCell(new Label(1, index + 1, use.getName()));
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
                index++;  
            }  
        }  
        wwb.write();//从内存中写入文件中     
        try {
			wwb.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//关闭资源，释放内存        
    }
}
