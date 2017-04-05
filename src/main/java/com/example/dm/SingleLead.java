package com.example.dm;


import com.getbase.models.Lead;
import lombok.Data;

@Data
public class SingleLead {
    private final Lead lead;

    public SingleLead(Lead lead) {
        this.lead = lead;
    }

    boolean isAccountManager() {
        return lead.getTitle().equals("Account Manager");
    }

    boolean isSalesRep() {
        return lead.getTitle().equals("Sales rep");
    }

}
