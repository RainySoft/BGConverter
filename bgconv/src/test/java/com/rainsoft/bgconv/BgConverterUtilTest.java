/**
 * 
 */
package com.rainsoft.bgconv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

import org.junit.Test;

/**
 * @author Lance
 *
 */
public class BgConverterUtilTest {

	/**
	 * Test method for {@link com.rainsoft.bgconv.BgConverterUtil#getMapping4Big5(byte[])}.
	 * F9F6 A86C
	 **/
	@Test
	public void testGetMapping() {
		byte bytes[]= new byte[2]; 
		int val1 = 0xF9;
		int val2 = 0xF6; 	
		byte a = (byte)val1;
		byte b = (byte)val2;		
		System.out.println("xxx" + (int) a + ":" + (int) b);
		bytes[0] = a;  
		bytes[1] = b;	
		String s =BgConverterUtil.getMapping4Big5(bytes);
		System.out.println(s);
		assertNotNull(s);
		int v1= 0xA8; 
		int v2= 0x6C; 
		byte c = (byte)v1; 
		byte d = (byte)v2;
		byte bytes2[]= new byte[2];
		bytes2[0] = c; 
		bytes2[1] = d; 
		HashMap<ByteBuffer,byte[]> map = new HashMap<>();
		map.put(ByteBuffer.wrap(bytes), bytes2);
		
		String s2 = BgConverterUtil.getMapping4Gbk(bytes2);
		System.out.println(s2);
		byte bytes3[]= new byte[2]; 		
		int va1 = 0xF9;
		int va2 = 0xF6; 		
		byte va = (byte)val1;
		byte vb = (byte)val2;
		bytes3[0] = va;  
		bytes3[1] = vb;
		byte[] o = map.get(ByteBuffer.wrap(bytes3));
		assertEquals(o, bytes2);
	}

//	@Test
	public void testGetBytesFromString() {
//		String s = "A3BF A841"; 
		String s = "F07C F1F9";
		BgConverterUtil util = new BgConverterUtil();
		System.out.println(util.getMappingFromString(s));
	}
	
//	@Test
	public void testReadDefinitionFile() {
		BgConverterUtil util = new BgConverterUtil();
		String s = util.readDefinitionFile();
		System.out.println(s);
	}
//		
	@Test
	public void testDirectChange() throws UnsupportedEncodingException {
		InputStream in = ClassLoader.getSystemResourceAsStream("big5test");
		Scanner scanner = new Scanner(new InputStreamReader(in,"MS950"));		
		while (scanner.hasNext()) {
			try {
				String s = scanner.nextLine();
				System.out.println(s);
				String source = new String(s.getBytes());
				System.out.println("source:" + source);
				String result = BgConverterUtil.b2gRawString(source);
				System.out.println("result:" + result);
			} catch (UnsupportedEncodingException e) {
				fail(e.getMessage()); 
			}
		}
		scanner.close();
	}
}
