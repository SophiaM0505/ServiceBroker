package negotiation.Negotiation;

public enum NegState {
	//server negotiation states
	Initiating, // received request from user
	OfferAck, //sent offerAck to user
	Revoking, //received revoke request from user
	RevAccpet, //sent revoke accept to user
	RevDenied, //sent revoke deny to user
	Accept, //sent accept to user
	AcceptAck, //received acceptack from user
	AccessProving, //sent access proving to user
	AccessProved, //sent access proved to user
	AccessDenied, //sent access denied to user
	Terminated, //sent/received terminate to user
	Reject, //sent reject to user
	ManTerminated, //received terminate from manager
	ReqTerminated, //received terminate to user
	ProTerminated, //received terminate from provider
	
	Negotiation,
	Contracted,
	Uncontracted,
	Completed,
	
	Error;
}
