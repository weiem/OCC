package org.occ.back.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.occ.back.service.LoginAuthService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OocController
{  
    @Resource
	private LoginAuthService loginAuthService;
	
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public Map<String,String> login(@RequestBody Map<String,Object> paramsMap) throws Exception
	{
		
		return loginAuthService.getFlash(paramsMap.get("userName").toString(), paramsMap.get("passWord").toString());
	}
	
	@RequestMapping("/service/osapi")
	public void OsapiHandler(HttpServletRequest request,HttpServletResponse response)
	{
		System.out.println("OsapiHandler 1111111111111111");
	}
	
	@RequestMapping("/service/token")
	public void TokenHandler(HttpServletRequest request,HttpServletResponse response)
	{
		System.out.println("TokenHandler 1111111111111111");
	}
}
