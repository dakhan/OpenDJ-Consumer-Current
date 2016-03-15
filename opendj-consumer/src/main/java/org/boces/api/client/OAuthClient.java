package org.boces.api.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OAuthClient implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(OAuthClient.class);
	
	@Value("${oauth.url}")
	private String oAuthUrl;
	
	@Value("${oauth.userid}")
	private String userId;
	
	@Value("${oauth.password}")
	private String password;
	
	public OAuthInfo retrieveTokensAndEndpoints() {
		RestTemplate restTemplate = new RestTemplate();
		
		Map<String, String> vars = new HashMap<String, String>();
        vars.put("username", userId);
        vars.put("password", password);
		
		OAuthInfo info = restTemplate.postForObject(oAuthUrl, vars, OAuthInfo.class);
		log.info("Data " + info);
		log.info("OAuthData " + info.getId());
		log.info("EndPoint Data " + info.getEndpoint()[0].getName());
		// Set the districtId at the endpoint level.
		if (info != null && info.getEndpoint() != null && info.getEndpoint().length > 0) {
			OAuthEndpointInfo[] endpoints = info.getEndpoint();
			for (int i = 0; i < endpoints.length; i++) {
				endpoints[i].setDistrictId(info.getId());
			}
		}
		
		return info;
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
	  MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
	  ObjectMapper objectMapper = new ObjectMapper();
	  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	  objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	  jsonConverter.setObjectMapper(objectMapper);
	  return jsonConverter;
	 }

}
