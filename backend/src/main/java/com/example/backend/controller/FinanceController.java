package com.example.backend.controller;

import com.example.backend.dto.request.BillRequest;
import com.example.backend.dto.request.PaymentRequest;
import com.example.backend.dto.response.BillDTO;
import com.example.backend.dto.response.PaymentDTO;
import com.example.backend.entity.Bill;
import com.example.backend.entity.Payment;
import com.example.backend.entity.Student;
import com.example.backend.service.FinanceService;
import com.example.backend.service.StudentService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {
    
    private final FinanceService financeService;
    private final StudentService studentService;
    private final UserService userService;
    
    @PostMapping("/payment")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentRequest request) {
        Payment payment = financeService.createPayment(request);
        return ResponseEntity.ok(convertPaymentToDTO(payment));
    }
    
    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getPayments(Pageable pageable) {
        Page<Payment> payments = financeService.getPayments(pageable);
        Page<PaymentDTO> paymentDTOs = payments.map(this::convertPaymentToDTO);
        return ResponseEntity.ok(paymentDTOs);
    }
    
    @GetMapping("/payments/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        Payment payment = financeService.getPaymentById(id);
        return ResponseEntity.ok(convertPaymentToDTO(payment));
    }

    @GetMapping("/my/payments")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<PaymentDTO>> getMyPayments(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Student student = studentService.getStudentByUserId(userId);
        List<PaymentDTO> paymentDTOs = financeService.getStudentPayments(student.getId())
            .stream()
            .map(this::convertPaymentToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }
    
    @PostMapping("/payments/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<PaymentDTO> processPayment(@PathVariable Long id) {
        Payment payment = financeService.processPayment(id);
        return ResponseEntity.ok(convertPaymentToDTO(payment));
    }
    
    @PostMapping("/payments/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long id) {
        Payment payment = financeService.refundPayment(id);
        return ResponseEntity.ok(convertPaymentToDTO(payment));
    }
    
    @GetMapping("/bills")
    public ResponseEntity<Page<BillDTO>> getBills(Pageable pageable) {
        Page<Bill> bills = financeService.getBills(pageable);
        Page<BillDTO> billDTOs = bills.map(this::convertBillToDTO);
        return ResponseEntity.ok(billDTOs);
    }
    
    @PostMapping("/bills")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillDTO> createBill(@RequestBody BillRequest request) {
        Bill bill = financeService.createBill(request);
        return ResponseEntity.ok(convertBillToDTO(bill));
    }
    
    @GetMapping("/bills/student/{studentId}")
    public ResponseEntity<List<BillDTO>> getStudentBills(@PathVariable Long studentId) {
        List<Bill> bills = financeService.getStudentBills(studentId);
        List<BillDTO> billDTOs = bills.stream()
            .map(this::convertBillToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(billDTOs);
    }

    @GetMapping("/my/bills")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<BillDTO>> getMyBills(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Student student = studentService.getStudentByUserId(userId);
        List<BillDTO> billDTOs = financeService.getStudentBills(student.getId())
            .stream()
            .map(this::convertBillToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(billDTOs);
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getFinanceStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Map<String, Object> statistics = financeService.getFinanceStatistics(start, end);
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/income")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Map<String, Object> income = Map.of(
            "totalIncome", financeService.calculateTotalIncome(start, end),
            "incomeByCategory", financeService.getIncomeByCategory(start, end)
        );
        return ResponseEntity.ok(income);
    }
    
    private PaymentDTO convertPaymentToDTO(Payment payment) {
        return PaymentDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .paymentMethod(payment.getPaymentMethod())
            .transactionId(payment.getTransactionId())
            .status(payment.getStatus())
            .paymentTime(payment.getPaymentTime())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }
    
    private BillDTO convertBillToDTO(Bill bill) {
        return BillDTO.builder()
            .id(bill.getId())
            .enrollmentId(bill.getEnrollmentId())
            .billType(bill.getBillType())
            .amount(bill.getAmount())
            .description(bill.getDescription())
            .dueDate(bill.getDueDate())
            .status(bill.getStatus())
            .createdAt(bill.getCreatedAt())
            .updatedAt(bill.getUpdatedAt())
            .build();
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof com.example.backend.security.UserDetailsImpl) {
            return ((com.example.backend.security.UserDetailsImpl) userDetails).getId();
        }
        return userService.findByUsername(userDetails.getUsername()).getId();
    }
}
