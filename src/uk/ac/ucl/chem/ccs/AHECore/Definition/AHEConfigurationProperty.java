package uk.ac.ucl.chem.ccs.AHECore.Definition;

public enum AHEConfigurationProperty {
	
	ACD_id, // an identifier for the ACD security server
	ACD_uri, // uri for ACD server
	ACD_ssl_certificate,
	
	ahe_upload_cred{
		
		public String toString(){
			return "ahe.upload.cred";
		}
		
	},
	
	module_jobservice{
		
		public String toString(){
			return "module.job";
		}
		
	},
	
	module_jobservice_globus{
		
		public String toString(){
			return module_jobservice.toString() + ".globus";
		}
		
	},
	
	module_jobservice_ucc{
		
		public String toString(){
			return module_jobservice.toString() + ".ucc";
		}
		
	},
	
	module_jobservice_uccbes{
		
		public String toString(){
			return module_jobservice.toString() + ".uccbes";
		}
		
	},
	
    module_jobservice_aws{
		
		public String toString(){
			return module_jobservice.toString() + ".aws";
			//return module_jobservice.toString() + ".aws";
		}
		
	},
	
    module_jobservice_redqueen{
		
		public String toString(){
			return module_jobservice.toString() + ".redqueen";
			//return module_jobservice.toString() + ".aws";
		}
		
	},
	
	module_jobservice_qcg{
		
		public String toString(){
			return module_jobservice.toString() + ".qcg";
		}
		
	},
	
	module_transferservice{
		
		public String toString(){
			return "module.transfer";
		}
		
	},
	
	module_transferservice_gsiftp{
		
		public String toString(){
			return module_transferservice.toString() + ".gsiftp";
		}
		
	},
	
	module_transferservice_webdav{
		
		public String toString(){
			return module_transferservice.toString() + ".webdav";
		}
		
	},
	
	module_security_myproxy{
		
		public String toString(){
			return "module.security.myproxy";
		}
		
	},
	
	module_username{
		
		
		public String toString(){
			return "module.username";
		}
		
	},
	
	module_password{
		
		
		public String toString(){
			return "module.password";
		}
		
	},
	
	SSH_knownhosts,
	ahe_tmp{
		
		public String toString(){
			return "ahe.tmp";
		}
		
	},
	runtime_root, //Runtime root folder, if not specified, AHE will use the runtime execution path, this is used to store the other config folders
	
	
	j2se_hibernate_config,
	j2se_datasource_url,
	j2se_datasource_username,
	j2se_datasource_password,
	logging, //true - false
	ahe_hibernateconfig_path,
	ahe_configfile_path,
	log_folder,
	data_folder,
	cred_folder,
	config_folder,
	workflow_folder,
	ssl, //true - false
	ssl_keystore_path,
	client_authentication, // true-false
	jbpm_persistence; //true - false
	
}

