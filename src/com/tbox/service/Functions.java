package com.tbox.service;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.tbox.entity.CommandMean;
import com.tbox.sql.DBConn;
import com.tbox.sql.SqlFunctions;

public class Functions {
	public SqlFunctions sqlFunctions = new SqlFunctions();
	public byte[] rootKey = {0x19,(byte) 0xC1,0x4D,(byte) 0xAA,(byte) 0xB0,0x3C,0x77,0x43,0x68,0x5E,(byte) 0x8B,(byte) 0xF3,0x49,(byte) 0xE4,(byte) 0x9D,0x2A};
	byte[] sessionKey = {0x19,(byte) 0xC1,0x4D,(byte) 0xAA,(byte) 0xB0,0x3C,0x77,0x43,0x68,0x5E,(byte) 0x8B,(byte) 0xF3,0x49,(byte) 0xE4,(byte) 0x9D,0x2A};
	// 命令标识解析
	public byte hexAnalysis(int i) {		
		return  (byte) (i & 0xFF);
	}

	// 解析sim卡号
	public void simAnalysis(int i) {

	}

	// 截取字符串
	/*
	 * public byte[] interceptByte(int sIndex,int length,byte[] bytes){ 
	 * 		byte[] returnByte = new byte[length]; 
	 * 			for (int i = 0; i < length; i++) {
	 * 				returnByte[i] = bytes[sIndex+1]; 
	 *			} 
	 *	return returnByte; 
	 *}
	 */
	// ascii字节数组转字符转
	/*
	 * public String interceptByte(byte[] bytes){ String returnByte = new
	 * byte[length]; for (int i = 0; i < length; i++) { returnByte[i] =
	 * bytes[sIndex+1]; } return returnByte; }
	 */
	/**
	 * 验证sim 0:没有sim;1:有sim,离线;2:有sim，在线;
	 */
	public int validateSim(String simCode) {
		int flag = sqlFunctions.validateSim(simCode);
		return flag;
	}
	/**
	 * BCC 校验
	 */
	public boolean booleanBCC(byte[] result1,String BCCCode) {	
		byte[] dataL = new byte[2];
		System.arraycopy(result1, 22, dataL, 0, 2);
		         
		// 转换16位
		int dataLToInt = toSixteenBit(dataL);
		int length = 22 + dataLToInt;
		String reportBCC =Integer.toHexString(result1[length+2]& 0xFF).toUpperCase();
		System.out.println(reportBCC+"report");
		if(reportBCC.equals(BCCCode)){
			return true;
		}
		return false;
	}
	/**
	 * 获取BCC验证码
	 */
	public String getBCC(byte[] result1) {
		
		byte[] dataL = new byte[2];
		System.arraycopy(result1, 22, dataL, 0, 2);
		         
		// 转换16位
		int dataLToInt = toSixteenBit(dataL);
		int length = 22 + dataLToInt;
		System.out.println("数据体长度："+dataLToInt+"数据包长度"+length);
		
		byte[] BCCByte = new byte[length];
		System.arraycopy(result1, 2, BCCByte, 0, length);
		
		String ret = "";
		byte BCC[] = new byte[1];
		for (int i = 0; i < BCCByte.length; i++) {
			BCC[0] = (byte) (BCC[0] ^ BCCByte[i]);
		}
		String hex = Integer.toHexString(BCC[0] & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}	
		ret += hex.toUpperCase();
		return ret;
	}
	/**
	 * AES 128位解密
	 * rootKey=19 C1 4D AA B0 3C 77 43 68 5E 8B F3 49 E4 9D 2A
	 */
	
