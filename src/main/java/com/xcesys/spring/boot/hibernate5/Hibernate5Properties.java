package com.xcesys.spring.boot.hibernate5;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.hibernate5")
public class Hibernate5Properties {
	private String[] mappingResources;
	private Boolean openInView;
	private String[] packages;
	private Properties properties;
	
	public String[] getMappingResources() {
		return mappingResources;
	}

	public Object getOpenInView() {
		return openInView;
	}

	public String[] getPackages() {
		return packages;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setMappingResources(String[] mappingResources) {
		this.mappingResources = mappingResources;
	}

	public void setOpenInView(Boolean openInView) {
		this.openInView = openInView;
	}

	public void setPackages(String[] packages) {
		this.packages = packages;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
