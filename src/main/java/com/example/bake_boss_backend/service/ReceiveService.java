package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.ReceiveDto;
import com.example.bake_boss_backend.entity.OfficeReceive;
import com.example.bake_boss_backend.repository.OfficeReceiveRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;

@Service
public class ReceiveService {
    @Autowired
    private OfficeReceiveRepository officeReceiveRepository;

    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

  
    public List<ReceiveDto> findReceivesForToday(String username, LocalDate date) {
        List<ReceiveDto> receives = new ArrayList<>();
        receives.addAll(officeReceiveRepository.findOfficeReceivesForToday(username, date));
        receives.addAll(retailerPaymentRepository.findRetailerPaymentsForToday(username, date));
        return receives;
    }

    public List<OfficeReceive> getReceivesForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return officeReceiveRepository.findReceiveByMonth(year, month, username);
    }

    public List<OfficeReceive> getDatewiseOfficeReceive(String username, LocalDate startDate, LocalDate endDate) {
        return officeReceiveRepository.findReceiveByDate(username, startDate, endDate);
    }

}
