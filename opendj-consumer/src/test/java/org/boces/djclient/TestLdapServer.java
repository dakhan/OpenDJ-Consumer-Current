package org.boces.djclient;

import java.io.FileInputStream;
import java.io.IOException;

import org.forgerock.opendj.ldap.Connections;
import org.forgerock.opendj.ldap.LDAPClientContext;
import org.forgerock.opendj.ldap.LDAPListener;
import org.forgerock.opendj.ldap.LDAPListenerOptions;
import org.forgerock.opendj.ldap.MemoryBackend;
import org.forgerock.opendj.ldap.ServerConnectionFactory;
import org.forgerock.opendj.ldif.LDIFEntryReader;

public class TestLdapServer {
	
	final int ldapServerPort = 23389;
	final String ldapHost = "localhost";
	LDAPListener ldapServer = null;
	final String ldifFile = "TestLdapServer.ldif";
	
	public void startLdapServer() throws IOException {
		// Create the LDAP Listener Option.
		final LDAPListenerOptions ldapOpts = new LDAPListenerOptions().setBacklog(4096);
		
		// Create the memory backend.
		final MemoryBackend ldapMemBkend = new MemoryBackend(new LDIFEntryReader(new FileInputStream(ldifFile)));
		
		// Create the LDAP Connection Handler.
		final ServerConnectionFactory<LDAPClientContext, Integer> ldapConnHandler = Connections.newServerConnectionFactory(ldapMemBkend);
		
		// Start the embedded LDAP Server.
		ldapServer = new LDAPListener(ldapHost, ldapServerPort, ldapConnHandler, ldapOpts);
		
		// Some data to show that the embedded LDAP server is up..
		System.out.println("\n*********Embedded LDAP server is up..");
	}
	
	public void stopLdapServer() {
		if (ldapServer != null) {
			ldapServer.close();
			System.out.println("\n*******Embedded LDAP server is shut down..");
		}
	}

}
