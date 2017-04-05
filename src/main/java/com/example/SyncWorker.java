package com.example;

import com.getbase.Client;
import com.getbase.services.StagesService;
import com.getbase.sync.Sync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class SyncWorker {
    @Autowired
    private String uuid;
    @Autowired
    private Client client;
    @Autowired
    private Sync sync;

    @PostConstruct
    private void init() {
        System.out.println(client.stages().list(new StagesService.SearchCriteria().page(100).perPage(100)));
        sync.fetch();
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    break;
                }
                sync.fetch();
            }
        }, "Synch worker");
        thread.setDaemon(true);
        thread.start();
    }
}
