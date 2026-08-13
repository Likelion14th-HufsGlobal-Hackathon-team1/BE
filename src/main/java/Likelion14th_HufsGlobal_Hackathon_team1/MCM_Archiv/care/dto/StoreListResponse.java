package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.Store;

import java.util.List;

public record StoreListResponse(
        List<StoreItem> stores
) {

    public static StoreListResponse from(List<Store> stores) {
        return new StoreListResponse(
                stores.stream()
                        .map(StoreItem::from)
                        .toList()
        );
    }

    public record StoreItem(
            Long storeId,
            String name,
            String address,
            String phone
    ) {

        public static StoreItem from(Store store) {
            return new StoreItem(
                    store.getId(),
                    store.getName(),
                    store.getAddress(),
                    store.getPhone()
            );
        }
    }
}
