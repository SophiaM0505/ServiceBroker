package negotiation.Contract;

public class ContractContents {
	
static String glo_service;
	
	public static String addGeneral(String user, String owner, String provider, String start, String end){
	    String contents = "This agreement is made between " + user  +
	    		" represented by " + owner + " and " + provider + ", " +
	    		"to cover the provision and support of the service as described hereafter. " +
	    		"This SLA is valid from " + start + " to " + end + ".";
	    
	    return contents;

	  }

	  public static String addDescription(String name, String ref, String description){

		  String beginning = "This SLA applies to the following service: \n";
		  String s_name = "Name: " + name + "\n";
		  String s_ref = "Service reference: " + ref + "\n";
		  String s_des = "Service description: " + description + "\n";
		  
		  return beginning + s_name + s_ref + s_des;
		  
	  }
	  
	  public static String addTime(String start, String end){
		  String beginning = "This service operates during the following hours: \n";
		  String s_start = "Start: " + start + "\n";
		  String s_end = "End: " + end + "\n";	  
		  String duration = String.valueOf(Long.parseLong(end) - Long.parseLong(start));
		  String s_dur = "The duration of service lasts: " + duration + "\n";
		  return beginning + s_start + s_end + s_dur;
	  }
	  
	  public static String addComponents(String comp, String description){
		  // as it mentions component dependency, can use wf lang here
		  String beginning = "The service covered by this SLA is made up of the following " +
		  		"(technical and logical) service components: \n";
		  String s_comp = "Component: " + comp + "\n";
		  String s_desc = "Decription: " + description + "\n";
		  return beginning + s_comp + s_desc;
	  }
	  
	  public static String addSupport(String contact, String time){
		  String beginning = "The service covered by the scope of this SLA are provided with" +
		  		"the following level of support: \n";
		  String s_contact = "Support contact: " + contact + "\n";
		  String s_time = "Support available time: " + time + "\n";
		  return beginning + s_contact + s_time;
	  }
	  
	  public static String addHandling(String guidelines){
		  String beginning = "Disruptions to the agreed service funtionality or quality will be " +
		  		"handlled according to an appropriate priority based on the impact and urgency of " +
		  		"the incident. In this context, the following priority guidelines apply: \n";
		  String ending = "Response and resolution times are provided as service level targets.";
		  return beginning + guidelines + "\n" + ending;
	  }
	  
	  public static String addFulfillment(String requests){
		  String beginning = "In addition to resolving incidents, the following standard service " +
		  		"requests are defined and will be fulfilled through the defined support channels: \n";
		  String ending = "Response and resolution times are provided as service level targets.";
		  return beginning + requests + "\n" + ending;
		  
	  }
	  
	  public static String addTargets(String paras, String targets){
		  String beginning = "The following are the agreed service level targets for " + glo_service + "\n";
		  String s_paras = "Overall service " + paras + "\n";
		  String s_targets = "Overall service" + targets + "\n";
		  return beginning + s_paras + s_targets;
	  }
	  
	  public static String addConstaints(String limits){
		  String beginning = "The provisioning of the service under the agreed service level targets " +
		  		"is subject to the following limitations and constraints: \n";
		  String s_limit = "Limits: " + limits;
		  return beginning + s_limit;
	  }
	  
	  public static String addContacts(String customer, String provider, String user){
		  String beginning = "The following contacts will be generally used for communications related " +
		  		"to the service in the scope of this SLA: \n";
		  String s_provider = "Customer contact for the service provider: " + provider + "\n";
		  String s_customer = "Service provider contact for the customer: " + customer + "\n";
		  String s_user = "Service provider contact for the service user: " + user + "\n";
		  return beginning + s_provider + s_customer + s_user;
		  
	  }
	  
	  public static String addReport(String title, String contents, String freq, String delivery){
		  String beginning = "As part of the fulfilment of this SLA and provisioning of the service, the " +
		  		"following reports will be provided:  \n";
		  String s_title = "Title: " + title + "\n";
		  String s_contents = "Contents: " + contents + "\n";
		  String s_freq = "Frequency: " + freq + "\n";
		  String s_delivery = "Delivery: " + delivery + "\n";
		  return beginning + s_title + s_contents + s_freq + s_delivery;
	  }
	  
	  public static String addViolations(String rules){
		  String beginning = "The service provider commits to inform the customer, if this SLA is violated " +
		  		"or violation is anticipated. The following rules are agreed for communication in the event " +
		  		"of SLA violation: \n";
		  String s_rules = "Rules: " + rules;
		  return beginning + s_rules;
		  
	  }
	  
	  public static String addEscalation(String rules){
		  String beginning = "For escalation and complaints, the defined service provider contact point shall " +
		  		"be used, and the following rules apply: \n";
		  String s_rules = "Rules: " + rules;
		  return beginning + s_rules;
	  }
	  
	  public static String addProtection(String rules){
		  String beginning = "The following rules for information security and data protection apply: \n";
		  String s_rules = "Rules: " + rules;
		  return beginning + s_rules;
	  }
	  
	  public static String addProviderResp(String resp){
		  String s_resp = "Provider additional responsibility: " + resp + "\n";
		  return s_resp;
	  }
	  
	  public static String addCustomerResp(String resp){
		  String s_resp = "Customer responsibility: " + resp + "\n";
		  return s_resp;
	  }
	  
	  public static String addReview(String review){
		  String beginning = "There will be reviews of the service performance against service level " +
		  		"targets and of this SLA at planned intervals with the customer according to the " +
		  		"following rules: \n";
		  return beginning + review;
	  }
	  
	  public static String addTerms(String terms, String def){
		  String beginning = "For the purpose of this SLA, the following terms and definitions apply: \n";
		  String s_terms = "Term: " + terms + "\n";
		  String s_def = "Definition: " + def + "\n";
		  return beginning + s_terms + s_def;
	  }
	  
	  public static String addControl(long id, String title, String location, String owner, String ver, String date_change, String date_next, String log){
		  String s_id = "Document ID: " + id + "\n";
		  String s_title = "Document title: " + title + "\n";
		  String s_loc = "Definitive storage location: " + location + "\n";
		  String s_owner = "Document owner: " + owner + "\n";
		  String s_version = "Version: " + ver + "\n";
		  String s_date_change = "Last date of change: " + date_change + "\n";
		  String s_date_next = "Next review due date: " + date_next + "\n";
		  String s_log = "Version & change tracking: " + log + "\n";
		  return s_id + s_title + s_loc + s_owner + s_version + s_date_change + s_date_next + s_log;
	  }

}
