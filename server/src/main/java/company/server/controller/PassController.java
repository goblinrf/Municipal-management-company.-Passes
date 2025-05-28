package company.server.controller;

import company.server.db.entity.Pass;
import company.server.db.facade.PassFacade;
import company.server.model.PassDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
