package negotiation.Contract.AwsContract;

import negotiation.Contract.ContractContents;

public class AwsEc2ContractContents extends ContractContents{
	private static String ec2_para = "Monthly Uptime Percentage";
	private static String ec2_target = "99.95%"; 
	private static String glo_service = "AWS EC2";
	
	public static String addTargets(){
		//paras = ec2_para;
		//targets = ec2_target;
		
		String beginning = "The following are the agreed service level targets for " + glo_service + "\n";
		String s_paras = "Overall service " + ec2_para + "\n";
		String s_targets = "Overall service" + ec2_target + "\n";
		return beginning + s_paras + s_targets;
	}
	
	public static String addTerms(){
		/*HashMap<String, String> terms = null;
		terms.put("Monthly Uptime Percentage", "calculated by subtracting from 100% the percentage of minutes " +
				"during the month in which Amazon EC2 or Amazon EBS, as applicable, was in the state of “Region Unavailable.” " +
				"Monthly Uptime Percentage measurements exclude downtime resulting directly or indirectly from any Amazon EC2 " +
				"SLA Exclusion (defined below).");
		terms.put("Region Unavailable/Region Unavaliability", "more than one Availability Zone in which you are running an " +
				"instance, within the same Region, is “Unavailable” to you");
		terms.put("Unavailable/Unavailiability", "For Amazon EC2, when all of your running instances have no external connectivity." +
				"For Amazon EBS, when all of your attached volumes perform zero read write IO, with pending IO in the queue.");
		terms.put("Service Credit", "a dollar credit, calculated as set forth below, that we may credit back to an eligible account.");*/
		String line1 = "Monthly Uptime Percentag: calculated by subtracting from 100% the percentage of minutes " +
				"during the month in which Amazon EC2 or Amazon EBS, as applicable, was in the state of “Region Unavailable.” " +
				"Monthly Uptime Percentage measurements exclude downtime resulting directly or indirectly from any Amazon EC2 " +
				"SLA Exclusion (defined below).";
		String line2 = "Region Unavailable/Region Unavaliability: more than one Availability Zone in which you are running an " +
			"instance, within the same Region, is “Unavailable” to you";
		String line3 = "Unavailable/Unavailiability: For Amazon EC2, when all of your running instances have no external connectivity." +
			"For Amazon EBS, when all of your attached volumes perform zero read write IO, with pending IO in the queue.";
		String line4 = "Service Credit: a dollar credit, calculated as set forth below, that we may credit back to an eligible account.";
		String output = line1 + line2 + line3 + line4;
		/*do{
			output.concat(terms.keySet().iterator().next())
		}
		while(terms.keySet().iterator().hasNext());*/
		return output;
	}
	
