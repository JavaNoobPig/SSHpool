package com.javanoob.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javanoob.model.Inputdata;
import com.javanoob.model.OutputdataSecVer;
import com.javanoob.model.ParamHandler;
import com.javanoob.util.Availablefgl;
import com.javanoob.util.JschCommandExecutor;
import com.javanoob.util.SessionFactory;

@RestController
public class SSHTestController {

	@Autowired
	private JschCommandExecutor jschCommandExecutor;
	@Autowired
	private Availablefgl availablefgl;
	
	@Autowired
	private ParamHandler paramHandler;
	@Autowired
	private SessionFactory sessionFactory;

	@RequestMapping(value = "/SSH", method = RequestMethod.POST)
	public OutputdataSecVer secondService(@RequestBody Inputdata inputdata) {

		if(!availablefgl.isExecAvailable(inputdata.getInput_fgl_name())) {
			return new OutputdataSecVer("111","This 4ge is not Available!!","");
		}
		return jschCommandExecutor.execute("/prod/shell/jh_4ge.s '" + inputdata.getInput_fgl_name() + "' "
				+ paramHandler.convertParamToSSHInputString(inputdata.getInput_param(), inputdata.getSeparatekey()));
	}
	
	
	@RequestMapping(value = "/SSHtoNull", method = RequestMethod.GET)
	public void secondService2() {
		sessionFactory.destroySession();
	}
}
