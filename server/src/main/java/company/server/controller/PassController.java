package company.server.controller;

import company.server.db.entity.Pass;
import company.server.db.facade.PassFacade;
import company.server.model.PassDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passes")
public class PassController {

    private final PassFacade facade;

    public PassController(PassFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<Pass> getAll() {
        return facade.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Pass> getPassById(@PathVariable Long id) {
        return facade.findById(id);
    }

    @PostMapping
    public Pass create(@RequestBody PassDto passDto) {
        return facade.save(passDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PassDto passDto) {
        try {
            Pass updated = facade.update(id, passDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        facade.delete(id);
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<?> extend(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        try {
            Pass updated = facade.extend(id, fields);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
