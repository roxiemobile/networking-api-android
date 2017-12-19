package com.roxiemobile.networkingapi.util;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.CollectionUtils;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpCookie;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class CookieUtils
{
// MARK: - Construction

    private CookieUtils() {
        // Do nothing
    }

// MARK: - Methods

    public static HttpCookie getCookie(@NonNull HttpCookie[] cookies, @NonNull String cookieName) {
        return getCookie(Arrays.asList(cookies), cookieName);
    }

    public static HttpCookie getCookie(@NonNull List<HttpCookie> cookies, @NonNull String cookieName) {
        Guard.notNull(cookies, "cookies is null");
        Guard.notEmpty(cookieName, "cookieName is empty");

        return Stream.of(cookies)
                     .filter(cookie -> cookie.getName().equals(cookieName))
                     .findFirst()
                     .orElse(null);
    }

    public static boolean isExpiredOrNull(HttpCookie cookie) {
        return isExpiredOrNull(cookie, 0);
    }

    public static boolean isExpiredOrNull(HttpCookie cookie, long offsetInMilliseconds) {
        boolean expired = true;

        if (cookie != null) {
            Date expiryDate = cookie.getExpiryDate();
            expired = (expiryDate != null) && (new Date().getTime() + offsetInMilliseconds >= expiryDate.getTime());
        }
        return expired;
    }

    public static URI cookiesUri(URI uri) {
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

    public static URI cookiesUri(HttpCookie cookie) {
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

    public static @NonNull HttpCookie[] asArray(CookieStore cookieStore) {
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

    private static final String TAG = CookieUtils.class.getSimpleName();

    private static final HttpCookie[] EMPTY_COOKIE_ARRAY = new HttpCookie[]{};
}
