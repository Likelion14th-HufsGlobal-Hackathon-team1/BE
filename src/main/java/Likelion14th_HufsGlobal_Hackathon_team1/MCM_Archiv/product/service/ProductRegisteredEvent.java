package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service;

import java.time.LocalDate;

public record ProductRegisteredEvent(Long productId, LocalDate purchaseDate) {
}
