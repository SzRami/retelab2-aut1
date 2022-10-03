package hu.bme.aut.retelab2.repository;

import hu.bme.aut.retelab2.SecretGenerator;
import hu.bme.aut.retelab2.domain.Ad;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Repository
public class AdRepository
{
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Ad save (Ad ad)
    {
        ad.setSecretCode(SecretGenerator.generate());
        return em.merge(ad);
    }
    public List<Ad> findAll() {
        return em.createQuery("SELECT a FROM Ad a", Ad.class).getResultList();
    }

    public List<Ad> findByMinMax (int min, int max)
    {
        return em.createQuery("SELECT a FROM Ad a WHERE a.price >=?1 AND a.price <=?2",Ad.class).setParameter(1, min).setParameter(2, max).getResultList();
    }

    public Ad findById(long id) {
        return em.find(Ad.class, id);
    }

    public List<Ad> findByTag(String tag)
    {
        return  em.createQuery("SELECT a FROM Ad a WHERE ?1 MEMBER a.tags", Ad.class).setParameter(1, tag).getResultList();
    }
    @Transactional
    public void deleteById(long id)
    {
        Ad ad = findById(id);
        em.remove(ad);
    }

    @Transactional
    public Ad updatedAd (Ad updatedAd) throws AccessDeniedException
    {
        Ad adFromId = em.find(Ad.class, updatedAd.getId());
        if (!Objects.equals(adFromId.getSecretCode(), updatedAd.getSecretCode()))
        {
            throw new AccessDeniedException("You can't update this");
        }
        return save(updatedAd);
    }
}
