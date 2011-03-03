package net.dirtyfilthy.bitcoin.test;

import java.util.Arrays;

import net.dirtyfilthy.bitcoin.core.OpCode;
import net.dirtyfilthy.bitcoin.core.Script;



import android.test.AndroidTestCase;

public class ScriptTest extends AndroidTestCase {
	
	public void testDecompileCompile(){
		Script s=new Script();
		s.pushData("hello nurse");
		s.pushOp(OpCode.OP_DUP);
		byte data[]=s.compile();
		Script s2=new Script(data);
		byte[] data2=s2.compile();
		assertTrue("Compiled scripts don't match", Arrays.equals(data, data2));
	}
	
	public void testSimpleEval(){
		Script s=new Script();
		s.pushData("hello nurse");
		s.pushData("hello nurse2");
		s.pushOp(OpCode.OP_EQUALVERIFY);
		assertFalse("Script should return false on eval", s.eval(null, 0, 0));
		s=new Script();
		s.pushData("hello nurse");
		s.pushData("hello nurse");
		s.pushOp(OpCode.OP_EQUAL);
		s.pushOp(OpCode.OP_VERIFY);
		assertTrue("Script should return true on eval", s.eval(null, 0, 0));
	}

}
