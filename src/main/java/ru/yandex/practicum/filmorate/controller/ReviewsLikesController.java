package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.ReviewsLikesService;

@RestController
@RequestMapping("/reviews/{id}")
public class ReviewsLikesController {
    private final ReviewsLikesService reviewsLikesService;

    public ReviewsLikesController(ReviewsLikesService reviewsLikesService) {
        this.reviewsLikesService = reviewsLikesService;
    }

    @PutMapping("/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewsLikesService.addLike(id, userId);
    }

    @PutMapping("/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewsLikesService.addDislike(id, userId);
    }

    @DeleteMapping("/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        reviewsLikesService.deleteLike(id, userId);
    }

    @DeleteMapping("/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        reviewsLikesService.deleteDislike(id, userId);
    }
}
