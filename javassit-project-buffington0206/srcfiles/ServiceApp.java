package target;

public class ServiceApp {
	public void bar(int n, int m) {
		System.out.println("[DBG] bar called");
	}
	
	public static void main(String args[]) {
		ServiceApp f = new ServiceApp();
		f.bar(10, 20);
	}
}
