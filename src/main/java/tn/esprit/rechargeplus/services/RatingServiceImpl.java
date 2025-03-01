package tn.esprit.rechargeplus.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Rating;
import tn.esprit.rechargeplus.repositories.ProductRepository.RatingRepository;
import java.util.List;
@Service
@AllArgsConstructor
public class RatingServiceImpl implements IRatingService {
    @Autowired
    RatingRepository ratingRepository;
    @Override
    public List<Rating> getAllRatings() {return ratingRepository.findAll();}

    @Override
    public Rating getRatingById(Long id) {return ratingRepository.findById(id).orElse(null);}

    @Override
    public Rating addRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public Rating updateRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }
}
