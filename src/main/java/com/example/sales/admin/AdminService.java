package com.example.sales.admin;


import com.example.sales.ad.AdRepository;
import com.example.sales.ad.model.*;
import com.example.sales.ad.model.moderation.AdModerationRequest;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyBlockedException;
import com.example.sales.exception.UserAlreadyEnabled;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import com.example.sales.user.UserMapper;
import com.example.sales.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdRepository adRepository;
    private final AdMapper adMapper;

    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponse(userRepository.findAll());
    }

    public void blockUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!user.isEnable())
            throw new AlreadyBlockedException();
        user.setEnable(false);

    }

    public void unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isEnable())
            throw new UserAlreadyEnabled();
        user.setEnable(true);
    }


    public void moderateAd(Long id, AdModerationRequest moderationRequest) {

        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        switch (moderationRequest.getChoice()) {
            case APPROVE -> {
                ad.setStatus(AdStatus.APPROVED);
                ad.setRejectionReason(null);
            }
            case REJECT -> {
                ad.setStatus(AdStatus.REJECTED);
                ad.setRejectionReason(moderationRequest.getRejectReason());
            }
        }
        adRepository.save(ad);
    }

    public List<PendingAd> getAllPendingAds() {
        return adMapper.toPendingAdList(adRepository.findAllByStatus(AdStatus.PENDING));
    }

}
