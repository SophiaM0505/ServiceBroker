package uk.ac.ucl.chem.ccs.AHECore.Configuration;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;

/**
 * Configuration file hander 
 * @author davidc
 *
 */

public class AHEConfigFile extends ConfigFiles {

	public static void main(String[] arg){
		AHEConfigFile file = new AHEConfigFile("ahe.2");
	}
	
	
	public AHEConfigFile(String filepath) {
		super(filepath);
	}
	
	public void DefaultData(){
		config.setProperty(AHEConfigurationProperty.j2se_datasource_url.toString(), "jdbc:mysql://localhost:3306/jbpm5db");
		config.setProperty(AHEConfigurationProperty.j2se_datasource_username.toString(), "aheserver");
		config.setProperty(AHEConfigurationProperty.j2se_datasource_password.toString(), "fortccs@2011");
	}

}

