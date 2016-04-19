package org.boces.djclient.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.boces.api.client.DistrictLeaInfo;
import org.boces.api.client.DistrictLeasList;
import org.boces.api.client.OAuthEndpointInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LeaService {
	private static final Logger log = LoggerFactory.getLogger(StudentService.class);

	@Value("${district.alias.url}")
	String leaDataUrl;
	
	//Create a lea API URL for a list of all Leas in a given endpoint
	private String getLeaListUrl(OAuthEndpointInfo endpoint){
		String leaUrl = endpoint.getHref() + leaDataUrl + ".json?accessToken=" + endpoint.getToken();
		log.info("Lea list URL: " + leaUrl);
		return leaUrl;
	}
	
	private List<HttpMessageConverter<?>> getMessageConverters() {
	    List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	    MappingJackson2HttpMessageConverter custom_converter = new MappingJackson2HttpMessageConverter();
	    ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		custom_converter.setObjectMapper(objectMapper);
	    converters.add(custom_converter);
	    return converters;
	}
	
	//Calls API using the URL from getLeaListUrl and returns the list of Leas in a given endpoint
	public DistrictLeasList retrieveLeaList(OAuthEndpointInfo endpoint){
		
		RestTemplate rt = new RestTemplate();
		rt.setMessageConverters(getMessageConverters());
		HttpHeaders headers = new HttpHeaders();
		try{
			headers.set("Authorization", "Bearer" + endpoint.getToken());
			HttpEntity<?> entity = new HttpEntity<DistrictLeasList>(headers);
			ResponseEntity<DistrictLeasList> response = rt.exchange(getLeaListUrl(endpoint), HttpMethod.GET, entity, DistrictLeasList.class);
			return response.getBody();
		}
		catch(Exception e){
			return null;
		}
		
	}

	public DistrictLeaInfo getDistrictObjectByRefId(DistrictLeasList leaList, String leaRefId){
			List<DistrictLeaInfo> leaInfo = leaList.getDistrictLeaInfoList();
			Iterator<DistrictLeaInfo> leaIter = leaInfo.iterator();
			while(leaIter.hasNext()){
				DistrictLeaInfo leaInfoObj = leaIter.next();
				if(leaInfoObj.getRefId().equals(leaRefId)){
					return leaInfoObj;
				}
			}
			return null;
	}
}
