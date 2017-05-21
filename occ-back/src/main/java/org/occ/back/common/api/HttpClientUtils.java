package org.occ.back.common.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils
{
	private static String browserVersion = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko";
	
	private static int connectionTimeout = 30000;
	
	private static int requestTimeout = 30000;
	 
	@SuppressWarnings("unchecked")
	public static String httpClientParams(Map<String, Object> map, CloseableHttpClient httpclient)
	{
		//返回请求到的html页面
		String responseHtml = null;
		HttpPost httpPost = null;
		
	    try {
			//设置不检查网站证书 如果网站证书过期没有及时更新会报错 需要忽略检查
			/*SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		            builder.build());
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();*/
			
			/*proxy是设置代理地址 格式如下 
			 例：HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http"); */
			
			//设置url请求配置
			RequestConfig requestConfig = RequestConfig.custom()  
			        .setConnectTimeout(connectionTimeout)
			        .setConnectionRequestTimeout(requestTimeout)
//			        .setProxy(proxy)
			        .build();
			
			//设置URL
	    	httpPost = new HttpPost(map.get("url").toString());
			httpPost.addHeader("User-Agent", browserVersion);
			
			//设置信息头Header
			if(map.get("header") != null)
			{
				Map<String, String> header = (Map<String, String>) map.get("header");
				for(String key : header.keySet())
				{
					httpPost.addHeader(key, header.get(key));
				}
			}
			
			//设置传输的参数Parameters
			if(map.get("entity") != null)
			{
				List<NameValuePair> list = (List<NameValuePair>) map.get("entity");
		        httpPost.setEntity(new UrlEncodedFormEntity(list));
			}
	        
	        httpPost.setConfig(requestConfig);
	        
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) 
			{
				responseHtml = EntityUtils.toString(entity);
				System.out.println(responseHtml);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ finally {
			try {
				httpPost.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return responseHtml;
	}
}
