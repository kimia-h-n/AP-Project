package org.example.divar.util;

import org.example.divar.dto.admin.AdminReportResponseDTO;
import org.example.divar.model.AdminReport;

public class ConvertToAdminReport {

    public static AdminReport convertToAdminReport(AdminReportResponseDTO dto) {
        if (dto == null) {
            return null;
        }

        AdminReport report = new AdminReport();
        report.setId(dto.getId());
        report.setAdId(dto.getAdId());
        report.setAdTitle(dto.getAdTitle());

        String fullName = (dto.getSellerFirstName() + " " + dto.getSellerLastName()).trim();
        report.setSellerFullName(fullName);

        report.setReason(dto.getReason());
        report.setImageUrl(dto.getImageUrl());

        return report;
    }
}