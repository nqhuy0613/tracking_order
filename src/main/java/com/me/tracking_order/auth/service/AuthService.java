package com.me.tracking_order.auth.service;

import com.me.tracking_order.auth.dto.request.RegisterRequest;
import com.me.tracking_order.auth.dto.response.UserResponse;
import com.me.tracking_order.cart.entity.Cart;
import com.me.tracking_order.cart.repository.CartRepository;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.user.entity.Role;
import com.me.tracking_order.user.entity.User;
import com.me.tracking_order.user.entity.UserRole;
import com.me.tracking_order.user.mapper.UserMapper;
import com.me.tracking_order.user.repository.RoleRepository;
import com.me.tracking_order.user.repository.UserRepository;
import com.me.tracking_order.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CartRepository cartRepository;

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if(userRepository.existsByUsername(req.getUsername()) ) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if(userRepository.existsByPhone(req.getPhone()) ) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        if(userRepository.existsByEmail(req.getEmail()) ) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if(!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setGender(req.getGender());
        user.setDob(req.getDob());


        User savedUser = userRepository.save(user);
        Cart cart = new Cart();
        cart.setUser( savedUser );
        cartRepository.save( cart );

        Role customerRole = roleRepository
                .findByNameAndIsDeletedFalse("CUSTOMER")
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.ROLE_NOT_FOUND)
                );

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(customerRole);

        userRoleRepository.save(userRole);



        return userMapper.toResponse(user);
    }
}
