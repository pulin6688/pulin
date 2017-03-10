package com.pulin.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:config.properties")
//@EnableElasticsearchRepositories(basePackages = "com.pulin")
public class MyConfiguration {
	
	@Autowired(required=false)
	private Environment environment;
	
	@Bean
	public String client() {
		try {
			String appName = environment.getProperty("app.name");
			System.out.println(appName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}