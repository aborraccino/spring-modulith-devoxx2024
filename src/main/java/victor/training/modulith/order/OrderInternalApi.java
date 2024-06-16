package victor.training.modulith.order;

import lombok.RequiredArgsConstructor;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import victor.training.modulith.inventory.InventoryInternalApi;
import victor.training.modulith.order.impl.Order;
import victor.training.modulith.order.impl.OrderRepo;
import victor.training.modulith.payment.PaymentCompletedEvent;
import victor.training.modulith.shipping.in.api.ShippingInternalApi;

@Service
@RequiredArgsConstructor
public class OrderInternalApi {

    private final ShippingInternalApi shippingInternalApi;
    private final InventoryInternalApi inventoryInternalApi;
    private final OrderRepo orderRepo;

    @ApplicationModuleListener
    public void onPaymentCompleted(PaymentCompletedEvent paymentCompletedEvent) {
        Order order = orderRepo.findById(paymentCompletedEvent.orderId()).orElseThrow();
        order.pay(paymentCompletedEvent.ok());
        if (order.status() == OrderStatus.PAYMENT_APPROVED) {
          inventoryInternalApi.confirmReservation(order.id());
          String trackingNumber = shippingInternalApi.requestShipment(order.id(), order.shippingAddress());
          order.wasScheduleForShipping(trackingNumber);
        }
        orderRepo.save(order);
    }

}
