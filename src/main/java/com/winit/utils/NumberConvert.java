package com.winit.utils;

/**
 * 数字转换工具类 Created by liyou 2017-07-30 16:31
 */
public class NumberConvert {

	/**
	 * int转成byte
	 * 
	 * @param x
	 * @return
	 */
	public static byte intToByte(int x) {
		return (byte) x;
	}

	/**
	 * byte转成int
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToInt(byte b) {
		// Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
		return b & 0xFF;
	}

	/**
	 * 字节数组转成int
	 * 
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
	}

	/**
	 * int转换成字节数组
	 * 
	 * @param a
	 * @return
	 */
	public static byte[] intToByteArray(int a) {
		return new byte[] { (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
				(byte) (a & 0xFF) };
	}
}
