package company.server.controller;

import company.server.db.entity.Address;
import company.server.db.facade.AddressFacade;
import company.server.db.facade.PassFacade;
import company.server.model.ReportDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final PassFacade passFacade;
    private final AddressFacade addressFacade;
    public ReportController(PassFacade passFacade, AddressFacade addressFacade) {
        this.passFacade = passFacade;
        this.addressFacade = addressFacade;
    }
    @GetMapping
    public List<Map<String, Object>> getPassesForReport(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return passFacade.getAll().stream()
                .filter(p -> {
                    LocalDate date = p.getLimitation();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .map(p -> {
                    Map<String, Object> passMap = new HashMap<>();
                    passMap.put("name", p.getName());
                    passMap.put("passType", p.getPassType());
                    passMap.put("kindPassType", p.getKindPassType());
                    passMap.put("code", p.getCode());
                    passMap.put("limitation", p.getLimitation());

                    Address addr = p.getAddress();
                    if (addr != null) {
                        Map<String, Object> addrMap = new HashMap<>();
                        addrMap.put("id", addr.getId());
                        addrMap.put("street", addr.getStreet());
                        addrMap.put("entrance", addr.getEntrance());
                        passMap.put("address", addrMap);
                    }

                    return passMap;
                })
                .toList();
    }

}
