package it.polito.wa2.order.domain

import it.polito.wa2.dto.OrderProductDTO
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.*

@Entity
class OrderProduct(
    @ManyToOne
    @Id
    var order: Order,
    @Id
    @Column(name="product_id")
    var productId: Long,
    var amount: Long,
    @Column(name="unit_price")
    var unitPrice: Float
): Serializable {

    fun toDTO(): OrderProductDTO {
        return OrderProductDTO(productId, amount, unitPrice)
    }

    override fun toString(): String {
        return "@Entity ${this.javaClass.name}(order_id=${order.getId()},product_id=$productId)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (javaClass != ProxyUtils.getUserClass(other))
            return false
        other as OrderProduct
        return if (null == this.order.getId()) false
        else this.order.getId() == other.order.getId()
                && this.productId == other.productId
    }

    override fun hashCode(): Int {
        return 42
    }
}
