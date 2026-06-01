package main;

import window.*;

public class MainApplication {
	public static void main(String args[]) {
		util.DatabaseManager.initDatabase();
		Window firstWindow = new Window();
		firstWindow.mainFrame();
		
		
	}
}
