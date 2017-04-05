import com.example.BasetrainingApplication
import com.getbase.models.Deal
import com.jayway.awaitility.Awaitility
import com.example.dm.Deals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

@Stepwise
@ContextConfiguration(classes = BasetrainingApplication.class)
class MyTestClass extends Specification {
    @Autowired
    private Deals deals

    def "won deal triggers switch from Sales Rep to Account Manager"() {
        given:
        Deal deal = deals.createNewDeal('CocaCola').getDeal()
        long dealId = deals.turnWon(deal, 10).getId()
        when:
        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(new Runnable() {
            @Override
            void run() { while (!deals.isOwnedByAccountManager(dealId)) {} }
        })
        then:
        deals.getOwnerName(dealId) == "Sara"
        deals.deleteDealWithContact(dealId)
    }

    def "lost deal not switched to Account Manager"() {
        given:
        Deal deal = deals.createNewDeal('LostCompany').getDeal()
        long dealId = deals.turnLost(deal).getId()
        when:
        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(new Runnable() {
            @Override
            void run() { while (!deals.isOwnedBySalesRep(dealId)) {} }
        })
        then:
        deals.getOwnerName(dealId) == "Amanda"
        deals.deleteDealWithContact(dealId)
    }

    def "new deal triggers to reswitch contact to Sales Rep"() {
        given:
        Deal deal = deals.createNewDeal('NewCompany').getDeal()
        long dealId = deals.turnWon(deal, 10).getId()
        when:
        Awaitility.await().atMost(40, TimeUnit.SECONDS).until(new Runnable() {
            @Override
            void run() { while (!deals.isOwnedByAccountManager(dealId)) {} }
        })
        Deal deal2 = deals.createNewDeal('NewCompany').getDeal()
        long dealId2 = deals.turnLost(deal2).getId()
        Awaitility.await().atMost(40, TimeUnit.SECONDS).until(new Runnable() {
            @Override
            void run() { while (!deals.isOwnedBySalesRep(dealId2)) {} }
        })
        then:
        deals.getOwnerName(dealId2) == "Amanda"
        deals.delete(dealId)
        deals.deleteDealWithContact(dealId2)

    }
}

