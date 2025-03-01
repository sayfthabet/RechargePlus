package tn.esprit.rechargeplus.controllers;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Rating;
import tn.esprit.rechargeplus.services.IRatingService;
import tn.esprit.rechargeplus.services.RatingServiceImpl;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    IRatingService ratingService;
    @GetMapping("/getRatings")
    public List<Rating> AllRatings() {return ratingService.getAllRatings();}
    @PostMapping("/addRating")
    public Rating addRating(@RequestBody Rating rating) {
    return ratingService.addRating(rating);
    }
    @DeleteMapping("/deleteRating/{id}")
    public void deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
    }
    public void updateRating() {
    }
    public void getRatingById() {
    }
}
