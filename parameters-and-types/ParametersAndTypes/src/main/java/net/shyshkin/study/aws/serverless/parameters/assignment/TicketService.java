package net.shyshkin.study.aws.serverless.parameters.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import net.shyshkin.study.aws.serverless.parameters.assignment.model.Payment;
import net.shyshkin.study.aws.serverless.parameters.assignment.model.Place;
import net.shyshkin.study.aws.serverless.parameters.assignment.model.Ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TicketService {

    public Ticket getTicket(Payment payment, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Payment received: " + payment);

        BigDecimal amount = payment.getAmount();
        String customer = payment.getName();

        Place place = new Place("2nd floor", 3, 4);
        return new Ticket(
                LocalDateTime.now().plus(2, ChronoUnit.DAYS),
                amount,
                "Pirates of Caribbeans", "Very funny movie",
                customer, place);
    }
}
