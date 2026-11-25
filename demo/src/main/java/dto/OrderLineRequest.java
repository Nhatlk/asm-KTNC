package dto;

public record OrderLineRequest(
    Integer productId,
    Integer quantity
) {}