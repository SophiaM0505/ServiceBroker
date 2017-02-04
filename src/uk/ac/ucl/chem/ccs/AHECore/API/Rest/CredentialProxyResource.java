package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

/**
 * Experimental :Upload proxy utility and update credential proxy location automatically
 * POST - upload proxy certificate file and update credential proxy field automatically
 * 
 * @author davidc
 *
 */

public class CredentialProxyResource  extends AHEResource{

	String cred_id;
	
    @Override
    public void doInit() {
    	this.cred_id = (String) getRequestAttributes().get("cred");
    }
    
    @Get
    public String get_Parser(Representation entity){
    	
    
    	return "";
    }
    
    @Post
    public String post_Parser(Representation entity){
    	
    	RestArgMap map = ResourceUtil.getArgumentMap(new Form(entity));
    	
    	try {
			
    		PlatformCredential c = SecurityUserAPI.getPlatformCredential(cred_id);

    		if(c == null){
    			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    	    	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No credential found for : " + cred_id, "/POST /cred/{cred_id}/proxy");
    		}
    	
    		//Check if Credential belongs to this user
    		if(!getUser().getCredentials().contains(c)){
    			//If credential doesn't belong to user, the only way to access this is if the user is an admin
		    	if(!checkAdminAuthorization(getUser())){
		    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("", getUser().getUsername(), "No Authorization to access this resource");
		    	}
    		}
	    	
	    	//Upload file
    		
    		String cred_folder = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getPropertyString(AHEConfigurationProperty.ahe_upload_cred.toString());
        	
    		if(cred_folder == null || cred_folder.isEmpty()){
    			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/Post", "No folder path specified in AHE configuration file for data upload");
    		}
    		
    		try {
    			
				String full_path = uploader(cred_folder, entity);
				
	    		//Update filename at credential location
				c.setProxy_location(full_path);
				HibernateUtil.SaveOrUpdate(c);
				
				return XMLServerMessageAPI.createInformationMessage("Proxy File uploaded, credential updated");
				
			} catch (Exception e) {
	    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getLocalizedMessage(), "/POST /cred/{cred_id}/proxy");

			}
    		

    	
    	
		} catch (AHESecurityException e) {
	    	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getLocalizedMessage(), "/POST /cred/{cred_id}/proxy");

		}
    	
    }
    
    private String uploader(String default_folder, Representation entity) throws Exception{
    	
    	if (entity != null) {

            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
                File storeDirectory = new File(default_folder);

                // storeDirectory = "c:\\temp\\";

                // The Apache FileUpload project parses HTTP requests which
                // conform to RFC 1867, "Form-based File Upload in HTML". That
                // is, if an HTTP request is submitted using the POST method,
                // and with a content type of "multipart/form-data", then
                // FileUpload can parse that request, and get all uploaded files
                // as FileItem.

                // 1/ Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1000240);

                // 2/ Create a new file upload handler based on the Restlet
                // FileUpload extension that will parse Restlet requests and
                // generates FileItems.
                RestletFileUpload upload = new RestletFileUpload(factory);
                List<FileItem> items;

                // 3/ Request is parsed by the handler which generates a
                // list of FileItems
                try {
                    items = upload.parseRequest(getRequest());
                } catch (FileUploadException e1) {
                    e1.printStackTrace();
                    items = new ArrayList<FileItem>();
                }


                for (final Iterator<FileItem> it = items.iterator(); it
                        .hasNext();) {
                    FileItem fi = it.next();
                    // Process only the items that *really* contains an uploaded
                    // file and save them on disk
                    if (fi.getName() != null) {

                        File file = new File(storeDirectory, fi.getName());

                         fi.write(file);
                         return file.getAbsolutePath();

                    } else {
                        // This is a simple text form input.
                        System.out.println(fi.getFieldName() + " "
                                + fi.getString());
                    }
                }

                throw new AHEException("No file found");
            }
        }
    	
        // POST request with no entity.
        return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/POST", "Data upload failed");
    	
    }
    
    protected String ThrowErrorWithHTTPCode(Status status, String cmd, String errormsg){
    	setStatus(status);
    	return ResourceUtil.ThrowErrorMessage(cmd,errormsg,"");
    }

}
