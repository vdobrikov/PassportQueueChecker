package com.vdobrikov.passportqueuechecker.services;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Author: Vladimir Dobrikov (hedin.mail@gmail.com)
 */

@Service
public class QueueService {
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    private static final String CSS_SELECTOR_FREE_SLOTS = "ul#date_times > li";
    private static final String ATTR_PLACE = "org-name";
    private static final String ATTR_TIME = "data-id";

    @Value("${dmsu.queue.url}")
    private String queueUrl;

    @Value("${dmsu.no.free.slots.elements.count}")
    private int noFreeSlotsElementsCount;

    public Multimap<String, String> hasFreeSlots() {
        LOG.debug("Checking queue");
        Document doc;
        ImmutableSetMultimap.Builder<String, String> placesTimes = ImmutableSetMultimap.builder();
        try {
            doc = Jsoup.connect(queueUrl)
                .data("setting[near]", "true")
                .data("setting[region][name]", "ОДЕСЬКА")
                .data("setting[region][id]", "5100000000")
                .data("setting[service][name]", "Паспорт громадянина України для виїзду за кордон")
                .data("setting[service][id]", "1")
                .data("setting[service][type]", "type_passport")
                .userAgent("Queue-checker-bot")
                .timeout(30000)
                .post();

        } catch (IOException e) {
            LOG.error("Failed to get queue data by url={}", queueUrl, e);
            return placesTimes.build();
        }

        Elements elements = doc.select(CSS_SELECTOR_FREE_SLOTS);
        elements.stream().forEach(element -> placesTimes.put(element.attr(ATTR_PLACE), element.attr(ATTR_TIME)));

        return placesTimes.build();
    }

}
