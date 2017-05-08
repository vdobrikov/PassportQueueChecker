package com.vdobrikov.passportqueuechecker;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.vdobrikov.passportqueuechecker.services.QueueService;
import com.vdobrikov.passportqueuechecker.services.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Author: Vladimir Dobrikov (hedin.mail@gmail.com)
 */

@Component
public class ScheduledChecks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledChecks.class);

    private boolean hadFreeSlots = false;
    private Joiner.MapJoiner joiner = Joiner.on("\n").withKeyValueSeparator(": ");

    @Autowired
    QueueService queueService;

    @Autowired
    TelegramService telegramService;

    @Scheduled(cron = "${task.checkqueue.cron}")
    public void checkQueue() {
        Multimap<String, String> placesTimes = queueService.hasFreeSlots();
        if (!placesTimes.isEmpty() && !hadFreeSlots) {
            // Found bookable slot!
            LOG.info("Found bookable slot");

            telegramService.sendMessage("Found bookable slot! http://dmsu.gov.ua/online \n" + joiner.join(placesTimes.entries()));
            hadFreeSlots = true;
        }
        if (placesTimes.isEmpty() && hadFreeSlots) {
            hadFreeSlots = false;
            LOG.debug("No free slots");
        }
    }
}
