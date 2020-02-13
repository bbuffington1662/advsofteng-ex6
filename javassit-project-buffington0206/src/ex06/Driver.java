package ex06;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Driver extends ClassLoader{
	
	static final String WORK_DIR = System.getProperty("user.dir");
   static String SEP = File.separator;
   static final String INPUT_PATH = WORK_DIR + SEP + "classfiles";
   static final String OUTPUT_PATH = WORK_DIR + SEP + "output";

   public static String FACT_METHOD = null;
   public static String TARGET_MYAPP = null;

   static String _L_ = System.lineSeparator();

   private ClassPool pool;
   
	public static void main(String args[]) throws Throwable {
		Scanner input = new Scanner(System.in);
		String[] values = null;
		
		System.out.print("Please enter the classname, method name, and method parameter to edit separated by commas: ");
		values = input.nextLine().split(",");
		TARGET_MYAPP = values[0];
		FACT_METHOD = values[1];
		input.close();
		
		ClassPool defaultPool = ClassPool.getDefault();
	      defaultPool.insertClassPath(INPUT_PATH);
	      CtClass cc = defaultPool.get(TARGET_MYAPP);
	      CtMethod m = cc.getDeclaredMethod(FACT_METHOD);
	      m.useCflow(FACT_METHOD);
	      String block1 = String.format("\n\t{\n\t\tSystem.out.println(\"[Inserted] %s.%s\'s param %s: \" + $%s);\n\t}", values[0], values[1], values[2], values[2]);
	      System.out.println("[DBG] Insert:");
	      System.out.println(block1);
	      System.out.println("------------------------------------------");
	      m.insertBefore(block1);
	      cc.writeFile(OUTPUT_PATH);

	      Driver s = new Driver(values[0],values[1]);
	      TARGET_MYAPP = values[0];
			FACT_METHOD = values[1];
	      Class<?> c = s.loadClass(TARGET_MYAPP);
	      Method mainMethod = c.getDeclaredMethod("main", new Class[] { String[].class });
	      mainMethod.invoke(null, new Object[] { args });
	}
	
	public Driver(String a, String b) throws NotFoundException {
		TARGET_MYAPP = a;
		FACT_METHOD = b;
      pool = new ClassPool();
      pool.insertClassPath(OUTPUT_PATH); // TARGET must be there.
      print("[CLASS-LOADER] CLASS_PATH: " + INPUT_PATH);
      System.out.println("------------------------------------------");
   }

   protected Class<?> findClass(String name) throws ClassNotFoundException {
      CtClass cc = null;
      try {
         cc = pool.get(name);
         byte[] b = cc.toBytecode();
         return defineClass(name, b, 0, b.length);
      } catch (NotFoundException e) {
         throw new ClassNotFoundException();
      } catch (IOException e) {
         throw new ClassNotFoundException();
      } catch (CannotCompileException e) {
         throw new ClassNotFoundException();
      }
   }
   
   public static void print(String str) {
	      final int COLUMN_SIZE = 80;
	      int cnt = 0;
	      char[] words = str.toCharArray();
	      for (int i = 0; i < words.length; i++) {
	         System.out.print(words[i]);
	         if (cnt++ > COLUMN_SIZE) {
	            System.out.println();
	            cnt = 0;
	         }
	      }
	      System.out.println();
	   }
}
