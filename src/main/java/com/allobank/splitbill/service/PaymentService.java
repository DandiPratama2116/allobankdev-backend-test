package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.PaymentResponse;
import com.allobank.splitbill.dto.RecordPaymentRequest;
import com.allobank.splitbill.entity.BillGroup;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.entity.Payment;
import com.allobank.splitbill.exception.InvalidOperationException;
import com.allobank.splitbill.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final BillGroupService groupService;
    private final PaymentRepository paymentRepository;

    public PaymentService(BillGroupService groupService, PaymentRepository paymentRepository) {
        this.groupService = groupService;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse recordPayment(UUID groupId, RecordPaymentRequest request) {
        BillGroup group = groupService.getGroupEntity(groupId);

        String payerName = request.getPayer().trim();
        String payeeName = request.getPayee().trim();

        if (payerName.equalsIgnoreCase(payeeName)) {
            throw new InvalidOperationException("Payer and payee cannot be the same participant");
        }

        Participant payer = group.getParticipants().stream()
                .filter(p -> p.getName().equalsIgnoreCase(payerName))
                .findFirst()
                .orElseThrow(() -> new InvalidOperationException("Payer '" + payerName + "' is not a participant in this group"));

        Participant payee = group.getParticipants().stream()
                .filter(p -> p.getName().equalsIgnoreCase(payeeName))
                .findFirst()
                .orElseThrow(() -> new InvalidOperationException("Payee '" + payeeName + "' is not a participant in this group"));

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment(payer, payee, amount, request.getNotes());
        group.addPayment(payment);
        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                saved.getPayer().getName(),
                saved.getPayee().getName(),
                saved.getAmount(),
                saved.getNotes(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByGroup(UUID groupId) {
        groupService.getGroupById(groupId); // Validate existence
        return paymentRepository.findByBillGroupIdWithDetails(groupId).stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getPayer().getName(),
                        p.getPayee().getName(),
                        p.getAmount(),
                        p.getNotes(),
                        p.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
