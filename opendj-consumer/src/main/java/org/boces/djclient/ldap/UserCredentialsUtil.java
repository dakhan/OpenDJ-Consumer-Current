package org.boces.djclient.ldap;

public class UserCredentialsUtil {
	
	public static String generateAlgorithmicPassword(String givenName, String familyName){
		String password = givenName.substring(0, 2) + familyName;
		return password;
	}
	
	public static String generateAlgorithmicPassword(String givenName, String familyName, String birthDate){
		String password = givenName.substring(0, 2) + familyName + birthDate.substring(0, 4);
		return password;
	}
	
	//Generate username method. Uses a 5 character + 2 number string.
	public static String generateUsername(String givenName, String familyName, String number, String role, int isDuplicate){
		String characters = " abcdefghijklmnopqrstuvwxyz";
		//break out parts of the name that will be used by username generation
		//first name parts
		String givenName_f2 = givenName.substring(0, 2);
		String givenName_f1 = givenName.substring(0, 1);
		String givenName_l1 = givenName.substring(givenName.length() - 1,givenName.length());
		
		//last name parts
		String familyName_f1 = familyName.substring(0, 1);
		String familyName_l1 = familyName.substring(familyName.length() - 1,familyName.length());
		
		//get 2 characters from end of birthDate (represents day)

		String userName = "";
		
		String givenName_build = givenName_f2 + givenName_l1;
		String familyName_build = familyName_f1 + familyName_l1;
		
		//handle 3 char first names
		if(givenName.length()<4){
			givenName_build = givenName_f2;
		}
		//handle 2 char first names
		if(givenName.length()<3){
			givenName_build = givenName_f1;
		}
		//handle 2 char last names
		if(familyName.length()<3){
			familyName_build = familyName_f1; 
		}
		userName += givenName_build + familyName_build;
		//add number chars to username
		if (role == "t"){
			//take last two digits of the string passed
			//String numberSub = number.substring(number.length() - 2, number.length());
			//userName = userName + numberSub;
		} else if (role == "s"){
			String birthDateDay = number.substring(number.length() - 2, number.length());
			userName = userName + birthDateDay;
		}
		//duplication handling
		if (isDuplicate > 0){
			if(role == "s"){
				userName = userName + characters.charAt(isDuplicate);
			} else {
				userName = userName + isDuplicate;
			}
			
		}
		return userName.toLowerCase();
	}
}
