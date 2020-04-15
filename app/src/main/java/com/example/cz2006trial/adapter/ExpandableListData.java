package com.example.cz2006trial.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListData {

    // return data based on the type parameter passed
    public static HashMap<String, List<String>> getData(String type) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        // currently, there is only one type of data but it can be extended to multiple types
        if (type.equals("FAQ")) {

            List<String> qn_1 = new ArrayList<String>();
            qn_1.add("It is a mobile application that enables users to" +
                    " easily make routes within Singapore whether it is jogging or cycling. Users can not only " +
                    "plan routes within a single park but also across different parks.\n\n" +
                    "In addition, users can save their routes " +
                    "and track their progress.");
            expandableListDetail.put("What is this app?", qn_1);

            List<String> qn_2 = new ArrayList<String>();
            qn_2.add("The objective of the application is to make it easy to plan jogging or cycling, encouraging Singaporeans to have a healthier life.");
            expandableListDetail.put("What's the purpose of this app?", qn_2);

            List<String> qn_3 = new ArrayList<String>();
            qn_3.add("The great developers of this great application are: Nick, Ju Xiang, Fazli, Brendon, and Irham.");
            expandableListDetail.put("Who are the developers of this awesome app?", qn_3);

            List<String> qn_4 = new ArrayList<String>();
            qn_4.add("At github.com/irham/piratica. You're welcome!");
            expandableListDetail.put("Where I can find the source code of this app?", qn_4);

            List<String> qn_5 = new ArrayList<String>();
            qn_5.add("Yup! You're welcome!");
            expandableListDetail.put("Is it free?", qn_5);

            List<String> qn_6 = new ArrayList<String>();
            qn_6.add("You need an internet access and give the permission to access your location.");
            expandableListDetail.put("Why does the application not work on my phone?", qn_6);
        }

        return expandableListDetail;
    }
}