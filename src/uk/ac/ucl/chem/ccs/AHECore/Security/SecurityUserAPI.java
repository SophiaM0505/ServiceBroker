package uk.ac.ucl.chem.ccs.AHECore.Security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.naming.ldap.LdapName;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;

/**
 * This class provides the general function dealing with AHE users, credentials and the mapping of credential to users and resources
 * @author davidc
 *
 */

public class SecurityUserAPI {

	/**
	 * Create a new AHE User
	 * @param username Username
	 * @param role AHE role (admin or user)
	 * @param password Password
	 * @param email Email
	 * @param alt_ident Alternate Identifier
	 * @param ACD_VO_group ACD VO group, only required for security type = ACD
	 * @param sec_type Security type (AHE_SSL_CLIENT,AHE_PASSWORD,ACD)
	 * @return AHE User entity
	 * @throws AHESecurityException
	 */
	
	/*public static void main(String[] args) {
        System.out.println(getUserList());
    }*/
	
	public static AHEUser createUser(String username, String role,
			String password, String email, String alt_ident,
			String ACD_VO_group, AHE_SECURITY_TYPE sec_type)
			throws AHESecurityException {

		if (username.length() == 0) {
			throw new AHESecurityException("No Username was supplied");
		}

		if (getUser(username) != null) {
			throw new AHESecurityException("Username already exists");
		}

		// if(password.length() == 0){
		// throw new AHESecurityException("No password was supplied");
		// }

		if (role.length() == 0) {
			throw new AHESecurityException("No role was supplied");
		}

		// if(email.length() == 0){
		// throw new AHESecurityException("No email was supplied");
		// }

		byte[] hash = new byte[0];

		if (password.length() > 0 && email.length() > 0) {

			try {
				hash = SecurityUserAPI.getHash(password, email.getBytes());
			} catch (UnsupportedEncodingException e) {
				throw new AHESecurityException(e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				throw new AHESecurityException(e.getMessage());
			}

		}

		AHEUser user = new AHEUser();
		user.setUsername(username);
		user.setRole(role);
		user.setHash_pwd(hash);
		user.setEmail(email);
		user.setTimestamp(new Date());
		user.setActive(true);
		user.setAlt_identifer(alt_ident);
		user.setAcd_vo_group(ACD_VO_group);
		user.setSecurity_type(sec_type.toString());

		HibernateUtil.SaveOrUpdate(user);
        System.out.println(user.getEmail());
		return user;
	}

	/**
	 * Edit an AHE user
	 * @param original_username AHE user to be edited
	 * @param new_username New username
	 * @param new_password New password
	 * @param new_email new  email
	 * @param new_role new role
	 * @param alt_ident new alternate identifier
	 * @param sec_type new Security type
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static AHEUser editUser(String original_username,
			String new_username, String new_password, String new_email,
			String new_role, String alt_ident, AHE_SECURITY_TYPE sec_type)
			throws AHESecurityException {

		AHEUser user = getUser(original_username);

		if (user == null) {
			throw new AHESecurityException("Username not found");
		}

		if (!original_username.equalsIgnoreCase(new_username)) {

			if (getUser(new_username) != null) {
				throw new AHESecurityException("Username already exists");
			}

			user.setUsername(new_username);
		}

		try {
			user.setHash_pwd(getHash(new_password, new_email.getBytes()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		user.setRole(new_role);
		user.setEmail(new_email);
		user.setAlt_identifer(alt_ident);

		if (sec_type != null)
			user.setSecurity_type(sec_type.toString());

		HibernateUtil.SaveOrUpdate(user);

		return user;

	}

	/**
	 * Disable user
	 * @param username user to be disabled
	 * @throws AHESecurityException
	 */
	
	public static void disableUser(String username) throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user == null) {
			throw new AHESecurityException("Username not found");
		}

