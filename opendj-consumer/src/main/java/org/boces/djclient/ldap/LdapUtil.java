package org.boces.djclient.ldap;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.boces.api.client.DistrictStaffInfo;
import org.boces.api.client.DistrictStudentInfo;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Entries;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.EntryNotFoundException;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.LDAPConnectionFactory;
import org.forgerock.opendj.ldap.LinkedHashMapEntry;
import org.forgerock.opendj.ldap.Modification;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.TreeMapEntry;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LdapUtil {
	private static final Logger log = LoggerFactory.getLogger(LdapUtil.class);

	//Capture LDAP connection settings from application.properties
	@Value("${ldap.basedn}")
	private String baseDn;
	
	@Value("${ldap.admindn}")
	private String adminDn;
	
	@Value("${ldap.adminpwd}")
	private String adminPwd;
	
	@Value("${ldap.host}")
	private String ldapHost;
	
	@Value("${ldap.port}")
	private String ldapPort;
	
	private char[] adminPwdChars;
	
	//Using openDJ SDK to set up an LDAP connection
	private LDAPConnectionFactory factory;
	
	private LDAPConnectionFactory getLdapConnectionFactory() {
		if (factory == null) {
			log.info("Creating LDAP Connection " + adminDn + " " + adminPwd);
			factory = new LDAPConnectionFactory(ldapHost, Integer.parseInt(ldapPort));
		}
		return factory;
	}
	
	private Connection getLdapConnection() throws ErrorResultException {
		return getLdapConnectionFactory().getConnection();
	}
	
	private char[] getAdminPwdChars() {
		if (adminPwdChars == null) {
			adminPwdChars = adminPwd.toCharArray();
		}
		return adminPwdChars;
	}
	
	//Search for existing Staff objects
	public Entry searchStaffEntry(DistrictStaffInfo staff) {
		Connection conn = null;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			
			// determine the staff district info below.
			String baseDN = "ou=" + staff.getDistrictName() + ",ou=Users,dc=districts,dc=onefederation,dc=org";
			
			// Search for the entry in the subtree using the refID.
			SearchResultEntry entry = conn.searchSingleEntry(baseDN, 
					SearchScope.SINGLE_LEVEL, 
					Filter.equality("fedRefID", staff.getRefId()).toString(),
					new String[0]);
			
			String uid = entry.getAttribute("uid").firstValueAsString();
			log.debug("Retrieved staff " + uid);
			return entry;
		} catch (EntryNotFoundException enfe) {
			log.debug("No results Returned for the user " + enfe);
		}
		catch (ErrorResultException ere) {
			log.error("Error searching ldap entry " + ere);
		} finally {
			if (conn != null) conn.close();
		}
		return null;
	}
	
	//Search for existing Student objects
	public Entry searchStudentEntry(DistrictStudentInfo student) {
		Connection conn = null;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			
			// determine the student district info below and change logic based on uid if need be.
			String baseDN = "ou=" + student.getDistrictName() + ",ou=Users,dc=districts,dc=onefederation,dc=org";
			
			// Search for the entry in the subtree using the email address.
			// Confirmed that the refId is not going to be changed and the only case in which refId 
			// will change if the district data is being ripped and replaced.
			// Is the refId unique across the board? within the subtree will be ok to do.
			// Is there a need to check for duplication?
			SearchResultEntry entry = conn.searchSingleEntry(baseDN, 
					SearchScope.SINGLE_LEVEL, 
					Filter.equality("fedRefID", student.getRefId()).toString(),
					new String[0]);
			
			String uid = entry.getAttribute("uid").firstValueAsString();
			log.debug("Retrieved student " + uid);
			return entry;
		} catch (EntryNotFoundException enfe) {
			log.debug("No results Returned for the user " + enfe);
		} catch (ErrorResultException ere) {
			log.error("Error searching ldap entry " + ere);
		} finally {
			if (conn != null) conn.close();
		}
		return null;
	}
	
	//Lea Create Processing. Creates a new OU based on districtName
	public void createLeaEntry(String lea) {
		Connection conn = null;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			
			// Create the district entry.
			String entryDn = "ou=" + lea + ",ou=Users,dc=districts,dc=onefederation,dc=org";
			Entry entry = new LinkedHashMapEntry(entryDn).
					addAttribute("objectclass", "top").
					addAttribute("objectClass", "organizationalUnit");
			
			conn.add(entry);
		} catch (ErrorResultException ere) {
			log.error("Error creating ldap entry for the district " + ere);
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	//LDAP Create Processing for staff objects.
	public void createStaffEntry(DistrictStaffInfo staff, int isDuplicate) {
		Connection conn = null;
		int x = isDuplicate;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			log.info("Username: " + UserCredentialsUtil.generateUsername(staff.getGivenName(), staff.getFamilyName(), staff.getLocalId(), "t", x));
			String entryDn = "uid=" 
					+ UserCredentialsUtil.generateUsername(staff.getGivenName(), staff.getFamilyName(), staff.getLocalId(),"t", x) 
					+ ",ou=" 
					+ staff.getDistrictName() 
					+ ",ou=Users,dc=districts,dc=onefederation,dc=org";
			/*
			 * log.info("Username: " + staff.getUsername());
			 * String entryDn = "uid="
			 * 		+ staff.getUsername()
			 * 		+ ",ou="
			 * 		+ staff.getDistrictName()
			 * 		+ ",ou=Users,dc=districts,dc=onefederation,dc=org";
			 */
			
			//addAttribute("userPassword", staff.getPassword().
			
			//initial attributes defined are 'required' attributes. Below are conditionals to handle attributes that may be returning null values from the API
			Entry entry = new LinkedHashMapEntry(entryDn).
					addAttribute("objectclass", "top").
					addAttribute("objectClass", "fedUser").
					addAttribute("fedRefID", staff.getRefId()).
					addAttribute("fedUserType", "T").
					addAttribute("userPassword", UserCredentialsUtil.generateAlgorithmicPassword(staff.getGivenName(), staff.getFamilyName())).		
					addAttribute("fedGUID", UUID.randomUUID().toString());
			if(staff.getLocalId() != ""){
				entry.addAttribute("fedLocalID", staff.getLocalId());
			}
			if(staff.getStateProvinceId() != ""){
				entry.addAttribute("fedStateID", staff.getStateProvinceId());
			}
			if(staff.getEmailAddress() != ""){
				entry.addAttribute("mail", staff.getEmailAddress());
			}
			if(staff.getDistrictId() != ""){
				entry.addAttribute("fedDistrictID", staff.getDistrictId());
			}
			conn.add(entry);
			
		} catch (ErrorResultException ere) {
			if(ere.getResult().getResultCode().intValue() == 68){
				this.createStaffEntry(staff, x + 1);
			} else {
				log.error("Error creating ldap entry. Result Code:  " + ere.getResult().getResultCode().intValue());
			}
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	//LDAP Create Processing for student objects
	public void createStudentEntry(DistrictStudentInfo student, int isDuplicate) {
		Connection conn = null;
		int x = isDuplicate;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			log.info("Username: " + UserCredentialsUtil.generateUsername(student.getGivenName(), student.getFamilyName(), student.getBirthDate(), "s", x));
			String entryDn = "uid=" 
					+ UserCredentialsUtil.generateUsername(student.getGivenName(), student.getFamilyName(), student.getBirthDate(), "s", x) 
					+ ",ou=" 
					+ student.getDistrictName() 
					+ ",ou=Users,dc=districts,dc=onefederation,dc=org";
			
			/* 
			 * log.info("Username: " + student.getUsername());
			 * String entryDn = "uid="
			 * 		+ student.getUsername()
			 * 		+ ",ou="
			 * 		+ student.getDistrictName()
			 * 		+ ",ou=Users,dc=districts,dc=onefederation,dc=org";
			 */
			
			//addAttribute("userPassword", student.getPassword()).
			
			//initial attributes defined are 'required' attributes. Below are conditionals to handle attributes that may be returning null values from the API
			Entry entry = new LinkedHashMapEntry(entryDn).
					addAttribute("objectclass", "top").
					addAttribute("objectClass", "fedUser").
					addAttribute("fedRefID", student.getRefId()).
					addAttribute("fedUserType", "S").
					addAttribute("userPassword", UserCredentialsUtil.generateAlgorithmicPassword(student.getGivenName(), student.getFamilyName(), student.getBirthDate())).
					addAttribute("fedGUID", UUID.randomUUID().toString());
			if(student.getLocalId() != ""){
				entry.addAttribute("fedLocalID", student.getLocalId());
			}
			if(student.getStateProvinceId() != ""){
				entry.addAttribute("fedStateID", student.getStateProvinceId());
			}
			if(student.getDistrictId() != ""){
				entry.addAttribute("fedDistrictID", student.getDistrictId());
			}
			conn.add(entry);
		} catch (ErrorResultException ere) {
			//Handle result code 68 (represents duplicate objects)
			if(ere.getResult().getResultCode().intValue() == 68){
				this.createStudentEntry(student, x + 1);
			} else {
				log.error("Error creating ldap entry. Result Code: " + ere.getResult().getResultCode().intValue());
			}
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	// LDAP Update Processing for staff objects.
	public void updateStaffEntry(Entry staffEntry, DistrictStaffInfo staff, int isDuplicate) {
		Connection conn = null;
		int x = isDuplicate;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			//String newEntryRDN = UserCredentialsUtil.generateUsername(staff.getGivenName(), staff.getFamilyName(), staff.getLocalId(),"t", x);
			// Update the Staff Entry entry after making a deep copy.
			Entry oldEntry = TreeMapEntry.deepCopyOfEntry(staffEntry);
			if(staff.getLocalId() != ""){
				staffEntry = staffEntry.replaceAttribute("fedLocalID", staff.getLocalId());
			} else {
				staffEntry = staffEntry.removeAttribute("fedLocalID");
			}
			if(staff.getStateProvinceId() != ""){
				staffEntry = staffEntry.replaceAttribute("fedStateID", staff.getStateProvinceId());
			} else {
				staffEntry = staffEntry.removeAttribute("fedStateID");
			}
			if(staff.getDistrictId() != ""){
				staffEntry = staffEntry.replaceAttribute("fedDistrictID", staff.getDistrictId());
			} else {
				staffEntry = staffEntry.removeAttribute("fedDistrictID");
			}
			if(staff.getEmailAddress() != ""){
				staffEntry = staffEntry.replaceAttribute("mail", staff.getEmailAddress());
			} else {
				staffEntry = staffEntry.removeAttribute("mail");
			}
			// Diff the new and old entities to get the update request.
			ModifyRequest request = Entries.diffEntries(oldEntry, staffEntry);
			List<Modification> updatedEntries = request.getModifications(); 
			if (updatedEntries.size() > 0) {
				log.info("Modifications needed for the given staff entry " + staff.getRefId());
				if (log.isDebugEnabled()) {
					Iterator<Modification> it = updatedEntries.iterator();
					while (it.hasNext()) {
						Modification mod = it.next();
						log.debug("Updated Staff Entry " + mod.getAttribute() + "=" + mod.getModificationType());
					}
				}
				conn.modify(request);
				/*
				if(oldEntry.getName().rdn().toString().equals("uid=" + newEntryRDN)){}{
					log.info("Renaming " + oldEntry.getName().toString() + " to " + newEntryRDN);
					conn.modifyDN(oldEntry.getName().toString(), "uid=" + newEntryRDN);
				}
				*/
			}
		} catch (ErrorResultException ere) {
			if(ere.getResult().getResultCode().intValue() == 68){
				this.updateStaffEntry(staffEntry, staff, x + 1);
			} else {
				log.error("Error updating ldap entry " + ere.getResult().getResultCode().intValue());
			}
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	//LDAP Update Processing for student objects
	public void updateStudentEntry(Entry studentEntry, DistrictStudentInfo student, int isDuplicate) {
		Connection conn = null;
		int x  = isDuplicate;
		try {
			conn = getLdapConnection();
			conn.bind(adminDn, getAdminPwdChars());
			//String newEntryRDN = UserCredentialsUtil.generateUsername(student.getGivenName(), student.getFamilyName(), student.getBirthDate(), "s", x);
			// Update the Student Entry entry after making a deep copy.
			Entry oldEntry = TreeMapEntry.deepCopyOfEntry(studentEntry);

			if(student.getLocalId() != ""){
				studentEntry = studentEntry.replaceAttribute("fedLocalID", student.getLocalId());
			} else {
				studentEntry = studentEntry.removeAttribute("fedLocalID");
			}
			if(student.getStateProvinceId() != ""){
				studentEntry = studentEntry.replaceAttribute("fedStateID", student.getStateProvinceId());
			} else {
				studentEntry = studentEntry.removeAttribute("fedStateID");
			}
			if(student.getDistrictId() != ""){
				studentEntry = studentEntry.replaceAttribute("fedDistrictID", student.getDistrictId());
			} else {
				studentEntry = studentEntry.removeAttribute("fedDistrictID");
			}
			// Diff the new and old entities to get the update request.
			ModifyRequest request = Entries.diffEntries(oldEntry, studentEntry);
			List<Modification> updatedEntries = request.getModifications(); 
			if (updatedEntries.size() > 0) {
				log.info("Modifications needed for the given staff entry " + student.getRefId());
				if (log.isDebugEnabled()) {
					Iterator<Modification> it = updatedEntries.iterator();
					while (it.hasNext()) {
						Modification mod = it.next();
						log.debug("Updated Student Entry " + mod.getAttribute() + "=" + mod.getModificationType());
					}
				}
				conn.modify(request);
			}
			/*
			if(oldEntry.getName().rdn().toString().equals("uid=" + newEntryRDN)){}{
				log.info("Renaming " + oldEntry.getName().toString() + " to " + newEntryRDN);
				conn.modifyDN(oldEntry.getName().toString(), "uid=" + newEntryRDN);
			}
			*/
		} catch (ErrorResultException ere) {
			if(ere.getResult().getResultCode().intValue() == 68){
				this.updateStudentEntry(studentEntry, student, x + 1);
			} else {
				log.error("Error creating ldap entry " + ere.getResult().getResultCode().intValue());
			}
		} finally {
			if (conn != null) conn.close();
		}
	}
}