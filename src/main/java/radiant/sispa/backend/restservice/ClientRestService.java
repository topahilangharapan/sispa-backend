package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.Client;
import radiant.sispa.backend.model.Vendor;
import radiant.sispa.backend.restdto.request.AddClientRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateClientRequestRestDTO;
import radiant.sispa.backend.restdto.response.ClientResponseDTO;

import java.util.List;

public interface ClientRestService {
    void deleteClient(String id) throws EntityNotFoundException;

    ClientResponseDTO addClient(AddClientRequestRestDTO clientDTO, String username);

    ClientResponseDTO updateClient(String id, UpdateClientRequestRestDTO clientDTO, String username);

    ClientResponseDTO getClientById(String id);

    List<ClientResponseDTO> getAllClient();

    Client addPurchaseOrder(String idClient, Long idPo);
}
