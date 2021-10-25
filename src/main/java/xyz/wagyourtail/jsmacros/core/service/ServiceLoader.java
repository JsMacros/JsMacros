package xyz.wagyourtail.jsmacros.core.service;

import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceLoader {
    protected final Core<?, ?> runner;
    protected final Map<String, Pair<ServiceTrigger, EventContainer<EventService>>> registeredServices = new LinkedHashMap<>();

    public ServiceLoader(Core<?, ?> runner) {
        this.runner = runner;
        load();
    }

    /**
     * @param name
     * @param trigger
     *
     * @return false if service with that name already registered
     */
    public synchronized boolean registerService(String name, ServiceTrigger trigger) {
        if (registeredServices.containsKey(name)) return false;
        registeredServices.put(name, new Pair<>(trigger, null));
        if (trigger.enabled) startService(name);
        return true;
    }

    public synchronized void unregisterService(String name) {
        stopService(name);
        registeredServices.remove(name);
    }

    /**
     * starts service once
     * @param name service name
     *
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus startService(String name) {
        Pair<ServiceTrigger, EventContainer<EventService>> service = registeredServices.get(name);
        if (service == null) return ServiceStatus.UNKNOWN;
        if (service.getU() == null || service.getU().getCtx().isContextClosed()) {
            service.setU((EventContainer<EventService>) runner.exec(service.getT().toScriptTrigger(), new EventService(name)));
            return ServiceStatus.STOPPED;
        }
        return ServiceStatus.RUNNING;
    }

    /**
     * @param name service name
     *
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus stopService(String name) {
        Pair<ServiceTrigger, EventContainer<EventService>> service = registeredServices.get(name);
        if (service == null) return ServiceStatus.UNKNOWN;
        if (service.getU() != null && !service.getU().getCtx().isContextClosed()) {
            ((EventService) service.getU().getCtx().getTriggeringEvent()).stopListener.get();
            service.getU().getCtx().closeContext();
            return ServiceStatus.RUNNING;
        }
        return ServiceStatus.STOPPED;
    }

    /**
     * @param name service name
     *
     * @return state before "restarting" (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus restartService(String name) {
        ServiceStatus state = stopService(name);
        startService(name);
        return state;
    }

    /**
     * @param name service name
     *
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus enableService(String name) {
        Pair<ServiceTrigger, EventContainer<EventService>> service = registeredServices.get(name);
        if (service == null) return ServiceStatus.UNKNOWN;
        if (!service.getT().enabled) {
            service.getT().enabled = true;
            return ServiceStatus.DISABLED;
        }
        return ServiceStatus.ENABLED;
    }

    /**
     * @param name service name
     *
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus disableService(String name) {
        Pair<ServiceTrigger, EventContainer<EventService>> service = registeredServices.get(name);
        if (service == null) return ServiceStatus.UNKNOWN;
        if (service.getT().enabled) {
            service.getT().enabled = false;
            return ServiceStatus.ENABLED;
        }
        return ServiceStatus.DISABLED;
    }

    /**
     * @param name service name
     *
     * @return {@link ServiceStatus#UNKNOWN} if unknown service, {@link ServiceStatus#RUNNING} if disabled and running, {@link ServiceStatus#DISABLED} if disabled and stopped, {@link ServiceStatus#STOPPED} if enabled and stopped, {@link ServiceStatus#ENABLED} if enabled and running.
     */
    public synchronized ServiceStatus status(String name) {
        Pair<ServiceTrigger, EventContainer<EventService>> service = registeredServices.get(name);
        if (service == null) return ServiceStatus.UNKNOWN;
        if (service.getT().enabled) {
            if (service.getU().getCtx().isContextClosed()) return ServiceStatus.STOPPED;
            return ServiceStatus.ENABLED;
        }
        if (service.getU().getCtx().isContextClosed()) return ServiceStatus.DISABLED;
        return ServiceStatus.RUNNING;
    }

    public synchronized void load() {
        Map<String, ServiceTrigger> configTriggers = runner.config.getOptions(CoreConfigV2.class).services;
        for (Map.Entry<String, Pair<ServiceTrigger, EventContainer<EventService>>> registered : registeredServices.entrySet()) {
            if (!configTriggers.containsKey(registered.getKey()) || !configTriggers.get(registered.getKey()).equals(registered.getValue().getT())) {
                ServiceStatus prevStatus = status(registered.getKey());
                registered.getValue().setT(configTriggers.get(registered.getKey()));
                ServiceStatus newStatus = status(registered.getKey());
                if (newStatus == ServiceStatus.RUNNING) stopService(registered.getKey());
                else if (newStatus == ServiceStatus.ENABLED) restartService(registered.getKey());
            }
        }
        for (Map.Entry<String, ServiceTrigger> trigger : configTriggers.entrySet()) {
            if (!registeredServices.containsKey(trigger.getKey())) {
                registeredServices.put(trigger.getKey(), new Pair<>(trigger.getValue(), null));
                if (trigger.getValue().enabled) startService(trigger.getKey());
            }
        }
    }

    public synchronized void save() {
        Map<String, ServiceTrigger> services = new HashMap<>();
        for (Map.Entry<String, Pair<ServiceTrigger, EventContainer<EventService>>> service : registeredServices.entrySet()) {
            services.put(service.getKey(), service.getValue().getT());
        }

        runner.config.getOptions(CoreConfigV2.class).services = services;
        runner.config.saveConfig();
    }

    public enum ServiceStatus {
        ENABLED, DISABLED, // returned by start/stop
        RUNNING, STOPPED, // returned by enable/disable
        UNKNOWN // service doesn't exist
    }
}