		user.setActive(false);
		HibernateUtil.SaveOrUpdate(user);
	}
	
	/**
	 * Disable platform credential
	 * @param credential_id credential to be disabled
	 * @throws AHESecurityException
	 */

	public static void disablePlatformCredential(String credential_id)
			throws AHESecurityException {

		PlatformCredential cred = getPlatformCredential(credential_id);

		if (cred == null) {
			throw new AHESecurityException("Credential not found : "
					+ credential_id);
		}

		cred.setActive(false);
		HibernateUtil.SaveOrUpdate(cred);
	}

	/**
	 * Enable user
	 * @param user User to be enabled
	 * @throws AHESecurityException
	 */
	
	public static void enableUser(AHEUser user) throws AHESecurityException {

		user.setActive(true);
		HibernateUtil.SaveOrUpdate(user);

	}
	
	/**
	 * Create an Unicore 6 based Credential
	 * @param credential_id Credential ID name
	 * @param authen_code Authentication type
	 * @param resource_name resource to be attached to, this field can be left blank
	 * @param keystore_location keystore location
	 * @param registery_path Unicore registry path
	 * @param username username
	 * @param password password
	 * @param truststore_path trust store path
	 * @param truststore_password trust store password
	 * @return
	 * @throws AHESecurityException
	 */

	public static PlatformCredential createUniCore6CredentialDetail(
			String credential_id, AuthenCode authen_code, String resource_name,
			String keystore_location, String registery_path, String username,
			String password, String truststore_path, String truststore_password)
			throws AHESecurityException {
		return createCredentialDetail(credential_id, authen_code.toString(),
				resource_name, keystore_location, "", "", "", registery_path,
				username, password, "", truststore_path, truststore_password);
	}

	/**
	 * Create a globus based credential
	 * @param credential_id Credential name
	 * @param authen_code Authentication code
	 * @param resource_name resource to be attached to, this field can be left blank
	 * @param user_cer User certificate
	 * @param proxy_location proxy location
	 * @param user_key user key
	 * @param cert_dir certificate directory
	 * @param password key password (For MyProxy auto generation only). This is optional
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential createGlobusCredentialDetail(
			String credential_id, AuthenCode authen_code, String resource_name,
			String user_cer, String proxy_location, String user_key,
			String cert_dir, String password) throws AHESecurityException {
		return createCredentialDetail(credential_id, authen_code.toString(),
				resource_name, user_cer, proxy_location, user_key, cert_dir,
				"", "", password, "", "", "");
	}
	
	/**
	 * Create an amazon web service based credential
	 * @param credential_id Credential name (.pem file name)
	 * @param authen_code Authentication code (CERTIFICATE)
	 * @param resource_name resource to be attached to, this field can be left blank
	 * @param user_cer User certificate
	 * @param proxy_location proxy location
	 * @param user_key user key
	 * @param cert_dir certificate directory
	 * @param password key password (For MyProxy auto generation only). This is optional
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential createAwsCredentailDetail(
			String credential_id, AuthenCode authen_code, String resource_name,
			String user_cer, String proxy_location, String user_key,
			String cert_dir, String password) throws AHESecurityException {
		return createCredentialDetail(credential_id, authen_code.toString(),
				resource_name, user_cer, "", "", cert_dir,
				"", "", "", "", "", "");
	}

	/**
	 * Create a credential
	 * @param credential_id credential name
	 * @param authen_code Authentication type
	 * @param resource_name resource that this credential will be attached to (optional)
	 * @param credential_location credential location
	 * @param proxy_location proxy location
	 * @param user_key user key
	 * @param cert_dir certificate directory
	 * @param registery_path registry path
	 * @param username username
	 * @param password password
	 * @param credential_alias credential alias
	 * @param truststore_path trust store path
	 * @param truststore_password trust store password
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential createCredentialDetail(
			String credential_id, String authen_code, String resource_name,
			String credential_location, String proxy_location, String user_key,
			String cert_dir, String registery_path, String username,
			String password, String credential_alias, String truststore_path,
			String truststore_password) throws AHESecurityException {

		Resource r = null;
		
		if(resource_name != null){
			if(resource_name.length() > 0){
				r = ResourceRegisterAPI.getResource(resource_name);
		
				if (r == null) {
					throw new AHESecurityException("Resource not found for : "
							+ resource_name);
				}
			}
		}

		return createCredentialDetail(credential_id, authen_code, r,
				credential_location, proxy_location, user_key, cert_dir,
				registery_path, username, password, credential_alias,
				truststore_path, truststore_password);
	}

	/**
	 * Map a credential to a resource
	 * @param credential_id credential name
	 * @param resource_name resource name
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential addCredentialToResource(String credential_id, String resource_name) throws AHESecurityException{
		
		PlatformCredential cred = getPlatformCredential(credential_id);
		
		if(cred == null){
			throw new AHESecurityException("Credentail does not exist for : "
					+ credential_id);
		}
		
		Resource r = ResourceRegisterAPI.getResource(resource_name);
		
		if(r == null){
			throw new AHESecurityException("Resource does not exist for : "
					+ resource_name);
		}
		
		if(!cred.getResource().contains(r)){
			cred.getResource().add(r);
			HibernateUtil.SaveOrUpdate(cred);
		}
		
		return cred;
	}
	
	/**
	 * Checks if a credential is already mapped to a resource
	 * @param credential_id credential name
	 * @param resource_name resource name
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static boolean checkPlatformCredential_Belong_Resource(String credential_id, String resource_name) throws AHESecurityException{
		
		PlatformCredential cred = getPlatformCredential(credential_id);
		
		if(cred == null){
			throw new AHESecurityException("Credentail does not exist for : "
					+ credential_id);
		}
		
		Iterator<Resource> it = cred.getResource().iterator();
		
		while(it.hasNext()){
			
			Resource r = it.next();
			
			if(r.getCommonname().equalsIgnoreCase(resource_name))
				return true;
		}
	
		return false;
	}
	
	/**
	 * Create a credential
	 * @param credential_id credential name
	 * @param authen_code Authentication type
	 * @param resource resource entity that this credential will be attached to (optional)
	 * @param credential_location credential location
	 * @param proxy_location proxy location
	 * @param user_key user key
	 * @param cert_dir certificate directory
	 * @param registery_path registry path
	 * @param username username
	 * @param password password
	 * @param credential_alias credential alias
	 * @param truststore_path trust store path
	 * @param truststore_password trust store password
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential createCredentialDetail(
			String credential_id, String authen_code, Resource resource,
			String credential_location, String proxy_location, String user_key,
			String cert_dir, String registry_path, String username,
			String password, String credential_alias, String truststore_path,
			String truststore_password) throws AHESecurityException {

		PlatformCredential search = getPlatformCredential(credential_id);

		if (search != null) {
			throw new AHESecurityException("Credentail already exists for : "
					+ credential_id);
		}

		PlatformCredential cred = new PlatformCredential();
		cred.setActive(true);
		cred.setTimestamp(new Date());
		cred.setAuthen_type(authen_code);
		
		if(resource != null)
			cred.getResource().add(resource);
		
		cred.setCredential_id(credential_id);
		cred.setCredential_location(credential_location);
		cred.setCertificate_directory(cert_dir);
		cred.setProxy_location(proxy_location);
		cred.setUser_key(user_key);
		cred.setRegistry_path(registry_path);
		cred.setUsername(username);
		cred.setCredential_alias(credential_alias);
		cred.setTruststore_path(truststore_path);
		cred.setTruststore_password(truststore_password);

		// Need to encrypt
		cred.setPassword(password);

		HibernateUtil.SaveOrUpdate(cred);

		return cred;
	}
	
	/**
	 * Edit credentail
	 * @param old_platform_id Credential to be edited
	 * @param new_platform_id
	 * @param credential_location
	 * @param proxy_location
	 * @param user_key
	 * @param cert_dir
	 * @param username
	 * @param password
	 * @param cred_alias
	 * @param truststore
	 * @param trustpass
	 * @param authen_type
	 * @return
	 * @throws AHESecurityException
	 */

	public static PlatformCredential editCredentialDetail(
			String old_platform_id, String new_platform_id,
			String credential_location, String proxy_location, String user_key,
			String cert_dir, String username, String password,
			String cred_alias, String truststore, String trustpass, String authen_type)
			throws AHESecurityException {

		PlatformCredential search = getPlatformCredential(old_platform_id);

		if (search == null) {
			throw new AHESecurityException("Credentail not found for : "
					+ old_platform_id);
		}

		if (!old_platform_id.equalsIgnoreCase(new_platform_id)) {

			PlatformCredential search1 = getPlatformCredential(new_platform_id);

			if (search1 != null) {
				throw new AHESecurityException(
						"Credentail already exists for : " + new_platform_id);
			}

		}

		search.setTimestamp(new Date());
		search.setAuthen_type(authen_type);
		search.setCredential_id(new_platform_id);
		search.setCredential_location(credential_location);
		search.setCertificate_directory(cert_dir);
		search.setProxy_location(proxy_location);
		search.setUser_key(user_key);
		search.setUsername(username);
		search.setPassword(password);
		search.setCredential_alias(cred_alias);
		search.setTruststore_path(truststore);
		search.setTruststore_password(trustpass);

		HibernateUtil.SaveOrUpdate(search);

		return search;
	}
	
	/**
	 * Create a Credential and map that credential to an user
	 * @param user
	 * @param credential_name
	 * @param type
	 * @param resource_commonname
	 * @param certificate_location
	 * @param proxy_location
	 * @param user_dir
	 * @param cert_dir
	 * @param registery_path
	 * @param username
	 * @param password
	 * @param credential_alias
	 * @param truststore_path
	 * @param truststore_password
	 * @return
	 * @throws AHESecurityException
	 */

	public static PlatformCredential addCredentialToUser(AHEUser user,
			String credential_name, AuthenCode type,
			String resource_commonname, String certificate_location,
			String proxy_location, String user_dir, String cert_dir,
			String registery_path, String username, String password,
			String credential_alias, String truststore_path,
			String truststore_password) throws AHESecurityException {

		PlatformCredential cred = createCredentialDetail(credential_name,
				type.toString(), resource_commonname, certificate_location,
				proxy_location, user_dir, cert_dir, registery_path, username,
				password, credential_alias, truststore_path,
				truststore_password);

		user.getCredentials().add(cred);

		HibernateUtil.SaveOrUpdate(cred);
		HibernateUtil.SaveOrUpdate(user);

		return cred;
	}

	/**
	 * Map a credential to an user
	 * @param username User
	 * @param cred_id Credential name
	 * @throws AHESecurityException
	 */
	
	public static void addCredentialToUser(String username, String cred_id)
			throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user == null) {
			throw new AHESecurityException("Username not found : " + username);
		}

		PlatformCredential cred = getPlatformCredential(cred_id);

		if (cred == null) {
			throw new AHESecurityException("Credentail not found for : "
					+ cred_id);
		}

		addCredentialToUser(user, cred);

