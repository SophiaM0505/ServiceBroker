package negotiation.Negotiation;

public enum NegState {
	//server negotiation states
	initiating, // received request from user
	OfferAck, //sent offerAck to user
	Revoking, //received revoke request from user
	RevAccpet, //sent revoke accept to user
	RevDenied, //sent revoke deny to user
	Accept, //sent accept to user
	AcceptAck, //received acceptack from user
	AccessProving, //sent access proving to user
	AccessProved, //sent access proved to user
	AccessDenied, //sent access denied to user
	terminated, //sent/received terminate to user
	Reject, //sent reject to user
	ManTerminated, //received terminate from manager
	reqTerminated, //received terminate to user
	proTerminated, //received terminate from provider
	
	negotiating,
	contracted,
	uncontracted,
	completed,
	
	Error;
}
