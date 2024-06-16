package victor.training.modulith.payment.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import victor.training.modulith.payment.PaymentCompletedEvent;

@RestController
@RequiredArgsConstructor
// Webhook = a call back to me over HTTP
public class PaymentGatewayWebHookApi {
  private final ApplicationEventPublisher applicationEventPublisher;

  @PutMapping("payment/{orderId}/status")
  public String confirmPayment(@PathVariable long orderId, @RequestBody boolean ok) {
    applicationEventPublisher.publishEvent(new PaymentCompletedEvent(orderId, ok));
    return "Payment callback received";
  }
}
