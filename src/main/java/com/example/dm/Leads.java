package com.example.dm;

import com.getbase.Client;
import com.getbase.services.LeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Leads {
    @Autowired
    private Client client;

    SingleLead getSalesRep() {
        return new SingleLead(client.leads().list(new LeadsService.SearchCriteria()
                .page(1)
                .perPage(1)
                .firstName("Amanda")).get(0));
    }

    SingleLead getAccountManager() {
        return new SingleLead(client.leads().list(new LeadsService.SearchCriteria()
                .page(1)
                .perPage(1)
                .firstName("Sara")).get(0));
    }
}
