package com.example.dm;

import com.getbase.models.Contact;
import lombok.*;

@Data
public class SingleContact {
    private final Contact contact;
    private SingleLead lead;

    public SingleContact(Contact contact) {
        this.contact = contact;
    }
}