//		if(!user.getCredentials().contains(cred)){
//			user.getCredentials().add(cred);
//			HibernateUtil.SaveOrUpdate(user);
//		}
		
	}
	
	/**
	 * Unmap a resource from a credential
	 * @param resource_id resource name
	 * @param cred_id credential name
	 * @throws AHESecurityException
	 */

	public static void removeCredentialfromPlatform(String resource_id, String cred_id) throws AHESecurityException{
		
		PlatformCredential cred = getPlatformCredential(cred_id);

		if (cred == null) {
			throw new AHESecurityException("Credentail not found for : "
					+ cred_id);
		}
		
		Resource res = ResourceRegisterAPI.getResource(resource_id);
		
		if(res == null){
			throw new AHESecurityException("Resource not found for : " + resource_id);
		}
		
		removeCredentialfromPlatform(res, cred);
		
	}
	
	/**
	 * Unmap a credential from a resource
	 * @param resource resource entity
	 * @param credential credential entity
	 */
	
	public static void removeCredentialfromPlatform(Resource resource, PlatformCredential credential){
		
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Iterator it = credential.getResource().iterator();

			while (it.hasNext()) {

				if (((Resource) it.next()).getId() == resource.getId()) {
					it.remove();
					break;
				}

			}

			// Can not remove a object that is retrieved outside of a session
			// user.getCredentials().remove(cred);

			session.saveOrUpdate(credential);

			session.flush();
			txn.commit();
			session.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
	
	/**
	 * Unmap a credential from a user 
	 * @param username user
	 * @param cred_id credential name
	 * @throws AHESecurityException
	 */
	
	public static void removeCredentialfromUser(String username,
			String cred_id) throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user == null) {
			throw new AHESecurityException("Username not found : " + username);
		}

		PlatformCredential cred = getPlatformCredential(cred_id);

		if (cred == null) {
			throw new AHESecurityException("Credentail not found for : "
					+ cred_id);
		}

		removeCredentialfromUser(user, cred);

	}
	
	/**
	 * unmap a credential from a user
	 * @param user user entity
	 * @param cred credential entity
	 */

	public static void removeCredentialfromUser(AHEUser user,
			PlatformCredential cred) {

		// user.getCredentials().remove(cred);
		// HibernateUtil.SaveOrUpdate(user);
		// HibernateUtil.SaveOrUpdate(cred);

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Iterator it = user.getCredentials().iterator();

			while (it.hasNext()) {

				if (((PlatformCredential) it.next()).getId() == cred.getId()) {
					it.remove();
					break;
				}

			}

			// Can not remove a object that is retrieved outside of a session
			// user.getCredentials().remove(cred);

			session.saveOrUpdate(user);

			session.flush();
			txn.commit();
			session.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	/**
	 * Map a credential to an user
	 * @param user user entity
	 * @param cred credential entity
	 */

	public static void addCredentialToUser(AHEUser user,
			PlatformCredential cred) {

		if(!user.getCredentials().contains(cred)){
			user.getCredentials().add(cred);
			HibernateUtil.SaveOrUpdate(user);
		}

		// HibernateUtil.SaveOrUpdate(cred);
	}
	
	/**
	 * return an array of Platform credentials that is mapped to the specified resource
	 * @param resource resource entity
	 * @return
	 */

	public static PlatformCredential[] getResource_PlatformCredentialList(Resource resource) {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from PlatformCredential ap inner join ap.resource as res where res.id = :resid AND ap.active = :active");
			query.setParameter("active", true);
			query.setParameter("resid", resource.getId());
			List r = query.list();

			PlatformCredential[] array = (PlatformCredential[]) r.toArray(new PlatformCredential[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return new PlatformCredential[0];

	}
	
	/**
	 * Return all platform credentials
	 * @return
	 */
	
	public static PlatformCredential[] getPlatformCredentialList() {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from PlatformCredential ap where ap.active = :active");
			query.setParameter("active", true);
			List r = query.list();

			PlatformCredential[] array = (PlatformCredential[]) r
					.toArray(new PlatformCredential[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return new PlatformCredential[0];

	}
	
	/**
	 * Return all users
	 * @return
	 */

	public static AHEUser[] getUserList() {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from AHEUser ap where ap.active = :active");
			query.setParameter("active", true);
			List r = query.list();

			AHEUser[] array = (AHEUser[]) r.toArray(new AHEUser[r.size()]);

			for (AHEUser u : array) {
				u.getCredentials().size(); // Force Hibernate to populate the
											// credential array
											// (Lazyinitialization)
			}

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return new AHEUser[0];

	}

	/**
	 * Search for a user by its alternate identifier
	 * @param identifier alternate identifier
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static AHEUser getUserByAltIdent(String identifier)
			throws AHESecurityException {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from AHEUser ap where ap.alt_identifer = :alt_identifer AND ap.active = :active");
			query.setParameter("active", true);
			query.setParameter("alt_identifer", identifier);
			List r = query.list();

			AHEUser[] array = (AHEUser[]) r.toArray(new AHEUser[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			if (array.length > 0)
				return array[0];
			else
				return null;

		} catch (Exception e) {
			throw new AHESecurityException(e.getMessage());
		}

	}
	
	/**
	 * Get user by it's subject dn.
	 * @param subject_dn
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static AHEUser getUserBySubjectDN(String subject_dn) throws AHESecurityException {


		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction txn = session.beginTransaction();

		Query query = session
				.createQuery("select ap from AHEUser ap where ap.security_type = :security_type AND ap.active = :active");
		query.setParameter("active", true);
		query.setParameter("security_type",
				AHE_SECURITY_TYPE.AHE_SSL_CLIENT.toString());
		List r = query.list();

		AHEUser[] array = (AHEUser[]) r.toArray(new AHEUser[r.size()]);

		session.flush();
		txn.commit();

		session.close();

		for (AHEUser user : array) {

			if (compareSubjectDN(user.getAlt_identifer(), subject_dn)) {
				return user;
			}

		}

		throw new AHESecurityException(
				"User Not Found with SSL_CLIENT_AUTHENTICTION with subject_dn of : "
						+ subject_dn);

	}
	
	private static boolean compareSubjectDN(String dn1, String dn2){
		
		try {
			
			LdapName name1 = new LdapName(dn1);
			LdapName name2 = new LdapName(dn2);
			
			return name1.compareTo(name2) == 0;
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
	private static String[] CleanUpArray(String[] dirty){
		
		for(int i=0; i < dirty.length; i++){
			dirty[i] = dirty[i].trim();
			dirty[i] = dirty[i].toLowerCase();
		}
		
		Arrays.sort(dirty);
		
		return dirty;
	}

	/**
	 * Get AHE user by username
	 * @param username Username
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static AHEUser getUser(String username) throws AHESecurityException {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from AHEUser ap where ap.username = :username AND ap.active = :active");
			query.setParameter("active", true);
			query.setParameter("username", username);
			List r = query.list();

			AHEUser[] array = (AHEUser[]) r.toArray(new AHEUser[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			if (array.length > 0)
				return array[0];
			else
				return null;

		} catch (Exception e) {
			throw new AHESecurityException(e.getMessage());
		}

	}

	/*public static void main(String[] arg) throws AHESecurityException {
		System.out.println(getUser("admin"));
	}*/
	// public static PlatformCredential[] getUserPlatformCredential(String
	// username) throws AHESecurityException{
	//
	//
	//
	// try{
	//
	// AHEUser user = getUser(username);
	//
	// Session session = HibernateUtil.getSessionFactory().openSession();
	//
	// PlatformCredential[] r = user.getCredentials().toArray(new
	// PlatformCredential[user.getCredentials().size()]);
	//
	// session.close();
	// //HibernateUtil.getSessionFactory().close();
	//
	// return r;
	//
	// }catch (Exception e) {
	// throw new AHESecurityException(e.getMessage());
	// }
	//
	// }
	
	/**
	 * Get platform credential
	 * @param credential_id credential ID
	 * @return
	 * @throws AHESecurityException
	 */

	public static PlatformCredential getPlatformCredential(String credential_id)
			throws AHESecurityException {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from PlatformCredential ap where ap.credential_id = :id AND ap.active = :active");
			query.setParameter("active", true);
			query.setParameter("id", credential_id);
			List r = query.list();

			PlatformCredential[] array = (PlatformCredential[]) r
					.toArray(new PlatformCredential[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			if (array.length > 0)
				return array[0];
			else
				return null;

		} catch (Exception e) {
			throw new AHESecurityException(e.getMessage());
		}

	}
	
	/**
	 * Get a specific credential mapped to a specific user
	 * @param username username
	 * @param credential_id credential ID
	 * @return Returns a valid credential if a mapping exists between credential and user. Null if not
	 * @throws AHESecurityException
	 */

	public static PlatformCredential getUserCertificateDetail(String username,
			String credential_id) throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user == null) {
			throw new AHESecurityException("user not found");
		}

		PlatformCredential[] array = user.getCredentials().toArray(
				new PlatformCredential[user.getCredentials().size()]);

		for (PlatformCredential p : array) {

			if (p.getCredential_id().equalsIgnoreCase(credential_id))
				return p;

		}

		return null;
	}
	
	/**
	 * Get a specific credential mapped to a specific user
	 * @param user user
	 * @param credential_id credential ID
	 * @return Returns a valid credential if a mapping exists between credential and user. Null if not
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential getUserCertificateDetail(AHEUser user,
			String credential_id) throws AHESecurityException {

		PlatformCredential[] array = user.getCredentials().toArray(
				new PlatformCredential[user.getCredentials().size()]);

		for (PlatformCredential p : array) {

			if (p.getCredential_id().equalsIgnoreCase(credential_id))
				return p;

		}

		return null;
	}

	/**
	 * Return all certificates mapped to this user where the certificates is used for a specific platform interface
	 * @param username
	 * @param platform_interface
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential[] getUserCertificateList(String username,
			String platform_interface) throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user == null) {
			throw new AHESecurityException("user not found");
		}

		PlatformCredential[] array = user.getCredentials().toArray(
				new PlatformCredential[user.getCredentials().size()]);

		ArrayList<PlatformCredential> r = new ArrayList<PlatformCredential>();

		for (PlatformCredential p : array) {

			if (p.getPlatform_interface().equalsIgnoreCase(platform_interface))
				r.add(p);

		}

		return r.toArray(new PlatformCredential[r.size()]);
	}
	
	/**
	 * Return all certificates mapped to this user
	 * @param username
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static PlatformCredential[] getUserCertificateList(String username) throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user == null) {
			throw new AHESecurityException("user not found");
		}

		PlatformCredential[] array = user.getCredentials().toArray(
				new PlatformCredential[user.getCredentials().size()]);

		return array;
	}

	/**
	 * Get a credential that is mapped to both a user and platform
	 * @param user
	 * @param platform
	 * @return
	 * @throws PlatformCredentialException
	 */
	
	public static PlatformCredential getUserPlatformCredential(AHEUser user,
			Resource platform) throws PlatformCredentialException {

		Set<PlatformCredential> set = user.getCredentials();

		Iterator<PlatformCredential> iter = set.iterator();

		while (iter.hasNext()) {

			PlatformCredential cred = iter.next();

			Iterator<Resource> it = cred.getResource().iterator();

			while (it.hasNext()) {

				if (it.next().getId() == platform.getId()) {

					return cred;
				}

			}

		}

		throw new PlatformCredentialException(
				"Unable to find Platform Credential for : "
						+ platform.getCommonname());

	}

	/**
	 * Check if a user has a specific role
	 * @param username
	 * @param expected_role
	 * @return
	 * @throws AHESecurityException
	 */
	
	public static boolean validateUserRole(String username, String expected_role)
			throws AHESecurityException {

		AHEUser user = getUser(username);

		if (user != null) {

			if (user.getRole().equals(expected_role))
				return true;

		}

		return false;

	}

	public static byte[] getHash(String password, byte[] salt)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(salt);
		return digest.digest(password.getBytes("UTF-8"));
	}

	/**
	 * Generate a session token for a user for the given timelimit
	 * @param user User
	 * @param time_limit_ms Time limit by millisecond
	 * @return
	 */
	
	public static String generateSessionToken(AHEUser user, long time_limit_ms){
		
		UUID token = UUID.randomUUID();
		
		Date d1 = new Date();
	    Calendar cl = Calendar. getInstance();
		cl.setTimeInMillis(d1.getTime() + time_limit_ms);
		
		user.setSession_token(token.toString());
		user.setToken_expiry_timestamp(cl.getTime());

		HibernateUtil.SaveOrUpdate(user);
	    
		return token.toString();
		
	}
	
	/**
	 * Check if a session token exists
	 * @param token
	 * @return
	 */
	
	public static boolean checkSessionTokenExist(String token){
		
		AHEUser[] list = getUserList();
		
		for(AHEUser u : list){
			
			if(u.getSession_token() != null){
				
				if(u.getSession_token().equalsIgnoreCase(token))
					return true;
				
			}
			
		}

		return false;
	}
	
	/**
	 * Checks if a user have a valid session token
	 * @param user
	 * @return
	 */
	
	public static boolean checkUserSessionTokenValid(AHEUser user){
		
		Date expiry = user.getToken_expiry_timestamp();
		Date current = new Date();
		
		if(current.getTime() < expiry.getTime()){
			return true;
		}
		
		
		return false;
	}
	
}

