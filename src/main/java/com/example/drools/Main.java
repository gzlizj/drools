package com.example.drools;

import com.example.drools.model.Person;
import com.example.drools.model.Trade;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class Main {
    public static void main(String[] args) {
        try {
            // Load the knowledge base from kmodule.xml
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();

            // Create a stateful session
            KieSession kSession = kContainer.newKieSession("ksession-rules");

            // --- Testing Existing Person Rules ---
            System.out.println("--- Testing Person Rules ---");
            Person john = new Person("John", 15);
            Person jane = new Person("Jane", 20);
            Person bob = new Person("Bob", 70);

            kSession.insert(john);
            kSession.insert(jane);
            kSession.insert(bob);

            kSession.getAgenda().getAgendaGroup("bonus-group").setFocus();
            kSession.fireAllRules();

            // --- Testing New Trade VWAP Rule ---
            System.out.println("\n--- Testing Trade VWAP Rule (STREAM processing) ---");
            String symbol = "APPL";

            // 1. Simulate 10 normal trades around $100 price
            System.out.println("Inserting 10 normal trades around $100...");
            for (int i = 1; i <= 10; i++) {
                double price = 100.0 + (Math.random() * 5); // Price between 100 and 105
                Trade t = new Trade(symbol, price, 100);
                kSession.insert(t);
                // In STREAM mode, firing rules after every event makes the sliding window slide
                // forward
                kSession.fireAllRules();
            }

            // 2. Simulate an anomaly trade (e.g. $140), which is > 30% higher than the
            // ~$102 VWAP
            System.out.println("Inserting Anomaly Trade at $140.0...");
            Trade anomalyTrade = new Trade(symbol, 140.0, 100);
            kSession.insert(anomalyTrade);
            kSession.fireAllRules();

            // Dispose the session
            kSession.dispose();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
