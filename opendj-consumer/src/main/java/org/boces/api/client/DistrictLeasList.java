package org.boces.api.client;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DistrictLeasList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("xLeas")
	private XLeas xLeas;

	public XLeas getxLeas() {
		return xLeas;
	}
	
	public List<DistrictLeaInfo> getDistrictLeaInfoList() {
		return xLeas.getLeaList();
	}
	
	static class XLeas implements Serializable {
		private static final long serialVersionUID = 1L;

		@JsonProperty("xLea")
		private List<DistrictLeaInfo> leaList;

		public List<DistrictLeaInfo> getLeaList() {
			return leaList;
		}
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
