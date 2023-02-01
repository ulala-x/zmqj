package org.zeromq;

public enum GssApiPrincipalNameTypes {

    NT_HOSTBASED(0),
    NT_USER_NAME(1),
    KRB5_PRINCIPAL(2),
    ;
    private final int value;

    public int value() {
        return this.value;
    }

    GssApiPrincipalNameTypes(int eventNumber) {
        this.value = eventNumber;
    }
}
