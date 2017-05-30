/**
 * Dictionary and convert table from BGConv 1.0.35 
 */
package com.rainsoft.bgconv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Lance
 *
 */
public class BgConverterUtil {
	private final static HashMap<ByteBuffer, byte[]> bgMap = new HashMap<>();

	static {
		InputStream in = ClassLoader.getSystemResourceAsStream("bg2gb.map");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Scanner scanner = new Scanner(new InputStreamReader(in));
		while (scanner.hasNext()) {
			String s = scanner.nextLine();
			String[] innerCodes = s.split(" ");
			byte[] bg5key = getBytesFromString(innerCodes[0]);
//			System.out.println("bg5:" + (int)bg5key[0] + ":" + (int)bg5key[1]) ;
			byte[] gbkey = getBytesFromString(innerCodes[1]);
//			System.out.println("gbk:" + (int)gbkey[0] + ":" + (int)gbkey[1]);
			bgMap.put(ByteBuffer.wrap(bg5key), gbkey);			
		}
		scanner.close();
	}

	public static String b2gRawString(String source) throws UnsupportedEncodingException {
		System.out.println(bgMap.size());
		StringBuilder sb = new StringBuilder(); 
		byte[] src = source.getBytes("MS950");
		for (int start=0; start <src.length; ) {
			if (src[start] >0 ) {				
				char c = (char) src[start];
				sb.append(c);
				start++;
				continue; 
			}
			Byte c1 = src[start];
			Byte c2 = src[start+1];
			
			System.out.println("src byte:" + c1.intValue() + ":" + c2.intValue());
			
			byte[] dest = bgMap.get(ByteBuffer.wrap(new byte[] {src[start],src[start+1]}));
			if (dest !=null) {
				Byte d1 = dest[0];
				Byte d2 = dest[1];
				System.out.println("dest byte:" + d1.intValue() + ":" + d2.intValue());
				sb.append(new String(dest,"GBK"));
			} 
			start++; 
			start++; 
		}
		return sb.toString(); 
	}
	
	public String readDefinitionFile() {
		InputStream in = ClassLoader.getSystemResourceAsStream("bg2gb.map");
		Scanner scanner = new Scanner(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		
		while (scanner.hasNext()) {
			String s = scanner.nextLine();
			if (getMappingFromString(s) == "") {
				continue;
			}
			sb.append(getMappingFromString(s) + System.lineSeparator());
		}
		scanner.close();
		return sb.toString();
	}

	public static String getMapping4Big5(byte bytes[]) {
		String s = "";
		try {
			s = new String(bytes, "MS950");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getMapping4Gbk(byte bytes[]) {
		String s = "";
		try {
			s = new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static byte[] getBytesFromString(String str) {
		if (str.length() != 4)
			throw new RuntimeException("String format error [" + str + "] Length should be 4.");
		byte bytes[] = new byte[2];
		int val = Integer.parseInt(str, 16);
		int v1 = val / 256;
		int v2 = val % 256;
		bytes[0] = (byte) v1;
		bytes[1] = (byte) v2;
		return bytes;
	}

	public String getMappingFromString(String str) {
		String[] innerCodes = str.split(" ");
		if (innerCodes[1].equalsIgnoreCase("2020")) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(innerCodes[0] + ":");
		sb.append(getMapping4Big5(getBytesFromString(innerCodes[0])));
		sb.append(":");
		sb.append(innerCodes[1] + ":");
		sb.append(getMapping4Gbk(getBytesFromString(innerCodes[1])));
		return sb.toString();
	}

	private void createByteMapping(String str) {
		String[] innerCodes = str.split(" ");
		byte[] bg5key = getBytesFromString(innerCodes[0]);
		byte[] gbkey = getBytesFromString(innerCodes[1]);
		bgMap.put(ByteBuffer.wrap(bg5key), gbkey);
	}

}
