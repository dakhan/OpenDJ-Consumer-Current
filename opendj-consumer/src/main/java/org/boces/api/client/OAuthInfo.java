package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String user_name;
	private String token;
	private OAuthEndpointInfo[] endpoint;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public OAuthEndpointInfo[] getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(OAuthEndpointInfo[] endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
