package com.adda.service;

import com.adda.DTO.AdvertisementDTO;
import com.adda.DTO.FilterDTO;
import com.adda.domain.PhotoEntity;
import com.adda.domain.UserEntity;
import com.adda.domain.AdvertisementEntity;
import com.adda.exception.AdvertisementNotFoundException;
import com.adda.model.Advertisement;
import com.adda.repository.CategoriesRepository;
import com.adda.repository.UserRepository;
import com.adda.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdvertisementService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    public AdvertisementEntity addAdvert(AdvertisementDTO advertisementDTO, UserEntity user) {
        return addAdvert(null, advertisementDTO, user);
    }

    public AdvertisementEntity addAdvert(UUID id, AdvertisementDTO advertisementDTO, UserEntity user) {
        UUID advertisementID = Optional.ofNullable(id).orElse(UUID.randomUUID());
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setPhotos(new String[8]);
        AdvertisementEntity advertisement = new AdvertisementEntity(
                advertisementID,
                advertisementDTO.getTitle(),
                advertisementDTO.getPrice(),
                advertisementDTO.getDescription(),
                user.getEmail(),
                user.getUsername(),
                photoEntity,
                categoriesRepository.findById(advertisementDTO.getCategoryId()).get(),
                user
        );
        Advertisement.toModel(advertisementRepository.save(advertisement));
        return advertisement;
    }

    public Advertisement addPhoto(PhotoEntity photoEntity, UUID id) {
        AdvertisementEntity advertisement = advertisementRepository.findById(id);
        advertisement.setPhotos(photoEntity);
        //advertisement.setPhotos(photo);
        return Advertisement.toModel(advertisementRepository.save(advertisement));
    }

    public Iterable<AdvertisementEntity> getOneAdvertisementByTitle(String id) throws AdvertisementNotFoundException {
        Iterable<AdvertisementEntity> advertisements = advertisementRepository.findAllByTitle(id);
        if (advertisements == null) {
            throw new AdvertisementNotFoundException("Advert is not found");
        }
        return advertisements;
    }

    public Iterable<AdvertisementEntity> getAdvertisementsByCategory(String category_name) throws AdvertisementNotFoundException {
        Iterable<AdvertisementEntity> advertisements = advertisementRepository.findAllByCategory(categoriesRepository.findByCategoryName(category_name));
        if (advertisements == null) {
            throw new AdvertisementNotFoundException("No adverts in that category");
        }
        return advertisements;
    }

    public Iterable<AdvertisementEntity> getAdvertisementsByFilters(FilterDTO filterDTO) throws AdvertisementNotFoundException {
        Iterable<AdvertisementEntity> advertisements = null;
        if (filterDTO.getCategoryName() == null || filterDTO.getCategoryName().equals("")) {
            advertisements = advertisementRepository.findAllByPriceBetween(filterDTO.getStartPrice(), filterDTO.getEndPrice());
        } else {
            advertisements = advertisementRepository.findAllByPriceBetweenAndCategory(filterDTO.getStartPrice(), filterDTO.getEndPrice(),categoriesRepository.findByCategoryName(filterDTO.getCategoryName()));
        }

        if (advertisements == null) {
            throw new AdvertisementNotFoundException("No adverts in that category");
        }
        return advertisements;
    }
    public Iterable<AdvertisementEntity> getAllAdvertisements() {
        return advertisementRepository.findAll();
    }
}
