package com.xcesys.spring.boot.hibernate5;

import java.util.Arrays;

import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(HibernateTemplate.class)
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(Hibernate5Properties.class)
public class Hibernate5AutoConfiguration {
	@Order(0)
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnWebApplication(type = Type.SERVLET)
	@ConditionalOnClass(WebMvcConfigurer.class)
	@ConditionalOnMissingBean({ OpenSessionInViewInterceptor.class, OpenSessionInViewFilter.class })
	@ConditionalOnMissingFilterBean(OpenEntityManagerInViewFilter.class)
	@EnableConfigurationProperties(Hibernate5Properties.class)
	@ConditionalOnProperty(prefix = "spring.hibernate5", name = "open-in-view", havingValue = "true", matchIfMissing = true)
	protected static class Hibernate5WebConfiguration {

		private static final Log logger = LogFactory.getLog(Hibernate5WebConfiguration.class);

		private final Hibernate5Properties hibernate5Properties;
		private final SessionFactory sessionFactory;

		protected Hibernate5WebConfiguration(SessionFactory sessionFactory, Hibernate5Properties hibernate5Properties) {
			this.hibernate5Properties = hibernate5Properties;
			this.sessionFactory = sessionFactory;
		}

		@Bean
		public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
			if (this.hibernate5Properties.getOpenInView() == null) {
				logger.warn("spring.hibernate5.open-in-view is enabled by default. "
						+ "Therefore, database queries may be performed during view "
						+ "rendering. Explicitly configure spring.jpa.open-in-view to disable this warning");
			}
			OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
			interceptor.setSessionFactory(this.sessionFactory);
			return interceptor;
		}

		@Bean
		public WebMvcConfigurer openSessionInViewInterceptorConfigurer(OpenSessionInViewInterceptor interceptor) {
			return new WebMvcConfigurer() {

				@Override
				public void addInterceptors(InterceptorRegistry registry) {
					registry.addWebRequestInterceptor(interceptor);
				}

			};
		}

	}

	@Autowired
	private Hibernate5Properties properties;

	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	@ConditionalOnMissingBean(SessionFactory.class)
	public SessionFactory hibernate5SessionFactory(DataSource dataSource) {
		LocalSessionFactoryBuilder b = new LocalSessionFactoryBuilder(dataSource, resourceLoader);
		b.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
		if (properties.getProperties().containsKey("hibernate.physical_naming_strategy")) {
			b.setPhysicalNamingStrategy(null);
		}
		String[] mappingResources = properties.getMappingResources();
		if (mappingResources != null && mappingResources.length > 0) {
			Arrays.asList(mappingResources).stream().forEach(rs -> b.addResource(rs));
		}
		b.scanPackages(properties.getPackages()).addProperties(properties.getProperties()); // don't use setProperties()
		return b.buildSessionFactory();
	}

	@Bean
	@ConditionalOnMissingBean(HibernateTemplate.class)
	public HibernateTemplate hibernate5Template(SessionFactory sessionFactory) {
		return new HibernateTemplate(sessionFactory);
	}

	@Bean
	@ConditionalOnMissingBean
	public PlatformTransactionManager hibernate5TransactionManager(SessionFactory sessionFactory, DataSource dataSource) {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(sessionFactory);
		hibernateTransactionManager.setDataSource(dataSource);
		return hibernateTransactionManager;
	}
}
