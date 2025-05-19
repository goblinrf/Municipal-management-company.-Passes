package company.server.controller;

import company.server.db.entity.Pass;
import company.server.db.facade.PassFacade;
import java.util.List;

import company.server.model.PassDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passes")
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        facade.delete(id);
    }
}
