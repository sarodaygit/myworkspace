package com.hp.tests.hibernettests;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Student_Info student = new Student_Info();
		Student_Info student1 = new Student_Info();
		student.setName("nagaraj");
//		student.setRollno(4);
		student.setDate(new Date());
		
		student1.setName("saroday");
//		student1.setRollno(4);
		student1.setDate(new Date());
		
		@SuppressWarnings("deprecation")
		SessionFactory sessionFactory = new AnnotationConfiguration()
				.configure("com/hp/tests/hibernettests/hibernate.cfg.xml")
				.buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(student);
		session.save(student1);
		session.getTransaction().commit();
		session.close();
		sessionFactory.close();

	}

}
