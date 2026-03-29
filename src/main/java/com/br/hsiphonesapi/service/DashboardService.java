package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.response.DashboardResponseDTO;

public interface DashboardService {

    DashboardResponseDTO getDashboard(int month, int year);
}
