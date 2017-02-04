package uk.ac.ucl.chem.ccs.AHECore.Definition;

public enum AppInstanceStates {

	Initalising,
	Wait_UserCmd,
	Workflow_Running,
	
	Submit_Completed,
	
	Submit_Job,
	Data_Staging,
	Data_Stageout,
	
	Polling_CheckResource,
	Polling_WaitState,
	Finished,
	
	Suspended_Wait_UserCmd,
	Suspended_Polling,
	
	Error_Initialising,
	Error_StageIn,
	Error_StageOut,
	
	Error_DataStaging,
	Error_Credential_Setup,
	Error_Credential_Setup_UserWait,
	Error_Credential_Dispose,
	Error_Credential_Dispose_UserWait,
	
	Error_DataStaging_UserWait, //Recoverable, waiting for user decision
	

	Error_Wait_UserCmd,
	Error_Job_Submission,
	
	Error_StageIn_UserWait,
	Error_StageOut_UserWait,
	Error_Job_Submission_UserWait, //Recoverable, waiting for user decision
	
	Error_Polling,
	Error_Polling_UserWait,
	
	Error_ResultStaged,
	Error_ResultStaged_UserWait;
	
}
