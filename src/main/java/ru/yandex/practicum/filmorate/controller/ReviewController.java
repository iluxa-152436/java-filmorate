package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
       return reviewService.update(review);
    }

    @DeleteMapping ("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.delete(id);
    }

    @GetMapping ("/{id}")
    public Review getById(@PathVariable int id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getList(@RequestParam(defaultValue = "0") @Positive int filmId,
                                @RequestParam(defaultValue = "10") int count) {
        return reviewService.getList(filmId, count);
    }

}
