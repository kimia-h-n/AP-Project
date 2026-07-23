package org.example.divar.util;

import org.example.divar.service.*;
import org.example.divar.validation.UserValidation;
import org.example.divar.validation.AdvertisementValidation;

public class AppContext {

    private static final UserValidation USER_VALIDATION = new UserValidation();
    private static final UserService USER_SERVICE = new UserServiceHttp();
    private static final AdvertisementValidation ADVERTISEMENT_VALIDATION = new AdvertisementValidation();
    private static final AdvertisementService ADVERTISEMENT_SERVICE = new AdvertisementServiceHttp();
    private static final ConversationService CONVERSATION_SERVICE = new ConversationServiceHttp();
    private static final AdminService ADMIN_SERVICE = new AdminServiceHttp();
    private static final ChatService CHAT_SERVICE = new ChatServiceWebSocket();
    private static final RatingService RATING_SERVICE = new RatingServiceHttp();

    public static RatingService getRatingService() {
        return RATING_SERVICE;
    }

    public static UserValidation getUserValidation() {
        return USER_VALIDATION;
    }

    public static UserService getUserService() {
        return USER_SERVICE;
    }

    public static AdvertisementValidation getAdvertisementValidation() {
        return ADVERTISEMENT_VALIDATION;
    }

    public static AdvertisementService getAdvertisementService() {
        return ADVERTISEMENT_SERVICE;
    }

    public static ConversationService getConversationService() {
        return CONVERSATION_SERVICE;
    }

    public static AdminService getAdminService() {
        return ADMIN_SERVICE;
    }

    public static ChatService getChatService() {
        return CHAT_SERVICE;
    }
}



