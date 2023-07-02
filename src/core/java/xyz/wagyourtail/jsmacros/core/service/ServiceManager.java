package xyz.wagyourtail.jsmacros.core.service;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.*;

/**
 * @author Wagyourtail
 * @since 1.6.3
 */
public class ServiceManager {
    protected boolean reloadOnModify;
    protected final Object2LongMap<String> lastModifiedMap = new Object2LongArrayMap<>();
    protected final Set<String> crashedServices = new HashSet<>();

    protected final Core<?, ?> runner;
    protected final Map<String, Pair<ServiceTrigger, EventContainer<?>>> registeredServices = new LinkedHashMap<>();

    public ServiceManager(Core<?, ?> runner) {
        this.runner = runner;
    }

    /**
     * @param name
     * @param pathToFile relative to macro folder
     * @return false if service with that name is already registered
     */
    public synchronized boolean registerService(String name, String pathToFile) {
        return registerService(name, new ServiceTrigger(runner.config.macroFolder.getAbsoluteFile().toPath().resolve(pathToFile).toFile(), true));
    }

    /**
     * @param name
     * @param pathToFile relative to macro folder
     * @param enabled
     * @return false if service with that name is already registered
     */
    public synchronized boolean registerService(String name, String pathToFile, boolean enabled) {
        return registerService(name, new ServiceTrigger(runner.config.macroFolder.getAbsoluteFile().toPath().resolve(pathToFile).toFile(), enabled));
    }

    /**
     * @param name
     * @param trigger
     * @return false if service with that name already registered
     */
    public synchronized boolean registerService(String name, ServiceTrigger trigger) {
        if (registeredServices.containsKey(name)) {
            return false;
        }
        registeredServices.put(name, new Pair<>(trigger, null));
        if (trigger.enabled) {
            startService(name);
        }
        return true;
    }

    /**
     * @param name
     * @return
     */
    public synchronized boolean unregisterService(String name) {
        stopService(name);
        disableReload(name);
        return registeredServices.remove(name) != null;
    }

