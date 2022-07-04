package com.roxiemobile.networkingapi.network.http;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.util.CollectionUtils;
import com.roxiemobile.networkingapi.util.CookieUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class InMemoryCookieStore implements CookieStore {

// MARK: - Methods

    public InMemoryCookieStore(@Nullable HttpCookie[] cookies) {
        this((cookies != null) ? Arrays.asList(cookies) : null);
    }

    public InMemoryCookieStore(@Nullable List<HttpCookie> cookies) {
        // Add cookies to CookieStore
        if (CollectionUtils.isNotEmpty(cookies)) {
            for (HttpCookie cookie : cookies) {
                add(CookieUtils.cookiesUri(cookie), cookie);
            }
        }
    }

    public InMemoryCookieStore(@Nullable CookieStore otherCookieStore) {
        // Copy cookies from other CookieStore
        if (otherCookieStore != null) {
            for (URI uri : otherCookieStore.getURIs()) {
                for (HttpCookie cookie : otherCookieStore.get(uri)) {
                    add(uri, cookie);
                }
            }
        }
    }

    public InMemoryCookieStore() {
        // Do nothing
    }

// MARK: - Methods

    public synchronized void add(@Nullable URI uri, @NotNull HttpCookie cookie) {
        Guard.notNull(cookie, "cookie is null");

        uri = CookieUtils.cookiesUri(uri);
        List<HttpCookie> cookies = map.get(uri);
        if (cookies == null) {
            cookies = new ArrayList<>();
            map.put(uri, cookies);
        }
        else {
            cookies.remove(cookie);
        }
        cookies.add(cookie);
    }

    public synchronized @NotNull List<HttpCookie> get(@Nullable URI uri) {
        Guard.notNull(uri, "uri is null");

        List<HttpCookie> result = new ArrayList<>();
        final Date date = new Date();

        // Get cookies associated with given URI. If none, returns an empty list
        List<HttpCookie> cookiesForUri = map.get(uri);
        if (cookiesForUri != null) {
            for (Iterator<HttpCookie> i = cookiesForUri.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired(date)) {
                    i.remove(); // remove expired cookies
                }
                else {
                    result.add(cookie);
                }
            }
        }

        // Get all cookies that domain matches the URI
        for (Map.Entry<URI, List<HttpCookie>> entry : map.entrySet()) {
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
                    i.remove(); // remove expired cookies
                }
                else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    public synchronized @NotNull List<HttpCookie> getCookies() {
        List<HttpCookie> result = new ArrayList<>();
        final Date date = new Date();

        for (List<HttpCookie> list : map.values()) {
            for (Iterator<HttpCookie> i = list.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired(date)) {
                    i.remove(); // remove expired cookies
                }
                else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    public synchronized @NotNull List<URI> getURIs() {
        List<URI> result = new ArrayList<>(map.keySet());
        result.remove(null); // sigh
        return Collections.unmodifiableList(result);
    }

    public synchronized boolean remove(@Nullable URI uri, @NotNull HttpCookie cookie) {
        Guard.notNull(cookie, "cookie is null");

        List<HttpCookie> cookies = map.get(CookieUtils.cookiesUri(uri));
        return (cookies != null) && cookies.remove(cookie);
    }

    public synchronized boolean removeAll() {
        boolean result = !map.isEmpty();
        map.clear();
        return result;
    }

// MARK: - Variables

    // NOTE: This map may have null keys!
    private final @NotNull Map<URI, List<HttpCookie>> map = new HashMap<>();
}
