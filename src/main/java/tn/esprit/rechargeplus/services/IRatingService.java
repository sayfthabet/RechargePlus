package tn.esprit.rechargeplus.services;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.entities.Rating;

import java.util.List;
public interface IRatingService {
    List<Rating> getAllRatings();
    Rating getRatingById(Long id);
    Rating addRating(Rating rating);
    Rating updateRating(Rating rating);
    void deleteRating(Long id);
}
