package com.xcesys.spring.boot.hibernate5;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.xcesys.spring.boot.hibernate5.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HibernateDemoApplication.class)
public class HibernateApplicationTests {
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Test
	@Transactional
	public void create() {
		User user = new User();
		user.setId(1l);
		user.setUsername("test1");
		user.setGender("M");
		user.setPassword("123456");
		hibernateTemplate.save(user);
		System.out.println(user.getId());
		user = hibernateTemplate.get(User.class, 1L);
		System.out.println(user);
	}

}
