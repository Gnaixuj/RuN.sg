package com.example.cz2006trial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListData {
    public static HashMap<String, List<String>> getData(String type) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        if (type.equals("FAQ")) {

            List<String> qn_1 = new ArrayList<String>();
            qn_1.add("Answer: Fazli");
            expandableListDetail.put("Who?", qn_1);

            List<String> qn_2 = new ArrayList<String>();
            qn_2.add("Answer: 18");
            expandableListDetail.put("Age?", qn_2);

            List<String> qn_3 = new ArrayList<String>();
            qn_3.add("Answer: Use finger");
            expandableListDetail.put("How to press button?", qn_3);

            List<String> qn_4 = new ArrayList<String>();
            qn_4.add("Answer: Run.sg");
            expandableListDetail.put("WHat is the name of the app?", qn_4);
        }

        return expandableListDetail;
    }
}