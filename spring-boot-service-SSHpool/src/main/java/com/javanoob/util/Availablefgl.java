package com.javanoob.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Availablefgl {

	@Value("${ssh.exec.available}")
	private String availablefgl;

	private List<String> availablefglList;

	public void getAvailablefglArray() {
		System.out.println("availablefgl=" + availablefgl);
		this.availablefglList = Arrays.asList(availablefgl.split(","));
	}

	public boolean isExecAvailable(String fglname) {
		if (availablefglList != null && this.availablefglList.contains(fglname)) {
			return true;
		}
		return false;
	}
}
