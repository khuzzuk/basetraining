package com.example;

import com.example.dm.Deals;
import com.getbase.Client;
import com.getbase.Configuration;
import com.getbase.models.Deal;
import com.getbase.services.LeadsService;
import com.getbase.sync.Sync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
@Slf4j
public class BasetrainingApplication {
	private static String client_id = "b68e87aa7a00399dec1170788260c2af912417f9d3132518456862a0b196f117";
	private static String client_secret = "3123b6200b511619611d55f7c86cbb158d717d11f63a1f9f544c3610a43c1db2";
	private static String app_access_token = "dd9c47b01f591a362fc7a4a832652c5d4736fa2254aeff0e645e34bc44df6dd3";
	private static String pat = "aa403bcbf313d3eecbe06ecbe586d17128dd2bdf81bd055616cd155362a1cee6";

	public static void main(String[] args) {
		SpringApplication.run(BasetrainingApplication.class, args);
	}

	@Bean
	public Client getClient() {
		return new Client(new Configuration.Builder().accessToken(pat).verbose().build());
    }

    @Bean
	public String uuid() {
        return "UUID:" + new Random().nextInt();
	}

	@Bean
	public Sync sync(Client client, String uuid, Deals deals) {
        Sync sync = new Sync(client, uuid);
        sync.subscribe(Deal.class, (meta, deal) -> {
            log.info(meta.toString());
            log.info(deal.toString());
            deals.incomingDeal(deal);
            return true;
        });
        return sync;
    }
}
