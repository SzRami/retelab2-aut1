package hu.bme.aut.retelab2.controller;

import hu.bme.aut.retelab2.domain.Ad;
import hu.bme.aut.retelab2.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController
{
    @Autowired
    private AdRepository adRepository;

    @PostMapping
    public Ad create(@RequestBody Ad ad)
    {
        ad.setId(null);
        return adRepository.save(ad);
    }

    @GetMapping
    public List<Ad> getAll(@RequestParam(required = false, defaultValue = "0") int min, @RequestParam(required = false, defaultValue = "10000000") int max)
    {
        List<Ad> foundAds = adRepository.findByMinMax(min, max);
        for (Ad ad : foundAds)
        {
            ad.setSecretCode(null);
        }
        return foundAds;
    }

    @PutMapping
    public ResponseEntity<Ad> update(@RequestBody Ad updated)
    {
        try
        {
            adRepository.updatedAd(updated);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (AccessDeniedException e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("{tag}")
    public List<Ad> getFromTags(@PathVariable String tag)
    {
        return adRepository.findByTag(tag);
    }

    @Scheduled(fixedDelay = 6000)
    public void deleteExpiredAds()
    {
        List<Ad> ads = adRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Ad a:ads)
        {
            if (a.getExpirationDate()!= null && a.getExpirationDate().isBefore(now))
            {
                adRepository.deleteById(a.getId());
            }
        }
    }
}
