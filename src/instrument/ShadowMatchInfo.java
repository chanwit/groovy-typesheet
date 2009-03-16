package instrument;

public class ShadowMatchInfo {

    public enum CallKind {CURRENT, STATIC, CONSTRUCTOR};

    private String messageName;
    private CallKind kind = null;
    private String desc;

    public ShadowMatchInfo(String callsiteName, String callName, String desc) {
        this.messageName = callsiteName;
        if(callName.equals("callCurrent")) {
            kind = CallKind.CURRENT;
        } /* todo */
        this.desc = desc; // to determine a number of argument
    }

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public CallKind getKind() {
		return kind;
	}

	public void setKind(CallKind kind) {
		this.kind = kind;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
