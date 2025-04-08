package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Client;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.model.Vendor;
import radiant.sispa.backend.repository.ClientDb;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.restdto.request.AddClientRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateClientRequestRestDTO;
import radiant.sispa.backend.restdto.response.ClientResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class ClientRestServiceImpl implements ClientRestService {

    @Autowired
    ClientDb clientDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    PurchaseOrderDb purchaseOrderDb;

    @Override
    public void deleteClient(String id) throws EntityNotFoundException {
        Client clientToDelete = clientDb.findByIdAndDeletedAtNull(id);

        if (clientToDelete == null) {
            throw new EntityNotFoundException("Klien tidak ditemukan");
        }

        if (clientToDelete.getPurchaseOrders() != null && !clientToDelete.getPurchaseOrders().isEmpty()) {
            throw new IllegalStateException("Klien tidak dapat dihapus karena memiliki PO.");
        }

        clientToDelete.setDeletedAt(new Date());
        clientDb.save(clientToDelete);
    }

    @Override
    public ClientResponseDTO addClient(AddClientRequestRestDTO clientDTO, String username) throws EntityExistsException {

        List<Client> existingClient = clientDb.findByNameIgnoreCaseAndContactAndDeletedAtNull(clientDTO.getName(), clientDTO.getContact());

        for (Client client : existingClient) {
            if (client.getName().equalsIgnoreCase(clientDTO.getName()) && client.getContact().equals(clientDTO.getContact())) {
                throw new EntityExistsException("Klien dengan nama dan kontak ini sudah terdaftar.");
            }
        }
        Client newClient = new Client();

        newClient.setId(generateClientId(clientDTO.getName()));
        newClient.setName(clientDTO.getName());

        if (!isValidEmail(clientDTO.getEmail())) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }

        newClient.setEmail(clientDTO.getEmail());
        newClient.setAddress(clientDTO.getAddress());
        newClient.setContact(clientDTO.getContact());
        newClient.setIndustry(clientDTO.getIndustry());
        newClient.setDescription(clientDTO.getDescription());
        newClient.setCreatedAt(new Date());
        newClient.setUpdatedAt(new Date());
        newClient.setUpdatedBy(username);
        newClient.setCreatedBy(username);

        clientDb.save(newClient);
        return clientToClientResponseDTO(newClient);
    }

    private ClientResponseDTO clientToClientResponseDTO(Client client) {
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO();

        clientResponseDTO.setId(client.getId());
        clientResponseDTO.setName(client.getName());
        clientResponseDTO.setContact(client.getContact());
        clientResponseDTO.setAddress(client.getAddress());
        clientResponseDTO.setEmail(client.getEmail());
        clientResponseDTO.setIndustry(client.getIndustry());
        clientResponseDTO.setDescription(client.getDescription());
        clientResponseDTO.setCreatedAt(client.getCreatedAt());
        clientResponseDTO.setUpdatedAt(client.getUpdatedAt());
        clientResponseDTO.setCreatedBy(client.getCreatedBy());
        clientResponseDTO.setUpdatedBy(client.getUpdatedBy());
        clientResponseDTO.setDeletedAt(client.getDeletedAt());
        return clientResponseDTO;
    }

    public String generateClientId(String clientName) {
        String prefix = "CLI";

        String[] nameParts = clientName.split(" ");
        String clientInitials;

        if (nameParts.length > 1) {
            clientInitials = (nameParts[0].substring(0, 2) + nameParts[1].substring(0, 2)).toUpperCase();
        } else {
            clientInitials = nameParts[0].substring(0, 3).toUpperCase();
        }

        int totalClient = clientDb.findAll().size();
        String clientNumber = String.format("%03d", totalClient + 1);

        return prefix + clientInitials + clientNumber;
    }

    @Override
    public ClientResponseDTO updateClient(String id, UpdateClientRequestRestDTO clientDTO, String username) {
        Client client = clientDb.findById(id).orElse(null);
        if (client == null || client.getDeletedAt() != null){
            return null;
        }

        client.setName(clientDTO.getName());
        client.setContact(clientDTO.getContact());
        client.setAddress(clientDTO.getAddress());
        client.setEmail(clientDTO.getEmail());
        client.setIndustry(clientDTO.getIndustry());
        client.setDescription(clientDTO.getDescription());

        client.setUpdatedBy(username);

        Client updatedClient = clientDb.save(client);
        return clientToClientResponseDTO(updatedClient);
    }

    @Override
    public ClientResponseDTO getClientById(String id) {
        Client client = clientDb.findById(id).orElse(null);

        if (client == null || client.getDeletedAt() != null){
            return null;
        }

        return clientToClientResponseDTO(client);
        
    }

    @Override
    public List<ClientResponseDTO> getAllClient() {
        List<Client> listClient = clientDb.findByDeletedAtNull();

        List<ClientResponseDTO> listClientResponseDTO = new ArrayList<>();
        for (Client client : listClient) {
            var clientResponseDTO = clientToClientResponseDTO(client);
            listClientResponseDTO.add(clientResponseDTO);
        }

        return listClientResponseDTO;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public Client addPurchaseOrder(String idClient, Long idPo) {
        Client client = clientDb.findByIdAndDeletedAtNull(idClient);

        if (client == null || client.getDeletedAt() != null){
            return null;
        }

        if (client.getPurchaseOrders() == null) {
            client.setPurchaseOrders(new ArrayList<PurchaseOrder>());
        }

        client.getPurchaseOrders().add(purchaseOrderDb.findPurchaseOrderById(idPo));
        return clientDb.save(client);
    }
}
