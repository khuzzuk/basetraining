package com.example.dm;

import com.getbase.Client;
import com.getbase.models.Contact;
import com.getbase.services.ContactsService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class Contacts {
    @Autowired
    private Client client;
    @Autowired
    private Leads leads;
    private static final String ownerLeadName = "owner_lead_name";


    SingleContact getPredicatedByName(String contactName) {
        SingleContact contact = client.contacts()
                .list(new ContactsService.SearchCriteria().page(1).perPage(1).name(contactName))
                .stream().findAny().map(SingleContact::new)
                .orElseGet(() -> getNew(contactName));
        if (contact.getLead() == null) {
            refresh(contact);
        }
        return contact;
    }

    private SingleContact getNew(String name) {
        Contact contact = new Contact();
        contact.setName(name);
        Map<String, Object> fields = new HashMap<>();
        fields.put(ownerLeadName, "Amanda");
        contact.setCustomFields(fields);
        client.contacts().create(contact);

        Map<String, Object> criteria = new HashMap<>();
        criteria.put("name", name);
        SingleContact singleContact = new SingleContact(
                Optional.ofNullable(getContactWithRetries(criteria, 10)).orElse(contact));
        singleContact.setLead(leads.getSalesRep());
        return singleContact;
    }

    private Contact getContactWithRetries(Map<String, Object> criteria, int retries) {
        List<Contact> list;
        for (int i = 0; i <= retries; i++) {
            list = client.contacts().list(criteria);
            if (list.size() > 0) return list.get(0);
        }
        return null;
    }

    void refresh(SingleContact contact) {
        contact.setLead(leads.getSalesRep());
        Map<String, Object> fields = new HashMap<>();
        fields.put(ownerLeadName, "Amanda");
        contact.getContact().setCustomFields(fields);
        client.contacts().update(contact.getContact());
    }

    void switchToAccountManager(long contactId) {
        Contact contact = client.contacts().get(contactId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(ownerLeadName, "Sara");
        contact.setCustomFields(fields);
        client.contacts().update(contact);
    }

    String getNameBy(long contactId) {
        return client.contacts().get(contactId).getCustomFields().get(ownerLeadName).toString();
    }

    public void deleteContact(long id) {
        client.contacts().delete(id);
    }
}
