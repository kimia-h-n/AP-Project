package org.example.divar.util;

import org.example.divar.service.*;
import org.example.divar.validation.UserValidation;
import org.example.divar.validation.AdvertisementValidation;

public class AppContext {

    private static final UserValidation USER_VALIDATION = new UserValidation();
    private static final UserService USER_SERVICE = new UserServiceHttp();

    private static final AdvertisementValidation ADVERTISEMENT_VALIDATION = new AdvertisementValidation();
    private static final AdvertisementService ADVERTISEMENT_SERVICE = new AdvertisementServiceHttp();

    private static final ConversationService CONVERSATION_SERVICE = new ConversationServiceLocal();

    private static final AdminService ADMIN_SERVICE = new AdminServiceHttp();

    public static UserValidation getUserValidation() { return USER_VALIDATION; }
    public static UserService getUserService() { return USER_SERVICE; }

    public static AdvertisementValidation getAdvertisementValidation() { return ADVERTISEMENT_VALIDATION; }
    public static AdvertisementService getAdvertisementService() { return ADVERTISEMENT_SERVICE; }

    public static ConversationService getConversationService() { return CONVERSATION_SERVICE; }

    public static AdminService getAdminService() { return ADMIN_SERVICE; }
}



