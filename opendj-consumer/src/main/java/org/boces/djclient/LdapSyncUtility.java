package org.boces.djclient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;

import org.boces.djclient.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.boces"})
@EnableJms
public class LdapSyncUtility implements CommandLineRunner{
	
	private static final Logger log = LoggerFactory.getLogger(LdapSyncUtility.class);

    @Inject
    private Environment env;
    	
    /**
     * Determine whether the profiles are active for the application or not.
     * @throws IOException
     */
	@PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            log.warn("No Spring profile configured, running with default configuration");
        } else {
            log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        }
	}
	
	/**
	 * Main method to run the application.
	 * @param args
	 * @throws UnknownHostException
	 */
    public static void main(String[] args) {
    	SpringApplication app = new SpringApplication(LdapSyncUtility.class);
    	
    	SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        addDefaultProfile(app, source);
        Environment env = app.run(args).getEnvironment();
    }
    
    /**
     * Set a default profile (dev) if it has not been set
     */
    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active")) {
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }

	@Override
	public void run(String... arg0) throws Exception {
		log.info("LDAP Sync Utility started with scheduling, persistence, and async features");
	}
	
	@Bean
    JmsListenerContainerFactory<?> djConsumerJmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
