package uk.ac.ucl.chem.ccs.AHEModule.Def;

public enum Arg_def {

	App_Arg{
		
		public String toString(){
			return "app.arg";
		}
		
	},
	
	App_Arg_stdout{
		
		public String toString(){
			return App_Arg.toString() + ".stdout";
		}
		
	},
	
	App_Arg_stderr{
		
		public String toString(){
			return App_Arg.toString() + ".stderr";
		}
		
	},
	
	//For Pre-Processing 
	App_Pre{
		
		public String toString(){
			return "app.pre";
		}
		
	},
	
	App_Pre_Connector{
		
		public String toString(){
			return App_Pre+".connector";
		}
		
	},
	
	App_Pre_Exec{
		
		public String toString(){
			return App_Pre+".exec";
		}
		
	},
	//For Post processing
	App_Post{
		
		public String toString(){
			return "app.post";
		}
		
	},
	
	App_Post_Connector{
		
		public String toString(){
			return App_Post+".connector";
		}
		
	},
	
	App_Post_Exec{
		
		public String toString(){
			return App_Post+".exec";
		}
		
	},
	
	//Resource related arguments
	Resource_Arg{
		
		public String toString(){
			return "resource.arg";
		}
		
	},
	
	Resource_Reservation{
		
		public String toString(){
			return "resource.reservation";
		}
		
	},
	
	//AHE related arguments
	AHE_Arg{
		
		public String toString(){
			return "ahe.arg";
		}
		
	},
	Resource_Env{
		
		public String toString(){
			return "resource.env";
		}
		
	},
	
	Workflow{
		
		public String toString(){
			return "workflow";
		}
		
	},
	
	input{
		
		public String toString(){
			return "input";
		}
		
	},
	
	output{
		
		public String toString(){
			return "ouput";
		}
		
	},
	
	arg{
		
		public String toString(){
			return "arg";
		}
		
	},
	
	jobtype{
		
		public String toString(){
			return "jobtype";
		}
		
	}
	
}

