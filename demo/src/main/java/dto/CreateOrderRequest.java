package dto;

import java.util.List;

public record CreateOrderRequest(
    Integer customerId,
    String note,
    List<OrderLineRequest> items
) {}