	public static String addViolations(){
		//HashMap<String, String> rules = null;
		  String beginning = "The service provider commits to inform the customer, if this SLA is violated " +
		  		"or violation is anticipated. The following rules are agreed for communication in the event " +
		  		"of SLA violation: \n";
		  final String level1 = "Less than 99.95% but equal to or greater than 99.0%";
		  final String value1 = "10%";
		  final String level2 = "Less than 99.0%";
		  final String value2 = "30%";
		  //rules.put(level1, "10%");
		  //rules.put(level2, "30%");
		  /*String description = "Service Credits are calculated as a percentage of the total charges paid by you (excluding one-time payments" +
		  		" such as upfront payments made for Reserved Instances) for either Amazon EC2 or Amazon EBS (whichever was Unavailable, " +
		  		"or both if both were Unavailable) in the Region affected for the monthly billing cycle in which the Region " +
		  		"Unavailability occurred in accordance with the schedule below. \n" +
		  		"When Monthly Uptime Percentage is " + rules.keySet().toArray()[0] + ", Service Credit Percentage is " + rules.get(level1) +
		  		"; When Monthly Uptime Percentage is " + rules.keySet().toArray()[1] + ", Service Credit Percentage is " + rules.get(level2) +
		  		"We will apply any Service Credits only against future Amazon EC2 or Amazon EBS payments otherwise due from you. At our " +
		  		"discretion, we may issue the Service Credit to the credit card you used to pay for the billing cycle in which the " +
		  		"Unavailability occurred. Service Credits will not entitle you to any refund or other payment from AWS. A Service Credit " +
		  		"will be applicable and issued only if the credit amount for the applicable monthly billing cycle is greater than one " +
		  		"dollar ($1 USD). Service Credits may not be transferred or applied to any other account. Unless otherwise provided in the " +
		  		"AWS Agreement, your sole and exclusive remedy for any unavailability, non-performance, or other failure by us to provide " +
		  		"Amazon EC2 or Amazon EBS is the receipt of a Service Credit (if eligible) in accordance with the terms of this SLA.";*/
		  String description = "Service Credits are calculated as a percentage of the total charges paid by you (excluding one-time payments" +
			  		" such as upfront payments made for Reserved Instances) for either Amazon EC2 or Amazon EBS (whichever was Unavailable, " +
			  		"or both if both were Unavailable) in the Region affected for the monthly billing cycle in which the Region " +
			  		"Unavailability occurred in accordance with the schedule below. \n" +
			  		"When Monthly Uptime Percentage is " + level1 + ", Service Credit Percentage is " + value1 +
			  		"; When Monthly Uptime Percentage is " + level2 + ", Service Credit Percentage is " + value2 +
			  		"We will apply any Service Credits only against future Amazon EC2 or Amazon EBS payments otherwise due from you. At our " +
			  		"discretion, we may issue the Service Credit to the credit card you used to pay for the billing cycle in which the " +
			  		"Unavailability occurred. Service Credits will not entitle you to any refund or other payment from AWS. A Service Credit " +
			  		"will be applicable and issued only if the credit amount for the applicable monthly billing cycle is greater than one " +
			  		"dollar ($1 USD). Service Credits may not be transferred or applied to any other account. Unless otherwise provided in the " +
			  		"AWS Agreement, your sole and exclusive remedy for any unavailability, non-performance, or other failure by us to provide " +
			  		"Amazon EC2 or Amazon EBS is the receipt of a Service Credit (if eligible) in accordance with the terms of this SLA.";
		  String procedures = "To receive a Service Credit, you must submit a claim by opening a case in the AWS Support Center. To be eligible," +
		  		" the credit request must be received by us by the end of the second billing cycle after which the incident occurred and must include: \n" +
		  		"1. the words “SLA Credit Request” in the subject line; \n" +
		  		"2. the dates and times of each Unavailability incident that you are claiming; \n" +
		  		"3. the affected EC2 instance IDs or the affected EBS volume IDs; and \n" +
		  		"4. your request logs that document the errors and corroborate your claimed outage (any confidential or sensitive " +
		  		"information in these logs should be removed or replaced with asterisks). \n" +
		  		"If the Monthly Uptime Percentage of such request is confirmed by us and is less than the Service Commitment, then we will issue " +
		  		"the Service Credit to you within one billing cycle following the month in which your request is confirmed by us. Your failure to " +
		  		"provide the request and other information as required above will disqualify you from receiving a Service Credit.";

		  //String s_rules = "Rules: " + rules;
		  return beginning + description + procedures;
		  
	  }
	
	public static String addConstaints(){
		String limits = "The Service Commitment does not apply to any unavailability, suspension or termination of Amazon EC2 or Amazon EBS, or any other " +
				"Amazon EC2 or Amazon EBS performance issues: (i) that result from a suspension described in Section 6.1 of the AWS Agreement; (ii) " +
				"caused by factors outside of our reasonable control, including any force majeure event or Internet access or related problems beyond " +
				"the demarcation point of Amazon EC2 or Amazon EBS; (iii) that result from any actions or inactions of you or any third party, " +
				"including failure to acknowledge a recovery volume; (iv) that result from your equipment, software or other technology and/or " +
				"third party equipment, software or other technology (other than third party equipment within our direct control); (v) that result " +
				"from failures of individual instances or volumes not attributable to Region Unavailability; (vi) that result from any maintenance as " +
				"provided for pursuant to the AWS Agreement; or (vii) arising from our suspension and termination of your right to use Amazon EC2 or " +
				"Amazon EBS in accordance with the AWS Agreement (collectively, the “Amazon EC2 SLA Exclusions”). If availability is impacted by " +
				"factors other than those used in our Monthly Uptime Percentage calculation, then we may issue a Service Credit considering such factors " +
				"at our discretion.";
		return limits;
	}
	
	public static String getAwsContract(){
		String newLine = System.getProperty("line.separator");
		return addTargets() + newLine + addTerms() + newLine + newLine + addViolations() + newLine
				+ newLine + addConstaints();
	}
	
	/*public static void main(String[] args){
		System.out.println(getAwsContract());
	}*/
}
