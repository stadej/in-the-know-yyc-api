package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.SubscriptionRequest;
import com.intheknowyyc.api.data.exceptions.SubscriptionNotFoundException;
import com.intheknowyyc.api.data.models.Subscription;
import com.intheknowyyc.api.data.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.intheknowyyc.api.utils.Constants.*;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Retrieves all subscriptions.
     *
     * @return a list of all subscriptions
     */
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Finds a subscription by its ID.
     *
     * @param id the ID of the subscription
     * @return the subscription with the given ID
     * @throws SubscriptionNotFoundException if no subscription is found with the given ID
     */
    public Subscription findSubscriptionById(int id) {
        return subscriptionRepository.findById(id).orElseThrow(() -> new SubscriptionNotFoundException(String.format(SUBSCRIPTION_NOT_FOUND_BY_ID, id)));
    }

    /**
     * Retrieves a subscription by email.
     *
     * @param email the email of the subscription
     * @return the subscription with the given email
     * @throws SubscriptionNotFoundException if no subscription is found with the given email
     */
    public Subscription getSubscriptionByEmail(String email) {
        return subscriptionRepository.findByEmail(email).orElseThrow(() -> new SubscriptionNotFoundException(String.format(SUBSCRIPTION_NOT_FOUND_BY_EMAIL, email)));
    }

    /**
     * Subscribes a new user.
     *
     * @param subscription the subscription request containing the email
     * @return the newly created subscription
     * @throws IllegalStateException if a subscription with the given email already exists
     */
    public Subscription subscribe(@RequestBody SubscriptionRequest subscription) {
        if (subscriptionRepository.findByEmail(subscription.getEmail()).isPresent()) {
            throw new IllegalStateException("Subscription with this email already exists");
        } else {
            Subscription newSubscription = new Subscription();
            newSubscription.setUuid(UUID.randomUUID());
            newSubscription.setEmail(subscription.getEmail());
            newSubscription.setCreatedAt(LocalDateTime.now());
            return subscriptionRepository.save(newSubscription);
        }
    }

    /**
     * Unsubscribes a user by email.
     *
     * @param uuid the email of the subscription to be deleted
     * @throws SubscriptionNotFoundException if no subscription is found with the given email
     */
    @Transactional
    public void unsubscribe(UUID uuid) {
        if (subscriptionRepository.existsByUuid(uuid)) {
            subscriptionRepository.deleteByUuid(uuid);
        } else {
            throw new SubscriptionNotFoundException(String.format(SUBSCRIPTION_NOT_FOUND_BY_UUID, uuid));
        }
    }

}