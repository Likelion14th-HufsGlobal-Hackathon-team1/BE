package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.Store;

import java.util.List;

public record StoreListResponse(
        List<StoreItem> stores
) {

    public static StoreListResponse from(List<StoreItem> stores) {
        return new StoreListResponse(stores);
    }

    public record StoreItem(
            Long storeId,
            String name,
            String address,
            String phone,
            double distanceKm
    ) {

        public static StoreItem from(Store store, double distanceKm) {
            return new StoreItem(
                    store.getId(),
                    store.getName(),
                    store.getAddress(),
                    store.getPhone(),
                    distanceKm
            );
        }
    }
}
