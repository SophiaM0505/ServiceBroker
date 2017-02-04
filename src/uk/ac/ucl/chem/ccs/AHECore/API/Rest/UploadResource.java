package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;

/**
 * Upload a file into the /cred folder
 * @author davidc
 *
 */

public class UploadResource  extends AHEResource{

	String storage;

    @Override
    public void doInit() {

    	this.storage = (String) getRequestAttributes().get("storage");
    }
    
    /**
     * Returns the content of the /cred folder
     * @return
     */
    
    @Get
    public String getHandler(){
    	
		if (!AuthenticateUser()) {
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_UNAUTHORIZED, "/Post", "API not implemented yet");
		}
		
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("", getUser().getUsername(), "No Authorization to access this resource");
    	}
    	
		String cred_folder = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getPropertyString(AHEConfigurationProperty.ahe_upload_cred.toString());

    	File folder = new File(cred_folder);
  
    	if(folder.isDirectory()){
    		
    		File[] files = folder.listFiles();
    		return XMLServerMessageAPI.createFileListMessage("/GET /upload", files);
    		
    	}
    	
    	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/Get", "API not implemented yet");
    }
    
    /**
     * Upload a file into the /cred folder
     * @param entity
     * @return
     */
    
    @Post
    public String postHandler(Representation entity){
    	
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_UNAUTHORIZED, "/Post", "API not implemented yet");
		}

		String cred_folder = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getPropertyString(AHEConfigurationProperty.ahe_upload_cred.toString());
    	
		if(cred_folder == null || cred_folder.isEmpty()){
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/Post", "No folder path specified in AHE configuration file for data upload");
		}

    	return handleUpload(cred_folder, entity);
    }
    
    protected String ThrowErrorWithHTTPCode(Status status, String cmd, String errormsg){
    	setStatus(status);
    	return ResourceUtil.ThrowErrorMessage(cmd,errormsg,"");
    }
    
    private String handleUpload(String default_folder, Representation entity){
    	
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

                // Says if at least one file has been handled
                boolean found = false;
                // List the files that haven't been uploaded
                List<String> oops = new ArrayList<String>();
                // count the number files
                int nbFiles = 0;

                for (final Iterator<FileItem> it = items.iterator(); it
                        .hasNext();) {
                    FileItem fi = it.next();
                    // Process only the items that *really* contains an uploaded
                    // file and save them on disk
                    if (fi.getName() != null) {
                        found = true;
                        nbFiles++;
                        File file = new File(storeDirectory, fi.getName());
                        try {
                            fi.write(file);
                        } catch (Exception e) {
                            System.out.println("Can't write the content of "
                                    + file.getPath() + " due to "
                                    + e.getMessage());
                            oops.add(file.getName() + " due to " + e.getMessage());
                        }
                    } else {
                        // This is a simple text form input.
                        System.out.println(fi.getFieldName() + " "
                                + fi.getString());
                    }
                }

                // Once handled, you can send a report to the user.
                StringBuilder sb = new StringBuilder();
                if (found) {
                    sb.append(nbFiles);
                    if (nbFiles > 1) {
                        sb.append(" files sent");
                    } else {
                        sb.append(" file sent");
                    }
                    if (!oops.isEmpty()) {
                        sb.append(", ").append(oops.size());
                        if (oops.size() > 1) {
                            sb.append(" files in error:");
                        } else {
                            sb.append(" file in error:");
                        }
                        for (int i = 0; i < oops.size(); i++) {
                            if (i > 0) {
                                sb.append(",");
                            }
                            sb.append(" \"").append(oops.get(i)).append("\"");
                        }
                    }
                    sb.append(".");
                    if(oops.isEmpty())
                    	return XMLServerMessageAPI.createInformationMessage(sb.toString());
                    else
                    	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/Post", sb.toString());
                } 
                
                // Some problem occurs, sent back a simple line of text.
            	 return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/POST", "Data upload failed");
                
            }
        }
    	
        // POST request with no entity.
        return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "/POST", "Data upload failed");
    	
    }

}