package net.purevirtual.springbootexample.boundary.dto;

import javax.validation.constraints.NotBlank;

public class NegativeRequest {

    @NotBlank
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
