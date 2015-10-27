package org.boces.djclient;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LdapSyncUtility.class)
public class LdapUtilityApplicationTests {
	
	final TestLdapServer testLdap = new TestLdapServer();
	
	@Before
	public void setup() {
		// Create the OpenDJ embedded server.
		try {
			testLdap.startLdapServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void contextLoads() {
		System.out.println("Running Tests with the embedded LDAP server..");
	}
	
	@After
	public void tearDown() {
		testLdap.stopLdapServer();
	}

}
