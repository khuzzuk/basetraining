package com.example.dm;

import com.getbase.models.Deal;
import lombok.*;

@Data
public class SingleDeal {
    private final Deal deal;
    private SingleContact contact;

    @java.beans.ConstructorProperties({"deal"})
    public SingleDeal(Deal deal) {
        this.deal = deal;
    }

    public boolean isOwnedByAccountManager() {
        return contact.getLead().isAccountManager();
    }

    public String getOwnerName() {
        return contact.getLead().getLead().getFirstName();
    }
}
