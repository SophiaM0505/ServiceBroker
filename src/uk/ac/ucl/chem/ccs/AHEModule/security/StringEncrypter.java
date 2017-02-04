package uk.ac.ucl.chem.ccs.AHEModule.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

import uk.ac.ucl.chem.ccs.AHEModule.exception.AHEEncryptionException;


/**
 * This is a simple utility class meant to encrypt sensitive string data for the database. The sole purpose is not to expose
 * the data in a clear text format.
 * 
 * A proper private key encryption will be implemented later
 * 
 * @author davidc
 *
 */

public class StringEncrypter {

	public static void main(String[] arg){
		
		StringEncrypter e;
		try {
			e = new StringEncrypter();
			
			String encrypt = e.encrypt("s");
			System.out.println(encrypt);
			
			System.out.println(e.decrypt("uO0fP3kk8w0eHkY52Xrmk0VnUInNg1rG"));
			
		} catch (AHEEncryptionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	public static final String DEFAULT_ENCRYPTION_KEY	= "zqFSU5ORO54qe5vaU8pFqvg1wHi7p8";
	
	private KeySpec				keySpec;
	private SecretKeyFactory	keyFactory;
	private Cipher				cipher;
	
	private static final String	UNICODE_FORMAT = "UTF8";

	private static StringEncrypter encryptor;
	
	public static StringEncrypter getStringEncryptor() throws AHEEncryptionException{
		
		if(encryptor == null){
			encryptor = new StringEncrypter();
		}
		
		return encryptor;
		
	}
	
	public static String encryptStr(String data) throws AHEEncryptionException{
		return StringEncrypter.getStringEncryptor().encrypt(data);
	}
	
	public static String decryptStr(String data) throws AHEEncryptionException{
		return StringEncrypter.getStringEncryptor().decrypt(data);
	}
	
	private StringEncrypter() throws AHEEncryptionException
	{
		this(DES_ENCRYPTION_SCHEME, DEFAULT_ENCRYPTION_KEY );
	}

	private StringEncrypter( String encryptionScheme, String encryptionKey )
			throws AHEEncryptionException
	{

		if ( encryptionKey == null )
				throw new IllegalArgumentException( "encryption key was null" );
		if ( encryptionKey.trim().length() < 24 )
				throw new IllegalArgumentException(
						"encryption key was less than 24 characters" );

		try
		{
			byte[] keyAsBytes = encryptionKey.getBytes( UNICODE_FORMAT );

			if ( encryptionScheme.equals( DESEDE_ENCRYPTION_SCHEME) )
			{
				keySpec = new DESedeKeySpec( keyAsBytes );
			}
			else if ( encryptionScheme.equals( DES_ENCRYPTION_SCHEME ) )
			{
				keySpec = new DESKeySpec( keyAsBytes );
			}
			else
			{
				throw new IllegalArgumentException( "Encryption scheme not supported: "
													+ encryptionScheme );
			}

			keyFactory = SecretKeyFactory.getInstance( encryptionScheme );
			cipher = Cipher.getInstance( encryptionScheme );

		}
		catch (InvalidKeyException e)
		{
			throw new AHEEncryptionException( e );
		}
		catch (UnsupportedEncodingException e)
		{
			throw new AHEEncryptionException( e );
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new AHEEncryptionException( e );
		}
		catch (NoSuchPaddingException e)
		{
			throw new AHEEncryptionException( e );
		}

	}

	public String encrypt( String unencryptedString ) throws AHEEncryptionException
	{
		if ( unencryptedString == null || unencryptedString.trim().length() == 0 )
				throw new IllegalArgumentException(
						"unencrypted string was null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.ENCRYPT_MODE, key );
			byte[] cleartext = unencryptedString.getBytes( UNICODE_FORMAT );
			byte[] ciphertext = cipher.doFinal( cleartext );

			return Base64.encodeBase64String(ciphertext);
		}
		catch (Exception e)
		{
			throw new AHEEncryptionException( e );
		}
	}

	public String decrypt( String encryptedString ) throws AHEEncryptionException
	{
		if ( encryptedString == null || encryptedString.trim().length() <= 0 )
				throw new IllegalArgumentException( "encrypted string was null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.DECRYPT_MODE, key );
			byte[] cleartext = Base64.decodeBase64(encryptedString.getBytes());
			byte[] ciphertext = cipher.doFinal( cleartext );

			return bytes2String( ciphertext );
		}
		catch (Exception e)
		{
			throw new AHEEncryptionException( e );
		}
	}

	private static String bytes2String( byte[] bytes )
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++)
		{
			stringBuffer.append( (char) bytes[i] );
		}
		return stringBuffer.toString();
	}

}
