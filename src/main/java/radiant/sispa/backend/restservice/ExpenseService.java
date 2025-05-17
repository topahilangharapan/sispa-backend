package radiant.sispa.backend.restservice;


import radiant.sispa.backend.restdto.request.CreateExpenseRequestDTO;
import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.response.CreateExpenseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateIncomeResponseDTO;

public interface ExpenseService {
    CreateExpenseResponseDTO addExpense(CreateExpenseRequestDTO requestDTO, String authHeader);
}
