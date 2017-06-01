/**
 * Dictionary and convert table from BGConv 1.0.35 
 */
package com.rainsoft.bgconv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lance
 *
 */
@SuppressWarnings("resource")
public class BgConverterUtil {
	private final static Logger logger = LoggerFactory.getLogger(BgConverterUtil.class);
	
	private final static HashMap<ByteBuffer, byte[]> bgMap = new HashMap<>();
	private final static HashMap<ByteBuffer, byte[]> gbMap = new HashMap<>();
	
	static {
		InputStream inbg = ClassLoader.getSystemResourceAsStream("bg2gb.map");
		Scanner scannerbg = new Scanner(new InputStreamReader(inbg));
		logger.trace("Reader mapping table");
		while (scannerbg.hasNext()) {
			String s = scannerbg.nextLine();
			String[] innerCodes = s.split(" ");
			byte[] bg5key = getByteFromInnercode(innerCodes[0]);
			byte[] gbkey = getByteFromInnercode(innerCodes[1]);
			logger.trace("big5:" + (int)bg5key[0] + ":" + (int)bg5key[1] + " -- > " + (int)gbkey[0] + ":" + (int)gbkey[1]);
			bgMap.put(ByteBuffer.wrap(bg5key), gbkey);			
		}
		scannerbg.close();		
		InputStream ingb = ClassLoader.getSystemResourceAsStream("gb2bg.map");
		Scanner scannergb = new Scanner(new InputStreamReader(ingb));
		while (scannergb.hasNext()) {
			String s = scannergb.nextLine();
			String[] innerCodes = s.split(" ");
			byte[] gbkey = getByteFromInnercode(innerCodes[0]);
			byte[] big5key = getByteFromInnercode(innerCodes[1]);
			logger.trace("gbk:" + + (int)gbkey[0] + ":" + (int)gbkey[1] + " -- > " + (int)big5key[0] + ":" + (int)big5key[1]);
			gbMap.put(ByteBuffer.wrap(gbkey), big5key);			
		}
		scannerbg.close();		
	}

	public static String b2gRawString(String source) throws UnsupportedEncodingException {
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
			
			logger.trace("src byte:" + c1.intValue() + ":" + c2.intValue());
			
			byte[] dest = bgMap.get(ByteBuffer.wrap(new byte[] {src[start],src[start+1]}));
			if (dest !=null) {
				Byte d1 = dest[0];
				Byte d2 = dest[1];
				logger.trace("dest byte:" + d1.intValue() + ":" + d2.intValue());
				sb.append(new String(dest,"GBK"));
			} else {
				logger.error("No gbk mapping innercode : " +  getCharacter4Big5(new byte[] {c1,c2}));
				sb.append(getCharacter4Big5(new byte[] {c1,c2}));
			}
			
			start++; 
			start++; 
		}
		return sb.toString(); 
	}
	
	public static String g2bRawString(String source) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(); 
		byte[] src = source.getBytes("GBK");
		for (int start=0; start <src.length; ) {
			if (src[start] >0 ) {				
				char c = (char) src[start];
				sb.append(c);
				start++;
				continue; 
			}
			Byte c1 = src[start];
			Byte c2 = src[start+1];
			
			logger.trace("src byte:" + c1.intValue() + ":" + c2.intValue());
			
			byte[] dest = gbMap.get(ByteBuffer.wrap(new byte[] {src[start],src[start+1]}));
			if (dest !=null) {
				Byte d1 = dest[0];
				Byte d2 = dest[1];
				logger.trace("dest byte:" + d1.intValue() + ":" + d2.intValue());
				sb.append(new String(dest,"MS950"));
			} else {
				logger.error("No big5 mapping innercode : " +  getCharacter4Gbk(new byte[] {c1,c2}));
				sb.append(getCharacter4Gbk(new byte[] {c1,c2}));
			}
			start++;start++; 
		}
		return sb.toString(); 
	}	
	
	public String readDefinitionFile() {
		InputStream in = ClassLoader.getSystemResourceAsStream("bg2gb.map");
		Scanner scanner = new Scanner(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		
		while (scanner.hasNext()) {
			String s = scanner.nextLine();
			if (getMapCharacterFromInnerCode(s) == "") {
				continue;
			}
			sb.append(getMapCharacterFromInnerCode(s) + System.lineSeparator());
		}
		scanner.close();
		return sb.toString();
	}

	public static String getCharacter4Big5(byte bytes[]) {
		String s = "";
		try {
			s = new String(bytes, "MS950");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * give
	 * @param bytes
	 * @return
	 */
	public static String getCharacter4Gbk(byte bytes[]) {
		String s = "";
		try {
			s = new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Get byte array from inner code definition
	 * @param str - GBK "C3B4", Big5 "A45C"
	 * @return
	 */
	public static byte[] getByteFromInnercode(String str) {
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
	
	
	/**
	 * Read from definition innercode and translate to chinese character
	 * innercode finding : http://bianma.supfree.net/chaye.asp?id=4E48
	 * @param str - "MS950 GBK" for ex: BBD5 B8F3
	 * @return
	 */
	public static String getMapCharacterFromInnerCode(String str) {
		String[] innerCodes = str.split(" ");
		if (innerCodes[1].equalsIgnoreCase("2020")) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(innerCodes[0] + ":");
		sb.append(getCharacter4Big5(getByteFromInnercode(innerCodes[0])));
		sb.append(":");
		sb.append(innerCodes[1] + ":");
		sb.append(getCharacter4Gbk(getByteFromInnercode(innerCodes[1])));
		return sb.toString();
	}


}
