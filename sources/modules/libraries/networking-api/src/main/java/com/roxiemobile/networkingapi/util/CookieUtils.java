package com.roxiemobile.networkingapi.util;

import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.CollectionUtils;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpCookie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class CookieUtils {

// MARK: - Construction

    private CookieUtils() {
        // Do nothing
    }

// MARK: - Methods

    public static @Nullable HttpCookie getCookie(@NotNull HttpCookie[] cookies, @NotNull String cookieName) {
        return getCookie(Arrays.asList(cookies), cookieName);
    }

    public static @Nullable HttpCookie getCookie(@NotNull List<HttpCookie> cookies, @NotNull String cookieName) {
        Guard.notNull(cookies, "cookies is null");
        Guard.notEmpty(cookieName, "cookieName is empty");

        return Stream.of(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }

    public static boolean isExpiredOrNull(@Nullable HttpCookie cookie) {
        return isExpiredOrNull(cookie, 0);
    }

    public static boolean isExpiredOrNull(@Nullable HttpCookie cookie, long offsetInMilliseconds) {
        boolean expired = true;

        if (cookie != null) {
            Date expiryDate = cookie.getExpiryDate();
            expired = (expiryDate != null) && (new Date().getTime() + offsetInMilliseconds >= expiryDate.getTime());
        }
        return expired;
    }

    public static @Nullable URI cookiesUri(@Nullable URI uri) {
        if (uri != null) {
            try {
                uri = new URI("http", uri.getHost(), null, null);
            }
            catch (URISyntaxException e) {
                Logger.w(TAG, e);
                uri = null;
            }
        }
        return uri;
    }

    public static @Nullable URI cookiesUri(@NotNull HttpCookie cookie) {
        URI uri = null;
        if (cookie != null) {
            try {
                uri = new URI("http", cookie.getDomain(), null, null);
            }
            catch (URISyntaxException e) {
                Logger.w(TAG, e);
                uri = null;
            }
        }
        return uri;
    }

    public static @NotNull HttpCookie[] asArray(@NotNull CookieStore cookieStore) {
        HttpCookie[] cookies = EMPTY_COOKIE_ARRAY;

        if (cookieStore != null) {
            List<HttpCookie> cookieList = cookieStore.getCookies();

            if (CollectionUtils.isNotEmpty(cookieList)) {
                cookies = cookieList.toArray(new HttpCookie[cookieList.size()]);
            }
        }
        return cookies;
    }

// MARK: - Constants

    private static final @NotNull String TAG = CookieUtils.class.getSimpleName();

    private static final @NotNull HttpCookie[] EMPTY_COOKIE_ARRAY = new HttpCookie[]{};
}
