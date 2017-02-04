package uk.ac.ucl.chem.ccs.AHEModule.Def;

public enum AHECommands {

	//AHE Admin commands (Proxy commands for implementing functionalities for now, since there are no user control avaliable yet)
	
	ServerStatus, //Get
	ListJobs, //Get
	
	
	AddUser, //Post
	EditUser, //Post
	ListUsers, //Get
	DeleteUser, //Delete
	AddCredential, //Post
	ListCredentials, //Get
	EditCredential, //Post
	DeleteCredential, //Delete
	AddUserCredential, //Post
	DeleteUserCredential, //Delete
	
	//Normal AHE Commands
	
	Status, //Get - AppInstance, Platform
	SetDataStaging, //Post -- Redundant
	
	setStageIn, //Post
	setStageOut, //Post

	deleteStageIn,
	deleteStageOut,
	deleteDataStaging,
	
	getDataStaging,
	getStageIn, //Get
	getStageOut, //Get
	
	Clone, // Post <-- Clone a resource into a beginning state but retain all the configuration setup
	Prepare, //Post
	monitor, //Get
	getRegisteryEPR, //Get
	Start, //Post
	Terminate, //Delete
	SetProperty, //Post - prop=key, value=blah
	GetProperty, //Get - prop=key,value=blah
	deleteProperty,
	ListProperties, //Get
	
	CreateResource,//Post
	EditResource,
	ListResource,// Get
	DeleteResource,
	
	CreateApp,// Post
	EditApp,
	listApp, // Get
	DeleteApp,
	
	transfer,
	
	EditAppArg, //Post- Expert User Only
	DeleteAppArg, //Delete - Expert user Only
	EditAppInstArg,//Post
	DeleteAppInstArg,//Delete
	CreateProperty, //Post - Can be used to set properties that for custom workflows
	ListWorkflows,
	SetWorkflows;
	
}

