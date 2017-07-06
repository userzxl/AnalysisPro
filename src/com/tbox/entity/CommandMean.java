package com.tbox.entity;

public class CommandMean {
	public static class finalValue{
		public static final byte VEHICLE_LOGIN= 0x01;//车辆登入
		public static final byte EVENT_REPORT= (byte) 0xD2;//事件数据上报
		public static final byte EVENT_ISSUED= (byte) 0xE1;//事件数据下发
		public static final byte BUS_DATA_REPORT= (byte) 0xD0;//总线数据上报
		public static final byte VEHICLE_LOGOUT= (byte) 0x04;//车辆登出
		public static final byte SAFE_INFO_VALIDATE= (byte) 0x007;//安全信息认证上报事件ID
		public static final byte VEHICLE_ALARM= (byte) 0x002;//车辆通用报警事件ID
		public static final byte HEART_BEAT= (byte) 0x07;//心跳
		/*public static final byte SECURITY_VERIFICATION= 0x02;
		public static final byte REAL_TIME_INFORMATION= 0x03;
		public static final byte ALART_EVENT= 0x04;*/
	}
}
