package org.occ.ui.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrontService
{
	@Autowired
	BackService backService; 

//	@HystrixCommand(fallbackMethod = "fallbackSave") //1
	public Map<String, String> login(Map<String,Object> paramsMap)
	{
		return backService.login(paramsMap);
	}
}
