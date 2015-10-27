package org.boces.djclient.service;

import java.util.List;
import java.util.Iterator;

import org.boces.api.client.DistrictLeaInfo;
import org.boces.api.client.DistrictLeasList;
import org.boces.api.client.OAuthEndpointInfo;
import org.boces.djclient.ldap.LdapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LeaService {
	private static final Logger log = LoggerFactory.getLogger(StudentService.class);

	@Autowired
	private LdapUtil ldapUtil;
	
	@Value("${district.alias.url}")
	String leaDataUrl;
	
	//Create a lea API URL for a list of all Leas in a given endpoint
	private String getLeaListUrl(OAuthEndpointInfo endpoint){
		String leaUrl = endpoint.getHref() + leaDataUrl + ".json?accessToken=" + endpoint.getToken();
		log.info("Lea list URL: " + leaUrl);
		return leaUrl;
	}
	
	//Calls API using the URL from getLeaListUrl and returns the list of Leas in a given endpoint
	public DistrictLeasList retrieveLeaList(OAuthEndpointInfo endpoint){
		RestTemplate rt = new RestTemplate();
		DistrictLeasList leaList = rt.getForObject(getLeaListUrl(endpoint), DistrictLeasList.class);
		return leaList;
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
