package entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "OrderId")
    private Integer orderId;

    @Column(name = "ProductId")
    private Integer productId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OrderItemId other)) return false;

        return Objects.equals(orderId, other.orderId)
            && Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}