package com.example.dm;

import com.getbase.Client;
import com.getbase.models.Deal;
import com.getbase.services.ContactsService;
import com.getbase.services.DealsService;
import org.aspectj.apache.bcel.util.ClassLoaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class Deals {
    @Autowired
    private Client client;
    @Autowired
    private Contacts contacts;

    public SingleDeal createNewDeal(String contactName) {
        SingleContact contact = contacts.getPredicatedByName(contactName);
        if (contact.getLead().isAccountManager()) {
            contacts.refresh(contact);
        }
        Deal deal = new Deal();
        deal.setName(contactName + " " + new Date().toString());
        deal.setCurrency("EUR");
        deal.setContactId(contact.getContact().getId());
        deal.setValue(new BigDecimal(10000));
        client.deals().create(deal);
        SingleDeal singleDeal = new SingleDeal(deal);
        singleDeal.setContact(contact);
        return singleDeal;
    }

    public void incomingDeal(Deal deal) {
        if (deal.getStageId() == StageIds.WON.stageId) {
            contacts.switchToAccountManager(deal.getContactId());
        }
    }

    public boolean isOwnedByAccountManager(long id) {
        String name = contacts.getNameBy(client.deals().get(id).getContactId());
        return name != null && name.equals("Sara");
    }

    public Deal turnWon(Deal deal, int retries) {
        Optional<Deal> dealWithRetry = Optional.ofNullable(getDealWithRetry(getCriteria(deal), 10));
        dealWithRetry.ifPresent(d -> {
            d.setStageId(StageIds.WON.stageId);
            client.deals().update(d);
        });
        Deal fromBase = dealWithRetry.orElse(deal);
        return fromBase;
    }

    public Deal turnLost(Deal deal) {
        Optional<Deal> dealWithRetry = Optional.ofNullable(getDealWithRetry(getCriteria(deal), 10));
        dealWithRetry.ifPresent(d -> {
            d.setStageId(StageIds.LOST.stageId);
            client.deals().update(d);
        });
        Deal fromBase = dealWithRetry.orElse(deal);
        return fromBase;
    }

    private Deal getDealWithRetry(Map<String, Object> criteria, int retries) {
        for (int i = 0; i <= retries; i++) {
            List<Deal> list = client.deals().list(criteria);
            if (list.size() > 0) return list.get(0);
        }
        return null;
    }

    private Map<String, Object> getCriteria(Deal deal) {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("page", 1L);
        criteria.put("per_page", 1L);
        criteria.put("name", deal.getName());
        return criteria;
    }

    public String getOwnerName(long dealId) {
        return contacts.getNameBy(client.deals().get(dealId).getContactId());
    }

    public boolean isOwnedBySalesRep(long dealId) {
        String name = contacts.getNameBy(client.deals().get(dealId).getContactId());
        return name != null && name.equals("Amanda");
    }

    public void deleteDealWithContact(long dealId) {
        Long contactId = client.deals().get(dealId).getContactId();
        client.deals().delete(dealId);
        contacts.deleteContact(contactId);
    }

    public void delete(long dealId) {
        client.deals().delete(dealId);
    }
}
