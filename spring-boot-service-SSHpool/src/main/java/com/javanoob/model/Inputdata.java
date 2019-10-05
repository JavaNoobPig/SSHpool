package com.javanoob.model;

public class Inputdata {
	private String input_fgl_name;
	private String input_param;
	private String separatekey;
	
	public String getInput_fgl_name() {
		return input_fgl_name;
	}
	public void setInput_fgl_name(String input_fgl_name) {
		this.input_fgl_name = input_fgl_name;
	}
	public String getInput_param() {
		return input_param;
	}
	public void setInput_param(String input_param) {
		this.input_param = input_param;
	}
	public String getSeparatekey() {
		return separatekey;
	}
	public void setSeparatekey(String separatekey) {
		this.separatekey = separatekey;
	}
	@Override
	public String toString() {
		return "Inputdata [input_fgl_name=" + input_fgl_name + ", input_param=" + input_param + ", separatekey="
				+ separatekey + "]";
	}
	

	

}
