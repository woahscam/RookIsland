package com.rs.utils;

public class HexColours {

	public enum Colours {

		WHITE("000000"),
		
		RED("b50404"),
		
		YELLOW("EAEC2C"),
		
		GREEN("A0DB8E");

		private String hex;

		private Colours(String hex) {
			this.setHex("<col=" + hex + ">");
		}

		public String getHex() {
			return hex;
		}

		public void setHex(String hex) {
			this.hex = hex;
		}
	}
	
	public static String end() {
		return "</col>";
	}
	
	public static String getShortMessage(Colours colour, String message) {
		return colour.getHex() + message + end();
	}
	
	public static String getMessage(Colours colour, String message) {
		return colour.getHex() + message;
	}
}
