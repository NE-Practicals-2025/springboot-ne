package com.proj.springsecrest.services.serviceImpls;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.exceptions.ResourceNotFoundException;
import com.proj.springsecrest.helpers.MailService;
import com.proj.springsecrest.helpers.Utility;
import com.proj.springsecrest.models.User;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.repositories.IUserRepository;
import com.proj.springsecrest.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final MailService mailService;

//    private final IFileService fileService;
//    private final FileStorageService fileStorageService;

    @Override
    public Page<User> getAll(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    public User getById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));
    }

    @Override
    public User create(User user) {
        try {
            Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
            Optional<User> userOptionPhone = this.userRepository.findByTelephone(user.getTelephone());

            if (userOptional.isPresent())
                throw new BadRequestException(String.format("User with email '%s' already exists", user.getEmail()));
            if (userOptionPhone.isPresent())
                throw new BadRequestException(String.format("User with phone '%s' already exists", user.getTelephone()));
            User newUser =  this.userRepository.save(user);
            this.mailService.registrationSuccessful(user.getEmail(), newUser.getFullName());
            return newUser;
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, user);
            throw new BadRequestException(errorMessage, ex);
        }
    }

    @Override
    public User save(User user) {
        try {
            return this.userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, user);
            throw new BadRequestException(errorMessage, ex);
        }
    }

//    @Override
//    public User update(UUID id, UpdateUserDTO dto) {
//        User entity = this.userRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("User", "id", id.toString()));
//
//        Optional<User> userOptional = this.userRepository.findByEmail(dto.getEmail());
//        if (userOptional.isPresent() && (userOptional.get().getId() != entity.getId()))
//            throw new BadRequestException(String.format("User with email '%s' already exists", entity.getEmail()));
//
//        entity.setEmail(dto.getEmail());
//        entity.setFirstName(dto.getFirstName());
//        entity.setLastName(dto.getLastName());
//        entity.setTelephone(dto.getTelephone());
//        entity.setGender(dto.getGender());
//
//        return this.userRepository.save(entity);
//    }

    @Override
    public User update(UUID id, UpdateUserDTO dto) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setTelephone(dto.getTelephone());
        return this.userRepository.save(user);
    }

    @Override
    public boolean delete(UUID id) {
        this.userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", id));

        this.userRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<User> getAllByRole(Pageable pageable, ERole role) {
        return this.userRepository.findByRoles(pageable, role);
    }

    @Override
    public Page<User> searchUser(Pageable pageable, String searchKey) {
        return this.userRepository.searchUser(pageable, searchKey);
    }

    @Override
    public User getLoggedInUser() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }


    @Override
    public User changeStatus(UUID id, EUserStatus status) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        entity.setStatus(status);

        return this.userRepository.save(entity);
    }

//    @Override
//    public User changeProfileImage(UUID id, File file) {
//        User entity = this.userRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("Document", "id", id.toString()));
//        File existingFile = entity.getProfileImage();
//        if (existingFile != null) {
//            this.fileStorageService.removeFileOnDisk(existingFile.getPath());
//        }
//        entity.setProfileImage(file);
//        return this.userRepository.save(entity);
//
//    }

//    @Override
//    public User removeProfileImage(UUID id) {
//        User user = this.userRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("User", "id", id.toString()));
//        File file = user.getProfileImage();
//        if (file != null) {
//            this.fileService.delete(file.getId());
//        }
//        user.setProfileImage(null);
//        return this.userRepository.save(user);
//    }

    @Override
    public Optional<User> findByActivationCode(String activationCode) {
        return this.userRepository.findByActivationCode(activationCode);
    }
}
