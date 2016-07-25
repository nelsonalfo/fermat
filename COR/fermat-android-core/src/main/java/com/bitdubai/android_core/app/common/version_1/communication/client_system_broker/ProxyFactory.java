package com.bitdubai.android_core.app.common.version_1.communication.client_system_broker;

import android.util.Log;

import com.bitdubai.android_core.app.common.version_1.communication.client_system_broker.exceptions.CantCreateProxyException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantGetModuleManagerException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.ModuleManagerNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;
import com.bitdubai.fermat_core.FermatSystem;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mati on 2016.03.31..
 */
public class ProxyFactory {

    private static final String TAG = "ProxyFactory";

    private Map<Class, ModuleManager> openModules;

    public ProxyFactory() {
        this.openModules = new HashMap<>();
    }

    public ModuleManager createModuleManagerProxy(PluginVersionReference pluginVersionReference, InvocationHandler invocationHandler) throws CantCreateProxyException {
        ModuleManager moduleManager = null;
        if (!openModules.containsKey(pluginVersionReference)) {
            try {
                ModuleManager moduleManagerBase = FermatSystem.getInstance().getModuleManager2(pluginVersionReference);
                if (moduleManagerBase == null)
                    throw new RuntimeException(new StringBuilder().append("Module manager null in platform, please check if your plugin is connected, pluginVersionReference: ").append(pluginVersionReference.toString3()).toString());
                Class clazz = moduleManagerBase.getClass();
                moduleManager = (ModuleManager) Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        clazz.getInterfaces(),
                        invocationHandler);
                openModules.put(clazz, moduleManager);
            } catch (CantGetModuleManagerException e) {
                try {
                    Class clazz = FermatSystem.getInstance().getModuleManager3(pluginVersionReference);
                    if (clazz == null)
                        throw new RuntimeException(new StringBuilder().append("Module manager null in platform, please check if your plugin is connected, pluginVersionReference: ").append(pluginVersionReference.toString3()).toString());
                    moduleManager = (ModuleManager) Proxy.newProxyInstance(
                            clazz.getClassLoader(),
                            clazz.getInterfaces(),
                            invocationHandler);
                    openModules.put(clazz, moduleManager);
                } catch (Exception e2) {
                    Log.e(TAG, new StringBuilder().append("Cant get module manager in platform, please check if your plugin is connected, pluginVersionReference: ").append(pluginVersionReference.toString3()).toString());
                    throw new CantCreateProxyException("Cant get module manager from system", e, "factory", "");
                }
            } catch (ModuleManagerNotFoundException e) {
                Log.e(TAG, new StringBuilder().append("Cant get module manager in platform, please check if your plugin is connected, pluginVersionReference: ").append(pluginVersionReference.toString3()).toString());
                throw new CantCreateProxyException("Cant fount module manager from system", e, "factory", "");

            }
        } else {
            moduleManager = openModules.get(pluginVersionReference);
        }
//        Log.i(TAG,"interfaces: ");
//        for (Class<?> aClass : moduleManager.getClass().getInterfaces()) {
//            Log.i(TAG,aClass.getName());
//        }
        return moduleManager;
    }

    public ModuleManager createModuleManagerProxy(Class<ModuleManager> moduleManagerClass, InvocationHandler invocationHandler) throws CantCreateProxyException {
        ModuleManager moduleManager = null;
        if (moduleManagerClass == null)
            throw new RuntimeException("ModuleManagerClass is null, please check this");
        if (!openModules.containsKey(moduleManagerClass)) {
            try {
                moduleManager = (ModuleManager) Proxy.newProxyInstance(
                        moduleManagerClass.getClassLoader(),
                        moduleManagerClass.getInterfaces(),
                        invocationHandler);
                openModules.put(moduleManagerClass, moduleManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            moduleManager = openModules.get(moduleManagerClass);
        }
        return moduleManager;
    }

    public ModuleManager disposalModuleManager(PluginVersionReference pluginVersionReference) {
        //todo: tengo que ir guardando un historial de pedidos con el pluginVersionReference o ver como obtener la clase del core sin tanto lio.
        return null;// openModules.remove(pluginVersionReference);
    }


}
