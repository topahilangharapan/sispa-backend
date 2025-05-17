package radiant.sispa.backend.restservice;


import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.request.CreateItemCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateIncomeResponseDTO;
import radiant.sispa.backend.restdto.response.ItemCategoryResponseDTO;

import java.util.List;

public interface IncomeService {
    CreateIncomeResponseDTO addIncome(CreateIncomeRequestDTO requestDTO, String authHeader);
}