    /**
     * @param serviceName the name of the service to disable the reload feature for
     * @since 1.8.4
     */
    public synchronized void disableReload(String serviceName) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(serviceName);
        if (service == null) {
            return;
        }
        crashedServices.remove(serviceName);
        lastModifiedMap.removeLong(service.getT().file);
    }

    /**
     * @param oldName
     * @param newName
     * @return false if service with new name already registered or old name doesn't exist
     */
    public synchronized boolean renameService(String oldName, String newName) {
        if (registeredServices.containsKey(newName)) {
            return false;
        }
        if (crashedServices.contains(oldName)) {
            crashedServices.remove(oldName);
            crashedServices.add(newName);
        }
        if (lastModifiedMap.containsKey(oldName)) {
            lastModifiedMap.put(newName, lastModifiedMap.removeLong(oldName));
        }
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.remove(oldName);
        if (service == null) {
            return false;
        }
        registeredServices.put(newName, service);
        return true;
    }

    /**
     * @return registered service names
     */
    public synchronized Set<String> getServices() {
        return ImmutableSet.copyOf(registeredServices.keySet());
    }

    /**
     * starts service once
     *
     * @param name service name
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus startService(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        if (service == null) {
            return ServiceStatus.UNKNOWN;
        }
        if (service.getU() == null || service.getU().getCtx().isContextClosed()) {
            service.setU(runner.exec(service.getT().toScriptTrigger(), new EventService(name)));
            return ServiceStatus.STOPPED;
        }
        return ServiceStatus.RUNNING;
    }

    /**
     * @param name service name
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus stopService(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        if (service == null) {
            return ServiceStatus.UNKNOWN;
        }
        if (service.getU() != null && !service.getU().getCtx().isContextClosed()) {
            try {
                if (((EventService) service.getU().getCtx().getTriggeringEvent()).stopListener != null) {
                    ((EventService) service.getU().getCtx().getTriggeringEvent()).stopListener.run();
                }
            } catch (Throwable e) {
                runner.profile.logError(e);
            }
            service.getU().getCtx().closeContext();
            return ServiceStatus.RUNNING;
        }
        return ServiceStatus.STOPPED;
    }

    /**
     * @param name service name
     * @return state before "restarting" (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus restartService(String name) {
        ServiceStatus state = stopService(name);
        startService(name);
        return state;
    }

    /**
     * @param name service name
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus enableService(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        if (service == null) {
            return ServiceStatus.UNKNOWN;
        }
        if (!service.getT().enabled) {
            service.getT().enabled = true;
            return ServiceStatus.DISABLED;
        }
        return ServiceStatus.ENABLED;
    }

    /**
     * @param name service name
     * @return previous state (or {@link ServiceStatus#UNKNOWN} if unknown service)
     */
    public synchronized ServiceStatus disableService(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        if (service == null) {
            return ServiceStatus.UNKNOWN;
        }
        disableReload(name);
        if (service.getT().enabled) {
            service.getT().enabled = false;
            return ServiceStatus.ENABLED;
        }
        return ServiceStatus.DISABLED;
    }

    /**
     * @param name the name of the service to check
     * @return {@code true} if the service is running, {@code false} otherwise.
     * @since 1.8.4
     */
    public synchronized boolean isRunning(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        if (service == null) {
            return false;
        }
        ServiceStatus status = status(name);
        return status == ServiceStatus.RUNNING || status == ServiceStatus.ENABLED;
    }

    /**
     * @param name the name of the service to check
     * @return {@code true} if the service is enabled, {@code false} otherwise.
     * @since 1.8.4
     */
    public synchronized boolean isEnabled(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        return service != null && service.getT().enabled;
    }

    /**
     * @param name service name
     * @return {@link ServiceStatus#UNKNOWN} if unknown service, {@link ServiceStatus#RUNNING} if disabled and running, {@link ServiceStatus#DISABLED} if disabled and stopped, {@link ServiceStatus#STOPPED} if enabled and stopped, {@link ServiceStatus#ENABLED} if enabled and running.
     */
    public synchronized ServiceStatus status(String name) {
        Pair<ServiceTrigger, EventContainer<?>> service = registeredServices.get(name);
        if (service == null) {
            return ServiceStatus.UNKNOWN;
        }
        if (service.getT().enabled) {
            if (service.getU() == null || service.getU().getCtx().isContextClosed()) {
                return ServiceStatus.STOPPED;
            }
            return ServiceStatus.ENABLED;
        }
        if (service.getU() == null || service.getU().getCtx().isContextClosed()) {
            return ServiceStatus.DISABLED;
        }
        return ServiceStatus.RUNNING;
    }

    /**
     * this might throw if the service is not running...
     *
     * @param name
     * @return the event that is current for the service
     * @since 1.6.5
     */
    public EventService getServiceData(String name) {
        return (EventService) registeredServices.get(name).getU().getCtx().getTriggeringEvent();
    }

    /**
     * @param name
     * @return
     * @since 1.6.5 [named getServiceData previously]
     */
    public ServiceTrigger getTrigger(String name) {
        return registeredServices.get(name).getT();
    }

    /**
     * load services from config
     */
    public synchronized void load() {
        Map<String, ServiceTrigger> configTriggers = runner.config.getOptions(CoreConfigV2.class).services;
        for (Map.Entry<String, Pair<ServiceTrigger, EventContainer<?>>> registered : registeredServices.entrySet()) {
            if (!configTriggers.containsKey(registered.getKey()) || !configTriggers.get(registered.getKey()).equals(registered.getValue().getT())) {
                ServiceStatus prevStatus = status(registered.getKey());
                registered.getValue().setT(configTriggers.get(registered.getKey()));
                ServiceStatus newStatus = status(registered.getKey());
                if (newStatus == ServiceStatus.RUNNING) {
                    stopService(registered.getKey());
                } else if (newStatus == ServiceStatus.ENABLED) {
                    restartService(registered.getKey());
                }
            }
        }
        for (Map.Entry<String, ServiceTrigger> trigger : configTriggers.entrySet()) {
            if (!registeredServices.containsKey(trigger.getKey())) {
                registeredServices.put(trigger.getKey(), new Pair<>(trigger.getValue(), null));
                if (trigger.getValue().enabled) {
                    startService(trigger.getKey());
                }
            }
        }
    }

    /**
     * save current registered services & enabled/disabled status to config
     */
    public synchronized void save() {
        Map<String, ServiceTrigger> services = new HashMap<>();
        for (Map.Entry<String, Pair<ServiceTrigger, EventContainer<?>>> service : registeredServices.entrySet()) {
            services.put(service.getKey(), service.getValue().getT());
        }

        runner.config.getOptions(CoreConfigV2.class).services = services;
        runner.config.saveConfig();
    }

    /**
     * Stops the service manager from reloading scrips on file changes.
     *
     * @since 1.8.4
     */
    public void stopReloadListener() {
        reloadOnModify = false;
    }

    /**
     * Will make the service manager reload scripts on file changes.
     *
     * @since 1.8.4
     */
    public void startReloadListener() {
        reloadOnModify = true;
    }

    /**
     * Mark a service as crashed so that it can be reloaded when its file changes. Crashed services
     * must be marked so that file change listener knows to restart them even if they are not
     * running because they crashed.
     *
     * @param serviceName the name of the service to mark as crashed
     * @since 1.8.4
     */
    public void markCrashed(String serviceName) {
        crashedServices.add(serviceName);
    }

    /**
     * @param serviceName the name of the service to check
     * @return {@code true} if the service previously crashed, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCrashed(String serviceName) {
        return crashedServices.contains(serviceName);
    }

    /**
     * Ticks the service manager. This will check if any services need to be reloaded and reloads
     * them if necessary.
     *
     * @since 1.8.4
     */
    public void tickReloadListener() {
        if (!reloadOnModify) {
            return;
        }
        for (Map.Entry<String, Pair<ServiceTrigger, EventContainer<?>>> service : registeredServices.entrySet()) {
            String file = service.getValue().getT().file;
            String name = service.getKey();
            // Only restart enabled and running services, i.e. services that are supposed to be running
            // If the service is not running because it crashed, try to restart it
            if (isEnabled(name) && (isRunning(name) || crashedServices.contains(name))) {
                long lastModified = runner.config.macroFolder.getAbsoluteFile().toPath().resolve(file).toFile().lastModified();
                if (!lastModifiedMap.containsKey(file)) {
                    lastModifiedMap.put(file, lastModified);
                    // Just assume that if the file was changed so was its content. Otherwise, use Adler-32 or MD5 checksum
                } else if (lastModifiedMap.getLong(file) != lastModified) {
                    lastModifiedMap.put(file, lastModified);
                    crashedServices.remove(name);
                    restartService(name);
                }
            }
        }
    }

    // Enabled = running & enabled
    // Disabled = !running & !enabled
    // Running = running & !enabled
    // Stopped = !running & enabled
    public enum ServiceStatus {
        ENABLED, DISABLED, // returned by start/stop
        RUNNING, STOPPED, // returned by enable/disable
        UNKNOWN // service doesn't exist
    }

}
