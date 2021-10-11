package net.purevirtual.springbootexample.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ApplicationStatus {
    CREATED(true),
    DELETED,
    VERIFIED(true),
    REJECTED,
    ACCEPTED,
    PUBLISHED;
    
    List<ApplicationStatus> predecessors;
    final boolean contentChangeAllowed;
    
    private ApplicationStatus() {
        contentChangeAllowed = false;
    }
    
    private ApplicationStatus(boolean contentChangeAllowed) {
        this.contentChangeAllowed = contentChangeAllowed;
    }
    
    static {
        CREATED.predecessors = Collections.emptyList();
        DELETED.predecessors = Arrays.asList(CREATED);
        VERIFIED.predecessors = Arrays.asList(CREATED);
        ACCEPTED.predecessors = Arrays.asList(VERIFIED);
        REJECTED.predecessors = Arrays.asList(VERIFIED, ACCEPTED);
        PUBLISHED.predecessors = Arrays.asList(ACCEPTED);
    }

    public boolean canChangeFrom(ApplicationStatus oldStatus) {
        return predecessors.contains(oldStatus);
    }

    public List<ApplicationStatus> getPredecessors() {
        return predecessors;
    }

    public boolean isContentChangeAllowed() {
        return contentChangeAllowed;
    }
    
}
