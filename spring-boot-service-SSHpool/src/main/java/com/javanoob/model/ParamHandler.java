package com.javanoob.model;

import org.springframework.stereotype.Service;

@Service
public class ParamHandler {

	
	public  String convertParamToSSHInputString(String inputParam,String separate) {
		String result="";
		String []tempArray=inputParam.split(separate);
		for(int i=0;i<=(tempArray.length-1);i++) {
			tempArray[i]="'"+tempArray[i]+"'";
			result=result+tempArray[i]+" ";
		}		
		return result;
		}
}
