package org.occ.back.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regular 
{
	@SuppressWarnings("serial")
	private static Map<String, String> mapRegular = new HashMap<String, String>()
	{{ 
        put("dmmToken" , "DMM_TOKEN\", \"(.*)\"" );
        put("token" , "\"token\": \"(.*)\"" );
        put("osApi" , "URL\\W+:\\W+\"(.*)\"," );
        put("intSesid" , "URL\u0020*:\u0020\"(.*)\"," );
	}}; 
	
	public static String regularParams(String responseHtml, String regular)
	{
		StringBuilder matching = new StringBuilder();
		Pattern p = Pattern.compile(mapRegular.get(regular)); 
		Matcher m = p.matcher(responseHtml); 
		if(m.find())
		{
			matching.append(m.group(1));
		}else{
			matching.append("获取" + regular + "失败!");
		}
		return matching.toString();
	}
}
