package de.lingua.shell;
public class Start {
	private Start(){		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length > 0){
			Installer installer=Installer.getInstance();
			installer.install(args[0]);
		}else{
			System.err.println("Too few arguements");
		}
	}
}
