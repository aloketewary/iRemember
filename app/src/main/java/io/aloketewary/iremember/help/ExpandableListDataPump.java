package io.aloketewary.iremember.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AlokeT on 2/9/2018.
 */

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        List<String> faq = new ArrayList<>();
        faq.add("Coming soon");

        List<String> howToUse = new ArrayList<>();
        howToUse.add("This application can be used as a supporting device that");

        expandableListDetail.put("FREQUENTLY ASK QUESTIONS", faq);
        expandableListDetail.put("HOW TO USE", howToUse);
        return expandableListDetail;
    }
}
