package org.occ.ui.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class Config extends WebMvcConfigurerAdapter
{
	/**
	 * 设置静态资源文件
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		//舰娘缓存目录
        registry.addResourceHandler("/kcs/**").addResourceLocations("file:H:/ShimakazeGo/cache/kcs/");
        super.addResourceHandlers(registry);
    }
}
