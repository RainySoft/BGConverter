/**
 * 
 */
package com.rainsoft.bgconv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * @author Lance
 *
 */
public class BgConverterUtilTest {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger("BgConverterUtilTest");
	static {
		Logger log = LogManager.getLogManager().getLogger("");
		for (Handler h : log.getHandlers()) {
		    h.setLevel(Level.FINEST);
		}			
	}
	/**
	 * Test method for
	 * {@link com.rainsoft.bgconv.BgConverterUtil#getCharacter4Big5(byte[])}. F9F6
	 * A86C
	 **/
	@Test
	public void testGetMapping() {
		byte bytes[] = new byte[2];
		int val1 = 0xF9;
		int val2 = 0xF6;
		byte a = (byte) val1;
		byte b = (byte) val2;
		System.out.println("xxx" + (int) a + ":" + (int) b);
		bytes[0] = a;
		bytes[1] = b;
		String s = BgConverterUtil.getCharacter4Big5(bytes);
		logger.info(s);
		assertNotNull(s);
		assertEquals("╨",s);
		int v1 = 0xA8;
		int v2 = 0x6C;
		byte c = (byte) v1;
		byte d = (byte) v2;
		byte bytes2[] = new byte[2];
		bytes2[0] = c;
		bytes2[1] = d;
		HashMap<ByteBuffer, byte[]> map = new HashMap<>();
		map.put(ByteBuffer.wrap(bytes), bytes2);

		String s2 = BgConverterUtil.getCharacter4Gbk(bytes2);
		assertEquals("╨",s2);
		byte bytes3[] = new byte[2];
		int va1 = 0xF9;
		int va2 = 0xF6;
		byte va = (byte) val1;
		byte vb = (byte) val2;
		bytes3[0] = va;
		bytes3[1] = vb;
		byte[] o = map.get(ByteBuffer.wrap(bytes3));
		assertEquals(o, bytes2);
	}

	@Test
	public void testGetBytesFromString() {
		String s = "F07C F1F9";
		logger.info(BgConverterUtil.getMapCharacterFromInnerCode(s));
	}

	// @Test
	public void testReadDefinitionFile() {
		BgConverterUtil util = new BgConverterUtil();
		String s = util.readDefinitionFile();
		System.out.println(s);
	}

	@Test
	public void testDirectChange() throws UnsupportedEncodingException {
	
		InputStream in = ClassLoader.getSystemResourceAsStream("big5test");
		Scanner scanner = new Scanner(new InputStreamReader(in, "MS950"));
		while (scanner.hasNext()) {
			try {
				String s = scanner.nextLine();
				String source = new String(s.getBytes());
				logger.info("source:" + source);
				String result = BgConverterUtil.b2gRawString(source);
				logger.info("result:" + result);
				String result2 = BgConverterUtil.g2bRawString(result);
				logger.info("result2:" + result2);
			} catch (UnsupportedEncodingException e) {
				fail(e.getMessage());
			}
		}
		scanner.close();
	}

	@Test
	public void testDirectChangeG2B() throws UnsupportedEncodingException {
		Logger log = LogManager.getLogManager().getLogger("");
		for (Handler h : log.getHandlers()) {
		    h.setLevel(Level.FINEST);
		}
		InputStream in = ClassLoader.getSystemResourceAsStream("gbktest.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "GBK"));
		try {
			while (reader.ready()) {
				String source = reader.readLine();
				logger.info("source:" + source);
				String result = BgConverterUtil.g2bRawString(source);
				logger.info("result:" + result);
				String result2 = BgConverterUtil.b2gRawString(result);
				logger.info("result2:" + result2);
				logger.info(BgConverterUtil.g2bRawString(result2));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		
	}
}
