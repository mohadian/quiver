
package com.zagros.quiver.rest.core;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class RestController {
    private static RestController mInstance = null;

    private static Map<Class<?>, RequestRepository<?>> mRepositoryMap;

    private RestController() {
        mRepositoryMap = new HashMap<Class<?>, RequestRepository<?>>();
        initRepositories();
    }

    public static synchronized RestController getInstance() {
        if (mInstance == null) {
            mInstance = new RestController();
        }
        return mInstance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not supported for singleton");
    }

    public void initRepositories() {

    }

    public <E extends Enum<E>> void addRepository(ServiceApi<E> serviceApi, Class<E> clazz) {
        DefaultRequestRepository<E> purhaseRepository = new DefaultRequestRepository<E>(
                serviceApi,
                clazz);
        addRepository(clazz, purhaseRepository);
    }

    public <E extends Enum<E>> void addRepository(Class<E> clazz,
                                                  RequestRepository<E> purhaseRepository
    ) {

        mRepositoryMap.put(clazz, purhaseRepository);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> RestRequest buildRequest(Class<E> clazz, E
            method, String... args) {
        RequestRepository<E> reps = (RequestRepository<E>) mRepositoryMap.get(clazz);
        if (reps != null) {
            return buildRequest(reps, method, args);
        } else {
            throw new RuntimeException("calling non existant method");
        }

    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> RestRequest buildRequest(RequestRepository<E> repo, E specKey,
                                                                String... args) {
        Object tmpSpecKey = specKey;
        RestRequest request = null;
        try {
            request = repo.buildRequest((E) tmpSpecKey, args);
        } catch (RestException e) {
            Log.e(RestController.class.getSimpleName(),
                    "request building has failed", e);
        }
        return request;
    }

    @SuppressWarnings("unchecked")
    public <D extends DefaultRequestRepository<?>, F extends Enum<F>> D getRequestRepository(
            Class<F> clazz) {
        if (!mRepositoryMap.containsKey(clazz)) {
            throw new RuntimeException("repository not found");
        }
        return (D) getRequestRepositoryHelper(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> DefaultRequestRepository<T> getRequestRepositoryHelper(
            Class<T> clazz) {
        return (DefaultRequestRepository<T>) mRepositoryMap.get(clazz);
    }

}
