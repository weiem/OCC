package org.occ.back.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.occ.back.common.api.HttpClientUtils;
import org.occ.back.common.exception.ServiceException;
import org.occ.back.common.util.Regular;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginAuthService 
{
	@Value("${login.url}")
	private String loinUrl;

	@Value("${ajax.tokenurl}")
	private String ajaxTokenurl;

	@Value("${dmm.com}")
	private String dmmCom;

	@Value("${auth.url}")
	private String authUrl;

	@Value("${game.url}")
	private String gameUrl;

	@Value("${world.url}")
	private String worldUrl;

	@Value("${get.flash.url}")
	private String getFlashUrl;

	@Value("${make.request.url}")
	private String makeRequestUrl;

	@Value("${flash.url}")
	private String flashUrl;

	private String[] worldIp = {"203.104.209.71",
	                             "203.104.209.87",
	                             "125.6.184.16",
	                             "125.6.187.205",
	                             "125.6.187.229",
	                             "125.6.187.253",
	                             "125.6.188.25",
	                             "203.104.248.135",
	                             "125.6.189.7",
	                             "125.6.189.39",
	                             "125.6.189.71",
	                             "125.6.189.103",
	                             "125.6.189.135",
	                             "125.6.189.167",
	                             "125.6.189.215",
	                             "125.6.189.247",
	                             "203.104.209.23",
	                             "203.104.209.39",
	                             "203.104.209.55",
	                             "203.104.209.102"};
	
	private String dmmToken;
	private String token;
	private String osapi;
	private String owner;
	private String st;
	private String apiWorldId;
	private StringBuilder responseHtml = new StringBuilder();
	CloseableHttpClient httpclient = null;
	HttpPost httpPost = null;
	
	public Map<String,String> getFlash(String userName,String passWord)
	{ 
		Map<String, String> map = new HashMap<String,String>();

		map.putAll(getOsapiUrl(userName,passWord));
		map.putAll(getWorld());
		map.putAll(getApiToken(map));		
//		
		return map;
	}
	
	private Map<String,String> getOsapiUrl(String userName,String passWord)
	{ 
		Map<String, String> map = new HashMap<String,String>();
		
		try {
			//设置不检查网站证书 如果网站证书过期没有及时更新会报错 需要忽略检查
			SSLContextBuilder builder = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy());
		    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
		    
		    httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		    
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    		

		map.putAll(getDmmToken(userName,passWord));
		map.putAll(getAjaxToken());
		map.putAll(getOsapi(map,userName,passWord));
		return map;
	}

	private Map<String, String> getDmmToken(String userName,String passWord)
	{ 
		Map<String, Object> params = new HashMap<String,Object>();
		Map<String, String> map = new HashMap<String,String>();
		
		params.put("url", loinUrl);
//		params.put("header", map);
		
		responseHtml.append(HttpClientUtils.httpClientParams(params,httpclient));
		
		if(StringUtils.isNotEmpty(responseHtml.toString()))
		{
			dmmToken = Regular.regularParams(responseHtml.toString(), "dmmToken");
//			dmmToken = "获取dmmToken失败!";
			if(dmmToken.indexOf("失败") > 0)
			{
				throw new ServiceException(dmmToken);
			}
			
			token = Regular.regularParams(responseHtml.toString(), "token");
			
			if(token.indexOf("失败") > 0)
			{
				map.put("", "");
				return map;
				//throw new ServiceException(dmmToken);
			}
		}else {
			throw new ServiceException("连接DMM网站失败");
		}
		
		map.put("dmmToken", dmmToken);
		map.put("token", token);

		responseHtml.setLength(0);
		return map;
	}
	
	private Map<String, String> getAjaxToken()
	{ 
		Map<String, Object> params = new HashMap<String,Object>();
		Map<String, String> map = new HashMap<String,String>();
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		map.put("Origin", dmmCom);
		map.put("Referer", loinUrl);
		map.put("X-Requested-With", "XMLHttpRequest");
		map.put("DMM_TOKEN", dmmToken);
		
		list.add(new BasicNameValuePair("token", token));
		
		params.put("url", ajaxTokenurl);
		params.put("header", map);
		params.put("entity", list);
		
		responseHtml.append(HttpClientUtils.httpClientParams(params,httpclient));
		
		JSONObject json = new JSONObject(responseHtml.toString());
		
		map.clear();
		map.put("token", json.get("token").toString());
		map.put("login_id", json.get("login_id").toString());
		map.put("password", json.get("password").toString());

		responseHtml.setLength(0);
		return map;
	}
	
	private Map<String, String> getOsapi(Map<String, String> loginParams,String userName,String passWord)
	{ 
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> map = new HashMap<String, String>();
		
		params.put("url", authUrl);

		map.put("Origin", dmmCom);
		map.put("Referer", loinUrl);
		params.put("header", map);
		
		list.add(new BasicNameValuePair("login_id", userName));
		list.add(new BasicNameValuePair("password", passWord));
		list.add(new BasicNameValuePair("token", loginParams.get("token")));
		list.add(new BasicNameValuePair(loginParams.get("login_id"), userName));
		list.add(new BasicNameValuePair(loginParams.get("password"), passWord));
		params.put("entity", list);
		
		responseHtml.append(HttpClientUtils.httpClientParams(params,httpclient));
		
   		if(StringUtils.isNotEmpty(responseHtml.toString()))
		{
			if(StringUtils.indexOf(responseHtml.toString(), "認証エラー") > 0)
			{
				throw new ServiceException("DMM强制要求用户修改密码");
			}
		}

		responseHtml.setLength(0);
		params.clear();
		
		params.put("url", gameUrl);
		map.put("Origin", dmmCom);
		map.put("Referer", loinUrl);

		params.put("header", map);
		
		responseHtml.append(HttpClientUtils.httpClientParams(params,httpclient));

		if(StringUtils.isEmpty(responseHtml.toString()))
		{
			osapi = Regular.regularParams(responseHtml.toString(), "intSesid");

			if(osapi.indexOf("失败") > 0)
			{
				/*map.put("", "");
				return map;*/
				throw new ServiceException(dmmToken);
			}
		}
		
		map.clear();
		map.put("osapi", osapi);

		responseHtml.setLength(0);
		return map;
	}
	
	private Map<String, String> getWorld()
	{ 
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> params = new HashMap<String, Object>();
		
		owner = StringUtils.substringBetween(osapi, "owner=", "&");
		
		try {
			st = URLDecoder.decode(StringUtils.substringBetween(osapi, "st=","#rpctoken"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String getWorldUrl = StringUtils.replace(worldUrl, "%s", owner);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 1);

		getWorldUrl = StringUtils.replace(getWorldUrl, "%d", Long.toString(calendar.getTimeInMillis()));
		
		map.put("Referer", osapi);
		params.put("header", map);
		params.put("url", getWorldUrl);
		
		responseHtml.append(HttpClientUtils.httpClientParams(params,httpclient));
		
		if(StringUtils.isNotEmpty(responseHtml.toString()))
		{
			JSONObject json = new JSONObject(StringUtils.substringBetween(responseHtml.toString(), "svdata=", "}") + "}}");
			
			if("1".equals(json.get("api_result").toString()))
			{
				apiWorldId = StringUtils.substringBetween(responseHtml.toString(), "\"api_world_id\":", "}}");
			}else{
				String a = "调查提督所在镇守府时发生错误";
			}
		}
		
		map.put("worldIp", worldIp[Integer.parseInt(apiWorldId)-1]);
		map.put("owner", owner);
		map.put("time", Long.toString(calendar.getTimeInMillis()));

		responseHtml.setLength(0);
		return map;
	}
	
	private Map<String, String> getApiToken(Map<String, String> loginParams)
	{ 
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String flashUrl = StringUtils.replace(getFlashUrl, "%w", loginParams.get("worldIp"));
		flashUrl = StringUtils.replace(flashUrl, "%d", loginParams.get("time"));
		flashUrl = StringUtils.replace(flashUrl, "%s", owner);
		
		list.add(new BasicNameValuePair("url", flashUrl));
		list.add(new BasicNameValuePair("st", st));
		list.add(new BasicNameValuePair("contentType", "Json"));
		list.add(new BasicNameValuePair("contentType", "JSON"));
		list.add(new BasicNameValuePair("authz", "signed"));
		list.add(new BasicNameValuePair("numEntries", "3"));
		list.add(new BasicNameValuePair("getSummaries", "false"));
		list.add(new BasicNameValuePair("signOwner", "true"));
		list.add(new BasicNameValuePair("signViewer", "true"));
		list.add(new BasicNameValuePair("gadget", "http://203.104.209.7/gadget.xml"));
		list.add(new BasicNameValuePair("container", "dmm"));
		list.add(new BasicNameValuePair("httpMethod", "GET"));

		params.put("entity", list);
		params.put("url", makeRequestUrl);
		
		responseHtml.append(HttpClientUtils.httpClientParams(params,httpclient));
		
		if(StringUtils.isNotEmpty(responseHtml.toString()))
		{
			JSONObject json = new JSONObject(StringUtils.substringBetween(responseHtml.toString(), loginParams.get("time") + "\":","}}}") + "}}}");

			if("200".equals(json.get("rc").toString()))
			{
				JSONObject svdata = new JSONObject(StringUtils.replace(StringUtils.substringBetween(responseHtml.toString(), "svdata=","}") + "}","\\",""));
				
				map.put("api_starttime", svdata.get("api_starttime").toString());
				map.put("api_token", svdata.get("api_token").toString());
			}else{
				String a = "调查提督所在镇守府时发生错误";
			}
		}
		
		responseHtml.setLength(0);
		return map;
	}
}