	public byte[] getDecCode(byte[] enc,byte[] keyByte) {
		byte[] dec = null;
		Cipher c;
		byte[] rootKey = {0x19,(byte) 0xC1,0x4D,(byte) 0xAA,(byte) 0xB0,0x3C,0x77,0x43,0x68,0x5E,(byte) 0x8B,(byte) 0xF3,0x49,(byte) 0xE4,(byte) 0x9D,0x2A};
		SecretKeySpec skeySpec = new SecretKeySpec(rootKey, "AES");
		try {
			c = Cipher.getInstance("AES/ECB/NoPadding");
			try {
				c.init(Cipher.DECRYPT_MODE,skeySpec);
				try {
					// 解密,保存到dec
					System.out.println(enc.length+"密文长度");
					dec = c.doFinal(enc);
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c = null;
		}
		return dec;
	}
	/**
	 * AES密钥生成
	 */
	private SecretKey getRootKey(byte[] rootKey) {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		//这个是密匙生成器，多种算法通用的。
		SecretKeySpec skeySpec = new SecretKeySpec(rootKey, "AES");
		/*KeyGenerator keyGen = KeyGenerator.getInstance("AES");*/
		// 使用用户输入的key，按照长度128初始化密匙生成器
		/*keyGen.init(128, new SecureRandom(rootKey));
		SecretKey key = keyGen.generateKey();
		keyGen = null;*/
		return skeySpec;
/*		System.out.println("key为null");
		return null;*/

	}
	/**
	 * 转换16进制
	 */
	public StringBuffer getHex(byte[] para){
		StringBuffer hex = new StringBuffer();
	    for(int i = 0; i < para.length; i++){
		    hex.append(Integer.toHexString((int)para[i]&0xFF).toUpperCase());
		 }
	    return hex;
	}
	/**
	 * 转换16位
	 */
	public int toSixteenBit(byte[] para){
		byte high = (byte) (0x000000FF & para[0]);
		byte low = (byte) (0x000000FF & para[1]);
		int dataLToInt = (int)(((high & 0x000000FF) << 8) | (0x000000FF & low));
		return dataLToInt;
	}
	/**
	 * 16进制字符串转换10进制
	 */
	public int sixteenToTen(String s){
		return Integer.parseInt(s,16);
	}
	/**
	 * 16进制转换10进制
	 */
	public String  sixteenToString(byte[] data){
		String replyData = "";
		for (int i = 0; i < data.length; i++) {
			String a = Integer.toHexString((int)data[i]&0xFF).toUpperCase();
			a = sixteenToTen(a)+"";
			if(a.length()==1){
				a="0"+a;
			}
			replyData+=a;
			
		}
		return replyData;
	}
	/**
	 * 随机生成16字节会话密钥session_key
	 */
	public byte[] getSessionKey(){
		Random random = new Random();
		byte[] bytes = new byte[16];
		for (int i = 0; i < bytes.length; i++){
			bytes[i] = (byte)random.nextInt(256);
		}
		return bytes;
		
	}
	/**
	 * AES 128位加密
	 * rootKey=19 C1 4D AA B0 3C 77 43 68 5E 8B F3 49 E4 9D 2A
	 */
	
	public byte[] getEncCode(byte[] init,byte[] keyByte) {
		byte[] enc = null;
		Cipher c;
		byte[] rootKey = {0x19,(byte) 0xC1,0x4D,(byte) 0xAA,(byte) 0xB0,0x3C,0x77,0x43,0x68,0x5E,(byte) 0x8B,(byte) 0xF3,0x49,(byte) 0xE4,(byte) 0x9D,0x2A};
		SecretKeySpec skeySpec = new SecretKeySpec(keyByte, "AES");
		try {
			c = Cipher.getInstance("AES/ECB/NoPadding");
			try {
				c.init(Cipher.ENCRYPT_MODE,skeySpec);
				try {
					// 解密,保存到dec
					System.out.println(init.length+"密文长度");
					enc = c.doFinal(init);
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c = null;
		}
		return enc;
	}
	/**
	 * 获取当前时间
	 * @return eq:170705185511 yyMMddHHmmss
	 */
	public String getLocalDate(){
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		String time = format.format(date);
		return time;
	}
	/**
	 * string型转byte[]
	 */
	public byte[] getByteArray(String time,int byteL){
		int[] timeInt = new int[byteL];
		char [] stringArr = time.toCharArray();
		for (int i = 0; i < stringArr.length/2; i++) {
			timeInt[i] = Integer.parseInt(String.valueOf(stringArr[2*i])+String.valueOf(stringArr[2*i+1]));
		}
		byte[] timeByte = new byte[byteL];
		for (int i = 0; i < timeByte.length; i++) {
			timeByte[i] = (byte) timeInt[i];
		}
		System.out.println();
		return timeByte;		
	}
	/**
	 * 生成下发事件包
	 * @param result1 上报事件包
	 * @param simCode 
	 * @return byte[]
	 */
	
	public byte[] getIssuedData(byte[] result1, String simCode){
		byte[] init = getSessionKey();//生成会话密钥
		System.out.println("会话密钥是："+getHex(init));
		while(init==null){
			init = getSessionKey();
		}
		int flag = 0;
		while(flag==0){
			flag = updateSessionKey(simCode,init);
		}
		
		byte[] enc = getEncCode(init,rootKey);//获得用根密钥加密后的会话密钥
		byte[] issuedTime = getByteArray(getLocalDate(),6);//获得事件下发时间
		int dataCellL = 30;//数据单元长度
		int dataL = 24+ dataCellL+1;//下发数据长度
		byte[] issuedData = new byte[dataL];//创建下发数据
		System.arraycopy(result1, 0, issuedData, 0, 21);//拷贝上报事件的起始符 命令单元 唯一识别码
		issuedData[2] = (byte) 0xE1;//数据下发包命令标识位0xE1
		issuedData[21] = 0x01;//数据下发包 数据单元不加密 0x01
		String dataCellLStr = String.valueOf(dataCellL);//将int型数据单元长度转换成16进制String型
		int dataCellLStrL = dataCellLStr.length();//获取String型数据单元长度的length
		for (int i = 0; i < 4-dataCellLStrL; i++) {
			dataCellLStr = "0" +dataCellLStr;
		}
		System.out.println(dataCellLStr+"字符串形式的数据单元长度");
		byte[] dataCellLByte = getByteArray(dataCellLStr,2);
		System.arraycopy(dataCellLByte, 0, issuedData, 22, 2);//添加数据单元长度
		//添加流水号
		issuedData[24] = 0x00;
		issuedData[25] = 0x00;
		
		System.arraycopy(issuedTime, 0, issuedData, 26, 6);//添加数据下发时间
		//添加下发事件ID
		issuedData[32] = 0x00;
		issuedData[33] = 0x02;
		//添加下发数据体长度
		issuedData[34] = 0x00;
		issuedData[35] = 0x12;
		
		issuedData[36] = 0x01;//添加数据体命令字ID 1：临时会话密钥、2、根密钥。
		issuedData[37] = 0x01;//添加加密方式  0：不加密、1：128位AES加密。
		
		System.arraycopy(enc, 0, issuedData, 38, 16);//添加通过root-key加密后的session-key
		String BCCCodeStr = getBCC(issuedData);      //获得字符串型BCC-Code
		byte[] BCCCode =  hexStringToBytes(BCCCodeStr);//字符串型BCC-Code 转成byte[]
		System.out.println("BCCCode length "+BCCCode.length+";"+"BCCCode to hex:"+getHex(BCCCode));
		System.arraycopy(BCCCode, 0, issuedData, dataL-1, 1);//添加BCC验证码到下发数据包
		
		return issuedData;
		
	}
	/**
	 * char型转换byte
	 */
	public  byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    }  
	/**
	 * 16进制字符串转换byte[]
	 */
	public  byte[] hexStringToBytes(String hexString) {  
        hexString = hexString.toUpperCase().replace(" ", "");  
        int length = hexString.length() / 2;  
        char[] hexChars = hexString.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }
		return d;  
    }  
	/**
	 * 修改终端会话密钥;
	 */
	public int updateSessionKey(String simCode,byte[] init) {
		String hex = "";
	    for(int i = 0; i < init.length; i++){
	    	String a = Integer.toHexString(init[i] & 0xFF);
			if (a.length() == 1) {
				a = '0' + a;
			}	
			hex += a.toUpperCase();
		}
	    int result = sqlFunctions.updateSessionKey(hex, simCode);
		return result;
	}
}
