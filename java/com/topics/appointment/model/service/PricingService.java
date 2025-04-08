package com.topics.appointment.model.service;

import java.util.Map;

import org.springframework.stereotype.Service;
@Service
public class PricingService {
    private static final Map<Integer, Integer> servicePrices = Map.of(
        1, 1000,
        2, 2000,
        3, 1900,
        4, 2800
    );

    private static final Map<Integer, Integer> extraPackagePrices = Map.of(
        1, 100,
        2, 300,
        3, 350,
        4, 350
    );

    public int getServicePrice(int serviceId) {
        return servicePrices.getOrDefault(serviceId, 0);
    }

    public int getExtraPackagePrice(int packageId) {
        return extraPackagePrices.getOrDefault(packageId, 0);
    }
}
