package com.roxiemobile.networkingapi.network.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.CollectionUtils;
import com.roxiemobile.androidcommons.util.IOUtils;
import com.roxiemobile.networkingapi.util.CookieUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class PersistentCookieStore implements CookieStore
{
// MARK: - Construction

    public PersistentCookieStore(Context context, HttpCookie[] cookies) {
        this(context, (cookies != null) ? Arrays.asList(cookies) : null);
    }

    public PersistentCookieStore(Context context, List<HttpCookie> cookies) {
        this(context);

        // Add cookies to CookieStore
        if (CollectionUtils.isNotEmpty(cookies)) {
            for (HttpCookie cookie : cookies) {
                add(CookieUtils.cookiesUri(cookie), cookie);
            }
        }
    }

    public PersistentCookieStore(Context context, CookieStore otherCookieStore) {
        this(context);

        // Copy cookies from other CookieStore
        if (otherCookieStore != null) {
            for (URI uri : otherCookieStore.getURIs()) {
                for (HttpCookie cookie : otherCookieStore.get(uri)) {
                    add(uri, cookie);
                }
            }
        }
    }

    public PersistentCookieStore(Context context) {
        // Init instance variables
        mSharedPreferences = context.getSharedPreferences(SP_COOKIE_STORE, Context.MODE_PRIVATE);
        loadAllFromPersistence();
    }

// MARK: - Methods

    public synchronized void add(URI uri, @NonNull HttpCookie cookie) {
        Guard.notNull(cookie, "cookie is null");

        uri = CookieUtils.cookiesUri(uri);
        List<HttpCookie> cookies = mMap.get(uri);
        if (cookies == null) {
            cookies = new ArrayList<>();
            mMap.put(uri, cookies);
        }
        else {
            cookies.remove(cookie);
        }
        cookies.add(cookie);
        saveToPersistence(uri, cookie);
    }

    public synchronized List<HttpCookie> get(@NonNull URI uri) {
        Guard.notNull(uri, "uri is null");

        List<HttpCookie> result = new ArrayList<>();
        List<HttpCookie> cookiesToRemoveFromPersistence = new ArrayList<>();
        final Date date = new Date();

        // Get cookies associated with given URI. If none, returns an empty list
        List<HttpCookie> cookiesForUri = mMap.get(uri);
        if (cookiesForUri != null) {
            for (Iterator<HttpCookie> i = cookiesForUri.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired(date)) {
                    cookiesToRemoveFromPersistence.add(cookie);
                    i.remove(); // remove expired cookies
                }
                else {
                    result.add(cookie);
                }
            }
        }

        // Get all cookies that domain matches the URI
        for (Map.Entry<URI, List<HttpCookie>> entry : mMap.entrySet()) {
            if (uri.equals(entry.getKey())) {
                continue; // skip the given URI; we've already handled it
            }

            List<HttpCookie> entryCookies = entry.getValue();
            for (Iterator<HttpCookie> i = entryCookies.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (!HttpCookie.domainMatches(cookie.getDomain(), uri.getHost())) {
                    continue;
                }
                if (cookie.hasExpired(date)) {
                    cookiesToRemoveFromPersistence.add(cookie);
                    i.remove(); // remove expired cookies
                }
                else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }

        if (!cookiesToRemoveFromPersistence.isEmpty()) {
            removeFromPersistence(uri, cookiesToRemoveFromPersistence);
        }

        return Collections.unmodifiableList(result);
    }

    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> result = new ArrayList<>();
        List<HttpCookie> cookiesToRemoveFromPersistence = new ArrayList<>();
        final Date date = new Date();

        for (URI uri : mMap.keySet()) {
            for (Iterator<HttpCookie> i = mMap.get(uri).iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired(date)) {
                    cookiesToRemoveFromPersistence.add(cookie);
                    i.remove(); // remove expired cookies
                }
                else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }

            if (!cookiesToRemoveFromPersistence.isEmpty()) {
                removeFromPersistence(uri, cookiesToRemoveFromPersistence);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized List<URI> getURIs() {
        List<URI> result = new ArrayList<>(mMap.keySet());
        result.remove(null); // sigh
        return Collections.unmodifiableList(result);
    }

    public synchronized boolean remove(URI uri, @NonNull HttpCookie cookie) {
        Guard.notNull(cookie, "cookie is null");

        List<HttpCookie> cookies = mMap.get(CookieUtils.cookiesUri(uri));
        boolean result = false;

        if (cookies != null) {
            result = cookies.remove(cookie);
            if (cookies.isEmpty()) {
                mMap.remove(uri);
            }
            if (result) {
                removeFromPersistence(uri, cookie);
            }
        }

        return result;
    }

    public synchronized boolean removeAll() {
        boolean result = !mMap.isEmpty();
        mMap.clear();
        removeAllFromPersistence();
        return result;
    }

// MARK: - Private Methods

    private void loadAllFromPersistence() {
        mMap = new HashMap<>();

        Map<String, ?> allPairs = mSharedPreferences.getAll();
        for (Entry<String, ?> entry : allPairs.entrySet()) {
            String[] uriAndName = entry.getKey().split(SP_KEY_DELIMITER_REGEX, 2);
            try {
                URI uri = new URI(uriAndName[0]);
                String encodedCookie = (String) entry.getValue();
                HttpCookie cookie = (HttpCookie) IOUtils.decodeObject(encodedCookie);

                List<HttpCookie> targetCookies = mMap.get(uri);
                if (targetCookies == null) {
                    targetCookies = new ArrayList<>();
                    mMap.put(uri, targetCookies);
                }
                // Repeated cookies cannot exist in persistence
                // targetCookies.remove(cookie)
                targetCookies.add(cookie);
            }
            catch (URISyntaxException ex) {
                Logger.w(TAG, ex);
            }
        }
    }

    private void saveToPersistence(URI uri, HttpCookie cookie) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(uri.toString() + SP_KEY_DELIMITER + cookie.getName(), IOUtils.encodeObject(cookie));
        editor.apply();
    }

    private void removeFromPersistence(URI uri, List<HttpCookie> cookiesToRemove) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (HttpCookie cookieToRemove : cookiesToRemove) {
            editor.remove(uri.toString() + SP_KEY_DELIMITER + cookieToRemove.getName());
        }
        editor.apply();
    }

    private void removeFromPersistence(URI uri, HttpCookie cookieToRemove) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(uri.toString() + SP_KEY_DELIMITER + cookieToRemove.getName());
        editor.apply();
    }

    private void removeAllFromPersistence() {
        mSharedPreferences.edit().clear().apply();
    }

// MARK: - Constants

    private static final String TAG = PersistentCookieStore.class.getSimpleName();

    // Persistence
    private static final String SP_COOKIE_STORE = "cookieStore";
    private static final String SP_KEY_DELIMITER = "|"; // Unusual char in URL
    private static final String SP_KEY_DELIMITER_REGEX = "\\" + SP_KEY_DELIMITER;

// MARK: - Variables

    private SharedPreferences mSharedPreferences;

    // This map may have null keys!
    private Map<URI, List<HttpCookie>> mMap = new HashMap<>();

}
