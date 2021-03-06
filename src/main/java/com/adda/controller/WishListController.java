package com.adda.controller;

import com.adda.domain.UserEntity;
import com.adda.exception.AdvertisementNotFoundException;
import com.adda.repository.AdvertisementRepository;
import com.adda.service.UserService;
import com.adda.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.adda.service.UserService.getBearerTokenHeader;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api/advert/wishlist/")
public class WishListController {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WishListService wishListService;

    @GetMapping()
    public ResponseEntity getWishListFromUser() {
        try {
            UserEntity user = userService.encodeUserFromToken(getBearerTokenHeader());
            return ResponseEntity.ok(wishListService.getWishList(user));

        } catch (AdvertisementNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("WishList isn't available" + e);
        }
    }

    @PutMapping("/{advertisementId}")
    public ResponseEntity addAdvertisementByIdToWishList(@PathVariable UUID advertisementId) {
        try {
            UserEntity user = userService.encodeUserFromToken(getBearerTokenHeader());
            return ResponseEntity.ok(wishListService.addAdvertToWishList(user, advertisementRepository.findById(advertisementId)));
        } catch (AdvertisementNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Advertisement isn't available");
        }
    }

    @DeleteMapping("/{advertisementId}")
    public ResponseEntity deleteAdvertisementByIdFromWishList(@PathVariable UUID advertisementId) {
        try {
            UserEntity user = userService.encodeUserFromToken(getBearerTokenHeader());
            return ResponseEntity.ok(wishListService.deleteAdvertFromWishList(user, advertisementRepository.findById(advertisementId)));
        } catch (AdvertisementNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Advertisement isn't available");
        }
    }

}
