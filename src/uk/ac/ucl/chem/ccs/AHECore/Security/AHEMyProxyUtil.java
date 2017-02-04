package uk.ac.ucl.chem.ccs.AHECore.Security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEMyProxyDelegationException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.ProxyCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Security.ACD.ACDRestClient;
import uk.ac.ucl.chem.ccs.AHECore.Security.ACD.objects.ACDProxy;


/**
 * Contains helper functions that interacts with MyProxy Certificate
 * 
 * Currently, AHE3.0 Will interact with MyProxy Certificate in this manner
 * 1) Check if a user requring MyProxy has a proxy certificate, if yes proceed to 2, if no, generate and use 
 * 2) Check if proxy is valid (i.e. time to live beyond i.e. 6hours etc), if valid use existing certificate, if not, generate a new one and use
 * 
 * @author davidc
 *
 */

public class AHEMyProxyUtil {

	private static Logger cat = LoggerFactory.getLogger(AHEMyProxyUtil.class);
	
	public static void main(String[] arg){
		
		try {
			
//			 FileInputStream fis = new FileInputStream("/home/davidc/.globus/usercert.der");
//			 BufferedInputStream bis = new BufferedInputStream(fis);
//
//			 CertificateFactory cf = CertificateFactory.getInstance("X.509");
//
//			 while (bis.available() > 0) {
//				 
//				 
//				 X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
//			    System.out.println(cert.toString());
//			 }
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			 PEMReader reader = new PEMReader(new FileReader("/home/davidc/.globus/usercert.pem"));
			 Object pemObject = reader.readObject();
			 Certificate cert = (X509Certificate)pemObject;

			 
			 PEMReader reader2 = new PEMReader(new FileReader("/home/davidc/.globus/userkey.pem"),new PasswordFinder() {
			        @Override
			        public char[] getPassword() {
			            return "".toCharArray();
			        }
			    });
			 
		 
			 KeyPair pair = (KeyPair) reader2.readObject();
			 PrivateKey key = pair.getPrivate();
			 
//			 AHEMyProxyTool tool = new AHEMyProxyTool();
//			 tool.setProxyInfo("", "", "", "", 0, 0, "/home/davidc/myproxy/meow.xxx");
//			 
//			 tool.generateCredential(new Certificate[]{cert}, key, 1024, 10000, "/home/davidc/myproxy/meow.xxx");
			 
//			 PrivateKey key = getPrivateKey("/home/davidc/.globus/userkey.pem");
//			 System.out.println(key.toString());
//			 
//			AHEMyProxyUtil.checkUserProxyValid("/tmp/x509up_u15550");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Load PEM certificate
	 * @param path
	 * @return Certificate
	 * @throws IOException
	 */
	
	private static Certificate loadPemCertificate(String path) throws IOException{
		
		 PEMReader reader = new PEMReader(new FileReader(path));
		 Object pemObject = reader.readObject();
		 Certificate cert = (X509Certificate)pemObject;
		 
		 return cert;
		
	}
	
	/**
	 * Load PEM key pair
	 * @param path
	 * @param password
	 * @return
	 * @throws IOException
	 */
	
	private static KeyPair loadPemKeyPair(String path, final String password) throws IOException{
		
		 PEMReader reader2 = new PEMReader(new FileReader(path),new PasswordFinder() {
		        @Override
		        public char[] getPassword() {
		            return password.toCharArray();
		        }
		    });
		 
	 
		 KeyPair pair = (KeyPair) reader2.readObject();
		 
		 return pair;
		
	}
	
	/**
	 * Use ACD to generate proxy, check's if existing proxy already exists and returns a path of a valid User proxy certificate
	 * @return
	 * @throws ProxyCredentialException 
	 */
	
//	public static String getACDUserProxy(AHEUser acd_user, String file_path) throws AHEMyProxyDelegationException, ProxyCredentialException{
//		
//		if(!checkUserProxyValid(file_path)){
//			
//			try{
//			
//			//Get a new proxy
//				ACDProxy proxy_info = null;
//				AHEMyProxyTool ahe_proxy_tool = new AHEMyProxyTool();
//				
//				proxy_info = ACDRestClient.generateProxy(1000, Integer.parseInt(acd_user.getAlt_identifer()), acd_user.getAcd_vo_group());
//
//				if(proxy_info != null){
//					
//					ahe_proxy_tool.setProxyInfo(proxy_info.getSubject_dn(),proxy_info.getPassword(),proxy_info.getUsername(),proxy_info.getUri(),Integer.parseInt(proxy_info.getLifetime()),Integer.parseInt(proxy_info.getPort()), file_path);
//					ahe_proxy_tool.getCredentialInfo();
//
//					return file_path;
//					
//				}else{
//					throw new AHEMyProxyDelegationException("Unable to retrieve MyProxy from ACD");
//				}
//			
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (ResourceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (ACDRestClientException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (SAXException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (ParserConfigurationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			}
//			
//		}
//		
//		return file_path;
//	}
	
//	public static String getAHEUserProxy(AHEUser user, PlatformCredential credential, String file_path) throws AHEMyProxyDelegationException, ProxyCredentialException{
//		
//		if(!checkUserProxyValid(file_path)){
//			
//			try{
//				
//				String proxy_dn;
//				String proxy_uri;
//				String proxy_port;
//				String proxy_user;
//				String proxy_pwd;
//				
//				int bits = 1024;
//				int ttl = 86400;
//
//				Certificate cert = loadPemCertificate(credential.getCredential_location());
//				PrivateKey key = loadPemKeyPair(credential.getUser_key(), credential.getPassword()).getPrivate();
//				 
//				AHEMyProxyTool.generateCredential(new Certificate[]{cert}, key, bits, ttl, file_path);
//
//				return file_path;
//					
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (ResourceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new AHEMyProxyDelegationException(e);
//			}
//			
//		}
//		
//		return file_path;
//	}
	
//	public static boolean checkUserProxyValid(String file_path) throws AHEMyProxyDelegationException{
//		
//		try {
//			//Get file path and see if it is there
//		    File f = new File(file_path);
//		    byte [] data = new byte[(int)f.length()];
//		    FileInputStream in;
//	
//			in = new FileInputStream(f);
//	
//		    // read in the credential data
//		    in.read(data);
//		    in.close();
//	
//		    ExtendedGSSManager manager = (ExtendedGSSManager)ExtendedGSSManager.getInstance();
//		    GSSCredential cred = manager.createCredential(data,ExtendedGSSCredential.IMPEXP_OPAQUE,GSSCredential.DEFAULT_LIFETIME,null,GSSCredential.INITIATE_AND_ACCEPT);
//	
//		    System.out.println(cred.getName());
//		    System.out.println(cred.getRemainingLifetime());
//
//		    if(cred.getRemainingLifetime() > 1800)
//		    	return true;
//		    else
//		    	return false;
//		    
//		} catch (FileNotFoundException e) {
//			cat.warn("User Proxy File Validation : File Not Found", e);
//			return false;
//		} catch (GSSException e) {
//			cat.warn("User Proxy File Validation : GSS Exception", e);
//			return false;
//		} catch (IOException e) {
//			cat.warn("User Proxy File Validation : IOException", e);
//			return false;
//		}
//
//		
//	}
	
	public static String generateNewProxy() throws AHEMyProxyDelegationException{
		
		//Request New Proxy, download it into a local path and return it
		
		return "";
	}
	
	public static void destroyProxy() throws AHEMyProxyDelegationException{
		
	}
	
	/**
	 * Get private key 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	
	public static PrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        return kf.generatePrivate(spec);
    }
	
	
}
