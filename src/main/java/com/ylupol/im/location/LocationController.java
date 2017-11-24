package com.ylupol.im.location;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.ylupol.im.web.exception.NonUniqueLocationNameException;
import com.ylupol.im.web.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getById(@PathVariable int id) {
        return locationRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Location> getByName(@PathVariable String name) {
        return locationRepository.findByName(name)
            .map(ResponseEntity::ok)
            .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Location create(@RequestBody @Valid LocationInputDto locationInputDto) {
        if (isLocationWithNameExists(locationInputDto.getName())) {
            throw new NonUniqueLocationNameException();
        }

        return locationRepository.save(locationInputDto.toLocation());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        if (!locationRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException();
        }

        locationRepository.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public void update(@RequestBody @Valid LocationInputDto locationInputDto,
        @PathVariable int id) {
        Optional<Location> locationOptional = locationRepository.findById(id);

        if (!locationOptional.isPresent()) {
            throw new ResourceNotFoundException();
        }

        if (isLocationWithNameExists(locationInputDto.getName())) {
            throw new NonUniqueLocationNameException();
        }

        Location location = locationOptional.get();
        location.setName(locationInputDto.getName());
        location.setDescription(locationInputDto.getDescription());

        locationRepository.save(location);

    }

    private boolean isLocationWithNameExists(String name) {
        return locationRepository.findByName(name).isPresent();
    }
}
