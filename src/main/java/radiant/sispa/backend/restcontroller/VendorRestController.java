package radiant.sispa.backend.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restservice.VendorRestService;

@RestController
@RequestMapping("/api/vendor")
public class VendorRestController {
    @Autowired
    VendorRestService vendorRestService;


}
