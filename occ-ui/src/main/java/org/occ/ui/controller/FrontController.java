package org.occ.ui.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.occ.ui.common.api.HttpClientUtils;
import org.occ.ui.service.FrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontController
{
	@Autowired
	FrontService frontService;

	@RequestMapping("/")
	public String index() 
	{
		return "index";
	}
	
	@RequestMapping(value="/flashgame")
//	public String login(HttpServletRequest request,HttpServletResponse response) throws Exception
	public String login(HttpServletRequest request,HttpSession session) throws Exception
	{
		Map<String, String[]> params = request.getParameterMap();
//        ModelAndView view = new ModelAndView();
//		HttpSession session = request.getSession();
		
		if(params.get("username")[0].equals("") || params.get("password")[0].equals(""))
		{
			return "index";
		}
		
		String userName = params.get("username")[0];
		String passWord = params.get("password")[0];
		
		if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord))
		{
//			session.setAttribute("message", "账号或者密码为空!");
//			attr.addFlashAttribute("message", "账号或者密码为空!");
			return "index";
		}
		
		if(!Pattern.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", userName))
		{
//			attr.addFlashAttribute("message", "账号必须为邮箱格式!");
//			return view;
			return "index";
		}
		
		if(Pattern.matches("[~!/@#$%^&*()-_=+\\|[{}];:\'\",<.>/?]+", passWord))
		{
//			attr.addFlashAttribute("message", "密码中不能包含特殊字符!");
//			return view;
			return "index";
		}
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("userName", userName);
		paramsMap.put("passWord", passWord);
		
		Map<String,String> map = frontService.login(paramsMap);

		session.setAttribute("scheme", request.getScheme());
		session.setAttribute("host", request.getHeader("Host"));
		session.setAttribute("owner", map.get("owner"));
		session.setAttribute("apiToken", map.get("api_token"));
		session.setAttribute("apiStarttime", map.get("api_starttime"));
		session.setAttribute("worldIp", map.get("worldIp"));
		
		return "flashgame";
	}
	
	@RequestMapping("/kcsapi/**")
	public void kcs(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		String worldIp = request.getSession().getAttribute("world_ip").toString();
//		String worldIp = "203.104.209.102";
		String requestUrl = StringUtils.substringAfterLast(request.getServletPath(), "/kcsapi/");
		
		if(StringUtils.isNotEmpty(worldIp))
		{
			if("api_start2".equals(requestUrl))
			{
				InputStream in = null;
				OutputStream out = null;
				try {
					in = new FileInputStream("D:/workspace/occ-front/src/main/resources/kcsapi/api_start2.txt");
					out  = response.getOutputStream(); 
					int len = 0;
					byte buffer[] = new byte[1024];
					while ((len = in.read(buffer)) != -1) 
					{
						out.write(buffer, 0, len);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						in.close();
						out.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				Map<String, Object> params = new HashMap<String,Object>();
				Map<String, String> map = new HashMap<String,String>();
				List<NameValuePair> list = new ArrayList<NameValuePair>();

				String referer = request.getHeader("Referer");
				referer = StringUtils.replace(referer, "localhost", worldIp);
				referer = StringUtils.replace(referer, "&world_ip=" + worldIp, "");
				
				map.put("Origin", "http://" + worldIp + "/");
				map.put("Referer", referer);
				map.put("X-Requested-With", "ShockwaveFlash/18.0.0.232");
	
		        list.add(new BasicNameValuePair("api_token", request.getParameterMap().get("api_token")[0]));
		        list.add(new BasicNameValuePair("api_verno", request.getParameterMap().get("api_verno")[0]));
				
				params.put("url", "http://" + worldIp + request.getServletPath());
				params.put("header", map);
				params.put("entity", list);

				String responseHtml = HttpClientUtils.httpClientParams(params);
				
//				response.addHeader("Content-Type", entity.getContentType().getValue());
				response.getWriter().write(responseHtml);
				System.out.println(responseHtml);
			}
		}
	}
	
	@RequestMapping("/kcs/resources/image/world/{server}_{image}")
	public void image(HttpServletRequest request,HttpServletResponse response,@PathVariable String server,@PathVariable String image)
	{
		String worldIp = request.getSession().getAttribute("worldIp").toString();
//		String worldIp = "203.104.209.102";

		if(StringUtils.isNotEmpty(worldIp))
		{
			image = StringUtils.replace(worldIp, ".", "_") + "_" + image + ".png";
			
			response.addHeader("Content-Type", "image/png");
			response.addHeader("Cache-Control", "no-cache");
			
			//发送请求到静态资源服务器
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream("H:/ShimakazeGo/cache/kcs/resources/image/world/" + image);
				out  = response.getOutputStream(); 
				int len = 0;
				byte buffer[] = new byte[1024];
				while ((len = in.read(buffer)) != -1) 
				{
					out.write(buffer, 0, len);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					in.close();
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("image 111111111111111111");
	}
}
