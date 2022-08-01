package com.company.service;

import com.company.dto.ProfileDTO;
import com.company.entity.ProfileEntity;
import com.company.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;


    public ProfileDTO create(ProfileDTO profileDTO) {
        ProfileEntity profile = new ProfileEntity();
        profile.setName(profileDTO.getName());
        profile.setSurname(profileDTO.getSurname());
        this.profileRepository.save(profile);

        profileDTO.setId(profile.getId());
        return profileDTO;
    }

    @Cacheable(value = "profileCache", key = "#id")
    public ProfileDTO get(Integer id) {
        ProfileEntity entity = profileRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("Item not found");
        });

        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        return dto;
    }

    @CachePut(value = "profileCache", key = "#id")
    public ProfileDTO update(Integer id, ProfileDTO dto) {
        ProfileEntity entity = profileRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("Item not found");
        });
        dto.setId(id);
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        profileRepository.save(entity);
        return dto;
    }

    @CacheEvict(value = "profileCache", key = "#id")
    public void delete(Integer id) {
        profileRepository.deleteById(id);
    }

    @CacheEvict(value = "profileCache", allEntries = true)
    public void deleteAll() {
        profileRepository.deleteAll();
    }


    public List<ProfileDTO> list() {

        return profileRepository.findAll().stream().map(profileEntity -> {
            ProfileDTO profileDTO = new ProfileDTO();
            profileDTO.setId(profileEntity.getId());
            profileDTO.setName(profileEntity.getName());
            profileDTO.setSurname(profileEntity.getSurname());
            return profileDTO;
        }).collect(Collectors.toList());
    }
}
