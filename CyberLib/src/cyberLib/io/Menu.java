package cyberLib.io;

import java.util.ArrayList;

public class Menu {
	private String title;
	private ArrayList<String> list;
	
	public Menu(String title, ArrayList<String> list) {
		this.title = title;
		this.list = list;
	}
	
	public Menu(String title) {
		this.title = title;
		list = new ArrayList<>();
	}
	
	public void show() {
		System.out.println("~ " + title + " ~");
		System.out.println("Select one option: ");
		
		for(int i = 0; i < list.size(); i++) {
			System.out.printf("[%d] %s\n", i, list.get(i));
		}
	}
	
	public int select() {
		show();
		
		if(list.size() < 1) {
			System.err.println("No options in the list. Please fill the menu before selecting");
			return -1;
		}
		
		int selection = -1;
		while(selection < 0 || selection >= list.size())
			selection = Input.askInt("Insert a valid number[0..." + (list.size() - 1) + "]");
		return selection;
	}
	
	public void add(String s) {
		list.add(s);
	}
	
	public void add(ArrayList<String> strings) {
		list.addAll(strings);
	}
	
	public ArrayList<String> getList() {
		return list;
	}
	
	public String getOption(int i) {
		return list.get(i);
	}
	
	public void setOption(int i, String newOption) {
		list.add(i, newOption);
		// The old one got shifted one place after
		list.remove(i + 1);
	}
}
