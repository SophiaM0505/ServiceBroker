package uk.ac.ucl.chem.ccs.AHEModule.Def;

public enum rest_arg {
	
	username,
	pwd,
	role,
	email,
	identifier,
	
	credential_id, credential_location, proxy_location, user_key, cert_dir, registery,
	
	name,
	resource_interface,
	type,
	cpucount,
	memory,
	vmemory,
	walltimelimit,
	arch,
	ip,
	opsys,
	port,
	endpoint_reference,
	
	workflow_name,
	
	
	env, //environement argument
	work_dir, //work directory
	
	user,
	appinst,
	cmd,
	arg,
	app_arg{
		
		public String toString(){
			return Arg_def.App_Arg.toString();
		}
		
	},
	appname,
	value,
	jobname,
	stagein,
	stageout,
	resource_name,
	exec,
	security_type,
	alias,
	acd_vo,
	
	transfer,
	
	source,
	target,
	
	authen_type,
	time,
	description,
	trust_path,
	trust_pwd,
	
	credential_filename
	
	
	
	;

}

